package com.thinker.cloud.redis.cache.fast;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.thinker.cloud.core.cache.fast.IDyKey;
import com.thinker.cloud.core.cache.fast.IFastCache;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * redis 增强实现
 *
 * @author admin
 */
public class FastStringRedisCache extends AbstractRedisCache implements IFastCache {

    @Override
    public void setCache(@NonNull String key, @NonNull Object value) {
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(value, "缓存对象不能为空");

        String cacheValue = value instanceof String ? (String) value : JSON.toJSONString(value);
        super.setCache(key, cacheValue);
    }

    @Override
    public void setCache(@NonNull String key, @NonNull Object value, long timeout, @NonNull TimeUnit timeUnit) {
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(value, "缓存对象不能为空");
        Objects.requireNonNull(timeUnit, "缓存单位不能为空");

        String cacheValue = value instanceof String ? (String) value : JSON.toJSONString(value);
        super.setCache(key, cacheValue, timeout, timeUnit);
    }

    @Override
    public <T, R extends T> T getCache(String key, Supplier<T> missedSupplier, Class<R> entityClass) {
        try {
            String value = super.getCache(key);
            if (value == null) {
                return missedSupplier.get();
            }
            return JSONObject.parseObject(value, entityClass);
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> T getCache(String key, Supplier<T> missedSupplier, TypeReference<R> type) {
        try {
            String value = super.getCache(key);
            if (value == null) {
                return missedSupplier.get();
            }
            return JSONObject.parseObject(value, type);
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> T getCache(String key, Supplier<T> missedSupplier, TimeUnit timeUnit, int expire, Class<R> entityClass) {
        try {
            String value = super.getCache(key);
            if (value == null) {
                return missedSupplier.get();
            }
            return JSONObject.parseObject(value, entityClass);
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> T getCache(String keyPrefix, IDyKey dyKey, Supplier<T> missedSupplier
            , TimeUnit timeUnit, int expire, Class<R> entityClass) {
        String finalLastKey = keyPrefix + ":" + dyKey;
        return getCache(finalLastKey, missedSupplier, timeUnit, expire, entityClass);
    }

    @Override
    public <T, R extends T> List<T> getCacheList(String key, Supplier<List<T>> missedSupplier, Class<R> entityClass) {
        try {
            String value = super.getCache(key);
            if (value == null) {
                return missedSupplier.get();
            }
            return Lists.newArrayList(JSONArray.parseArray(value, entityClass));
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> List<T> getCacheList(String key, Supplier<List<T>> missedSupplier
            , TimeUnit timeUnit, int expire, Class<R> entityClass) {
        try {
            String value = super.getCache(key);
            if (value == null) {
                List<T> list = missedSupplier.get();
                String cacheValue = JSON.toJSONString(list);
                super.setCache(key, cacheValue, expire, timeUnit);
                return list;
            }
            return Lists.newArrayList(JSONArray.parseArray(value, entityClass));
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> List<T> getCacheList(String keyPrefix, IDyKey dyKey, Supplier<List<T>> missedSupplier
            , TimeUnit timeUnit, int expire, Class<R> entityClass) {
        String finalLastKey = keyPrefix + ":" + dyKey.getKey();
        return getCacheList(finalLastKey, missedSupplier, timeUnit, expire, entityClass);
    }

    @Override
    public Set<String> getKeys(String keyPrefix, Supplier<Set<String>> missedSupplier) {
        try {
            Set<String> keys = redisClientService.keys(keyPrefix);
            if (CollectionUtil.isEmpty(keys)) {
                return missedSupplier.get();
            }
            return keys;
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }
}
