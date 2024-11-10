package com.thinker.cloud.dubbo.filter;

import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.utils.tenant.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;

/**
 * 服务消费者相关拦截处理
 *
 * @author admin
 **/
@Slf4j
@Activate(group = CONSUMER)
public class ConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContextAttachment clientAttachment = RpcContext.getClientAttachment();
        clientAttachment.setAttachment(CommonConstants.TENANT, TenantContextHolder.getTenantId());
        log.info("服务消费者上下文拦截处理，context：{}", clientAttachment.getObjectAttachments());
        return invoker.invoke(invocation);
    }
}
