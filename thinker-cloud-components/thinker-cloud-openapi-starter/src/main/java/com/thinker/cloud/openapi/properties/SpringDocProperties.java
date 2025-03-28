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
package com.thinker.cloud.openapi.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 接口文档配置
 *
 * @author admin
 */
@Data
@RefreshScope
@ConfigurationProperties("springdoc.api-docs")
public class SpringDocProperties {

    /**
     * 标题
     **/
    private String title = "接口文档";

    /**
     * 描述
     */
    private String description = "接口文档";

    /**
     * 获取token
     */
    private String tokenUrl = "http://localhost:8888/auth/oauth2/token";

    /**
     * 作用域
     */
    private String scope = "server";
}
