package com.thinker.cloud.redis.lock.distributed;

import java.time.Duration;

/**
 * 分布式锁顶级接口
 *
 * @author admin
 */
public interface DistributedLock {

    /**
     * 默认最多等待时间
     */
    long WAIT_MILLIS = Duration.ofMinutes(1).toMillis();

    /**
     * 加锁的时间。超过这个时间后锁便自动解开
     */
    long LEASE_MILLIS = Duration.ofMinutes(1).toMillis();

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
     * 释放锁
     *
     * @param key key值
     */
    void releaseLock(String key);
}
