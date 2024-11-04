package com.thinker.cloud.redis.lock.aspect;

import com.thinker.cloud.redis.cache.generator.CacheKeyGenerator;
import com.thinker.cloud.redis.lock.annotation.DistributedLock;
import com.thinker.cloud.redis.lock.model.LockInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

/**
 * 锁信息提供者
 *
 * @author admin
 */
@Slf4j
@AllArgsConstructor
public class LockInfoProvider {

    private static final String LOCK_NAME_PREFIX = "distributed_lock:";

    private final CacheKeyGenerator cacheKeyGenerator;

    public LockInfo get(JoinPoint joinPoint, DistributedLock distributedLock) {
        // 若没有配置 幂等 标识编号，则使用 url + 参数列表作为区分
        String key = cacheKeyGenerator.generator(joinPoint, LOCK_NAME_PREFIX, distributedLock.key());

        // 获取等待、占用时长
        long waitTime = getWaitTime(distributedLock), leaseTime = getLeaseTime(distributedLock);

        // 如果占用锁的时间设计不合理，则打印相应的警告提示
        if (leaseTime == -1 && log.isWarnEnabled()) {
            log.warn("Trying to acquire Lock({}) with no expiration, " +
                    "Klock will keep prolong the lock expiration while the lock is still holding by current thread. " +
                    "This may cause dead lock in some circumstances.", key);
        }
        return new LockInfo(distributedLock.lockType(), key, waitTime, leaseTime, distributedLock.timeUnit());
    }

    private long getWaitTime(DistributedLock distributedLock) {
        return distributedLock.waitTime() == Long.MIN_VALUE ? 60 : distributedLock.waitTime();
    }

    private long getLeaseTime(DistributedLock distributedLock) {
        return distributedLock.leaseTime() == Long.MIN_VALUE ? 60 : distributedLock.leaseTime();
    }
}
