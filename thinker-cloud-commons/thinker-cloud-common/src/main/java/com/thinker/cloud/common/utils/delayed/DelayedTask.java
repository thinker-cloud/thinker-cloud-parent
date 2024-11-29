package com.thinker.cloud.common.utils.delayed;

import lombok.Data;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时任务
 *
 * @author admin
 */
@Data
public class DelayedTask<T> implements Delayed {
    /**
     * 引用
     */
    private T reference;

    /**
     * 到期时间
     */
    private long expireTime;

    public DelayedTask(T reference, Duration duration) {
        this.reference = reference;
        this.expireTime = System.currentTimeMillis() + duration.toMillis();
    }

    public DelayedTask(T reference, long delay, TimeUnit timeUnit) {
        this.reference = reference;
        this.expireTime = System.currentTimeMillis() + timeUnit.toMillis(delay);
    }

    public DelayedTask(T reference, LocalDateTime expireTime) {
        this.reference = reference;
        this.expireTime = expireTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public long getDelay(@NonNull TimeUnit unit) {
        return this.expireTime - System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NonNull Delayed delayed) {
        return Long.compare(this.expireTime, ((DelayedTask<?>) delayed).getExpireTime());
    }
}
