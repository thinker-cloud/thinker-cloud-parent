package com.thinker.cloud.db.tenant;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;

/**
 * 租户工具类
 *
 * @author admin
 */
@UtilityClass
public class TenantContextHolder {

    private final ThreadLocal<Long> THREAD_LOCAL_TENANT = new TransmittableThreadLocal<>();

    /**
     * TTL 设置租户ID
     *
     * @param tenantId tenantId
     */
    public void setTenantId(Long tenantId) {
        THREAD_LOCAL_TENANT.set(tenantId);
    }

    /**
     * 获取TTL中的租户ID
     *
     * @return Long
     */
    public Long getTenantId() {
        return THREAD_LOCAL_TENANT.get();
    }

    /**
     * 清除上下文
     */
    public void clear() {
        THREAD_LOCAL_TENANT.remove();
    }
}
