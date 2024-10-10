package com.thinker.cloud.security.listener;

import cn.hutool.core.collection.CollUtil;
import com.thinker.cloud.core.utils.WebUtil;
import com.thinker.cloud.security.handler.AuthenticationSuccessHandler;
import com.thinker.cloud.security.userdetail.AuthUser;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 认证成功事件监听器
 *
 * @author admin
 */
@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final AuthenticationSuccessHandler successHandler;

    @Autowired(required = false)
    public AuthenticationSuccessEventListener(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = (Authentication) event.getSource();
        if (successHandler != null && this.isUserAuthentication(authentication)) {
            WebUtil.getRequest().ifPresent(request -> {
                HttpServletResponse response = WebUtil.getResponse();
                successHandler.handle(authentication, request, response);
            });
        }
    }

    private boolean isUserAuthentication(Authentication authentication) {
        return authentication.getPrincipal() instanceof AuthUser
                || CollUtil.isNotEmpty(authentication.getAuthorities());
    }
}
