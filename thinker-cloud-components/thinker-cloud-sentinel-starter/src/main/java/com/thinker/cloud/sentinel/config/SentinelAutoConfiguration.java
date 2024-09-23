/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.thinker.cloud.sentinel.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.RequestOriginParser;
import com.thinker.cloud.sentinel.handler.ThinkerUrlBlockHandler;
import com.thinker.cloud.sentinel.parser.HeaderRequestOriginParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * sentinel 自动配置
 *
 * @author admin
 */
@Configuration(proxyBeanMethods = false)
public class SentinelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BlockExceptionHandler blockExceptionHandler() {
        return new ThinkerUrlBlockHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestOriginParser requestOriginParser() {
        return new HeaderRequestOriginParser();
    }

}
