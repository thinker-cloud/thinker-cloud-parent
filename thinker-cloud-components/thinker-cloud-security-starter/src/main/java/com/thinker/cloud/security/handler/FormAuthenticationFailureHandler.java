package com.thinker.cloud.security.handler;

import cn.hutool.http.HttpUtil;
import com.thinker.cloud.core.utils.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.nio.charset.Charset;

/**
 * 表单登录失败处理逻辑
 *
 * @author admin
 */
@Slf4j
public class FormAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     */
    @Override
    @SneakyThrows
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response
            , AuthenticationException exception) {
        log.debug("表单登录失败:{}", exception.getLocalizedMessage());
        WebUtil.getResponse().sendRedirect(String.format("/token/login?error=%s"
                , HttpUtil.encodeParams(exception.getMessage(), Charset.defaultCharset())));
    }
}
