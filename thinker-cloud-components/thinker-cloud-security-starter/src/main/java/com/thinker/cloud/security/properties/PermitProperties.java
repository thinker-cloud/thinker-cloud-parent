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

package com.thinker.cloud.security.properties;

import cn.hutool.core.util.ReUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.thinker.cloud.security.annotation.InnerAuth;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 资源服务器对外直接暴露URL,如果设置context-path要特殊处理
 *
 * @author admin
 */
@Slf4j
@Setter
@Getter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "thinker-cloud.security.oauth2.ignore")
public class PermitProperties implements InitializingBean {

    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)}");

    /**
     * 监控中心和swagger需要访问的url
     */
    private static final String[] DEFAULT_IGNORE_URLS = new String[]{
            "/actuator/**", "/error", "/doc.html", "/v3/api-docs/**",
            "/swagger-ui/**", "/webjars/**", "/druid/**", "/css/**",
            "/js/**", "/images/**", "/favicon.ico"
    };

    private List<String> urls = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        urls.addAll(Arrays.asList(DEFAULT_IGNORE_URLS));
        RequestMappingHandlerMapping mapping = SpringUtil.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        map.keySet().forEach(info -> {
            HandlerMethod handlerMethod = map.get(info);

            // 获取方法上边的注解 替代path variable 为 *
            InnerAuth method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), InnerAuth.class);
            Optional.ofNullable(method)
                    .ifPresent(innerAuth -> Objects.requireNonNull(info.getPathPatternsCondition())
                            .getPatternValues()
                            .forEach(url -> urls.add(ReUtil.replaceAll(url, PATTERN, "*"))));

            // 获取类上边的注解, 替代path variable 为 *
            InnerAuth controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), InnerAuth.class);
            Optional.ofNullable(controller)
                    .ifPresent(innerAuth -> Objects.requireNonNull(info.getPathPatternsCondition())
                            .getPatternValues()
                            .forEach(url -> urls.add(ReUtil.replaceAll(url, PATTERN, "*"))));
        });
    }
}
