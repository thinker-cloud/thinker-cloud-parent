package com.thinker.cloud.redis.lock.exception;


import com.thinker.cloud.core.exception.AbstractException;

/**
 * 锁超时异常
 *
 * @author admin
 */
public class DigitLockTimeoutException extends AbstractException {

    public DigitLockTimeoutException(String message) {
        super(message);
    }

    public DigitLockTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
