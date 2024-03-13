package com.thinker.cloud.redis.lock.distributed;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson分布式锁实现
 *
 * @author admin
 */
@Slf4j
@AllArgsConstructor
public class RedissonDistributedLock extends AbstractDistributedLock {

    private final RedissonClient redissonClient;

    @Override
    public boolean lock(String key, long waitTime, long leaseTime) {
        try {
            RLock rLock = redissonClient.getLock(key);
            return rLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return false;
        }
    }


    @Override
    public void releaseLock(String key) {
        RLock rLock = redissonClient.getLock(key);
        if (rLock.isHeldByCurrentThread()) {
            try {
                rLock.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
            }
        }
    }
}
