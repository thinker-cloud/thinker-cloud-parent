/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thinker.cloud.security.component;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.enums.ResponseCode;
import com.thinker.cloud.core.model.Result;
import com.thinker.cloud.security.exception.AbstractAuthenticationException;
import com.thinker.cloud.security.utils.SecurityMessageSourceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.PrintWriter;
import java.util.Locale;
import java.util.Objects;

/**
 * 客户端异常处理 AuthenticationException 不同细化异常处理
 *
 * @author admin
 */
@RequiredArgsConstructor
public class Oauth2AuthExceptionEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        response.setCharacterEncoding(CommonConstants.UTF8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
        Result<String> result = new Result<>();
        result.setSuccess(Boolean.FALSE);
        result.setCode(HttpStatus.HTTP_UNAUTHORIZED);
        String message = authException.getMessage();
        result.setMessage(StrUtil.isBlank(message) ? "登录失败" : message);

        if (authException instanceof OAuth2AuthenticationException e) {
            if (authException instanceof InvalidBearerTokenException) {
                String msg = SecurityMessageSourceUtils.getAccessor().getMessage(
                        "AbstractUserDetailsAuthenticationProvider.credentialsExpired"
                        , authException.getMessage(), Locale.CHINA);
                result.setMessage(msg);
            } else if (Objects.nonNull(e.getError())) {
                String errorCode = e.getError().getErrorCode();
                if (OAuth2ErrorCodes.INVALID_CLIENT.equals(errorCode)
                        || OAuth2ErrorCodes.UNAUTHORIZED_CLIENT.equals(errorCode)) {
                    String msg = SecurityMessageSourceUtils.getAccessor().getMessage(
                            "AbstractUserDetailsAuthenticationProvider.badClientCredentials"
                            , authException.getMessage(), Locale.CHINA);
                    result.setMessage(msg);
                }
            }
        }

        if (authException instanceof CredentialsExpiredException
                || authException instanceof InsufficientAuthenticationException) {
            String msg = SecurityMessageSourceUtils.getAccessor().getMessage(
                    "AbstractUserDetailsAuthenticationProvider.credentialsExpired"
                    , authException.getMessage(), Locale.CHINA);
            result.setMessage(msg);
        }

        if (authException instanceof UsernameNotFoundException) {
            String msg = SecurityMessageSourceUtils.getAccessor().getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials"
                    , authException.getMessage(), Locale.CHINA);
            result.setCode(ResponseCode.NOOP_BIND_ACCOUNT.getCode());
            result.setMessage(msg);
            response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
        }

        if (authException instanceof AbstractAuthenticationException) {
            result.setMessage(authException.getMessage());
            response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
        }

        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(result));
    }
}
