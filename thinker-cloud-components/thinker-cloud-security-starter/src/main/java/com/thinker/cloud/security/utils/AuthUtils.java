package com.thinker.cloud.security.utils;

import cn.hutool.core.codec.Base64;
import com.thinker.cloud.common.constants.CommonConstants;
import com.thinker.cloud.common.exception.ServiceException;
import com.thinker.cloud.core.utils.WebUtil;
import com.thinker.cloud.common.enums.AuthTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;

/**
 * 认证授权相关工具类
 *
 * @author admin
 */
@Slf4j
@UtilityClass
public class AuthUtils {

    private final String BASIC_ = "Basic ";

    /**
     * 从header 请求中的clientId/clientsecect
     *
     * @param header header中的参数
     * @throws RuntimeException if the Basic header is not present or is not valid Base64
     */
    public String[] extractAndDecodeHeader(String header) {

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new ServiceException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }

    /**
     * *从header 请求中的clientId/clientsecect
     */
    public String[] extractAndDecodeHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(BASIC_)) {
            throw new ServiceException("请求头中client信息为空");
        }

        return extractAndDecodeHeader(header);
    }

    /**
     * 获取客户端授权类型
     *
     * @param request request
     * @return String
     */
    public String getAuthType(HttpServletRequest request) {
        return WebUtil.getHeader(request
                , CommonConstants.AUTH_TYPE_HEADER
                , AuthTypeEnum.ADMIN.getValue()
        );
    }
}
