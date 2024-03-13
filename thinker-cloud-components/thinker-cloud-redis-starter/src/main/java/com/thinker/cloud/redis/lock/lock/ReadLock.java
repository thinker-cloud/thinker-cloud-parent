package com.thinker.cloud.redis.lock.lock;

import com.thinker.cloud.redis.lock.model.LockInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 读锁
 *
 * @author admin
 */
@Slf4j
public class ReadLock implements Lock {

    private RReadWriteLock rLock;

    private final LockInfo lockInfo;

    private final RedissonClient redisson;

    public ReadLock(RedissonClient redissonClient, LockInfo info) {
        this.redisson = redissonClient;
        this.lockInfo = info;
    }

    @Override
    public boolean acquire() {
        try {
            rLock = redisson.getReadWriteLock(lockInfo.getName());
            return rLock.readLock().tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean release() {
        if (rLock.readLock().isHeldByCurrentThread()) {
            try {
                return rLock.readLock().forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
                return false;
            }
        }

        return false;
    }
}
