package com.thinker.cloud.feign.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.annotation.Inner;
import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.utils.WebUtil;
import com.thinker.cloud.core.utils.tenant.TenantContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;

/**
 * feign 接口调用请求拦截器
 *
 * @author admin
 */
@RequiredArgsConstructor
public class FeignRequestInterceptor implements RequestInterceptor, Ordered {

    private final BearerTokenResolver tokenResolver;

    /**
     * Called for every request. Add data using methods on the supplied
     * {@link RequestTemplate}.
     *
     * @param template template
     */
    @Override
    public void apply(RequestTemplate template) {
        Collection<String> fromHeader = template.headers().get(CommonConstants.FROM);
        // 带 from 请求直接跳过
        if (CollUtil.isNotEmpty(fromHeader) && fromHeader.contains(CommonConstants.FROM_IN)) {
            return;
        }

        // 非web 请求直接跳过
        if (WebUtil.getRequest().isEmpty()) {
            return;
        }

        // 内部接口自动带上标识
        Method method = template.methodMetadata().method();
        if (method.getAnnotation(Inner.class) != null) {
            template.header(CommonConstants.FROM, CommonConstants.FROM_IN);
        }

        // 传递租户id
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            template.header(CommonConstants.TENANT, tenantId.toString());
        }

        // 获取 HttpServletRequest
        Optional<HttpServletRequest> requestOptional = WebUtil.getRequest();
        if (requestOptional.isEmpty()) {
            return;
        }

        // 传递token
        HttpServletRequest request = requestOptional.get();

        // 避免请求参数的 query token 无法传递
        String token = tokenResolver.resolve(request);
        if (StrUtil.isNotBlank(token)) {
            template.header(HttpHeaders.AUTHORIZATION, String.format("%s %s"
                    , OAuth2AccessToken.TokenType.BEARER.getValue(), token));
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
