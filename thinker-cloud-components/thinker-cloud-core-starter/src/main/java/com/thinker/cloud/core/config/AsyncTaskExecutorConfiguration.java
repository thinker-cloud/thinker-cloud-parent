package com.thinker.cloud.core.config;

import com.thinker.cloud.core.properties.AsyncTaskProperties;
import com.thinker.cloud.core.thread.ThinkerThreadPoolTaskExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务执行器配置
 *
 * @author admin
 */
@Slf4j
@EnableAsync
@Configuration
@EnableScheduling
@AllArgsConstructor
public class AsyncTaskExecutorConfiguration implements AsyncConfigurer {

    private final AsyncTaskProperties asyncTaskProperties;

    @Bean
    @Override
    public Executor getAsyncExecutor() {
        log.debug("Creating Async Task Executor");
        ThreadPoolTaskExecutor taskExecutor = new ThinkerThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(asyncTaskProperties.getCorePoolSize());
        taskExecutor.setMaxPoolSize(asyncTaskProperties.getMaxPoolSize());
        taskExecutor.setQueueCapacity(asyncTaskProperties.getQueueCapacity());
        taskExecutor.setKeepAliveSeconds(asyncTaskProperties.getKeepAliveSeconds());
        taskExecutor.setThreadNamePrefix(asyncTaskProperties.getThreadNamePrefix());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);

        // 配置拒绝策略
        taskExecutor.setRejectedExecutionHandler((r, executor) -> {
            log.error("异步任务线程池内加入任务被拒绝");
            // 抛异常
            new ThreadPoolExecutor.CallerRunsPolicy();
        });

        // 数据初始化
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
