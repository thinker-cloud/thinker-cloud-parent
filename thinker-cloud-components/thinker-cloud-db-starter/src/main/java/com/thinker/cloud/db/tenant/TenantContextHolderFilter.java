package com.thinker.cloud.db.tenant;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.utils.tenant.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;


/**
 * 租户上下文过滤器
 *
 * @author admin
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantContextHolderFilter extends GenericFilterBean {

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String tenant = request.getHeader(CommonConstants.TENANT);
        log.debug("获取header中的租户:{}", tenant);

        try {
            if (StrUtil.isNotBlank(tenant)) {
                TenantContextHolder.setTenantId(Long.valueOf(tenant));
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }
}
