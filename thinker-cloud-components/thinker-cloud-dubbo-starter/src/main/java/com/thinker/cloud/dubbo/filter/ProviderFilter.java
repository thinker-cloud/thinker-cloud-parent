package com.thinker.cloud.dubbo.filter;

import com.thinker.cloud.common.constants.CommonConstants;
import com.thinker.cloud.core.utils.tenant.TenantContextHolder;
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
        RpcContextAttachment serverAttachment = RpcContext.getServerAttachment();
        TenantContextHolder.setTenantId((Long) serverAttachment.getObjectAttachment(CommonConstants.TENANT));
        log.info("服务提供者上下文拦截处理，context：{}", RpcContext.getServiceContext());
        return invoker.invoke(invocation);
    }
}
