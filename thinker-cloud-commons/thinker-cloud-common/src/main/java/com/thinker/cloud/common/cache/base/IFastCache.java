package com.thinker.cloud.common.cache.base;

import com.thinker.cloud.common.cache.fast.IDyKey;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
     * 设置缓存对象
     *
     * @param key   缓存key
     * @param value 缓存对象
     */
    <T> void setCache(@NonNull String key, @NonNull T value);

    /**
     * 设置缓存对象
     *
     * @param key      缓存key
     * @param value    缓存对象
     * @param timeout  缓存时长
     * @param timeUnit 缓存单位
     */
    <T> void setCache(@NonNull String key, @NonNull T value, long timeout, @NonNull TimeUnit timeUnit);

    /**
     * 获取缓存
     *
     * @param key key
     * @param <T> 返回类型（可抽象的）
     * @return T
     */
    <T> T getCacheObj(@NonNull String key);

    /**
     * 获取缓存
     * 有则取缓存，无则加载Supplier
     *
     * @param key            缓存key
     * @param missedSupplier 提供数据获取过程
     * @param <T>            返回类型（可抽象的）
     * @return T
     */
    <T> T getCacheObj(@NonNull String key, @NonNull Supplier<T> missedSupplier);

    /**
     * 获取缓存
     * 有则取缓存，无则加载Supplier并缓存
     *
     * @param key            缓存key
     * @param missedSupplier 提供数据获取过程
     * @param timeUnit       超时时间单位
     * @param expire         超时时间
     * @param <T>            返回类型（可抽象的）
     * @return 对象
     */
    <T> T getCacheObj(@NonNull String key, @NonNull Supplier<T> missedSupplier, @NonNull TimeUnit timeUnit, long expire);

    /**
     * 获取缓存
     * 有则取缓存，无则加载Supplier并缓存
     *
     * @param keyPrefix      缓存key的前缀
     * @param dyKey          动态条件（组成key的后缀）
     * @param missedSupplier 提供数据获取过程
     * @param <T>            返回类型（可抽象的）
     * @return 对象
     */
    default <T> T getCacheObj(@NonNull String keyPrefix, @NonNull IDyKey dyKey, @NonNull Supplier<T> missedSupplier) {
        Objects.requireNonNull(dyKey, "动态key对象不能为空");

        String key = keyPrefix + ":" + dyKey.getKey();
        return this.getCacheObj(key, missedSupplier);
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
     * @param <T>            返回类型（可抽象的）
     * @return 对象
     */
    default <T> T getCacheObj(@NonNull String keyPrefix,
                              @NonNull IDyKey dyKey,
                              @NonNull Supplier<T> missedSupplier,
                              @NonNull TimeUnit timeUnit, long expire) {
        Objects.requireNonNull(dyKey, "动态key对象不能为空");

        String key = keyPrefix + ":" + dyKey.getKey();
        return this.getCacheObj(key, missedSupplier, timeUnit, expire);
    }

    /**
     * 获取缓存
     *
     * @param keys           缓存keys
     * @param missedSupplier 提供数据获取过程
     * @param <T>            返回类型（可抽象的）
     * @return 列表
     */
    <T> List<T> getCaches(@NonNull Collection<String> keys, @NonNull Supplier<List<T>> missedSupplier);
}
