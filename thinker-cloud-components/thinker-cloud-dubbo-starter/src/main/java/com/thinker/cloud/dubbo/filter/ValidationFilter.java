package com.thinker.cloud.dubbo.filter;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ConfigUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.validation.Validation;
import org.apache.dubbo.validation.Validator;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;
import static org.apache.dubbo.common.constants.FilterConstants.VALIDATION_KEY;

/**
 * Dubbo参数校验异常过滤器
 *
 * @author admin
 **/
@Slf4j
@Activate(group = {CONSUMER, PROVIDER}, value = VALIDATION_KEY, order = 10000)
public class ValidationFilter implements Filter {

    @Setter
    private Validation validation;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (validation != null && !invocation.getMethodName().startsWith("$")
                && ConfigUtils.isNotEmpty(invoker.getUrl().getMethodParameter(invocation.getMethodName(), VALIDATION_KEY))) {
            try {
                Validator validator = validation.getValidator(invoker.getUrl());
                if (validator != null) {
                    validator.validate(invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
                }
            } catch (RpcException e) {
                throw e;
            } catch (ValidationException e) {
                // only use exception's message to avoid potential serialization issue
                String message;
                if (e instanceof ConstraintViolationException exception) {
                    message = exception.getConstraintViolations().stream()
                            .map(ConstraintViolation::getMessage)
                            .findFirst()
                            .orElse(e.getMessage());
                } else {
                    message = e.getMessage();
                }
                return AsyncRpcResult.newDefaultAsyncResult(new ValidationException(message), invocation);
            } catch (Throwable t) {
                return AsyncRpcResult.newDefaultAsyncResult(t, invocation);
            }
        }
        return invoker.invoke(invocation);
    }
}
