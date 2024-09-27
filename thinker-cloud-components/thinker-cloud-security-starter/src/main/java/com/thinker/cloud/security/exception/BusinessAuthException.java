package com.thinker.cloud.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 业务身份验证异常
 *
 * @author admin
 */
public class BusinessAuthException extends AuthenticationException {

    public BusinessAuthException(String msg, Throwable t) {
        super(msg, t);
    }

    public BusinessAuthException(String msg) {
        super(msg);
    }
}
