package com.thinker.cloud.redis.lock.handler.release;


import com.thinker.cloud.redis.lock.model.LockInfo;

/**
 * 释放锁超时的处理逻辑接口
 *
 * @author admin
 */
public interface ReleaseTimeoutHandler {

    /**
     * 释放锁超时处理
     *
     * @param lockInfo lockInfo
     */
    void handle(LockInfo lockInfo);
}
