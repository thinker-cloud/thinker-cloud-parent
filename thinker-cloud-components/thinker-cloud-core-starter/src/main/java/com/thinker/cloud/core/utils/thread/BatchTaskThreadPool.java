package com.thinker.cloud.core.utils.thread;


import com.google.common.collect.Lists;
import com.thinker.cloud.common.exception.FailException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 批量任务线程池
 *
 * @author admin
 **/
@Slf4j
@UtilityClass
public class BatchTaskThreadPool {

    /**
     * 公共线程池
     */
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(16
            , 32, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000)
            , new BasicThreadFactory.Builder().namingPattern("BatchTaskThreadPool-thread-%d").build()
            , new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 异步处理数据
     */
    public static void execute(Runnable runnable) {
        try {
            EXECUTOR_SERVICE.execute(runnable);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FailException("未知异常，请联系管理员", e);
        }
    }

    /**
     * 异步处理数据
     *
     * @param data    数据
     * @param handler 处理逻辑
     * @param <T>     <T>
     */
    public static <T> void execute(@NonNull T data, @NonNull Consumer<T> handler) {
        try {
            EXECUTOR_SERVICE.execute(() -> handler.accept(data));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FailException("未知异常，请联系管理员", e);
        }
    }

    /**
     * 批量处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param <T>      <T>
     */
    public static <T> void execute(@NonNull Collection<T> dataList, @NonNull Consumer<T> handler) {
        try {
            dataList.stream()
                    .<Runnable>map(data -> () -> handler.accept(data))
                    .forEach(EXECUTOR_SERVICE::execute);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FailException("未知异常，请联系管理员", e);
        }
    }

    /**
     * 并行处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param <T>      <T>
     */
    public static <T> void parallel(@NonNull Collection<T> dataList, @NonNull Consumer<T> handler) throws TimeoutException {
        parallel(dataList, handler, 3);
    }

    /**
     * 并行处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param timeout  超时时间 单位秒
     * @param <T>      <T>
     */
    public static <T> void parallel(@NonNull Collection<T> dataList, @NonNull Consumer<T> handler, long timeout) throws TimeoutException {
        parallel(dataList, handler, timeout, TimeUnit.SECONDS);
    }

