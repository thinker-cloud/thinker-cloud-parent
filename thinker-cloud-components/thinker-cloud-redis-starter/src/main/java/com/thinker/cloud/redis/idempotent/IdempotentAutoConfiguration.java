package com.thinker.cloud.redis.idempotent;

import com.thinker.cloud.redis.cache.generator.CustomCacheKeyGenerator;
import com.thinker.cloud.redis.idempotent.aspect.IdempotentAspect;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 幂等插件初始化
 *
 * @author admin
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class IdempotentAutoConfiguration {

    /**
     * 切面 拦截处理所有 @Idempotent
     *
     * @return Aspect
     */
    @Bean
    @ConditionalOnBean({RedissonClient.class, CustomCacheKeyGenerator.class})
    public IdempotentAspect idempotentAspect(RedissonClient redissonClient, CustomCacheKeyGenerator cacheKeyGenerator) {
        return new IdempotentAspect(redissonClient, cacheKeyGenerator);
    }

}
