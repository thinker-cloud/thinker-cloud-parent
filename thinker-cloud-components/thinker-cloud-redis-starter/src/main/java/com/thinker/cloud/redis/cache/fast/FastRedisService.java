package com.thinker.cloud.redis.cache.fast;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * spring redis 工具类
 *
 * @author admin
 **/
@Slf4j
public class FastRedisService extends AbstractRedisCache<Object> {

    public FastRedisService(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }
}
