/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thinker.cloud.security.config;

import com.thinker.cloud.security.component.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 资源服务器认证授权配置
 *
 * @author admin
 */
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@ComponentScan("com.thinker.cloud.security")
public class ResourceServerConfiguration {

    private final PermitAllUrlMatcher permitAllUrlMatcher;
    private final BearerTokenExtractor bearerTokenExtractor;
    private final AuthAccessDeniedHandler accessDeniedHandler;
    private final Oauth2AuthExceptionEntryPoint authExceptionEntryPoint;
    private final AuthorizationServiceIntrospector authorizationServiceIntrospector;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(permitAllUrlMatcher)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(token -> token.introspector(authorizationServiceIntrospector))
                        // 身份验证入口点
                        .authenticationEntryPoint(authExceptionEntryPoint)
                        .bearerTokenResolver(bearerTokenExtractor))
                .exceptionHandling(handler -> handler
                        // 访问被拒绝处理程序
                        .accessDeniedHandler(accessDeniedHandler)
                        // 身份验证入口点
                        .authenticationEntryPoint(authExceptionEntryPoint))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
