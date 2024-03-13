package com.thinker.cloud.redis.lock.annotation;

import com.thinker.cloud.redis.lock.model.LockTimeoutStrategy;
import com.thinker.cloud.redis.lock.model.LockType;
import com.thinker.cloud.redis.lock.model.ReleaseTimeoutStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加锁注解
 *
 * @author Admin
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DigitLock {
    /**
     * 锁的名称
     *
     * @return name
     */
	String key();

    /**
     * 锁类型，默认可重入锁
     *
     * @return lockType
     */
    LockType lockType() default LockType.Reentrant;

    /**
     * 尝试加锁，最多等待时间
     *
     * @return waitTime
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     * 上锁以后xxx秒自动解锁
     *
     * @return leaseTime
     */
    long leaseTime() default Long.MIN_VALUE;

    /**
     * 加锁超时的处理策略
     *
     * @return lockTimeoutStrategy
     */
    LockTimeoutStrategy lockTimeoutStrategy() default LockTimeoutStrategy.FAIL_FAST;

    /**
     * 自定义加锁超时的处理策略
     *
     * @return customLockTimeoutStrategy
     */
    String customLockTimeoutStrategy() default "";

    /**
     * 释放锁时已超时的处理策略
     *
     * @return releaseTimeoutStrategy
     */
    ReleaseTimeoutStrategy releaseTimeoutStrategy() default ReleaseTimeoutStrategy.FAIL_FAST;

    /**
     * 自定义释放锁时已超时的处理策略
     *
     * @return customReleaseTimeoutStrategy
     */
    String customReleaseTimeoutStrategy() default "";

}
