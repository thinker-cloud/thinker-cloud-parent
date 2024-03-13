package com.thinker.cloud.redis.idempotent.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.thinker.cloud.core.exception.IdempotentException;
import com.thinker.cloud.redis.cache.generator.CacheKeyGenerator;
import com.thinker.cloud.redis.idempotent.annotation.Idempotent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The Idempotent Aspect
 *
 * @author admin
 */
@Slf4j
@Aspect
public class IdempotentAspect {

    private final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    private static final String CACHE_KEY = "idempotent:";
    private static final String KEY = "key";
    private static final String DEL_KEY = "delKey";

    @Resource
    private RedissonClient redisson;

    @Resource
    private CacheKeyGenerator cacheKeyGenerator;

    @Before(value = "@annotation(idempotent)")
    public void beforePointCut(JoinPoint joinPoint, Idempotent idempotent) {
        String cacheKey = CACHE_KEY + idempotent.prefix();
        // 若没有配置 幂等 标识编号，则使用 url + 参数列表作为区分
        String key = cacheKeyGenerator.generator(joinPoint, cacheKey, idempotent.key());

        long expireTime = idempotent.expireTime();
        String info = idempotent.info();
        TimeUnit timeUnit = idempotent.timeUnit();
        boolean delKey = idempotent.delKey();

        // do not need check null
        RMapCache<String, Object> rMapCache = redisson.getMapCache(cacheKey);
        String value = DateUtil.now();
        Object v1;
        if (null != rMapCache.get(key)) {
            // had stored
            throw new IdempotentException("[idempotent]:" + info);
        }
        synchronized (this) {
            v1 = rMapCache.putIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
            if (null != v1) {
                throw new IdempotentException("[idempotent]:" + info);
            } else {
                log.info("[idempotent]:has stored key={},value={},expireTime={}{},now={}", key, value, expireTime,
                        timeUnit, LocalDateTime.now());
            }
        }

        Map<String, Object> localMap = threadLocal.get();
        Map<String, Object> map = MapUtil.isEmpty(localMap) ? new HashMap<>(4) : localMap;
        map.put(KEY, key);
        map.put(DEL_KEY, delKey);
        threadLocal.set(map);

    }

    @After(value = "@annotation(com.thinker.cloud.redis.idempotent.annotation.Idempotent)")
    public void afterPointCut() {
        Map<String, Object> map = threadLocal.get();
        if (CollectionUtils.isEmpty(map)) {
            return;
        }

        RMapCache<Object, Object> mapCache = redisson.getMapCache(CACHE_KEY);
        if (mapCache.isEmpty()) {
            return;
        }

        String key = map.get(KEY).toString();
        boolean delKey = (boolean) map.get(DEL_KEY);

        if (delKey) {
            mapCache.fastRemove(key);
            log.info("[idempotent]:has removed key={}", key);
        }
        threadLocal.remove();
    }

}
