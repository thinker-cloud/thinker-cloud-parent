package com.thinker.cloud.redis.cache.fast;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Lists;
import com.thinker.cloud.common.cache.base.ICache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

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
@AllArgsConstructor
public abstract class AbstractRedisCache<V> implements ICache<V> {

    protected final RedisTemplate<String, V> redisTemplate;

    /**
     * 字符串 key序列化对象
     */
    private static final StringRedisSerializer STRING_KEY_SERIALIZER = new StringRedisSerializer();

    @Override
    public V setCache(@NonNull String key, @NonNull V value) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(value, "缓存对象不能为空");

        redisTemplate.opsForValue().set(key, value);
        return value;
    }

    @Override
    public V setCache(@NonNull String key, @NonNull V value, long timeout, @NonNull TimeUnit timeUnit) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(value, "缓存对象不能为空");
        Assert.notNull(timeUnit, "缓存单位不能为空");

        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        return value;
    }

    @Override
    public boolean hasCache(@NonNull String key) {
        Assert.hasText(key, "缓存key不能为空");

        return redisTemplate.hasKey(key);
    }

    @Override
    public V getCache(@NonNull String key) {
        Assert.hasText(key, "缓存key不能为空");

        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public <T> T getCache(@NonNull String key, @NonNull Function<V, T> dataConvertor) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(dataConvertor, "缓存数据转换器不能为空");

        V value = this.getCache(key);
        return Optional.ofNullable(value).map(dataConvertor).orElse(null);
    }

    @Override
    public List<V> getCaches(@NonNull Collection<String> keys) {
        Assert.notEmpty(keys, "缓存key列表不能为空");

        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public List<V> getCaches(@NonNull Collection<String> keys, @NonNull Predicate<V> dataFilter) {
        Assert.notEmpty(keys, "缓存key列表不能为空");
        Assert.notNull(dataFilter, "缓存数据过滤器不能为空");

        List<V> caches = this.getCaches(keys);
        if (Objects.isNull(caches)) {
            return null;
        }

        List<V> list = caches.stream()
                .filter(Objects::nonNull)
                .filter(dataFilter)
                .collect(Collectors.toList());

        return list.isEmpty() ? null : list;
    }

    @Override
    public <T> List<T> getCaches(@NonNull Collection<String> keys, @NonNull Function<V, T> dataConvertor, @NonNull Predicate<T> dataFilter) {
        Assert.notEmpty(keys, "缓存key列表不能为空");
        Assert.notNull(dataConvertor, "缓存数据转换器不能为空");
        Assert.notNull(dataFilter, "缓存数据过滤器不能为空");

        List<T> allCaches;

        // 无需分批次获取
        if (keys.size() <= BATCH_CACHE_MAX_SIZE) {
            List<V> caches = this.getCaches(keys);
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
            List<List<String>> sKeys = CollectionUtil.split(keys, BATCH_CACHE_MAX_SIZE);
            allCaches = sKeys.stream()
                    .map(this::getCaches)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .map(dataConvertor)
                    .filter(dataFilter)
                    .collect(Collectors.toList());
        }

        return allCaches.isEmpty() ? null : allCaches;
    }

    @Override
    public void multiSetCache(@NonNull Map<String, V> cacheMap, int batchSetCacheMaxSize) {
        Assert.notEmpty(cacheMap, "缓存Map不能为空");

        // 批量缓存最大长度
        if (batchSetCacheMaxSize < 1) {
            batchSetCacheMaxSize = BATCH_CACHE_MAX_SIZE;
        }

        // 如果缓存数量小于支持批量最大数量，则直接缓存
        if (cacheMap.size() <= batchSetCacheMaxSize) {
            redisTemplate.opsForValue().multiSet(cacheMap);
            return;
        }

        // 否则将数据拆分为 batchSetCacheMax 一组，分批次缓存
        List<Map.Entry<String, V>> allCache = Lists.newArrayList(cacheMap.entrySet());
        List<List<Map.Entry<String, V>>> smallLists = ListUtil.split(allCache, batchSetCacheMaxSize);

        // 防止批量缓存数据太多，考虑redis负载过重、内存、网络开销等情况，分批次设置redis缓存
        for (List<Map.Entry<String, V>> smallList : smallLists) {
            Map<String, V> multiSetCache = smallList.stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            redisTemplate.opsForValue().multiSet(multiSetCache);
        }
    }

    @Override
    public void multiSetCache(@NonNull Map<String, V> cacheMap, long timeout, @NonNull TimeUnit timeUnit, int batchSetCacheMaxSize) {
        Assert.notEmpty(cacheMap, "缓存Map不能为空");
        Assert.notNull(timeUnit, "缓存单位不能为空");

        // 批量缓存最大长度
        if (batchSetCacheMaxSize < 1) {
            batchSetCacheMaxSize = BATCH_CACHE_MAX_SIZE;
        }

        // 批量设置缓存
        this.multiSetCache(cacheMap, batchSetCacheMaxSize);

        // 如果数量小于支持批量最大数量，则直接设置过期时间
        Set<String> keys = cacheMap.keySet();
        if (keys.size() <= batchSetCacheMaxSize) {
            this.expire(keys, timeout, timeUnit);
            return;
        }

        // 否则将数据拆分为 batchSetCacheMax 一组，分批次设置
        List<List<String>> allKeys = ListUtil.split(Lists.newArrayList(keys), batchSetCacheMaxSize);
        for (List<String> sKeys : allKeys) {
            this.expire(new HashSet<>(sKeys), timeout, timeUnit);
        }
    }

    @Override
    public boolean removeCache(@NonNull String key) {
        Assert.hasText(key, "缓存key不能为空");

        return redisTemplate.delete(key);
    }

    @Override
    public void removeCache(@NonNull Collection<String> keys) {
        Assert.notEmpty(keys, "缓存keys不能为空");

        redisTemplate.delete(new ArrayList<>(keys));
    }

    @Override
    public void removeAllCache(@NonNull String prefix) {
        Assert.hasText(prefix, "缓存前缀不能为空");

        redisTemplate.delete(this.getKeys(prefix));
    }

    @Override
    public Set<String> getKeys(@NonNull String prefix) {
        Assert.hasText(prefix, "缓存前缀不能为空");

        return redisTemplate.keys(prefix + "*");
    }

    /**
     * 批量设置有效时间
     *
     * @param keys    Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     */
    private void expire(Set<String> keys, long timeout, TimeUnit unit) {
        long rawTimeout = TimeoutUtils.toMillis(timeout, unit);
        redisTemplate.execute(connection -> {
            for (String key : keys) {
                byte[] rawKey = STRING_KEY_SERIALIZER.serialize(key);

                assert rawKey != null;

                try {
                    connection.keyCommands().pExpire(rawKey, rawTimeout);
                } catch (Exception e) {
                    connection.keyCommands().expire(rawKey, TimeoutUtils.toSeconds(timeout, unit));
                }
            }
            return true;
        }, true);
    }
}