    /**
     * 并行处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @param <T>      <T>
     */
    public static <T> void parallel(@NonNull Collection<T> dataList, @NonNull Consumer<T> handler, long timeout, TimeUnit timeUnit) throws TimeoutException {
        Collection<Future<?>> tasks = dataList.stream()
                .<Runnable>map(data -> () -> handler.accept(data))
                .map(EXECUTOR_SERVICE::submit)
                .collect(Collectors.toList());
        try {
            for (Future<?> task : tasks) {
                task.get(timeout, timeUnit);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            throw new FailException("未知异常，请联系管理员", e);
        }
    }

    /**
     * 并行处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param <T>      <T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T, R> List<R> executeTask(@NonNull Collection<T> dataList, @NonNull Function<T, R> handler) throws TimeoutException {
        return executeTask(dataList, handler, 3);
    }

    /**
     * 并行处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param timeout  超时时间 单位秒
     * @param <T>      <T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T, R> List<R> executeTask(@NonNull Collection<T> dataList, @NonNull Function<T, R> handler, long timeout) throws TimeoutException {
        return executeTask(dataList, handler, timeout, TimeUnit.SECONDS);
    }

    /**
     * 并行处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @param <T>      <T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T, R> List<R> executeTask(@NonNull Collection<T> dataList, @NonNull Function<T, R> handler, long timeout, TimeUnit timeUnit) throws TimeoutException {
        List<Callable<R>> tasks = dataList.stream()
                .<Callable<R>>map(data -> () -> handler.apply(data))
                .collect(Collectors.toList());

        // 并发执行
        if (!tasks.isEmpty()) {
            return invokeAllTask(tasks, timeout, timeUnit);
        }
        return Lists.newArrayList();
    }

    /**
     * 并行处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param <T>      <T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T, R> List<R> executeTasks(@NonNull Collection<T> dataList, @NonNull Function<T, List<R>> handler) throws TimeoutException {
        return executeTasks(dataList, handler, 3);
    }

    /**
     * 并行处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param timeout  超时时间 单位秒
     * @param <T>      <T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T, R> List<R> executeTasks(@NonNull Collection<T> dataList, @NonNull Function<T, List<R>> handler, long timeout) throws TimeoutException {
        return executeTasks(dataList, handler, timeout, TimeUnit.SECONDS);
    }

    /**
     * 并行处理数据
     *
     * @param dataList 数据列表
     * @param handler  处理逻辑
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @param <T>      <T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T, R> List<R> executeTasks(@NonNull Collection<T> dataList, @NonNull Function<T, List<R>> handler, long timeout, TimeUnit timeUnit) throws TimeoutException {
        List<Callable<List<R>>> tasks = dataList.stream()
                .<Callable<List<R>>>map(data -> () -> handler.apply(data))
                .collect(Collectors.toList());

        // 并发执行
        if (!tasks.isEmpty()) {
            return invokeAllTasks(tasks, timeout, timeUnit);
        }
        return Lists.newArrayList();
    }

    /**
     * 并发执行查询
     *
     * @param tasks 任务
     * @param <T>   <T>
     * @return List<T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T> List<T> invokeAllTask(@NonNull List<Callable<T>> tasks) throws TimeoutException {
        return invokeAllTask(tasks, 3);
    }

    /**
     * 并发执行查询
     *
     * @param tasks   任务
     * @param timeout 超时时间 单位秒
     * @param <T>     <T>
     * @return List<T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T> List<T> invokeAllTask(@NonNull List<Callable<T>> tasks, long timeout) throws TimeoutException {
        return invokeAllTask(tasks, timeout, TimeUnit.SECONDS);
    }

    /**
     * 并发执行查询
     *
     * @param tasks    任务
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @param <T>      <T>
     * @return List<T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T> List<T> invokeAllTask(@NonNull List<Callable<T>> tasks, long timeout, TimeUnit timeUnit) throws TimeoutException {
        try {
            List<T> list = Lists.newArrayList();
            if (tasks.isEmpty()) {
                return list;
            }
            List<Future<T>> futures = EXECUTOR_SERVICE.invokeAll(tasks);
            for (Future<T> result : futures) {
                T data = result.get(timeout, timeUnit);
                Optional.ofNullable(data).ifPresent(list::add);
            }
            return list;
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            throw new FailException("未知异常，请联系管理员", e);
        }
    }

    /**
     * 并发执行查询
     *
     * @param tasks 任务
     * @param <T>   <T>
     * @return List<T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T> List<T> invokeAllTasks(@NonNull List<Callable<List<T>>> tasks) throws TimeoutException {
        return invokeAllTasks(tasks, 3);
    }

    /**
     * 并发执行查询
     *
     * @param tasks   任务
     * @param timeout 超时时间 单位秒
     * @param <T>     <T>
     * @return List<T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T> List<T> invokeAllTasks(@NonNull List<Callable<List<T>>> tasks, long timeout) throws TimeoutException {
        return invokeAllTasks(tasks, timeout, TimeUnit.SECONDS);
    }

    /**
     * 并发执行查询
     *
     * @param tasks    任务
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @param <T>      <T>
     * @return List<T>
     * @throws TimeoutException 执行超时异常
     */
    public static <T> List<T> invokeAllTasks(@NonNull List<Callable<List<T>>> tasks, long timeout, TimeUnit timeUnit) throws TimeoutException {
        try {
            List<T> list = Lists.newArrayList();
            if (tasks.isEmpty()) {
                return list;
            }
            List<Future<List<T>>> futures = EXECUTOR_SERVICE.invokeAll(tasks);
            for (Future<List<T>> result : futures) {
                list.addAll(result.get(timeout, timeUnit));
            }
            return list;
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            throw new FailException("未知异常，请联系管理员", e);
        }
    }
}
