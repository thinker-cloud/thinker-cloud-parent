package com.thinker.cloud.redis.lock;

import com.thinker.cloud.redis.cache.generator.CacheKeyGenerator;
import com.thinker.cloud.redis.lock.aspect.DistributedLockAspect;
import com.thinker.cloud.redis.lock.aspect.LockInfoProvider;
import com.thinker.cloud.redis.lock.distributed.RedissonDistributedLock;
import com.thinker.cloud.redis.lock.lock.LockFactory;
import lombok.AllArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁插件初始化
 *
 * @author admin
 */
@Configuration
@AllArgsConstructor
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class DistributedLockAutoConfiguration {

    private final RedissonClient redissonClient;
    private final CacheKeyGenerator cacheKeyGenerator;

    @Bean
    @ConditionalOnMissingBean
    public LockInfoProvider lockInfoProvider() {
        return new LockInfoProvider(cacheKeyGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    public LockFactory lockFactory() {
        return new LockFactory(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public DistributedLockAspect digitLockAspect(LockFactory lockFactory, LockInfoProvider lockInfoProvider) {
        return new DistributedLockAspect(lockFactory, lockInfoProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedissonDistributedLock redissonDistributedLock(LockFactory lockFactory) {
        return new RedissonDistributedLock(lockFactory, redissonClient);
    }
}
