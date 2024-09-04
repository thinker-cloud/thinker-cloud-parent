package com.thinker.cloud.redis.lock.aspect;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.redis.lock.annotation.DistributedLock;
import com.thinker.cloud.redis.lock.exception.LockInvocationException;
import com.thinker.cloud.redis.lock.lock.Lock;
import com.thinker.cloud.redis.lock.lock.LockFactory;
import com.thinker.cloud.redis.lock.model.LockInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 给添加@DistributedLock切面加锁处理
 *
 * @author admin
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class DistributedLockAspect {

    private final LockFactory lockFactory;

    private final LockInfoProvider lockInfoProvider;

    private final Map<String, LockRes> curThreadLock = new ConcurrentHashMap<>();

    @Around(value = "@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        LockInfo lockInfo = lockInfoProvider.get(joinPoint, distributedLock);
        String curLock = this.getCurrentLockId(joinPoint, distributedLock);
        curThreadLock.put(curLock, new LockRes(lockInfo, false));
        Lock lock = lockFactory.getLock(lockInfo);
        boolean lockRes = lock.acquire();

        // 如果获取锁失败了，则进入失败的处理逻辑
        if (!lockRes) {
            if (log.isWarnEnabled()) {
                log.warn("Timeout while acquiring Lock({})", lockInfo.getName());
            }
            // 如果自定义了获取锁失败的处理策略，则执行自定义的降级处理策略
            if (StrUtil.isNotBlank(distributedLock.customLockTimeoutStrategy())) {
                return this.handleCustomLockTimeout(distributedLock.customLockTimeoutStrategy(), joinPoint);
            } else {
                // 否则执行预定义的执行策略
                // 注意：如果没有指定预定义的策略，默认的策略为静默啥不做处理
                distributedLock.lockTimeoutStrategy().handle(lockInfo, lock, joinPoint);
            }
        }

        curThreadLock.get(curLock).setLock(lock);
        curThreadLock.get(curLock).setRes(true);

        return joinPoint.proceed();
    }

    @AfterReturning(value = "@annotation(distributedLock)")
    public void afterReturning(JoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String curLock = this.getCurrentLockId(joinPoint, distributedLock);
        releaseLock(distributedLock, joinPoint, curLock);
        cleanUpThreadLocal(curLock);
    }

    @AfterThrowing(value = "@annotation(distributedLock)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, DistributedLock distributedLock, Throwable ex) throws Throwable {
        String curLock = this.getCurrentLockId(joinPoint, distributedLock);
        releaseLock(distributedLock, joinPoint, curLock);
        cleanUpThreadLocal(curLock);
        throw ex;
    }

    /**
     * 处理自定义加锁超时
     */
    private Object handleCustomLockTimeout(String lockTimeoutHandler, JoinPoint joinPoint) throws Throwable {
        // prepare invocation context
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(lockTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        // invoke
        Object res;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new LockInvocationException("Fail to invoke custom lock timeout handler: " + lockTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return res;
    }

    /**
     * 释放锁
     */
    private void releaseLock(DistributedLock distributedLock, JoinPoint joinPoint, String currentLock) throws Throwable {
        LockRes lockRes = curThreadLock.get(currentLock);
        if (Objects.isNull(lockRes)) {
            throw new NullPointerException("Please check whether the input parameter used as the lock key value has been modified in the method, which will cause the acquire and release locks to have different key values and throw null pointers.curentLockKey:" + currentLock);
        }
        if (lockRes.getRes()) {
            boolean releaseRes = curThreadLock.get(currentLock).getLock().release();
            // avoid release lock twice when exception happens below
            lockRes.setRes(false);
            if (!releaseRes) {
                handleReleaseTimeout(distributedLock, lockRes.getLockInfo(), joinPoint);
            }
        }
    }

    private void cleanUpThreadLocal(String curLock) {
        curThreadLock.remove(curLock);
    }

    /**
     * 获取当前锁在map中的key
     *
     * @param joinPoint       joinPoint
     * @param distributedLock digitLock
     * @return String
     */
    private String getCurrentLockId(JoinPoint joinPoint, DistributedLock distributedLock) {
        LockInfo lockInfo = lockInfoProvider.get(joinPoint, distributedLock);
        return Thread.currentThread().getId() + lockInfo.getName();
    }

    /**
     * 处理释放锁时已超时
     */
    private void handleReleaseTimeout(DistributedLock distributedLock, LockInfo lockInfo, JoinPoint joinPoint) throws Throwable {
        if (log.isWarnEnabled()) {
            log.warn("Timeout while release Lock({})", lockInfo.getName());
        }

        if (StrUtil.isNotBlank(distributedLock.customReleaseTimeoutStrategy())) {
            handleCustomReleaseTimeout(distributedLock.customReleaseTimeoutStrategy(), joinPoint);
        } else {
            distributedLock.releaseTimeoutStrategy().handle(lockInfo);
        }
    }

    /**
     * 处理自定义释放锁时已超时
     */
    private void handleCustomReleaseTimeout(String releaseTimeoutHandler, JoinPoint joinPoint) throws Throwable {
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(releaseTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        try {
            handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new LockInvocationException("Fail to invoke custom release timeout handler: " + releaseTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Data
    private static class LockRes {
        private LockInfo lockInfo;
        private Lock lock;
        private Boolean res;

        LockRes(LockInfo lockInfo, Boolean res) {
            this.lockInfo = lockInfo;
            this.res = res;
        }
    }
}
