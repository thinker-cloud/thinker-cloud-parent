package com.thinker.cloud.core.config;

import com.thinker.cloud.core.properties.RestTemplateProperties;
import lombok.AllArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置
 *
 * @author admin
 */
@Configuration
@AllArgsConstructor
@ConditionalOnBean(RestTemplateProperties.class)
public class RestTemplateConfiguration {

    private final RestTemplateProperties restTemplateProperties;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory());
        return restTemplate;
    }

    /**
     * httpclient 实现的ClientHttpRequestFactory
     */
    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    /**
     * 使用连接池的 httpclient
     */
    @Bean
    public HttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder
                .create()
                .build();

        // 最大链接数
        connectionManager.setMaxTotal(restTemplateProperties.getMaxTotal());

        // 同路由并发数20
        connectionManager.setDefaultMaxPerRoute(restTemplateProperties.getMaxPerRoute());

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                // 读超时
                .setSocketTimeout(Timeout.ofMilliseconds(restTemplateProperties.getReadTimeout()))
                // 链接超时
                .setConnectTimeout(Timeout.ofMilliseconds(restTemplateProperties.getConnectTimeout()))
                .build();
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        RequestConfig requestConfig = RequestConfig.custom()
                // 链接不够用的等待时间
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(restTemplateProperties.getConnectTimeout()))
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(1L)))
                .build();
    }
}
