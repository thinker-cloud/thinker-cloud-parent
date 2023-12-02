package com.thinker.cloud.redis.delayqueue.executor;

import com.thinker.cloud.redis.delayqueue.DelayMessage;
import com.thinker.cloud.redis.delayqueue.DelayQueueHolder;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis延迟队列顶层接口抽象类
 *
 * @author admin
 * @since 2023-12-02 16:07
 **/
@Slf4j
@Getter
public abstract class AbstractRedisDelayQueueExecutor<T extends DelayMessage> implements DelayQueueExecutor<T> {

    @Resource
    private DelayQueueHolder redisDelayQueueHolder;

    @Override
    public T take() throws InterruptedException {
        return redisDelayQueueHolder.take(this.queueName());
    }
}
