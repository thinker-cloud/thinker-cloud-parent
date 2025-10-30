package com.thinker.cloud.common.utils.delayed;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 延时任务执行者
 * <p>
 * 构造时指定任务的执行过程同时可以调用start()开启任务 任务出现时putTask()添加一个任务
 *
 * @author admin
 */
@Slf4j
public class DelayedExecutor<Task extends DelayedTask<?>> {

    private ExecutorService executorService;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    @Getter
    private final DelayQueue<Task> delayedTasks = new DelayQueue<>();
    @Getter
    private volatile boolean isStart;

    private DelayedExecutor() {
    }

    private DelayedExecutor(Consumer<Task> execute, ExecutorService executorService) {
        this.executorService = executorService;
        constructExecutor(execute);
    }

    /**
     * 默认构造 2个核心线程 A线程持续取任务，B线程负责消费。 如果B线程处理不过来交给A线程处理,并发较高的情况下建议传入线程池参数自定义线程池
     * <p>
     * 1个备用线程 C线程负责执行executeOnce()的任务
     */
    public DelayedExecutor(Consumer<Task> execute) {
        this(execute, new ThreadPoolExecutor(2, 3, 10, TimeUnit.SECONDS
                , new ArrayBlockingQueue<>(1), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy()));
    }

    /**
     * 自定义线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   线程空闲时间后关闭
     * @param unit            时间单位
     * @param workQueue       阻塞队列
     * @param threadFactory   线程工厂
     */
    public DelayedExecutor(Consumer<Task> execute, int corePoolSize, int maximumPoolSize, long keepAliveTime,
                           TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(execute, new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                threadFactory, new ThreadPoolExecutor.CallerRunsPolicy()));
    }

    public void destroyTasks(Object reference) {
        delayedTasks.forEach(task -> {
            if (task.getReference().equals(reference)) {
                delayedTasks.remove(task);
            }
        });
    }

    /**
     * 构建延时任务执行器 持续等待延时任务到期，任务到期立刻执行
     *
     * @param execute 执行过程
     */
    private void constructExecutor(Consumer<Task> execute) {
        executorService.execute(() -> {
            do {
                try {
                    if (!isStart) {
                        lock.lock();
                        try {
                            while (!isStart) {
                                condition.await();
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                    Task task = delayedTasks.take();
                    if (isStart) {
                        executorService.execute(() -> execute.accept(task));
                    } else {
                        delayedTasks.put(task);
                    }
                } catch (InterruptedException e) {
                    log.error("延时任务执行失败，错误：{}", e.getMessage(), e);
                }
            } while (true);
        });
    }

    /**
     * 开始做任务
     *
     * @return DelayedExecutor<Task>
     */
    public DelayedExecutor<Task> start() {
        lock.lock();
        try {
            isStart = true;
            condition.signal();
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 停止做任务
     *
     * @return DelayedExecutor<Task>
     */
    public DelayedExecutor<Task> stop() {
        lock.lock();
        try {
            isStart = false;
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 清空任务
     *
     * @return DelayedExecutor<Task>
     */
    public DelayedExecutor<Task> clear() {
        delayedTasks.clear();
        return this;
    }

    /**
     * 放置一个任务
     *
     * @param task 延時任务
     */
    public DelayedExecutor<Task> putTask(Task task) {
        delayedTasks.put(task);
        return this;
    }

    /**
     * 执行一次自定义任务
     *
     * @param execute 执行过程
     * @return DelayedExecutor<Task>
     */
    public DelayedExecutor<Task> executeOnce(Runnable execute) {
        executorService.execute(execute);
        return this;
    }

    /**
     * 执行一次自定义任务
     *
     * @param execute 执行过程,有本执行器作为参数使用
     * @return DelayedExecutor<Task>
     */
    public DelayedExecutor<Task> executeOnce(Consumer<DelayedExecutor<Task>> execute) {
        executorService.execute(() -> execute.accept(this));
        return this;
    }
}
