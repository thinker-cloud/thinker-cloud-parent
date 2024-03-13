package com.thinker.cloud.redis.delayqueue.executor;

import com.thinker.cloud.redis.delayqueue.core.DelayMessage;

/**
 * 延迟队列顶层接口
 *
 * @author admin
 */
public interface DelayQueueExecutor<T extends DelayMessage> {

    /**
     * 延迟队列名称
     *
     * @return String
     */
    String queueName();

    /**
     * 执行业务
     *
     * @param message 消息对象
     */
    void execute(T message);

    /**
     * 获取延迟消息
     *
     * @return T
     * @throws InterruptedException 异常中断
     */
    T take() throws InterruptedException;
}
