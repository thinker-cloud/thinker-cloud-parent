package com.thinker.cloud.redis.lock.distributed;

import com.thinker.cloud.redis.lock.lock.Lock;
import com.thinker.cloud.redis.lock.lock.LockFactory;
import com.thinker.cloud.redis.lock.model.LockInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Optional;
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

    private final LockFactory lockFactory;
    private final RedissonClient redissonClient;

    @Override
    public boolean lock(String key, long waitTime, long leaseTime) {
        try {
            RLock rLock = redissonClient.getLock(key);
            return rLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("获取分布式锁失败，key:{}，ex={}", key, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean lock(LockInfo lockInfo) {
        try {
            Lock lock = lockFactory.getLock(lockInfo);
            return lock.acquire();
        } catch (Exception e) {
            log.error("获取分布式锁失败，key:{}, ex={}", lockInfo.getName(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean releaseLock(String key) {
        RLock rLock = redissonClient.getLock(key);
        if (rLock.isHeldByCurrentThread()) {
            try {
                return Optional.ofNullable(rLock.forceUnlockAsync().get()).orElse(false);
            } catch (InterruptedException | ExecutionException e) {
                log.error("释放分布式锁失败，key:{}，ex={}", key, e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    public boolean releaseLock(LockInfo lockInfo) {
        try {
            Lock lock = lockFactory.getLock(lockInfo);
            return lock.release();
        } catch (Exception e) {
            log.error("释放分布式锁失败，key:{}，ex={}", lockInfo.getName(), e.getMessage(), e);
            return false;
        }
    }
}
