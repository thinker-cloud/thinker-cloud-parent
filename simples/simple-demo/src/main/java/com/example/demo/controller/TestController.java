package com.example.demo.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.example.demo.PkFinishDelayMessage;
import com.google.common.collect.Maps;
import com.thinker.cloud.core.model.Result;
import com.thinker.cloud.core.model.entity.SuperEntity;
import com.thinker.cloud.redis.delayqueue.redisson.RedissonDelayQueueHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
public class TestController {

    @Resource
    private RedissonDelayQueueHolder redissonDelayQueueHolder;

    @PostMapping(value = "test")
    @Operation(summary = "测试接口", description = "测试接口")
    public Result<SuperEntity> test(@RequestBody SuperEntity entity) {
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
}
