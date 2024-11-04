package com.thinker.cloud.redis.delayqueue.redisson;

import com.thinker.cloud.redis.delayqueue.core.DelayMessage;
import com.thinker.cloud.redis.delayqueue.executor.DelayQueueExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;

/**
 * Redisson延迟队列顶层接口抽象类
 *
 * @author admin
 **/
@Slf4j
public abstract class AbstractRedissonDelayQueueExecutor<T extends DelayMessage> implements DelayQueueExecutor<T> {

    @Lazy
    @Resource
    private RedissonDelayQueueHolder redissonDelayQueueHolder;

    @Override
    public T take() throws InterruptedException {
        return redissonDelayQueueHolder.take(this.queueName());
    }
}
