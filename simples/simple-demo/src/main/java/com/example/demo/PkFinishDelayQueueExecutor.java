package com.example.demo;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.thinker.cloud.redis.delayqueue.redisson.AbstractRedissonDelayQueueExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 该执行器实现类会在服务启动后，会添加到`DelayQueueExecutorFactory`中进行管理。
 * <p>
 * 不需要在单独调用，只需要实现execute()的业务即可
 *
 * @author admin
 */
@Slf4j
@Component
public class PkFinishDelayQueueExecutor extends AbstractRedissonDelayQueueExecutor<PkFinishDelayMessage> {

    @Override
    public String queueName() {
        return "PkFinishQueue";
    }

    @Override
    public void execute(PkFinishDelayMessage delayMessage) {
        // TODO 处理业务逻辑
        Long pkId = delayMessage.getPkId();
        log.info("触发延迟任务：pkId:{}，time:{}", pkId, DateUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_MS_PATTERN));
    }
}
