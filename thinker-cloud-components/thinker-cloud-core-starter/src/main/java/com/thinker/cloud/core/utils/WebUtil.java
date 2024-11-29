package com.thinker.cloud.core.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.common.utils.ClassUtil;
import com.thinker.cloud.common.exception.FailException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.WebUtils;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Miscellaneous utilities for web applications.
 *
 * @author admin
 */
@Slf4j
@UtilityClass
public class WebUtil extends WebUtils {

    private final String BASIC_ = "Basic ";

    private final String UNKNOWN = "unknown";

    /**
     * 判断是否ajax请求 spring ajax 返回含有 ResponseBody 或者 RestController注解
     *
     * @param handlerMethod HandlerMethod
     * @return 是否ajax请求
     */
    public boolean isBody(HandlerMethod handlerMethod) {
        ResponseBody responseBody = ClassUtil.getAnnotation(handlerMethod, ResponseBody.class);
        return responseBody != null;
    }

    /**
     * 读取cookie
     *
     * @param name cookie name
     * @return cookie value
     */
    public String getCookieVal(String name) {
        if (WebUtil.getRequest().isPresent()) {
            return getCookieVal(WebUtil.getRequest().get(), name);
        }
        return null;
    }

    /**
     * 读取cookie
     *
     * @param request HttpServletRequest
     * @param name    cookie name
     * @return cookie value
     */
    public String getCookieVal(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 清除 某个指定的cookie
     *
     * @param response HttpServletResponse
     * @param key      cookie key
     */
    public void removeCookie(HttpServletResponse response, String key) {
        setCookie(response, key, null, 0);
    }

    /**
     * 设置cookie
     *
     * @param response        HttpServletResponse
     * @param name            cookie name
     * @param value           cookie value
     * @param maxAgeInSeconds maxage
     */
    public void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * 获取 HttpServletRequest
     *
     * @return {HttpServletRequest}
     */
    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(map -> (ServletRequestAttributes) map)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * 获取 HttpServletResponse
     *
     * @return {HttpServletResponse}
     */
    public HttpServletResponse getResponse() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(map -> (ServletRequestAttributes) map)
                .map(ServletRequestAttributes::getResponse)
                .orElse(null);
    }

    /**
     * 从request获取CLIENT_ID
     *
     * @param request request
     * @return clientId
     */
    @SneakyThrows
    public String getClientId(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return splitClient(header)[0];
    }

    /**
     * 从request获取CLIENT_ID
     *
     * @return clientId
     */
    @SneakyThrows
    public String getClientId() {
        if (WebUtil.getRequest().isPresent()) {
            String header = WebUtil.getRequest().get().getHeader(HttpHeaders.AUTHORIZATION);
            return splitClient(header)[0];
        }
        return null;
    }

    @NotNull
    private static String[] splitClient(String header) {
        if (header == null || !header.startsWith(BASIC_)) {
            throw new FailException("请求头中client信息为空");
        }
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new FailException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new FailException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }

    /**
     * 获取ip
     *
     * @return {String}
     */
    public String getIP() {
        return getRequest().map(WebUtil::getIP).orElse(null);

    }

    /**
     * 获取ip
     *
     * @param request HttpServletRequest
     * @return {String}
     */
    public String getIP(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest is null");
        String ip = request.getHeader("X-Requested-For");
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return StrUtil.isBlank(ip) ? null : ip.split(",")[0];
    }

    /**
     * 解析 client id
     *
     * @param header       请求头
     * @param defaultValue 默认值
     * @return 如果解析失败返回默认值
     */
    public String extractClientId(String header, final String defaultValue) {
        if (header == null || !header.startsWith(BASIC_)) {
            log.debug("请求头中client信息为空: {}", header);
            return defaultValue;
        }

        byte[] decoded;
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            log.debug("Failed to decode basic authentication token: {}", header);
            return defaultValue;
        }

        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(":");
        if (delim == -1) {
            log.debug("Invalid basic authentication token: {}", header);
            return defaultValue;
        }
        return token.substring(0, delim);
    }

    /**
     * 从请求头中解析 client id
     *
     * @param header header
     * @return Optional<String>
     */
    public Optional<String> extractClientId(String header) {
        return Optional.ofNullable(extractClientId(header, null));
    }

    /**
     * 从request 获取CLIENT_ID
     *
     * @return String
     */
    public String getClientId(String header) {
        String clientId = extractClientId(header, null);
        if (clientId == null) {
            throw new FailException("Invalid basic authentication token");
        }
        return clientId;
    }

    /**
     * 获取指定herder
     *
     * @param header header
     * @return String
     */
    public String getHeader(String header) {
        return getRequest().map(request -> getHeader(request, header)).orElse(null);
    }

    /**
     * 获取指定herder
     *
     * @param request request
     * @param header  header
     * @return String
     */
    public String getHeader(HttpServletRequest request, String header) {
        return getHeader(request, header, null);
    }

    /**
     * 获取指定herder
     *
     * @param request      request
     * @param header       请求头名称
     * @param defaultValue 默认值
     * @return String
     */
    public String getHeader(@Nonnull HttpServletRequest request, String header, String defaultValue) {
        Assert.notNull(request, "request is not null");
        Assert.hasText(header, "header is not blank");

        String var = request.getHeader(header);
        if (StrUtil.isBlank(var)) {
            return defaultValue;
        }
        return var;
    }
}
