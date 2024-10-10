package com.thinker.cloud.security.listener;

import com.thinker.cloud.core.utils.WebUtil;
import com.thinker.cloud.security.handler.AuthenticationFailureHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 认证失败事件监听器
 *
 * @author admin
 */
@Component
public class AuthenticationFailureEventListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private final AuthenticationFailureHandler failureHandler;

    @Autowired(required = false)
    public AuthenticationFailureEventListener(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NonNull AbstractAuthenticationFailureEvent event) {
        // 调用自定义业务实现
        if (failureHandler != null) {
            WebUtil.getRequest().ifPresent(request -> {
                HttpServletResponse response = WebUtil.getResponse();
                Authentication authentication = (Authentication) event.getSource();
                failureHandler.handle(event.getException(), authentication, request, response);
            });
        }
    }
}
