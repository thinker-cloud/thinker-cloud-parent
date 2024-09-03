package com.thinker.cloud.redis.cache.fast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.thinker.cloud.core.cache.fast.IDyKey;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * redis 增强实现
 *
 * @author admin
 */
public class FastStringRedisCache extends AbstractRedisCache implements IFastStringCache {

    @Override
    public void setCache(@NonNull String key, @NonNull Object value) {
        Objects.requireNonNull(value, "缓存对象不能为空");

        String cacheValue = value instanceof String ? (String) value : JSON.toJSONString(value);
        super.setCache(key, cacheValue);
    }

    @Override
    public void setCache(@NonNull String key, @NonNull Object value, long timeout, @NonNull TimeUnit timeUnit) {
        Objects.requireNonNull(value, "缓存对象不能为空");

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
                T cacheValue = missedSupplier.get();
                this.setCache(key, cacheValue, expire, timeUnit);
                return cacheValue;
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
        return this.getCache(finalLastKey, missedSupplier, timeUnit, expire, entityClass);
    }

    @Override
    public <T, R extends T> List<T> getCaches(String key, Supplier<List<T>> missedSupplier, Class<R> entityClass) {
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
    public <T, R extends T> List<T> getCaches(String key, Supplier<List<T>> missedSupplier
            , TimeUnit timeUnit, int expire, Class<R> entityClass) {
        try {
            String value = super.getCache(key);
            if (value == null) {
                List<T> list = missedSupplier.get();
                this.setCache(key, list, expire, timeUnit);
                return list;
            }
            return Lists.newArrayList(JSONArray.parseArray(value, entityClass));
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> List<T> getCaches(String keyPrefix, IDyKey dyKey, Supplier<List<T>> missedSupplier
            , TimeUnit timeUnit, int expire, Class<R> entityClass) {
        String finalLastKey = keyPrefix + ":" + dyKey.getKey();
        return this.getCaches(finalLastKey, missedSupplier, timeUnit, expire, entityClass);
    }
}
