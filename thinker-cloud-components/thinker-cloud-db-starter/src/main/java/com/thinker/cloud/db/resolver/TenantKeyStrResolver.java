package com.thinker.cloud.db.resolver;


import com.thinker.cloud.core.resolver.KeyStrResolver;
import com.thinker.cloud.core.utils.tenant.TenantContextHolder;
import org.springframework.context.annotation.Configuration;

/**
 * 租户字符串处理（方便其他模块获取）
 *
 * @author admin
 */
@Configuration
public class TenantKeyStrResolver implements KeyStrResolver {

    /**
     * 传入字符串增加 租户编号:in
     *
     * @param in    输入字符串
     * @param split 分割符
     * @return String
     */
    @Override
    public String extract(String in, String split) {
        return TenantContextHolder.getTenantId() + split + in;
    }

    /**
     * 返回当前租户ID
     *
     * @return String
     */
    @Override
    public String key() {
        return String.valueOf(TenantContextHolder.getTenantId());
    }

}
