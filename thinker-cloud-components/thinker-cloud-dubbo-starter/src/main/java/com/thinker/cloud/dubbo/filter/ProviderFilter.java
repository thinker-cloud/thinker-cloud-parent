package com.thinker.cloud.dubbo.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

/**
 * 服务提供者相关拦截处理
 *
 * @author admin
 **/
@Slf4j
@Activate(group = PROVIDER)
public class ProviderFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        log.info("服务提供者相关拦截处理，上下文：{}", RpcContext.getServiceContext());
        return invoker.invoke(invocation);
    }
}
