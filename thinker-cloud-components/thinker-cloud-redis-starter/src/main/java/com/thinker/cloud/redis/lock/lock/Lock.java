package com.thinker.cloud.redis.lock.lock;

/**
 * 锁抽象接口
 *
 * @author admin
 */
public interface Lock {

    /**
     * 获取锁
     *
     * @return boolean
     */
    boolean acquire();

    /**
     * 释放锁
     *
     * @return boolean
     */
    boolean release();
}

