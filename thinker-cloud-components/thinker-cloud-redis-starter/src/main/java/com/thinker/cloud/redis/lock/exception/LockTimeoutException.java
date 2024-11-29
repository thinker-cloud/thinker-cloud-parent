package com.thinker.cloud.redis.lock.exception;


import com.thinker.cloud.common.exception.LockException;

/**
 * 锁超时异常
 *
 * @author admin
 */
public class LockTimeoutException extends LockException {

    public LockTimeoutException(String message) {
        super(message);
    }

    public LockTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
