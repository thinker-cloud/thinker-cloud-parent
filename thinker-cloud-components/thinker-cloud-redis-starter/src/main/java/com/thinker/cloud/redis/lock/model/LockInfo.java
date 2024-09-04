package com.thinker.cloud.redis.lock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * 锁基本信息
 *
 * @author admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LockInfo {

    /**
     * 锁类型
     */
    private LockType type;

    /**
     * 锁名称
     */
    private String name;

    /**
     * 等待时长
     */
    private long waitTime;

    /**
     * 占用时长
     */
    private long leaseTime;

    /**
     * 时长单位
     */
    private TimeUnit timeUnit;
}
