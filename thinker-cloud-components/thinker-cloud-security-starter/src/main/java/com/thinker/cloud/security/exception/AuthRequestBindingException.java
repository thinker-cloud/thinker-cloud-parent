package com.thinker.cloud.security.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthRequestBindingException extends AuthenticationException {

	public AuthRequestBindingException(String msg, Throwable t) {
		super(msg, t);
	}

	public AuthRequestBindingException(String msg) {
		super(msg);
	}
}
