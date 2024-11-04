package com.thinker.cloud.redis.cache;

import com.thinker.cloud.core.aspect.expression.ExpressionResolver;
import com.thinker.cloud.redis.cache.aspect.CacheableAspect;
import com.thinker.cloud.redis.cache.fast.FastRedisService;
import com.thinker.cloud.redis.cache.fast.FastStringRedisCache;
import com.thinker.cloud.redis.cache.generator.CustomCacheKeyGenerator;
import com.thinker.cloud.redis.cache.service.RedisClientService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis缓存插件自动配置
 *
 * @author admin
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisCacheAutoConfiguration {

    /**
     * redis客户端服务
     *
     * @return BaseRedisService
     */
    @Bean
    public RedisClientService redisClientService() {
        return new RedisClientService();
    }

    /**
     * redis 增强实现
     *
     * @return FastRedisService
     */
    @Bean
    public FastRedisService fastRedisService() {
        return new FastRedisService();
    }

    /**
     * redis 增强实现
     *
     * @return FastStringRedisCache
     */
    @Bean
    public FastStringRedisCache fastStringRedisCache() {
        return new FastStringRedisCache();
    }

    /**
     * 缓存 key 生成器
     *
     * @return CacheKeyGenerator
     */
    @Bean
    @ConditionalOnBean(ExpressionResolver.class)
    @ConditionalOnMissingBean(CustomCacheKeyGenerator.class)
    public CustomCacheKeyGenerator customCacheKeyGenerator(ExpressionResolver expressionResolver) {
        return new CustomCacheKeyGenerator(expressionResolver);
    }

    /**
     * 切面 拦截处理所有 @Cacheable
     *
     * @return CacheableAspect
     */
    @Bean
    @ConditionalOnBean({RedissonClient.class, CustomCacheKeyGenerator.class})
    public CacheableAspect cacheableAspect(RedissonClient redissonClient, CustomCacheKeyGenerator cacheKeyGenerator) {
        return new CacheableAspect(redissonClient, cacheKeyGenerator);
    }
}
