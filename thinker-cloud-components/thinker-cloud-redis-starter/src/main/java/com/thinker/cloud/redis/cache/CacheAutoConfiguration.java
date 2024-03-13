package com.thinker.cloud.redis.cache;

import com.thinker.cloud.core.aspect.expression.ExpressionResolver;
import com.thinker.cloud.redis.cache.aspect.CacheableAspect;
import com.thinker.cloud.redis.cache.fast.FastStringRedisCache;
import com.thinker.cloud.redis.cache.generator.CacheKeyGenerator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存插件自动配置
 *
 * @author admin
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class CacheAutoConfiguration {

    /**
     * 切面 拦截处理所有 @Cacheable
     *
     * @return CacheableAspect
     */
    @Bean
    public CacheableAspect cacheableAspect() {
        return new CacheableAspect();
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
    @ConditionalOnMissingBean(CacheKeyGenerator.class)
    public CacheKeyGenerator cacheKeyGenerator() {
        return new CacheKeyGenerator();
    }
}
