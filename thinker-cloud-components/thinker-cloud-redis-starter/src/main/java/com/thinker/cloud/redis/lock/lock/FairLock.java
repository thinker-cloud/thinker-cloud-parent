package com.thinker.cloud.redis.lock.lock;

import com.thinker.cloud.redis.lock.model.LockInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 公平锁
 *
 * @author admin
 */
@Slf4j
public class FairLock implements Lock {

    private RLock rLock;

    private final LockInfo lockInfo;

    private final RedissonClient redisson;

    public FairLock(RedissonClient redissonClient, LockInfo info) {
        this.redisson = redissonClient;
        this.lockInfo = info;
    }

    @Override
    public boolean acquire() {
        try {
            rLock = redisson.getFairLock(lockInfo.getName());
            TimeUnit timeUnit = Optional.ofNullable(lockInfo.getTimeUnit()).orElse(TimeUnit.SECONDS);
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), timeUnit);
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
}
