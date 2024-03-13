package com.thinker.cloud.redis.lock.handler.lock;

import com.thinker.cloud.redis.lock.lock.Lock;
import com.thinker.cloud.redis.lock.model.LockInfo;
import org.aspectj.lang.JoinPoint;

/**
 * 获取锁超时的处理逻辑接口
 *
 * @author admin
 */
public interface LockTimeoutHandler {

    /**
     * 超时处理
     *
     * @param lockInfo  lockInfo
     * @param lock      lock
     * @param joinPoint joinPoint
     */
    void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint);
}
