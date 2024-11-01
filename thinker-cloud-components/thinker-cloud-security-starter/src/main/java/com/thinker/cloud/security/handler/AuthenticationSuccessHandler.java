package com.thinker.cloud.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;


/**
 * 登录认证成功处理
 *
 * @author admin
 */
public interface AuthenticationSuccessHandler extends org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     */
    @Override
    default void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) {
        this.handle(authentication, request, response);
    }

    /**
     * 业务处理
     *
     * @param authentication 认证信息
     * @param request        请求信息
     * @param response       响应信息
     */
    void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response);

}
