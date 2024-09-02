package com.thinker.cloud.redis.cache.fast;

import com.alibaba.fastjson.TypeReference;
import com.thinker.cloud.core.cache.fast.IDyKey;
import com.thinker.cloud.core.cache.fast.IFastCache;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * spring redis 工具类
 *
 * @author admin
 **/
public class FastRedisService extends AbstractRedisCache implements IFastCache {

    @Override
    public <T, R extends T> T getCache(String key, Supplier<T> missedSupplier, Class<R> entityClass) {
        return null;
    }

    @Override
    public <T, R extends T> T getCache(String cacheKey, Supplier<T> missedSupplier, TypeReference<R> type) {
        return null;
    }

    @Override
    public <T, R extends T> T getCache(String cacheKey, Supplier<T> missedSupplier, TimeUnit timeUnit, int expire, Class<R> entityClass) {
        return null;
    }

    @Override
    public <T, R extends T> T getCache(String cacheKeyPrefix, IDyKey dyKey, Supplier<T> missedSupplier, TimeUnit timeUnit, int expire, Class<R> entityClass) {
        return null;
    }

    @Override
    public <T, R extends T> List<T> getCacheList(String cacheKey, Supplier<List<T>> missedSupplier, Class<R> entityClass) {
        return List.of();
    }

    @Override
    public <T, R extends T> List<T> getCacheList(String cacheKey, Supplier<List<T>> missedSupplier, TimeUnit timeUnit, int expire, Class<R> entityClass) {
        return List.of();
    }

    @Override
    public <T, R extends T> List<T> getCacheList(String cacheKeyPrefix, IDyKey dyKey, Supplier<List<T>> missedSupplier, TimeUnit timeUnit, int expire, Class<R> entityClass) {
        return List.of();
    }

    @Override
    public Set<String> getKeys(String keyPrefix, Supplier<Set<String>> missedSupplier) {
        return Set.of();
    }
}
