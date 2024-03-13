package com.thinker.cloud.redis.lock.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 锁类型
 *
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum LockType {
    /**
     * 可重入锁
     */
    Reentrant,
    /**
     * 公平锁
     */
    Fair,
    /**
     * 读锁
     */
    Read,
    /**
     * 写锁
     */
    Write;
}
