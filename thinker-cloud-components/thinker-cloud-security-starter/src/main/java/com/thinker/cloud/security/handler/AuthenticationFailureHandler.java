package com.thinker.cloud.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


/**
 * token 发放失败处理
 *
 * @author admin
 */
public interface AuthenticationFailureHandler {

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
