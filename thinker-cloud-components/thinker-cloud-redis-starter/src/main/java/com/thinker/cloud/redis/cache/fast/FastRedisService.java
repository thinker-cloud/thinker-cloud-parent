package com.thinker.cloud.redis.cache.fast;

import com.thinker.cloud.common.cache.base.IFastCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * spring redis 工具类
 *
 * @author admin
 **/
@Slf4j
@SuppressWarnings("unchecked")
public class FastRedisService extends AbstractRedisCache<Object> implements IFastCache {

    public FastRedisService(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public <T> T getCacheObj(@NonNull String key) {
        Assert.hasText(key, "缓存key不能为空");

        return (T) super.getCache(key);
    }

    @Override
    public <T> T getCacheObj(@NonNull String key, @NonNull Supplier<T> missedSupplier) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(missedSupplier, "数据获取过程不能为空");

        Object value = super.getCache(key);
        if (Objects.isNull(value)) {
            return missedSupplier.get();
        }
        return (T) value;
    }

    @Override
    public <T> T getCacheObj(@NonNull String key, @NonNull Supplier<T> missedSupplier, @NonNull TimeUnit timeUnit, long expire) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(missedSupplier, "数据获取过程不能为空");
        Assert.notNull(timeUnit, "缓存单位不能为空");

        Object value = super.getCache(key);
        if (Objects.nonNull(value)) {
            return (T) value;
        }

        T cache = missedSupplier.get();
        if (Objects.nonNull(cache)) {
            super.setCache(key, cache, expire, timeUnit);
        }
        return cache;
    }

    @Override
    public <T> List<T> getCaches(@NonNull Collection<String> keys, @NonNull Supplier<List<T>> missedSupplier) {
        Assert.notEmpty(keys, "缓存key列表不能为空");
        Assert.notNull(missedSupplier, "数据获取过程不能为空");

        List<Object> caches = super.getCaches(keys);
        if (Objects.isNull(caches)) {
            return missedSupplier.get();
        }

        return (List<T>) caches;
    }
}
