package com.thinker.cloud.redis.cache.fast;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.thinker.cloud.core.cache.fast.IDyKey;
import com.thinker.cloud.core.cache.fast.IFastCache;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * redis 增强实现
 *
 * @author admin
 */
public class FastStringRedisCache implements IFastCache {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void cache(String cacheKey, Object value, TimeUnit timeUnit, int expire) {
        String json = JSONObject.toJSONString(value);
        stringRedisTemplate.opsForValue().set(cacheKey, json, expire, timeUnit);
    }

    @Override
    public <T, R extends T> T cache(String cacheKey, Supplier<T> missedSupplier, Class<R> entityClass) {
        try {
            String objectJson = stringRedisTemplate.opsForValue().get(cacheKey);
            if (objectJson == null) {
                return missedSupplier.get();
            }
            return JSONObject.parseObject(objectJson, entityClass);
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> T cache(String cacheKey, Supplier<T> missedSupplier, TypeReference<R> type) {
        try {
            String objectJson = stringRedisTemplate.opsForValue().get(cacheKey);
            if (objectJson == null) {
                return missedSupplier.get();
            }
            return JSONObject.parseObject(objectJson, type);
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> T cache(String cacheKey, Supplier<T> missedSupplier, TimeUnit timeUnit, int expire, Class<R> entityClass) {
        try {
            String objectJson = stringRedisTemplate.opsForValue().get(cacheKey);
            if (objectJson == null) {
                T object = missedSupplier.get();
                String value = JSONObject.toJSONString(object);
                stringRedisTemplate.opsForValue().set(cacheKey, value, expire, timeUnit);
                return object;
            }
            return JSONObject.parseObject(objectJson, entityClass);
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> T cache(String cacheKeyPrefix, IDyKey dyKey, Supplier<T> missedSupplier
            , TimeUnit timeUnit, int expire, Class<R> entityClass) {
        String finalLastKey = cacheKeyPrefix + ":" + dyKey;
        return cache(finalLastKey, missedSupplier, timeUnit, expire, entityClass);
    }

    @Override
    public <T, R extends T> List<T> cacheList(String cacheKey, Supplier<List<T>> missedSupplier, Class<R> entityClass) {
        try {
            String objectJson = stringRedisTemplate.opsForValue().get(cacheKey);
            if (objectJson == null) {
                return missedSupplier.get();
            }
            return Lists.newArrayList(JSONArray.parseArray(objectJson, entityClass));
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> List<T> cacheList(String cacheKey, Supplier<List<T>> missedSupplier, TimeUnit timeUnit, int expire, Class<R> entityClass) {
        try {
            String objectJson = stringRedisTemplate.opsForValue().get(cacheKey);
            if (objectJson == null) {
                List<T> list = missedSupplier.get();
                String value = JSONArray.toJSONString(list);
                stringRedisTemplate.opsForValue().set(cacheKey, value, expire, timeUnit);
                return list;
            }
            return Lists.newArrayList(JSONArray.parseArray(objectJson, entityClass));
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public <T, R extends T> List<T> cacheList(String cacheKeyPrefix, IDyKey dyKey, Supplier<List<T>> missedSupplier
            , TimeUnit timeUnit, int expire, Class<R> entityClass) {
        String finalLastKey = cacheKeyPrefix + ":" + dyKey.getKey();
        return cacheList(finalLastKey, missedSupplier, timeUnit, expire, entityClass);
    }

    @Override
    public Set<String> getKeys(String prefixKey, Supplier<Set<String>> missedSupplier) {
        try {
            Set<String> keys = stringRedisTemplate.keys(prefixKey);
            if (CollectionUtil.isEmpty(keys)) {
                return missedSupplier.get();
            }
            return keys;
        } catch (Exception e) {
            return missedSupplier.get();
        }
    }

    @Override
    public void delete(String cacheKey) {
        stringRedisTemplate.delete(cacheKey);
    }
}
