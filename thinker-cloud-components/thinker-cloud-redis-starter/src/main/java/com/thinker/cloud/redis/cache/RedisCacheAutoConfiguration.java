package com.thinker.cloud.redis.cache;

import com.thinker.cloud.core.aspect.expression.ExpressionResolver;
import com.thinker.cloud.redis.cache.aspect.CacheableAspect;
import com.thinker.cloud.redis.cache.fast.FastRedisService;
import com.thinker.cloud.redis.cache.fast.FastStringRedisCache;
import com.thinker.cloud.redis.cache.generator.CacheKeyGenerator;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis缓存插件自动配置
 *
 * @author admin
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({RedisAutoConfiguration.class, RedissonAutoConfiguration.class})
public class RedisCacheAutoConfiguration {

    /**
     * redis 增强实现
     *
     * @return FastRedisService
     */
    @Bean
    @ConditionalOnMissingBean(FastRedisService.class)
    public FastRedisService fastRedisService(RedisTemplate<String, Object> redisTemplate) {
        return new FastRedisService(redisTemplate);
    }

    /**
     * redis 增强实现
     *
     * @return FastStringRedisCache
     */
    @Bean
    @ConditionalOnMissingBean(FastStringRedisCache.class)
    public FastStringRedisCache fastStringRedisCache(StringRedisTemplate redisTemplate) {
        return new FastStringRedisCache(redisTemplate);
    }

    /**
     * 缓存 key 生成器
     *
     * @return CacheKeyGenerator
     */
    @Bean
    @ConditionalOnBean(ExpressionResolver.class)
    @ConditionalOnMissingBean(CacheKeyGenerator.class)
    public CacheKeyGenerator cacheKeyGenerator(ExpressionResolver expressionResolver) {
        return new CacheKeyGenerator(expressionResolver);
    }

    /**
     * 切面 拦截处理所有 @Cacheable
     *
     * @return CacheableAspect
     */
    @Bean
    @ConditionalOnBean({RedissonClient.class, CacheKeyGenerator.class})
    public CacheableAspect cacheableAspect(RedissonClient redissonClient, CacheKeyGenerator cacheKeyGenerator) {
        return new CacheableAspect(redissonClient, cacheKeyGenerator);
    }
}
