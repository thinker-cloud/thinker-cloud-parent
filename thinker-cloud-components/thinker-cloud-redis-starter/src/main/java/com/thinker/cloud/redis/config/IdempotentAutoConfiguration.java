package com.thinker.cloud.redis.config;

import com.thinker.cloud.redis.idempotent.aspect.IdempotentAspect;
import com.thinker.cloud.core.aspect.expression.ExpressionResolver;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    /**
     * key 解析器
     *
     * @return KeyResolver
     */
    @Bean
    @ConditionalOnMissingBean(ExpressionResolver.class)
    public ExpressionResolver keyResolver() {
        return new ExpressionResolver();
    }

}
