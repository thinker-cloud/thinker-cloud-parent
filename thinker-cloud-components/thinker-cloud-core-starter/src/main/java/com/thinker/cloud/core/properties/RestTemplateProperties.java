package com.thinker.cloud.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RestTemplate 配置
 *
 * @author admin
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thinker.cloud.rest-template")
public class RestTemplateProperties {

    /**
     * 最大链接数
     */
    private int maxTotal = 200;
    /**
     * 同路由最大并发数
     */
    private int maxPerRoute = 50;
    /**
     * 读取超时时间 ms
     */
    private int readTimeout = 35000;
    /**
     * 链接超时时间 ms
     */
    private int connectTimeout = 10000;

    /**
     * 链接不够用的等待时间 ms
     */
    private int connectionRequestTimeout = 35000;
}
