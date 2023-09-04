package com.thinker.cloud.core.utils.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.PageUtil;
import com.google.common.collect.Lists;
import com.thinker.cloud.core.exception.FailException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

/**
 * 批量查询工具类
 *
 * @author admin
 **/
@Slf4j
@UtilityClass
public class BatchQueryUtil {

    /**
     * 分批查询数据
     *
     * @param total 数据总条数
     * @param limit 分页条数
     * @param query 数据查询过程
     * @param <T>   返回数据类型
     * @return List<T>
     */
    public static <T> List<T> query(int total, int limit, IntFunction<List<T>> query) {
        int pages = PageUtil.totalPage(total, limit);
        return query(pages, query);
    }

    /**
     * 分批查询数据
     *
     * @param total 数据总条数
     * @param limit 分页条数
     * @param query 数据查询过程
     * @param <T>   返回数据类型
     * @return List<T>
     */
    public static <T> List<T> query(IntSupplier total, int limit, IntFunction<List<T>> query) {
        int pages = PageUtil.totalPage(total.getAsInt(), limit);
        return query(pages, query);
    }

    /**
     * 分批查询数据
     *
     * @param pages 数据总页数
     * @param query 数据查询过程
     * @param <T>   返回数据类型
     * @return List<T>
     */
    public static <T> List<T> query(int pages, IntFunction<List<T>> query) {
        List<Callable<List<T>>> tasks = Lists.newArrayList();
        for (int page = 1; page <= pages; page++) {
            tasks.add(getTask(page, query));
        }

        // 并发执行
        if (!tasks.isEmpty()) {
            try {
                return BatchTaskUtil.invokeAllTasks(tasks);
            } catch (TimeoutException e) {
                log.error("分批次查询数据失败，执行超时！", e);
            } catch (FailException e) {
                log.error("分批次查询数据失败，ex={}", e.getMessage(), e);
            }
        }

        // 返回空数据
        return Lists.newArrayList();
    }

    /**
     * 获取数据
     *
     * @param page  页码
     * @param query 数据查询过程
     * @param <T>   返回数据类型
     * @return Callable<List < T>>
     */
    private static <T> Callable<List<T>> getTask(int page, IntFunction<List<T>> query) {
        return () -> {
            if (Objects.nonNull(query)) {
                return query.apply(page);
            }
            return Lists.newArrayList();
        };
    }

    public static void main(String[] args) {
        TimeInterval timer = DateUtil.timer();
        List<Integer> list = query(30000, 1000, page -> {
            ThreadUtil.safeSleep(500);
            return Lists.newArrayList(page);
        });
        log.error("串行总耗时：{}，并行执行耗时：{}ms", list.size() * 500, timer.intervalMs());
        System.out.println(list);

        timer.restart();
        List<Integer> list1 = query(() -> 30000, 1000, page -> {
            ThreadUtil.safeSleep(300);
            return Lists.newArrayList(page);
        });
        log.error("串行总耗时：{}，并行执行耗时：{}ms", list1.size() * 300, timer.intervalMs());
        System.out.println(list1);
    }
}
