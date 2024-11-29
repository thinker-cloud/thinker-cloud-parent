package com.thinker.cloud.core.utils.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import com.google.common.collect.Lists;
import com.thinker.cloud.common.exception.FailException;
import com.thinker.cloud.core.utils.MyPageUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.LongSupplier;

/**
 * 批量任务工具类
 *
 * @author admin
 **/
@Slf4j
@UtilityClass
public class BatchTaskUtil {

    /**
     * 分批量查询数据
     *
     * @param total 数据总条数
     * @param limit 分页条数
     * @param query 数据查询过程
     * @param <T>   返回数据类型
     * @return List<T>
     */
    public static <T> List<T> batchQuery(long total, long limit, LongFunction<List<T>> query) {
        long pages = MyPageUtil.totalPage(total, limit);
        return batchQuery(pages, query);
    }

    /**
     * 分批量查询数据
     *
     * @param total 数据总条数
     * @param limit 分页条数
     * @param query 数据查询过程
     * @param <T>   返回数据类型
     * @return List<T>
     */
    public static <T> List<T> batchQuery(LongSupplier total, long limit, LongFunction<List<T>> query) {
        long pages = MyPageUtil.totalPage(total.getAsLong(), limit);
        return batchQuery(pages, query);
    }

    /**
     * 分批量查询数据
     *
     * @param pages 数据总页数
     * @param query 数据查询过程
     * @param <T>   返回数据类型
     * @return List<T>
     */
    public static <T> List<T> batchQuery(long pages, LongFunction<List<T>> query) {
        List<T> list = Lists.newArrayList();
        List<Callable<List<T>>> batchTasks = Lists.newArrayList();
        for (long page = 1; page <= pages; page++) {
            batchTasks.add(getTask(page, query));
        }

        if (!batchTasks.isEmpty()) {
            try {
                // 分批次批量查询
                List<List<Callable<List<T>>>> taskLists = Lists.partition(batchTasks, 10);
                for (List<Callable<List<T>>> tasks : taskLists) {
                    list.addAll(BatchTaskThreadPool.invokeAllTasks(tasks));
                }
            } catch (TimeoutException e) {
                log.error("分批次查询数据失败，执行超时！", e);
            } catch (FailException e) {
                log.error("分批次查询数据失败，ex={}", e.getMessage(), e);
            }
        }
        return list;
    }

    /**
     * 分批量查询数据
     *
     * @param total         数据总条数
     * @param limit         分页条数
     * @param query         数据查询过程
     * @param batchConsumer 批量消费处理
     * @param <T>           返回数据类型
     */
    public static <T> void batchQuery(long total, long limit, LongFunction<List<T>> query, Consumer<List<T>> batchConsumer) {
        long pages = MyPageUtil.totalPage(total, limit);
        batchQuery(pages, query, batchConsumer);
    }

    /**
     * 分批量查询数据
     *
     * @param total         数据总条数
     * @param limit         分页条数
     * @param query         数据查询过程
     * @param batchConsumer 批量消费处理
     * @param <T>           返回数据类型
     */
    public static <T> void batchQuery(LongSupplier total, long limit, LongFunction<List<T>> query, Consumer<List<T>> batchConsumer) {
        long pages = MyPageUtil.totalPage(total.getAsLong(), limit);
        batchQuery(pages, query, batchConsumer);
    }

    /**
     * 分批量查询数据
     *
     * @param pages         数据总页数
     * @param query         数据查询过程
     * @param batchConsumer 批量消费处理
     * @param <T>           返回数据类型
     */
    public static <T> void batchQuery(long pages, LongFunction<List<T>> query, Consumer<List<T>> batchConsumer) {
        List<Callable<List<T>>> batchTasks = Lists.newArrayList();
        for (int page = 1; page <= pages; page++) {
            batchTasks.add(getTask(page, query));
        }

        // 为空不处理
        if (batchTasks.isEmpty()) {
            return;
        }

        try {
            // 分批次批量执行任务
            List<List<Callable<List<T>>>> taskLists = Lists.partition(batchTasks, 10);
            for (List<Callable<List<T>>> tasks : taskLists) {
                batchConsumer.accept(BatchTaskThreadPool.invokeAllTasks(tasks));
            }
        } catch (TimeoutException e) {
            log.error("分批次批量执行任务失败，执行超时！", e);
        } catch (FailException e) {
            log.error("分批次批量执行任务失败，ex={}", e.getMessage(), e);
        }
    }

    /**
     * 分批次更新数据
     *
     * @param total    数据总条数
     * @param limit    分页条数
     * @param consumer 批量消费处理
     */
    public static void batchUpdate(long total, long limit, Consumer<Long> consumer) {
        long pages = MyPageUtil.totalPage(total, limit);
        batchUpdate(pages, consumer);
    }

    /**
     * 分批次更新数据
     *
     * @param pages    数据总页数
     * @param consumer 数据消费处理
     */
    public static void batchUpdate(long pages, Consumer<Long> consumer) {
        List<Callable<Boolean>> batchTasks = Lists.newArrayList();
        for (int page = 1; page <= pages; page++) {
            batchTasks.add(getTask(page, consumer));
        }

        // 为空不处理
        if (batchTasks.isEmpty()) {
            return;
        }

        try {
            // 分批次批量执行任务
            List<List<Callable<Boolean>>> taskLists = Lists.partition(batchTasks, 10);
            for (List<Callable<Boolean>> tasks : taskLists) {
                BatchTaskThreadPool.invokeAllTask(tasks);
            }
        } catch (TimeoutException e) {
            throw FailException.of("分批次批量执行任务失败，执行超时！");
        } catch (FailException e) {
            log.error("分批次批量执行任务失败，ex={}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取数据
     *
     * @param page  页码
     * @param query 数据查询过程
     * @param <T>   返回数据类型
     * @return Callable<List < T>>
     */
    private static <T> Callable<List<T>> getTask(long page, LongFunction<List<T>> query) {
        return () -> {
            if (Objects.nonNull(query)) {
                return query.apply(page);
            }
            return Lists.newArrayList();
        };
    }

    /**
     * 获取数据
     *
     * @param page     页码
     * @param consumer 数据消费处理
     * @return Callable<Boolean>
     */
    private static Callable<Boolean> getTask(long page, Consumer<Long> consumer) {
        return () -> {
            if (Objects.nonNull(consumer)) {
                consumer.accept(page);
                return true;
            }
            return false;
        };
    }

    public static void main(String[] args) {
        TimeInterval timer = DateUtil.timer();
        List<Long> list = batchQuery(30000, 1000, page -> {
            ThreadUtil.safeSleep(500);
            return Lists.newArrayList(page);
        });
        log.error("串行总耗时：{}，并行执行耗时：{}ms", list.size() * 500, timer.intervalMs());
        System.out.println(list);

        timer.restart();
        List<Long> list1 = batchQuery(() -> 30000, 1000, page -> {
            ThreadUtil.safeSleep(300);
            return Lists.newArrayList(page);
        });
        log.error("串行总耗时：{}，并行执行耗时：{}ms", list1.size() * 300, timer.intervalMs());
        System.out.println(list1);
    }
}
