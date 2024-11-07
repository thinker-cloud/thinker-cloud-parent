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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinker.cloud.security.component.BearerTokenExtractor;
import com.thinker.cloud.security.component.Oauth2AuthExceptionEntryPoint;
import com.thinker.cloud.security.component.PermissionService;
import com.thinker.cloud.security.component.PermitAllUrlMatcher;
import com.thinker.cloud.security.properties.ThinkerSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authorization.method.PrePostTemplateDefaults;

/**
 * 资源服务器自动配置
 *
 * @author admin
 */
@RequiredArgsConstructor
@EnableConfigurationProperties(ThinkerSecurityProperties.class)
public class ResourceServerAutoConfiguration {

    /**
     * 鉴权具体的实现逻辑
     *
     * @return （#pms.xxx）
     */
    @Bean("pms")
    public PermissionService permissionService() {
        return new PermissionService();
    }

    /**
     * 白名单放行url解析器
     *
     * @param thinkerSecurityProperties 安全配置信息
     * @return PermitAllUrlResolver
     */
    @Bean
    public PermitAllUrlMatcher permitAllUrlResolver(ThinkerSecurityProperties thinkerSecurityProperties) {
        return new PermitAllUrlMatcher(thinkerSecurityProperties);
    }

    /**
     * 请求令牌的抽取逻辑
     *
     * @param permitAllUrlMatcher 白名单放行url解析器
     * @return BearerTokenExtractor
     */
    @Bean
    public BearerTokenExtractor bearerTokenExtractor(PermitAllUrlMatcher permitAllUrlMatcher) {
        return new BearerTokenExtractor(permitAllUrlMatcher);
    }

    /**
     * 资源服务器异常处理
     *
     * @param objectMapper jackson 输出对象
     * @return ResourceAuthExceptionEntryPoint
     */
    @Bean
    public Oauth2AuthExceptionEntryPoint resourceAuthExceptionEntryPoint(ObjectMapper objectMapper) {
        return new Oauth2AuthExceptionEntryPoint(objectMapper);
    }

    /**
     * 支持自定义权限表达式
     *
     * @return {@link PrePostTemplateDefaults }
     */
    @Bean
    public PrePostTemplateDefaults prePostTemplateDefaults() {
        return new PrePostTemplateDefaults();
    }
}
