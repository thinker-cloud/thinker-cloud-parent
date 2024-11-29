package com.thinker.cloud.redis.cache.fast;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Lists;
import com.thinker.cloud.common.cache.base.ICache;
import com.thinker.cloud.redis.cache.service.RedisClientService;
import jakarta.annotation.Resource;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 缓存抽象类
 *
 * @author admin
 **/
public abstract class AbstractRedisCache implements ICache {

    @Resource
    protected RedisClientService redisClientService;

    @Override
    public void setCache(@NonNull String key, @NonNull Object value) {
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(value, "缓存对象不能为空");

        redisClientService.setCache(key, value);
    }

    @Override
    public void setCache(@NonNull String key, @NonNull Object value, long timeout, @NonNull TimeUnit timeUnit) {
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(value, "缓存对象不能为空");
        Objects.requireNonNull(timeUnit, "缓存单位不能为空");

        redisClientService.setCache(key, value, timeout, timeUnit);
    }

    @Override
    public void multiSetCache(@NonNull Map<String, Object> cacheMap, int batchSetCacheMaxSize) {
        Objects.requireNonNull(cacheMap, "缓存Map不能为空");

        if (cacheMap.isEmpty()) {
            return;
        }

        // 批量缓存最大长度
        if (batchSetCacheMaxSize < 1) {
            batchSetCacheMaxSize = BATCH_SET_CACHE_MAX_SIZE;
        }

        // 如果缓存数量小于支持批量最大数量，则直接缓存
        if (cacheMap.size() <= batchSetCacheMaxSize) {
            redisClientService.multiSetCache(cacheMap);
            return;
        }

        // 否则将数据拆分为 batchSetCacheMax 一组，分批次缓存
        List<Map.Entry<String, Object>> allCache = Lists.newArrayList(cacheMap.entrySet());
        List<List<Map.Entry<String, Object>>> smallLists = ListUtil.split(allCache, batchSetCacheMaxSize);

        // 防止批量缓存数据太多，考虑redis负载过重、内存、网络开销等情况，分批次设置redis缓存
        for (List<Map.Entry<String, Object>> smallList : smallLists) {
            Map<String, Object> multiSetCache = smallList.stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            redisClientService.multiSetCache(multiSetCache);
        }
    }

    @Override
    public void multiSetCache(@NonNull Map<String, Object> cacheMap, long timeout, @NonNull TimeUnit timeUnit, int batchSetCacheMaxSize) {
        Objects.requireNonNull(cacheMap, "缓存Map不能为空");
        Objects.requireNonNull(timeUnit, "缓存单位不能为空");

        if (cacheMap.isEmpty()) {
            return;
        }

        // 批量缓存最大长度
        if (batchSetCacheMaxSize < 1) {
            batchSetCacheMaxSize = BATCH_SET_CACHE_MAX_SIZE;
        }

        // 批量设置缓存
        this.multiSetCache(cacheMap, batchSetCacheMaxSize);

        // 如果数量小于支持批量最大数量，则直接设置过期时间
        Set<String> keys = cacheMap.keySet();
        if (keys.size() <= batchSetCacheMaxSize) {
            redisClientService.expire(keys, timeout, timeUnit);
            return;
        }

        // 否则将数据拆分为 batchSetCacheMax 一组，分批次设置
        List<List<String>> allKeys = ListUtil.split(Lists.newArrayList(keys), batchSetCacheMaxSize);
        for (List<String> sKeys : allKeys) {
            redisClientService.expire(new HashSet<>(sKeys), timeout, timeUnit);
        }
    }

    @Override
    public boolean hasCache(@NonNull String key) {
        Objects.requireNonNull(key, "缓存key不能为空");
        return redisClientService.hasKey(key);
    }

    @Override
    public <T> T getCache(@NonNull String key) {
        Objects.requireNonNull(key, "缓存key不能为空");
        return redisClientService.getCache(key);
    }

    @Override
    public <T, R> R getCache(@NonNull String key, @NonNull Function<T, R> dataConvertor) {
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(dataConvertor, "缓存数据转换器不能为空");

        T value = redisClientService.getCache(key);
        return Optional.ofNullable(value).map(dataConvertor).orElse(null);
    }

    @Override
    public <T> List<T> getCaches(@NonNull Collection<String> keys, @NonNull Predicate<T> dataFilter) {
        Objects.requireNonNull(keys, "缓存key列表不能为空");
        Objects.requireNonNull(dataFilter, "缓存数据过滤器不能为空");

        List<T> caches = redisClientService.getCaches(keys);
        if (Objects.isNull(caches)) {
            return null;
        }

        List<T> list = caches.stream()
                .filter(Objects::nonNull)
                .filter(dataFilter)
                .collect(Collectors.toList());

        return list.isEmpty() ? null : list;
    }

    @Override
    public <T, R> List<R> getCaches(@NonNull Collection<String> keys, @NonNull Function<T, R> dataConvertor, @NonNull Predicate<R> dataFilter) {
        Objects.requireNonNull(keys, "缓存key列表不能为空");
        Objects.requireNonNull(dataConvertor, "缓存数据转换器不能为空");
        Objects.requireNonNull(dataFilter, "缓存数据过滤器不能为空");

        if (CollectionUtil.isEmpty(keys)) {
            return null;
        }

        List<R> allCaches;

        // 无需分批次获取
        if (keys.size() <= BATCH_SET_CACHE_MAX_SIZE) {
            List<T> caches = redisClientService.getCaches(keys);
            if (Objects.isNull(caches)) {
                return null;
            }

            allCaches = caches.stream()
                    .filter(Objects::nonNull)
                    .map(dataConvertor)
                    .filter(dataFilter)
                    .collect(Collectors.toList());
        } else {
            // 防止keys太多，考虑redis负载过重、内存、网络开销等情况，分批次获取redis缓存
            List<List<String>> sKeys = CollectionUtil.split(keys, BATCH_SET_CACHE_MAX_SIZE);
            allCaches = sKeys.stream()
                    .<List<T>>map(redisClientService::getCaches)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .map(dataConvertor)
                    .filter(dataFilter)
                    .collect(Collectors.toList());
        }

        return allCaches.isEmpty() ? null : allCaches;
    }

    @Override
    public boolean removeCache(@NonNull String key) {
        Objects.requireNonNull(key, "缓存key不能为空");

        return redisClientService.delete(key);
    }

    @Override
    public void removeCache(@NonNull Collection<String> keys) {
        Objects.requireNonNull(keys, "缓存keys不能为空");
        redisClientService.delete(new ArrayList<>(keys));
    }

    @Override
    public void removeAllCache(@NonNull String prefix) {
        Objects.requireNonNull(prefix, "缓存前缀不能为空");
        redisClientService.deleteByPrefix(prefix);
    }

    @Override
    public Set<String> getKeys(String keyPrefix) {
        Objects.requireNonNull(keyPrefix, "缓存前缀不能为空");
        return redisClientService.keys(keyPrefix);
    }
}
