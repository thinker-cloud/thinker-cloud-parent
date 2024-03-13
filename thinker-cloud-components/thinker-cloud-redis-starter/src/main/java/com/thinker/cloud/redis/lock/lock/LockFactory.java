package com.thinker.cloud.redis.lock.lock;

import com.thinker.cloud.redis.lock.model.LockInfo;
import lombok.AllArgsConstructor;
import org.redisson.api.RedissonClient;

/**
 * 锁工程类
 *
 * @author admin
 */
@AllArgsConstructor
public class LockFactory {

    private final RedissonClient redissonClient;

    public Lock getLock(LockInfo lockInfo) {
        return switch (lockInfo.getType()) {
            case Fair -> new FairLock(redissonClient, lockInfo);
            case Read -> new ReadLock(redissonClient, lockInfo);
            case Write -> new WriteLock(redissonClient, lockInfo);
            default -> new ReentrantLock(redissonClient, lockInfo);
        };
    }
}
