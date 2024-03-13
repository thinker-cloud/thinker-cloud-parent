package com.thinker.cloud.redis.delayqueue.redisson;

import com.thinker.cloud.redis.delayqueue.core.DelayMessage;
import com.thinker.cloud.redis.delayqueue.executor.DelayQueueExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * Redisson延迟队列顶层接口抽象类
 *
 * @author admin
 * @since 2023-12-02 16:07
 **/
@Slf4j
public abstract class AbstractRedissonDelayQueueExecutor<T extends DelayMessage> implements DelayQueueExecutor<T> {

    @Resource
    private RedissonDelayQueueHolder redissonDelayQueueHolder;

    @Override
    public T take() throws InterruptedException {
        return redissonDelayQueueHolder.take(this.queueName());
    }
}
