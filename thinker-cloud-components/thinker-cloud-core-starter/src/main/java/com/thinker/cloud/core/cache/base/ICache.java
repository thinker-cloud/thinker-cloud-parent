package com.thinker.cloud.core.cache.base;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 缓存抽象基类
 *
 * @author admin
 **/
public interface ICache {

    /**
     * 最大批量缓存数量
     */
    Integer BATCH_SET_CACHE_MAX_SIZE = 30;

    /**
     * 设置缓存
     *
     * @param key   缓存key
     * @param value 缓存对象
     */
    void setCache(@NonNull String key, @NonNull Object value);

    /**
     * 设置缓存
     *
     * @param key      缓存key
     * @param value    缓存对象
     * @param timeout  缓存时长
     * @param timeUnit 缓存单位
     */
    void setCache(@NonNull String key, @NonNull Object value, long timeout, @NonNull TimeUnit timeUnit);

    /**
     * 批量set缓存
     *
     * @param cacheMap key、value 映射Map
     */
    default void multiSetCache(@NonNull Map<String, Object> cacheMap) {
        this.multiSetCache(cacheMap, BATCH_SET_CACHE_MAX_SIZE);
    }

    /**
     * 批量set缓存
     *
     * @param cacheMap         key、value 映射Map
     * @param batchSetCacheMax 批量设置缓存最大数量，超过将按此值分批次缓存
     */
    void multiSetCache(@NonNull Map<String, Object> cacheMap, int batchSetCacheMax);

    /**
     * 批量set缓存
     *
     * @param cacheMap key、value 映射Map
     * @param timeout  缓存时长
     * @param timeUnit 缓存单位
     */
    default void multiSetCache(@NonNull Map<String, Object> cacheMap, long timeout, @NonNull TimeUnit timeUnit) {
        this.multiSetCache(cacheMap, timeout, timeUnit, BATCH_SET_CACHE_MAX_SIZE);
    }

    /**
     * 批量set缓存
     *
     * @param cacheMap         key、value 映射Map
     * @param timeout          缓存时长
     * @param timeUnit         缓存单位
     * @param batchSetCacheMax 批量设置缓存最大数量，超过将按此值分批次缓存
     */
    void multiSetCache(@NonNull Map<String, Object> cacheMap, long timeout, @NonNull TimeUnit timeUnit, int batchSetCacheMax);

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
    String getCache(@NonNull String key);

    /**
     * 获取缓存
     *
     * @param key           缓存key
     * @param dataConvertor 缓存数据转换处理
     * @return String
     */
    @Nullable
    <T> T getCache(@NonNull String key, @NonNull Function<String, T> dataConvertor);

    /**
     * 根据keys获取缓存
     *
     * @param keys 缓存key列表
     * @return List<String>
     */
    @Nullable
    default List<String> getAllCaches(@NonNull Collection<String> keys) {
        return this.getAllCaches(keys, data -> data);
    }

    /**
     * 根据keys获取缓存
     *
     * @param keys          缓存key列表
     * @param dataConvertor 缓存数据转换处理
     * @return List<String>
     */
    @Nullable
    default <T> List<T> getAllCaches(@NonNull Collection<String> keys, @NonNull Function<String, T> dataConvertor) {
        return this.getAllCaches(keys, dataConvertor, data -> true);
    }

    /**
     * 根据keys获取缓存
     *
     * @param keys          缓存key列表
     * @param dataConvertor 缓存数据转换处理
     * @param dataFilter    条件过滤器
     * @return List<String>
     */
    @Nullable
    <T> List<T> getAllCaches(@NonNull Collection<String> keys, @NonNull Function<String, T> dataConvertor, @NonNull Predicate<T> dataFilter);

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
     * @param prefix prefix
     */
    void removeAllCache(@NonNull String prefix);
}
