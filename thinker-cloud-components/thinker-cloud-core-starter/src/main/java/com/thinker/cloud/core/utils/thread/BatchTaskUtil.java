package com.thinker.cloud.core.utils.thread;


import com.google.common.collect.Lists;
import com.thinker.cloud.core.exception.FailException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 批量任务工具类
 *
 * @author admin
 **/
@Slf4j
@UtilityClass
public class BatchTaskUtil {

    /**
     * 公共线程池
     */
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(16
            , 32, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100)
            , Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

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
        Collection<Future<?>> tasks = dataList.stream()
                .<Runnable>map(data -> () -> handler.accept(data))
                .map(EXECUTOR_SERVICE::submit)
                .collect(Collectors.toList());
        try {
            for (Future<?> task : tasks) {
                task.get(30, TimeUnit.SECONDS);
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
        List<Callable<R>> tasks = dataList.stream()
                .<Callable<R>>map(data -> () -> handler.apply(data))
                .collect(Collectors.toList());

        // 并发执行
        if (!tasks.isEmpty()) {
            return invokeAllTask(tasks);
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
        List<Callable<List<R>>> tasks = dataList.stream()
                .<Callable<List<R>>>map(data -> () -> handler.apply(data))
                .collect(Collectors.toList());

        // 并发执行
        if (!tasks.isEmpty()) {
            return invokeAllTasks(tasks);
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
        try {
            List<T> list = Lists.newArrayList();
            List<Future<T>> futures = EXECUTOR_SERVICE.invokeAll(tasks);
            for (Future<T> result : futures) {
                T data = result.get(30, TimeUnit.SECONDS);
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
        try {
            List<T> list = Lists.newArrayList();
            List<Future<List<T>>> futures = EXECUTOR_SERVICE.invokeAll(tasks);
            for (Future<List<T>> result : futures) {
                list.addAll(result.get(30, TimeUnit.SECONDS));
            }
            return list;
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            throw new FailException("未知异常，请联系管理员", e);
        }
    }
}
