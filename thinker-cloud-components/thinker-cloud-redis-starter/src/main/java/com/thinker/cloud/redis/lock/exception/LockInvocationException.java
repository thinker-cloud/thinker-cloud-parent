package com.thinker.cloud.redis.lock.exception;


import com.thinker.cloud.core.exception.LockException;

/**
 * 锁超时处理程序调用异常
 *
 * @author admin
 */
public class LockInvocationException extends LockException {

    public LockInvocationException(String message) {
        super(message);
    }

    public LockInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
