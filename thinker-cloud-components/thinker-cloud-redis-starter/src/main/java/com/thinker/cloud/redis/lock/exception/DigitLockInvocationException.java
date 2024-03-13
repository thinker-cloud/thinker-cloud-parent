package com.thinker.cloud.redis.lock.exception;


import com.thinker.cloud.core.exception.AbstractException;

/**
 * 锁超时处理程序调用异常
 *
 * @author admin
 */
public class DigitLockInvocationException extends AbstractException {

    public DigitLockInvocationException(String message) {
        super(message);
    }

    public DigitLockInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
