package com.example.demo.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.example.demo.PkFinishDelayMessage;
import com.google.common.collect.Maps;
import com.thinker.cloud.core.model.Result;
import com.thinker.cloud.core.model.entity.SuperEntity;
import com.thinker.cloud.redis.cache.fast.FastRedisService;
import com.thinker.cloud.redis.delayqueue.redisson.RedissonDelayQueueHolder;
import com.thinker.cloud.redis.lock.annotation.DistributedLock;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 **/
@Slf4j
@Tag(name = "测试接口", description = "测试接口")
@RestController
@AllArgsConstructor
public class TestController {

    private final FastRedisService fastRedisService;
    private final RedissonDelayQueueHolder redissonDelayQueueHolder;

    @DistributedLock(key = "#entity.id", waitTime = 3, leaseTime = 3)
    @PostMapping(value = "test")
    @Operation(summary = "测试接口", description = "测试接口")
    public Result<SuperEntity> test(@RequestBody SuperEntity entity) {
        ThreadUtil.sleep(2000);
        return Result.buildSuccess(entity);
    }

    @GetMapping(value = "test1")
    @Operation(summary = "测试Redisson延迟队列")
    public Result<Void> test1() {
        Map<Long, String> map = Maps.newHashMap();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 10; i < 50; i++) {
            PkFinishDelayMessage message = new PkFinishDelayMessage();
            message.setPkId(RandomUtil.randomLong());
            long delay = BigDecimal.valueOf(i * 0.7).longValue();
            LocalDateTime endTime = now.plusSeconds(delay);
            map.put(message.getPkId(), DateUtil.format(endTime, DatePattern.NORM_DATETIME_MS_PATTERN));
            redissonDelayQueueHolder.addTask("PkFinishQueue", message, delay, TimeUnit.SECONDS);
        }

        String start = DateUtil.format(now, DatePattern.NORM_DATETIME_MS_PATTERN);
        map.forEach((key, value) -> System.out.println(StrUtil.format("pkId:{}, start:{}, end:{}", key, start, value)));
        return Result.buildSuccess();
    }

    @PostMapping(value = "setCache")
    @Operation(summary = "测试接口", description = "测试接口")
    public Result<Void> setCache(@RequestParam String key, @RequestBody SuperEntity entity) {
        fastRedisService.setCache(key, entity);
        return Result.buildSuccess();
    }

    @GetMapping(value = "getCache")
    @Operation(summary = "测试接口", description = "测试接口")
    public Result<SuperEntity> getCache(@RequestParam String key) {
        SuperEntity entity = fastRedisService.getCache(key);
        return Result.buildSuccess(entity);
    }
}
