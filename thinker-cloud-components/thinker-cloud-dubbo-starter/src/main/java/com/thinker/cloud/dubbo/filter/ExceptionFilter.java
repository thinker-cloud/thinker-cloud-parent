package com.thinker.cloud.dubbo.filter;

import com.thinker.cloud.core.exception.AbstractException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;

import java.lang.reflect.Method;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

/**
 * 自定义统一异常处理
 *
 * @author admin
 **/
@Slf4j
@Activate(group = PROVIDER)
public class ExceptionFilter implements Filter, Filter.Listener {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        if (result.hasException() && GenericService.class != invoker.getInterface()) {
            try {
                Throwable exception = result.getException();

                // 业务异常直接返回
                if (exception instanceof AbstractException) {
                    return;
                }

                // directly throw if it's checked exception
                if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                    return;
                }

                // directly throw if the exception appears in the signature
                try {
                    Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    Class<?>[] exceptionClassList = method.getExceptionTypes();
                    for (Class<?> exceptionClass : exceptionClassList) {
                        if (exception.getClass().equals(exceptionClass)) {
                            return;
                        }
                    }
                } catch (NoSuchMethodException e) {
                    return;
                }

                // for the exception not found in method's signature, print ERROR message in server's log.
                log.error("Got unchecked and undeclared exception which called by {}. service: {}, method: {}, exception: {}: {}"
                        , RpcContext.getServiceContext().getRemoteHost(), invoker.getInterface().getName()
                        , invocation.getMethodName(), exception.getClass().getName(), exception.getMessage(), exception);

                // directly throw if exception class and interface class are in the same jar file.
                String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)) {
                    return;
                }

                // directly throw if it's JDK exception
                String className = exception.getClass().getName();
                if (className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("jakarta.")) {
                    return;
                }

                // directly throw if it's dubbo exception
                if (exception instanceof RpcException) {
                    return;
                }

                // otherwise, wrap with RuntimeException and throw back to the client
                result.setException(new RuntimeException(StringUtils.toString(exception)));
            } catch (Throwable e) {
                log.warn("Fail to ExceptionFilter when called by {}. service: {}, method: {}, exception: {}: {}"
                        , RpcContext.getServiceContext().getRemoteHost(), invoker.getInterface().getName()
                        , invocation.getMethodName(), e.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void onError(Throwable e, Invoker<?> invoker, Invocation invocation) {
        log.error("Got unchecked and undeclared exception which called by {}. service: {}, method: {}, exception: {}: {}"
                , RpcContext.getServiceContext().getRemoteHost(), invoker.getInterface().getName()
                , invocation.getMethodName(), e.getClass().getName(), e.getMessage(), e);
    }
}
