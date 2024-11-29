package com.thinker.cloud.db.tenant;

import com.thinker.cloud.common.constants.CommonConstants;
import com.thinker.cloud.core.utils.tenant.TenantContextHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * 传递请求的租户ID
 *
 * @author admin
 */
public class TenantRequestInterceptor implements ClientHttpRequestInterceptor {

    @NonNull
    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body
            , @NonNull ClientHttpRequestExecution execution) throws IOException {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            request.getHeaders().set(CommonConstants.TENANT, tenantId.toString());
        }
        return execution.execute(request, body);
    }

}
