package com.thinker.cloud.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 异步任务配置
 *
 * @author admin
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thinker.cloud.async.task")
public class AsyncTaskProperties {
    /**
     * 线程池维护线程的核心数量.
     */
    private int corePoolSize = 30;

    /**
     * 线程池维护线程的最大数量
     */
    private int maxPoolSize = 100;

    /**
     * 队列最大长度
     */
    private int queueCapacity = 1000;

    /**
     * 允许线程空闲时间
     */
    private int keepAliveSeconds = 3000;

    /**
     * 线程池前缀
     */
    private String threadNamePrefix = "thinker-task-executor-";
}
