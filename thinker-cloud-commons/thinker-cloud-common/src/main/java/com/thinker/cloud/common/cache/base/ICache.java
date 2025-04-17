package com.thinker.cloud.common.cache.base;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 缓存抽象基类
 *
 * @author admin
 **/
public interface ICache<V> {

    /**
     * 批量操作缓存最大数量
     */
    Integer BATCH_CACHE_MAX_SIZE = 30;

    /**
     * 设置缓存
     *
     * @param key   缓存key
     * @param value 缓存对象
     */
    void setCache(@NonNull String key, @NonNull V value);

    /**
     * 设置缓存
     *
     * @param key      缓存key
     * @param value    缓存对象
     * @param timeout  缓存时长
     * @param timeUnit 缓存单位
     */
    void setCache(@NonNull String key, @NonNull V value, long timeout, @NonNull TimeUnit timeUnit);

    /**
     * 批量set缓存
     *
     * @param cacheMap key、value 映射Map
     */
    default void multiSetCache(@NonNull Map<String, V> cacheMap) {
        this.multiSetCache(cacheMap, BATCH_CACHE_MAX_SIZE);
    }

    /**
     * 批量set缓存
     *
     * @param cacheMap         key、value 映射Map
     * @param batchSetCacheMax 批量设置缓存最大数量，超过将按此值分批次缓存
     */
    void multiSetCache(@NonNull Map<String, V> cacheMap, int batchSetCacheMax);

    /**
     * 批量set缓存
     *
     * @param cacheMap key、value 映射Map
     * @param timeout  缓存时长
     * @param timeUnit 缓存单位
     */
    default void multiSetCache(@NonNull Map<String, V> cacheMap, long timeout, @NonNull TimeUnit timeUnit) {
        this.multiSetCache(cacheMap, timeout, timeUnit, BATCH_CACHE_MAX_SIZE);
    }

    /**
     * 批量set缓存
     *
     * @param cacheMap         key、value 映射Map
     * @param timeout          缓存时长
     * @param timeUnit         缓存单位
     * @param batchSetCacheMax 批量设置缓存最大数量，超过将按此值分批次缓存
     */
    void multiSetCache(@NonNull Map<String, V> cacheMap, long timeout, @NonNull TimeUnit timeUnit, int batchSetCacheMax);

    /**
     * 是否存在缓存中
     *
     * @param key key
     * @return boolean
     */
    boolean hasCache(@NonNull String key);

    /**
     * 获取缓存
     *
     * @param key 缓存key
     * @return String
     */
    @Nullable
    V getCache(@NonNull String key);

    /**
     * 获取缓存
     *
     * @param key           缓存key
     * @param dataConvertor 缓存数据转换处理
     * @return T
     */
    @Nullable
    <T> T getCache(@NonNull String key, @NonNull Function<V, T> dataConvertor);

    /**
     * 根据keys获取缓存
     *
     * @param keys 缓存key列表
     * @return List<V>
     */
    @Nullable
    List<V> getCaches(@NonNull Collection<String> keys);

    /**
     * 根据keys获取缓存
     *
     * @param keys       缓存key列表
     * @param dataFilter 条件过滤器
     * @return List<V>
     */
    @Nullable
    List<V> getCaches(@NonNull Collection<String> keys, @NonNull Predicate<V> dataFilter);

    /**
     * 根据keys获取缓存
     *
     * @param keys          缓存key列表
     * @param dataConvertor 缓存数据转换处理
     * @return List<V>
     */
    @Nullable
    default <T> List<T> getCaches(@NonNull Collection<String> keys, @NonNull Function<V, T> dataConvertor) {
        return this.getCaches(keys, dataConvertor, data -> true);
    }

    /**
     * 根据keys获取缓存
     *
     * @param keys          缓存key列表
     * @param dataConvertor 缓存数据转换处理
     * @param dataFilter    条件过滤器
     * @return List<T>
     */
    @Nullable
    <T> List<T> getCaches(@NonNull Collection<String> keys, @NonNull Function<V, T> dataConvertor, @NonNull Predicate<T> dataFilter);

    /**
     * 根据key删除缓存
     *
     * @param key 缓存key
     * @return 是否删除
     */
    boolean removeCache(@NonNull String key);

    /**
     * 根据keys删除缓存
     *
     * @param keys 缓存keys
     */
    void removeCache(@NonNull Collection<String> keys);

    /**
     * 根据前缀删除所有缓存
     *
     * @param prefix 缓存key前缀
     */
    void removeAllCache(@NonNull String prefix);

    /**
     * 获取key列表
     *
     * @param prefix 缓存key前缀
     * @return key列表
     */
    Set<String> getKeys(@NonNull String prefix);
}
