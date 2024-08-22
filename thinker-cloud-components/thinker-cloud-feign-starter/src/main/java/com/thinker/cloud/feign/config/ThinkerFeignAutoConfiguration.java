package com.thinker.cloud.feign.config;

import com.thinker.cloud.feign.endpoint.FeignClientEndpoint;
import com.thinker.cloud.feign.interceptor.FeignRequestInterceptor;
import feign.Feign;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.ThinkerFeignClientsRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

/**
 * feign 自动化配置
 *
 * @author admin
 */
@Configuration
@ConditionalOnClass(Feign.class)
@Import(ThinkerFeignClientsRegistrar.class)
@AutoConfigureAfter(EnableFeignClients.class)
public class ThinkerFeignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public FeignClientEndpoint feignClientEndpoint(ApplicationContext context) {
        return new FeignClientEndpoint(context);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(BearerTokenResolver.class)
    public FeignRequestInterceptor feignRequestInterceptor(BearerTokenResolver tokenResolver) {
        return new FeignRequestInterceptor(tokenResolver);
    }
}

