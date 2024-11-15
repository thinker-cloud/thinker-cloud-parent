package com.thinker.cloud.security.component;

import cn.hutool.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinker.cloud.core.enums.ResponseCode;
import com.thinker.cloud.core.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.PrintWriter;

/**
 * 访问被拒绝处理程序
 *
 * @author admin
 */
@Slf4j
@RequiredArgsConstructor
public class AuthAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) {
        log.error("访问被拒绝：", exception);
        response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
        Result<Void> result = Result.failure(ResponseCode.UNAUTHORIZED);
        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(result));
    }
}
