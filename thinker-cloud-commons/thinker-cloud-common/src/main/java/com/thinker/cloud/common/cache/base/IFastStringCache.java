package com.thinker.cloud.common.cache.base;

import com.alibaba.fastjson.TypeReference;
import com.thinker.cloud.common.cache.fast.IDyKey;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存增强实现
 * 为了快速实现对一个业务场景的缓存需求
 *
 * @author admin
 */
public interface IFastStringCache {

    /**
     * 获取缓存
     *
     * @param key         缓存key
     * @param entityClass 指定返回类型class （实际类型）
     * @param <T>         返回类型（可抽象的）
     * @param <R>         返回类型（实际类型）
     * @return T
     */
    default <T, R extends T> T getCache(@NonNull String key, @NonNull Class<R> entityClass) {
        return this.getCache(key, () -> null, entityClass);
    }

    /**
     * 获取缓存
     * 有则取缓存，无则加载Supplier
     *
     * @param key            缓存key
     * @param missedSupplier 提供数据获取过程
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return T
     */
    <T, R extends T> T getCache(@NonNull String key,
                                @NonNull Supplier<T> missedSupplier,
                                @NonNull Class<R> entityClass);

    /**
     * 获取缓存
     *
     * @param key  缓存key
     * @param type 指定返回类型 （实际类型）
     * @param <T>  返回类型（可抽象的）
     * @param <R>  返回类型（实际类型）
     * @return T
     */
    default <T, R extends T> T getCache(@NonNull String key, @NonNull TypeReference<R> type) {
        return this.getCache(key, () -> null, type);
    }

    /**
     * 获取缓存
     * 有则取缓存，无则加载Supplier
     *
     * @param key            缓存key
     * @param missedSupplier 提供数据获取过程
     * @param type           指定返回类型 （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return T
     */
    <T, R extends T> T getCache(@NonNull String key,
                                @NonNull Supplier<T> missedSupplier,
                                @NonNull TypeReference<R> type);

    /**
     * 获取缓存
     * 有则取缓存，无则加载Supplier并缓存
     *
     * @param key            缓存key
     * @param missedSupplier 提供数据获取过程
     * @param timeUnit       超时时间单位
     * @param expire         超时时间
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 对象
     */
    <T, R extends T> T getCache(@NonNull String key,
                                @NonNull Supplier<T> missedSupplier,
                                @NonNull TimeUnit timeUnit, long expire,
                                @NonNull Class<R> entityClass);

    /**
     * 获取缓存
     * 有则取缓存，无则加载Supplier并缓存
     *
     * @param keyPrefix      缓存key的前缀
     * @param dyKey          动态条件（组成key的后缀）
     * @param missedSupplier 提供数据获取过程
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 对象
     */
    default <T, R extends T> T getCache(@NonNull String keyPrefix,
                                        @NonNull IDyKey dyKey,
                                        @NonNull Supplier<T> missedSupplier,
                                        @NonNull Class<R> entityClass) {
        String key = keyPrefix + ":" + dyKey;
        return this.getCache(key, missedSupplier, entityClass);
    }

    /**
     * 获取缓存
     * 有则取缓存，无则加载Supplier并缓存
     *
     * @param keyPrefix      缓存key的前缀
     * @param dyKey          动态条件（组成key的后缀）
     * @param missedSupplier 提供数据获取过程
     * @param timeUnit       超时时间单位
     * @param expire         超时时间
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 对象
     */
    default <T, R extends T> T getCache(@NonNull String keyPrefix,
                                        @NonNull IDyKey dyKey,
                                        @NonNull Supplier<T> missedSupplier,
                                        @NonNull TimeUnit timeUnit, long expire,
                                        @NonNull Class<R> entityClass) {
        String key = keyPrefix + ":" + dyKey.getKey();
        return this.getCache(key, missedSupplier, timeUnit, expire, entityClass);
    }

    /**
     * 获取缓存
     *
     * @param key            缓存key
     * @param missedSupplier 提供数据获取过程
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 列表
     */
    <T, R extends T> List<T> getCaches(@NonNull String key,
                                       @NonNull Supplier<List<T>> missedSupplier,
                                       @NonNull Class<R> entityClass);

    /**
     * 缓存列表
     * 有则取缓存，无则加载Supplier
     *
     * @param key            缓存key
     * @param missedSupplier 提供数据获取过程
     * @param timeUnit       超时时间单位
     * @param expire         超时时间
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 列表
     */
    <T, R extends T> List<T> getCaches(@NonNull String key,
                                       @NonNull Supplier<List<T>> missedSupplier,
                                       @NonNull TimeUnit timeUnit, long expire,
                                       @NonNull Class<R> entityClass);

    /**
     * 缓存列表
     * 有则取缓存，无则加载Supplier
     *
     * @param keyPrefix      缓存key的前缀
     * @param dyKey          动态条件（组成key的后缀）
     * @param missedSupplier 提供数据获取过程
     * @param timeUnit       超时时间单位
     * @param expire         超时时间
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 列表
     */
    default <T, R extends T> List<T> getCaches(@NonNull String keyPrefix,
                                               @NonNull IDyKey dyKey,
                                               @NonNull Supplier<List<T>> missedSupplier,
                                               @NonNull TimeUnit timeUnit, long expire,
                                               @NonNull Class<R> entityClass) {
        String key = keyPrefix + ":" + dyKey.getKey();
        return this.getCaches(key, missedSupplier, timeUnit, expire, entityClass);
    }
}
