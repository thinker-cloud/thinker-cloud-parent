package com.thinker.cloud.redis.cache.service;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis基础服务
 *
 * @author admin
 **/
@SuppressWarnings({"rawtypes", "unchecked"})
public class RedisClientService {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 设置缓存
     *
     * @param key   缓存key
     * @param value 缓存对象
     */
    public void setCache(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存
     *
     * @param key      缓存key
     * @param value    缓存对象
     * @param timeout  缓存时长
     * @param timeUnit 缓存单位
     */
    public void setCache(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 批量缓存数据
     *
     * @param cacheMap key、value 缓存映射
     */
    public <T> void multiSetCache(Map<String, T> cacheMap) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        operations.multiSet(cacheMap);
    }

    /**
     * 获得缓存的基本对象
     *
     * @param key 缓存key
     * @return 缓存键值对应的数据
     */
    public <T> T getCache(String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 获得缓存的list对象
     *
     * @param keys 缓存key
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCaches(Collection<String> keys) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        return operations.multiGet(keys);
    }

    /**
     * 设置有效时间
     *
     * @param key     缓存key
     * @param timeout 超时时间
     * @return boolean
     */
    public boolean expire(String key, long timeout) {
        return this.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     缓存key
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return boolean
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Optional.ofNullable(redisTemplate.expire(key, timeout, unit)).orElse(false);
    }

    /**
     * 批量设置有效时间
     *
     * @param keys    缓存key
     * @param timeout 超时时间
     * @param unit    时间单位
     */
    public void expire(Set<String> keys, long timeout, TimeUnit unit) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        long rawTimeout = TimeoutUtils.toMillis(timeout, unit);
        redisTemplate.execute(connection -> {
            for (String key : keys) {
                byte[] rawKey = keySerializer.serialize(key);
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

    /**
     * 判断缓存是否存在
     *
     * @param key 键
     * @return boolean
     */
    public boolean hasKey(String key) {
        return Optional.ofNullable(redisTemplate.hasKey(key)).orElse(false);
    }

    /**
     * 删除缓存
     *
     * @param key key
     * @return boolean
     */
    public boolean delete(String key) {
        return Optional.ofNullable(redisTemplate.delete(key)).orElse(true);
    }

    /**
     * 批量删除缓存
     *
     * @param keys keys
     */
    public void delete(List<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 根据前缀删除缓存
     *
     * @param prefix prefix
     */
    public void deleteByPrefix(String prefix) {
        Set<String> keys = this.keys(prefix);
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 根据前缀获取所有缓存keys
     *
     * @param keyPrefix keyPrefix
     * @return Set<String>
     */
    public Set<String> keys(String keyPrefix) {
        return redisTemplate.keys(keyPrefix + "*");
    }
}
