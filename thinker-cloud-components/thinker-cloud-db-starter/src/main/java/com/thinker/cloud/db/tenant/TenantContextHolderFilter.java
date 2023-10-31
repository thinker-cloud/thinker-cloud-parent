package com.thinker.cloud.db.tenant;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.security.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.util.Optional;


/**
 * 租户上下文过滤器
 *
 * @author admin
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantContextHolderFilter extends GenericFilterBean {

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String tenant = request.getHeader(CommonConstants.TENANT);
        log.debug("获取header中的租户ID为:{}", tenant);

        try {
            Long tenantId = Optional.ofNullable(tenant)
                    .filter(StrUtil::isNotBlank)
                    .map(Long::valueOf)
                    .orElse(SecurityConstants.DEFAULT_TENANT);
            TenantContextHolder.setTenantId(tenantId);
        } catch (Exception e) {
            TenantContextHolder.setTenantId(SecurityConstants.DEFAULT_TENANT);
        }

        filterChain.doFilter(request, response);
        TenantContextHolder.clear();
    }
}
