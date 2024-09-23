package com.thinker.cloud.feign.config;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.thinker.cloud.feign.sentinel.ThinkerSentinelFeign;
import feign.Feign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * feign Sentinel 自动配置
 *
 * @author admin
 **/
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(SentinelFeignAutoConfiguration.class)
public class FeignSentinelConfiguration {

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "spring.cloud.openfeign.sentinel.enabled")
    public Feign.Builder feignSentinelBuilder() {
        return ThinkerSentinelFeign.builder();
    }
}
