package com.thinker.cloud.redis.cache.aspect;

import com.thinker.cloud.core.exception.AbstractException;
import com.thinker.cloud.core.exception.CacheableException;
import com.thinker.cloud.core.exception.LockException;
import com.thinker.cloud.redis.cache.annotation.Cacheable;
import com.thinker.cloud.redis.cache.generator.CustomCacheKeyGenerator;
import com.thinker.cloud.redis.cache.interfaces.DisableCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.expression.spel.SpelEvaluationException;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * The Cacheable Aspect
 *
 * @author admin
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class CacheableAspect {

    private static final String CACHE_KEY = "cacheable";
    private static final String EMPTY = "EMPTY";
    private static final long LOCK_TIME = 3;

    private final RedissonClient redissonClient;
    private final CustomCacheKeyGenerator cacheKeyGenerator;

    @SneakyThrows
    @Around(value = "@annotation(cacheable)")
    public Object around(ProceedingJoinPoint joinPoint, Cacheable cacheable) {
        // 检查请求参数是否定义禁用缓存
        boolean disableCache = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof DisableCache)
                .anyMatch(arg -> ((DisableCache) arg).disableCache());
        if (disableCache) {
            return joinPoint.proceed();
        }

        String prefix = CACHE_KEY + cacheable.prefix();
        String[] keys = cacheable.keys(), ignoreKeys = cacheable.ignoreKeys();

        // 若没有配置 唯一标识编号，则使用 url + 参数列表作为区分
        String key = cacheKeyGenerator.generator(joinPoint, prefix, keys, ignoreKeys);

        long expireTime = cacheable.expireTime();
        TimeUnit timeUnit = cacheable.timeUnit();

        // 缓存存在值，则直接返回
        RMapCache<String, Object> rMapCache = redissonClient.getMapCache(prefix);
        Object result = rMapCache.get(key);
        if (null != result) {
            return result;
        }

        // 缓存没有则加锁执行代理方法然后放入缓存中
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(LOCK_TIME, LOCK_TIME, TimeUnit.SECONDS)) {
                // 再次尝试获取，防止其他请求已执行
                Object result1 = rMapCache.get(key);
                if (null != result1) {
                    return result1;
                }

                // 缓存没值则将接口响应数据缓存后在返回
                Object value = Optional.ofNullable(joinPoint.proceed()).orElse(EMPTY);
                rMapCache.put(key, value, expireTime, timeUnit);
                return EMPTY.equals(value) ? null : value;
            }
            log.warn("缓存加锁失败，执行代理接口方法，key:{}", key);

            // 缓存加锁失败，执行代理接口方法，并将执行结果放入缓存
            Object value = Optional.ofNullable(joinPoint.proceed()).orElse(EMPTY);
            rMapCache.put(key, value, expireTime, timeUnit);
            return EMPTY.equals(value) ? null : value;
        } catch (InterruptedException e) {
            log.error("缓存加锁异常，ex={}", e.getMessage(), e);
            throw new LockException("缓存加锁异常，请联系管理员");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @AfterThrowing(pointcut = "@annotation(com.thinker.cloud.redis.cache.annotation.Cacheable)", throwing = "ex")
    public void afterThrowing(Throwable ex) {
        if (ex instanceof SpelEvaluationException) {
            log.error("表达式解析异常，ex={}", ex.getMessage(), ex);
            throw new CacheableException("el表达式解析异常");
        }

        if (ex instanceof AbstractException) {
            throw (AbstractException) ex;
        }

        log.error("未知异常，ex={}", ex.getMessage(), ex);
        throw new CacheableException("未知异常，请联系管理员");
    }
}
