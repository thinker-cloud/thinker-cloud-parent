package com.thinker.cloud.redis.lock.distributed;

/**
 * 分布式锁抽象类
 *
 * @author admin
 */
public abstract class AbstractDistributedLock implements IDistributedLock {

    @Override
    public boolean lock(String key) {
        return lock(key, WAIT_MILLIS, LEASE_MILLIS);
    }

    @Override
    public boolean lock(String key, long waitTime) {
        return lock(key, waitTime, LEASE_MILLIS);
    }
}
