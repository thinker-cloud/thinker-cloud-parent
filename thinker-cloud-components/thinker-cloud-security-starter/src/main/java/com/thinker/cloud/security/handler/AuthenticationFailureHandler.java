package com.thinker.cloud.security.handler;

import com.thinker.cloud.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


/**
 * 登录认证失败处理
 *
 * @author admin
 */
public interface AuthenticationFailureHandler extends org.springframework.security.web.authentication.AuthenticationFailureHandler {

    /**
     * Called when an authentication attempt fails.
     *
     * @param request                 the request during which the authentication attempt occurred.
     * @param response                the response.
     * @param authenticationException the exception which was thrown to reject the authentication
     *                                request.
     */
    @Override
    default void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException authenticationException) {
        this.handle(authenticationException, SecurityUtils.getAuthentication(), request, response);
    }

    /**
     * 业务处理
     *
     * @param authenticationException 错误信息
     * @param authentication          认证信息
     * @param request                 请求信息
     * @param response                响应信息
     */
    void handle(AuthenticationException authenticationException, Authentication authentication,
                HttpServletRequest request, HttpServletResponse response);

}
