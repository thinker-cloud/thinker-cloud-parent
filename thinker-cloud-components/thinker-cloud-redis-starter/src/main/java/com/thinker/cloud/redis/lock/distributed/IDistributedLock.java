package com.thinker.cloud.redis.lock.distributed;

import com.thinker.cloud.redis.lock.model.LockInfo;

import java.time.Duration;

/**
 * 分布式锁顶级接口
 *
 * @author admin
 */
public interface IDistributedLock {

    /**
     * 默认最多等待时间
     */
    long WAIT_MILLIS = Duration.ofSeconds(3).toMillis();

    /**
     * 加锁的时间。超过这个时间后锁便自动解开
     */
    long LEASE_MILLIS = Duration.ofSeconds(3).toMillis();

    /**
     * 获取锁
     *
     * @param key key
     * @return 成功/失败
     */
    boolean lock(String key);

    /**
     * 获取锁
     *
     * @param key      key
     * @param waitTime 最多等待时间
     * @return 成功/失败
     */
    boolean lock(String key, long waitTime);

    /**
     * 获取锁
     *
     * @param key       key
     * @param waitTime  最多等待时间
     * @param leaseTime 加锁的时间
     * @return 成功/失败
     */
    boolean lock(String key, long waitTime, long leaseTime);

    /**
     * 获取锁
     *
     * @param lockInfo 锁信息
     * @return 成功/失败
     */
    boolean lock(LockInfo lockInfo);

    /**
     * 释放锁
     *
     * @param key key值
     * @return 成功/失败
     */
    boolean releaseLock(String key);

    /**
     * 释放锁
     *
     * @param lockInfo 锁信息
     * @return 成功/失败
     */
    boolean releaseLock(LockInfo lockInfo);
}
