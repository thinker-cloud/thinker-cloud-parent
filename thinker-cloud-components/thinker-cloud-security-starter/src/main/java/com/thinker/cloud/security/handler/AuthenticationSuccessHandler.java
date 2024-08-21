package com.thinker.cloud.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;


/**
 * token 发放成功处理
 *
 * @author admin
 */
public interface AuthenticationSuccessHandler {

    /**
     * 业务处理
     *
     * @param authentication 认证信息
     * @param request        请求信息
     * @param response       响应信息
     */
    void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response);

}
