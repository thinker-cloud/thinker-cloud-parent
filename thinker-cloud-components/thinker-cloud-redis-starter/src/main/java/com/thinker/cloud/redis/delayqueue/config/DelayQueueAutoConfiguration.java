package com.thinker.cloud.redis.delayqueue.config;

import com.thinker.cloud.redis.delayqueue.config.DelayQueueProperties.ThreadPoolProperties;
import com.thinker.cloud.redis.delayqueue.executor.DelayQueueExecutorFactory;
import com.thinker.cloud.redis.delayqueue.redisson.RedisDelayQueueHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 延迟队列配置
 *
 * @author admin
 */
@Slf4j
@Configuration
@ConditionalOnBean(DelayQueueProperties.class)
public class DelayQueueAutoConfiguration {

    @Resource
    private DelayQueueProperties delayQueueProperties;

    @Bean("delayQueueThreadPoolExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolProperties threadPoolProperties = delayQueueProperties.getThreadPool();
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 线程核心数目
        threadPoolTaskExecutor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        threadPoolTaskExecutor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());

        // 最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());

        // 配置队列大小
        threadPoolTaskExecutor.setQueueCapacity(threadPoolProperties.getQueueCapacity());

        // 队列线程名称
        threadPoolTaskExecutor.setThreadNamePrefix("delay-queue-thread-");

        // 配置拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler((r, executor) -> {
            log.info("线程池内加入任务被拒绝,使用当前线程执行: {}", r);
            // 抛异常
            new ThreadPoolExecutor.CallerRunsPolicy();
        });

        // 数据初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    public DelayQueueExecutorFactory delayQueueExecutorFactory() {
        return new DelayQueueExecutorFactory(threadPoolExecutor());
    }

    @Bean
    @ConditionalOnClass(RedissonClient.class)
    @ConditionalOnMissingBean(RedisDelayQueueHolder.class)
    public RedisDelayQueueHolder redisDelayQueueHolder(RedissonClient redissonClient) {
        return new RedisDelayQueueHolder(redissonClient);
    }
}
