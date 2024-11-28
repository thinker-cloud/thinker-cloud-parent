package com.thinker.cloud.session.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Spring Session 线程池配置
 *
 * @author admin
 **/
@Data
@RefreshScope
@ConfigurationProperties(prefix = "thinker.cloud.session.task")
public class SessionTaskProperties {

    /**
     * 线程池维护线程的核心数量.
     */
    private int corePoolSize = 8;

    /**
     * 线程池维护线程的最大数量
     */
    private int maxPoolSize = 16;

    /**
     * 队列最大长度
     */
    private int queueCapacity = 1000;

    /**
     * 允许线程空闲时间
     */
    private int keepAliveSeconds = 10;

    /**
     * 线程池前缀
     */
    private String threadNamePrefix = "thinker-session-task-executor-";

}
