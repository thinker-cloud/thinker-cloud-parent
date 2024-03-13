package com.thinker.cloud.redis.lock.lock;

import com.thinker.cloud.redis.lock.model.LockInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 可重入锁
 *
 * @author admin
 */
@Slf4j
public class ReentrantLock implements Lock {

    private RLock rLock;

    private final LockInfo lockInfo;

    private final RedissonClient redisson;

    public ReentrantLock(RedissonClient redissonClient, LockInfo lockInfo) {
        this.redisson = redissonClient;
        this.lockInfo = lockInfo;
    }

    @Override
    public boolean acquire() {
        try {
            rLock = redisson.getLock(lockInfo.getName());
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean release() {
        if (rLock.isHeldByCurrentThread()) {
            try {
                return rLock.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
                return false;
            }
        }
        return false;
    }

    public String getKey() {
        return this.lockInfo.getName();
    }
}
