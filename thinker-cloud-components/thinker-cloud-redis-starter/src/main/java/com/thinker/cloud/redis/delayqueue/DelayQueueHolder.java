package com.thinker.cloud.redis.delayqueue;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列持有者顶层接口
 * <p>
 * 后续可以通过实现以下接口，实现不同的实现，如基于Redisson的延迟队列实现，或者如java原生的延迟队列实现。
 *
 * @author admin
 */
public interface DelayQueueHolder {

    /**
     * 添加延迟任务
     *
     * @param queueName 队列名称
     * @param value     添加到队列的对象
     * @param delay     延迟时间
     * @param timeUnit  时间单位
     * @param <T>       <T>
     */
    <T> void addTask(String queueName, T value, long delay, TimeUnit timeUnit);

    /**
     * 添加延迟任务，仅当不存在时添加延迟队列任务
     *
     * @param queueName 队列名称
     * @param value     添加到队列的对象
     * @param delay     延迟时间
     * @param timeUnit  时间单位
     * @param <T>       <T>
     */
    default <T> void addTaskIfAbsent(String queueName, T value, long delay, TimeUnit timeUnit) {
        if (!this.contains(queueName, value)) {
            this.addTask(queueName, value, delay, timeUnit);
        }
    }

    /**
     * 移除延迟任务
     *
     * @param queueName 队列名称
     * @param value     队列的对象
     * @param <T>       <T>
     * @return boolean
     */
    <T> boolean removeTask(String queueName, T value);

    /**
     * 是否存在了延迟任务
     *
     * @param queueName 队列名称
     * @param value     队列的对象
     * @param <T>       <T>
     * @return boolean
     */
    <T> boolean contains(String queueName, T value);

    /**
     * 获取延迟任务队列
     *
     * @param queueName 队列名称
     * @param <T>       <T>
     * @return BlockingDeque<T>
     */
    <T> BlockingDeque<T> getQueue(String queueName);

    /**
     * 根据QueueName获取包装类
     *
     * @param queueName 队列名称
     * @param <T>       <T>
     * @return T
     * @throws InterruptedException 中断异常
     */
    <T> T take(String queueName) throws InterruptedException;
}
