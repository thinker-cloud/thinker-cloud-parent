package com.thinker.cloud.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 业务身份验证异常抽象类
 *
 * @author admin
 */
public abstract class AbstractAuthenticationException extends AuthenticationException {

    public AbstractAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public AbstractAuthenticationException(String msg) {
        super(msg);
    }
}
