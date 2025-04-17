package com.thinker.cloud.redis.cache.fast;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.thinker.cloud.common.cache.base.IFastStringCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
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
@Slf4j
public class FastStringRedisCache extends AbstractRedisCache<String> implements IFastStringCache {

    public FastStringRedisCache(StringRedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public <T, R extends T> T getCache(@NonNull String key,
                                       @NonNull Supplier<T> missedSupplier,
                                       @NonNull Class<R> entityClass) {
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(missedSupplier, "数据获取过程不能为空");
        Objects.requireNonNull(entityClass, "指定返回类型不能为空");

        String value = super.getCache(key);
        if (StrUtil.isBlank(value)) {
            return missedSupplier.get();
        }
        return JSONObject.parseObject(value, entityClass);
    }

    @Override
    public <T, R extends T> T getCache(@NonNull String key,
                                       @NonNull Supplier<T> missedSupplier,
                                       @NonNull TypeReference<R> type) {
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(missedSupplier, "数据获取过程不能为空");
        Objects.requireNonNull(type, "数据转换类型不能为空");

        String value = super.getCache(key);
        if (StrUtil.isBlank(value)) {
            return missedSupplier.get();
        }
        return JSONObject.parseObject(value, type);
    }

    @Override
    public <T, R extends T> T getCache(@NonNull String key,
                                       @NonNull Supplier<T> missedSupplier,
                                       @NonNull TimeUnit timeUnit, long expire,
                                       @NonNull Class<R> entityClass) {
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(missedSupplier, "数据获取过程不能为空");
        Objects.requireNonNull(timeUnit, "缓存单位不能为空");
        Objects.requireNonNull(entityClass, "指定返回类型不能为空");

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
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(missedSupplier, "数据获取过程不能为空");
        Objects.requireNonNull(entityClass, "指定返回类型不能为空");

        String value = super.getCache(key);
        if (StrUtil.isBlank(value)) {
            return missedSupplier.get();
        }
        return Lists.newArrayList(JSONArray.parseArray(value, entityClass));
    }

    @Override
    public <T, R extends T> List<T> getCaches(@NonNull String key,
                                              @NonNull Supplier<List<T>> missedSupplier,
                                              @NonNull TimeUnit timeUnit, long expire,
                                              @NonNull Class<R> entityClass) {
        Objects.requireNonNull(key, "缓存key不能为空");
        Objects.requireNonNull(missedSupplier, "数据获取过程不能为空");
        Objects.requireNonNull(timeUnit, "缓存单位不能为空");
        Objects.requireNonNull(entityClass, "指定返回类型不能为空");

        String value = super.getCache(key);
        if (StrUtil.isNotBlank(value)) {
            return Lists.newArrayList(JSONArray.parseArray(value, entityClass));
        }

        List<T> list = missedSupplier.get();
        if (CollUtil.isNotEmpty(list)) {
            super.setCache(key, JSONArray.toJSONString(list), expire, timeUnit);
        }
        return list;
    }
}
