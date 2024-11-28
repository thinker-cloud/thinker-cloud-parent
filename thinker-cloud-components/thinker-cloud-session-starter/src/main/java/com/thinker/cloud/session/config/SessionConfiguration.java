package com.thinker.cloud.session.config;

import com.thinker.cloud.session.properties.SessionTaskProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisIndexedHttpSessionConfiguration;

import java.util.concurrent.Executor;

/**
 * Spring Session 配置
 *
 * @author admin
 */
@Slf4j
@AllArgsConstructor
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60 * 12)
public class SessionConfiguration {

    private final SessionTaskProperties sessionTaskProperties;

    /**
     * 用于spring session，防止每次创建一个线程
     *
     * @see RedisIndexedHttpSessionConfiguration#setRedisTaskExecutor(Executor) 自定义
     */
    @Bean
    public ThreadPoolTaskExecutor springSessionRedisTaskExecutor() {
        log.debug("Creating Session Task Executor");
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(sessionTaskProperties.getCorePoolSize());
        taskExecutor.setMaxPoolSize(sessionTaskProperties.getMaxPoolSize());
        taskExecutor.setKeepAliveSeconds(sessionTaskProperties.getKeepAliveSeconds());
        taskExecutor.setQueueCapacity(sessionTaskProperties.getQueueCapacity());
        taskExecutor.setThreadNamePrefix(sessionTaskProperties.getThreadNamePrefix());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return taskExecutor;
    }

}
