package com.thinker.cloud.openapi.config;

import com.thinker.cloud.openapi.properties.SpringDocProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SpringDoc相关配置
 *
 * @author admin
 */
@Slf4j
@Configuration
public class SpringDocAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public OpenAPI openAPI(SpringDocProperties properties) {
        return new OpenAPI().info(new Info()
                        .title(properties.getTitle())
                        .version("v1.0.0")
                        .description(properties.getDescription()))
                .schemaRequirement(HttpHeaders.AUTHORIZATION, securityScheme(properties));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 配置访问`/swagger-ui/`路径时可以直接跳转到`/swagger-ui/index.html`
        registry.addViewController("/swagger-ui/").setViewName("redirect:/swagger-ui/index.html");
    }

    /**
     * 鉴权
     *
     * @param properties properties
     * @return SecurityScheme
     */
    private SecurityScheme securityScheme(SpringDocProperties properties) {
        OAuthFlow clientCredential = new OAuthFlow();
        clientCredential.setTokenUrl(properties.getTokenUrl());
        clientCredential.setScopes(new Scopes().addString(properties.getScope(), properties.getScope()));
        OAuthFlows oauthFlows = new OAuthFlows();
        oauthFlows.password(clientCredential);
        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.setName(HttpHeaders.AUTHORIZATION);
        securityScheme.setType(SecurityScheme.Type.OAUTH2);
        securityScheme.setFlows(oauthFlows);
        return securityScheme;
    }
}

