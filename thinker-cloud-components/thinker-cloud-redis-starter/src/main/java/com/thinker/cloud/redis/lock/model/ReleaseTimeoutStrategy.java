package com.thinker.cloud.redis.lock.model;


import com.thinker.cloud.redis.lock.exception.LockTimeoutException;
import com.thinker.cloud.redis.lock.handler.release.ReleaseTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 释放锁超时策略
 *
 * @author admin
 */
@Slf4j
public enum ReleaseTimeoutStrategy implements ReleaseTimeoutHandler {

    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo) {
            // do nothing
        }
    },
    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo) {
            String errorMsg = String.format("Found Lock(%s) already been released while lock lease time is %d s", lockInfo.getName(), lockInfo.getLeaseTime());
            log.error("释放分布式锁异常，key:{}，error:{}", lockInfo.getName(), errorMsg);
            throw new LockTimeoutException("服务器繁忙，请稍后再试");
        }
    }
}
