package com.thinker.cloud.redis.cache.fast;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson2.JSON;
import com.thinker.cloud.common.cache.base.IFastStringCache;
import com.thinker.cloud.common.utils.MyJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * redis 增强实现
 *
 * @author admin
 */
@Slf4j
public class FastStringRedisCache extends AbstractRedisCache<String> implements IFastStringCache {

    public FastStringRedisCache(StringRedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public <T> void setCacheObj(@NonNull String key, @NonNull T value) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(value, "缓存对象不能为空");

        if (value instanceof String str) {
            super.setCache(key, str);
        } else {
            super.setCache(key, JSON.toJSONString(value));
        }
    }

    @Override
    public <T> void setCacheObj(@NonNull String key, @NonNull T value, long timeout, @NonNull TimeUnit timeUnit) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(value, "缓存对象不能为空");
        Assert.notNull(timeUnit, "缓存单位不能为空");

        if (value instanceof String str) {
            super.setCache(key, str, timeout, timeUnit);
        } else {
            super.setCache(key, JSON.toJSONString(value), timeout, timeUnit);
        }
    }

    @Override
    public <T, R extends T> T getCacheObj(@NonNull String key,
                                          @NonNull Supplier<T> missedSupplier,
                                          @NonNull Class<R> entityClass) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(missedSupplier, "数据获取过程不能为空");
        Assert.notNull(entityClass, "指定返回类型不能为空");

        String value = super.getCache(key);
        if (StrUtil.isBlank(value)) {
            return missedSupplier.get();
        }
        return JSONObject.parseObject(value, entityClass);
    }

    @Override
    public <T, R extends T> T getCacheObj(@NonNull String key,
                                          @NonNull Supplier<T> missedSupplier,
                                          @NonNull TypeReference<R> type) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(missedSupplier, "数据获取过程不能为空");
        Assert.notNull(type, "数据转换类型不能为空");

        String value = super.getCache(key);
        if (StrUtil.isBlank(value)) {
            return missedSupplier.get();
        }
        return JSONObject.parseObject(value, type);
    }

    @Override
    public <T, R extends T> T getCacheObj(@NonNull String key,
                                          @NonNull Supplier<T> missedSupplier,
                                          @NonNull TimeUnit timeUnit, long expire,
                                          @NonNull Class<R> entityClass) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(missedSupplier, "数据获取过程不能为空");
        Assert.notNull(timeUnit, "缓存单位不能为空");
        Assert.notNull(entityClass, "指定返回类型不能为空");

        String value = super.getCache(key);
        if (StrUtil.isNotBlank(value)) {
            return JSONObject.parseObject(value, entityClass);
        }

        T cacheValue = missedSupplier.get();
        if (Objects.nonNull(cacheValue)) {
            super.setCache(key, JSON.toJSONString(cacheValue), expire, timeUnit);
        }
        return cacheValue;
    }

    @Override
    public <T, R extends T> List<T> getCaches(@NonNull String key,
                                              @NonNull Supplier<List<T>> missedSupplier,
                                              @NonNull Class<R> entityClass) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(missedSupplier, "数据获取过程不能为空");
        Assert.notNull(entityClass, "指定返回类型不能为空");

        String value = super.getCache(key);
        if (StrUtil.isBlank(value)) {
            return missedSupplier.get();
        }

        return MyJsonUtil.toJavaList(value, entityClass);
    }

    @Override
    public <T, R extends T> List<T> getCaches(@NonNull String key,
                                              @NonNull Supplier<List<T>> missedSupplier,
                                              @NonNull TimeUnit timeUnit, long expire,
                                              @NonNull Class<R> entityClass) {
        Assert.hasText(key, "缓存key不能为空");
        Assert.notNull(missedSupplier, "数据获取过程不能为空");
        Assert.notNull(timeUnit, "缓存单位不能为空");
        Assert.notNull(entityClass, "指定返回类型不能为空");

        String value = super.getCache(key);
        if (StrUtil.isNotBlank(value)) {
            return MyJsonUtil.toJavaList(value, entityClass);
        }

        List<T> list = missedSupplier.get();
        if (CollUtil.isNotEmpty(list)) {
            super.setCache(key, JSONArray.toJSONString(list), expire, timeUnit);
        }
        return list;
    }

    @Override
    public <T, R extends T> List<T> getCaches(@NonNull Collection<String> keys,
                                              @NonNull Supplier<List<T>> missedSupplier,
                                              @NonNull Class<R> entityClass) {
        Assert.notEmpty(keys, "缓存key不能为空");
        Assert.notNull(missedSupplier, "数据获取过程不能为空");
        Assert.notNull(entityClass, "指定返回类型不能为空");

        List<String> caches = super.getCaches(keys);
        if (Objects.isNull(caches)) {
            return missedSupplier.get();
        }

        return MyJsonUtil.toJavaList(caches, entityClass);
    }
}
