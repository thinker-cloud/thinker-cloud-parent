package com.thinker.cloud.core.cache.fast;

import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存增强实现
 * 为了快速实现对一个业务场景的缓存需求
 *
 * @author admin
 */
public interface IFastCache {

    /**
     * 缓存
     *
     * @param cacheKey 缓存key的前缀
     * @param data     缓存数据
     * @param timeUnit 超时时间单位
     * @param expire   超时时间
     */
    void cache(String cacheKey, Object data, TimeUnit timeUnit, int expire);

    /**
     * 根据Key获取缓存
     *
     * @param cacheKey       缓存key
     * @param missedSupplier 提供数据获取过程
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 对象
     */
    <T, R extends T> T cache(String cacheKey, Supplier<T> missedSupplier, Class<R> entityClass);

    /**
     * 根据Key获取缓存
     *
     * @param cacheKey       缓存key
     * @param missedSupplier 提供数据获取过程
     * @param type           指定返回类型 （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 对象
     */
    <T, R extends T> T cache(String cacheKey, Supplier<T> missedSupplier, TypeReference<R> type);

    /**
     * 缓存一个
     * 有则取缓存，无则加载Supplier
     *
     * @param cacheKey       缓存key
     * @param missedSupplier 提供数据获取过程
     * @param timeUnit       超时时间单位
     * @param expire         超时时间
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 对象
     */
    <T, R extends T> T cache(String cacheKey, Supplier<T> missedSupplier, TimeUnit timeUnit, int expire, Class<R> entityClass);

    /**
     * 缓存一个
     * 有则取缓存，无则加载Supplier
     *
     * @param cacheKeyPrefix 缓存key的前缀
     * @param dyKey          动态条件（组成key的后缀）
     * @param missedSupplier 提供数据获取过程
     * @param timeUnit       超时时间单位
     * @param expire         超时时间
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 对象
     */
    <T, R extends T> T cache(String cacheKeyPrefix, IDyKey dyKey, Supplier<T> missedSupplier, TimeUnit timeUnit, int expire, Class<R> entityClass);

    /**
     * 获取缓存
     *
     * @param cacheKey       缓存key
     * @param missedSupplier 提供数据获取过程
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 列表
     */
    <T, R extends T> List<T> cacheList(String cacheKey, Supplier<List<T>> missedSupplier, Class<R> entityClass);

    /**
     * 缓存列表
     * 有则取缓存，无则加载Supplier
     *
     * @param cacheKey       缓存key
     * @param missedSupplier 提供数据获取过程
     * @param timeUnit       超时时间单位
     * @param expire         超时时间
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 列表
     */
    <T, R extends T> List<T> cacheList(String cacheKey, Supplier<List<T>> missedSupplier
            , TimeUnit timeUnit, int expire, Class<R> entityClass);

    /**
     * 缓存列表
     * 有则取缓存，无则加载Supplier
     *
     * @param cacheKeyPrefix 缓存key的前缀
     * @param dyKey          动态条件（组成key的后缀）
     * @param missedSupplier 提供数据获取过程
     * @param timeUnit       超时时间单位
     * @param expire         超时时间
     * @param entityClass    指定返回类型class （实际类型）
     * @param <T>            返回类型（可抽象的）
     * @param <R>            返回类型（实际类型）
     * @return 列表
     */
    <T, R extends T> List<T> cacheList(String cacheKeyPrefix, IDyKey dyKey, Supplier<List<T>> missedSupplier
            , TimeUnit timeUnit, int expire, Class<R> entityClass);

    /**
     * 获取key列表
     *
     * @param prefixKey      key前缀
     * @param missedSupplier 提供数据获取过程
     * @return key列表
     */
    Set<String> getKeys(String prefixKey, Supplier<Set<String>> missedSupplier);

    /**
     * 清除缓存
     *
     * @param cacheKey 缓存key
     */
    void delete(String cacheKey);
}
