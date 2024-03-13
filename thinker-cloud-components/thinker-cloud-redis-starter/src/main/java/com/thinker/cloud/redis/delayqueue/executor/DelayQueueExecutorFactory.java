package com.thinker.cloud.redis.delayqueue.executor;

import cn.hutool.core.thread.ThreadUtil;
import com.thinker.cloud.redis.delayqueue.core.DelayMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonShutdownException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 延迟队列执行器工厂类
 *
 * @author admin
 */
@Slf4j
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class DelayQueueExecutorFactory implements ApplicationListener<ContextRefreshedEvent> {

    private final ThreadPoolTaskExecutor delayQueueThreadPoolExecutor;

    /**
     * 延迟队列执行器集合
     */
    public static final Map<String, DelayQueueExecutor> EXECUTOR_BEAN_MAP = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, DelayQueueExecutor> delayQueueExecutorMap = applicationContext.getBeansOfType(DelayQueueExecutor.class);
        delayQueueExecutorMap.forEach((k, v) -> {
            String queueName = v.queueName();
            if (EXECUTOR_BEAN_MAP.containsKey(queueName)) {
                DelayQueueExecutor delayQueueExecutor = EXECUTOR_BEAN_MAP.get(queueName);
                throw new IllegalStateException("已存在相同队列的DelayQueueExecutor实例[" + queueName + "]:"
                        + " ---> \n" + delayQueueExecutor.getClass()
                        + " ---> \n" + v.getClass());
            }
            EXECUTOR_BEAN_MAP.put(queueName, v);
            this.runExecutor(k, v);
        });
    }

    @SuppressWarnings("unchecked")
    private void runExecutor(String queueName, DelayQueueExecutor executor) {
        // 由于这个是固定永久循环监听一个队列，为了不占用通用线程池的线程，单独开启一个线程作为后台运行
        Thread thread = ThreadUtil.newThread(() -> {
            log.info("延迟队列「{}」执行器已启动...", queueName);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DelayMessage delayMessage = executor.take();
                    log.info("获取到队列「{}」到期消息：{}", queueName, delayMessage);

                    if (Objects.nonNull(delayMessage)) {
                        delayQueueThreadPoolExecutor.execute(() -> executor.execute(delayMessage));
                    }
                } catch (RedissonShutdownException ex) {
                    log.error("收到 RedissonShutdownException 结束延迟队列[{}]异常, ex={}", queueName, ex.getMessage(), ex);
                } catch (Exception e) {
                    log.error("延迟队列「{}」取值[{}]中断异常：{},  ", queueName, e.getMessage(), e.getMessage(), e);
                }
            }
            log.info("延迟队列线程名:{}, 被异常中断结束", Thread.currentThread().getName());
        }, "DelayQueueMainThread-" + queueName, true);
        thread.start();
    }
}
