package com.thinker.cloud.redis.delayqueue.redisson;

import com.thinker.cloud.common.exception.DelayedQueueException;
import com.thinker.cloud.redis.delayqueue.core.DelayQueueHolder;
import com.thinker.cloud.redis.delayqueue.properties.DelayQueueProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Redis延迟队列持有者
 * <p>
 * 主要处理添加任务，删除任务，获取任务
 *
 * @author admin
 */
@Slf4j
@Component
@ConditionalOnClass(RedissonClient.class)
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class RedissonDelayQueueHolder implements DelayQueueHolder {

    private final RedissonClient redissonClient;
    private final DelayQueueProperties delayQueueProperties;

    /**
     * 延迟队列本地缓存
     */
    private final Map<String, RDelayedQueue<?>> delayedQueueMap = new ConcurrentHashMap<>(16);

    /**
     * 添加到消息队列
     *
     * @param queueName 队列名称
     * @param value     添加到队列的对象
     * @param delay     延迟时间
     * @param timeUnit  时间单位
     */
    @Override
    public <T> void addTask(String queueName, T value, long delay, TimeUnit timeUnit) {
        try {
            log.info("添加到延时队列【{}】【{}】【{}-{}】", queueName, value, delay, timeUnit.name());
            RDelayedQueue<T> delayedQueue = this.initDelayQueue(queueName);
            delayedQueue.offer(value, delay, timeUnit);
        } catch (DelayedQueueException e) {
            log.error("添加到延时队列失败：queueName: {}, value: {}, error: {}", queueName, value, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("添加到延时队列失败：queueName: {}, value: {}, error: {}", queueName, value, e.getMessage(), e);
            throw new DelayedQueueException("添加到延时队列失败");
        }
    }

    /**
     * 删除任务
     *
     * @param queueName 队列名称
     * @param value     队列的对象
     * @return boolean
     */
    @Override
    public <T> boolean removeTask(String queueName, T value) {
        RDelayedQueue<T> delayedQueue = this.initDelayQueue(queueName);
        return delayedQueue.remove(value);
    }

    /**
     * 队列中是否包含某个值
     *
     * @param queueName 队列名称
     * @param value     队列的对象
     * @return boolean
     */
    @Override
    public <T> boolean contains(String queueName, T value) {
        RDelayedQueue<T> delayedQueue = this.initDelayQueue(queueName);
        return delayedQueue.contains(value);
    }

    /**
     * 取值
     */
    @Override
    public <T> RBlockingDeque<T> getQueue(String queueName) {
        try {
            // 应用重启后，如果没有新的消息添加到延迟队列时，会没有初始化延迟队列，会导致以前在队列里的消息不能消费.
            // 所以这里每次获取时，默认初始化一次.
            this.initDelayQueue(queueName);
            return redissonClient.getBlockingDeque(queueName);
        } catch (DelayedQueueException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取延时队列失败：queueName: {}, error: {}", queueName, e.getMessage(), e);
            throw new DelayedQueueException("获取延时队列失败");
        }
    }

    /**
     * 根据QueueName获取包装类
     *
     * @param queueName 队列名称
     * @return T
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T take(String queueName) throws InterruptedException {
        return (T) this.getQueue(queueName).take();
    }

    /**
     * 延迟队列初始化
     *
     * @param queueName 队列名称
     * @return RDelayedQueue<Object>
     */
    @SuppressWarnings("unchecked")
    private <T> RDelayedQueue<T> initDelayQueue(String queueName) {
        if (!delayQueueProperties.isEnabled()) {
            throw new DelayedQueueException("延迟队列配置未开启");
        }

        // 本地存在，使用本地缓存
        if (delayedQueueMap.containsKey(queueName)) {
            return (RDelayedQueue<T>) delayedQueueMap.get(queueName);
        }

        // 从队列中获取
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(queueName);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        delayedQueueMap.put(queueName, delayedQueue);
        return delayedQueue;
    }
}
