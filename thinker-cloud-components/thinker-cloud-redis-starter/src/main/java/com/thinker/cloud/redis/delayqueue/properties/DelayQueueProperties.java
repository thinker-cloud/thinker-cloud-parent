package com.thinker.cloud.redis.delayqueue.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 延迟队列线程池配置
 *
 * @author admin
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "thinker-cloud.redis.delay-queue")
@ConditionalOnExpression("${thinker-cloud.redis.delay-queue.enabled:false}")
public class DelayQueueProperties {

    /**
     * 是否启用延迟队列
     */
    private boolean enabled;

    /**
     * 线程池配置
     */
    private ThreadPoolProperties threadPool = new ThreadPoolProperties();

    @Data
    public static class ThreadPoolProperties {

        /**
         * 核心线程数 默认：2
         */
        private Integer corePoolSize = 2;

        /**
         * 最大线程数 默认：4
         */
        private Integer maxPoolSize = 4;

        /**
         * 队列容量大小 默认：200
         */
        private Integer queueCapacity = 100;

        /**
         * 保持活动秒 默认：60秒
         */
        private Integer keepAliveSeconds = 60;

        /**
         * 线程前缀
         */
        private String threadNamePrefix = "thinker-delay-queue-thread-";
    }
}
