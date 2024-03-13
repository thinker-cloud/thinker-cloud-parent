package com.thinker.cloud.redis.delayqueue;

import com.thinker.cloud.core.thread.ThinkerThreadPoolTaskExecutor;
import com.thinker.cloud.redis.delayqueue.executor.DelayQueueExecutorFactory;
import com.thinker.cloud.redis.delayqueue.properties.DelayQueueProperties;
import com.thinker.cloud.redis.delayqueue.properties.DelayQueueProperties.ThreadPoolProperties;
import com.thinker.cloud.redis.delayqueue.redisson.RedissonDelayQueueHolder;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@ConditionalOnBean(DelayQueueProperties.class)
public class DelayQueueAutoConfiguration {

    private final DelayQueueProperties delayQueueProperties;

    @Bean("delayQueueThreadPoolExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolProperties threadPoolProperties = delayQueueProperties.getThreadPool();
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThinkerThreadPoolTaskExecutor();
        // 线程核心数目
        threadPoolTaskExecutor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        threadPoolTaskExecutor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        // 最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        // 配置队列大小
        threadPoolTaskExecutor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        // 队列线程名称
        threadPoolTaskExecutor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        // 配置拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler((r, executor) -> {
            log.info("线程池内加入任务被拒绝, 使用当前线程执行: {}", r);
            // 抛异常
            new ThreadPoolExecutor.CallerRunsPolicy();
        });

        // 数据初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    @ConditionalOnMissingBean(DelayQueueExecutorFactory.class)
    public DelayQueueExecutorFactory delayQueueExecutorFactory() {
        return new DelayQueueExecutorFactory(threadPoolExecutor());
    }

    @Bean
    @ConditionalOnClass(RedissonClient.class)
    @ConditionalOnMissingBean(RedissonDelayQueueHolder.class)
    public RedissonDelayQueueHolder redissonDelayQueueHolder(RedissonClient redissonClient) {
        return new RedissonDelayQueueHolder(redissonClient);
    }
}
