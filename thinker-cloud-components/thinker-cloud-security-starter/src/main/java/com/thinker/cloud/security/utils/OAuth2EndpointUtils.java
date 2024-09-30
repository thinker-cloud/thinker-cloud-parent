package com.thinker.cloud.security.utils;

import cn.hutool.core.map.MapUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

/**
 * OAuth2 端点工具
 *
 * @author admin
 */
@UtilityClass
public class OAuth2EndpointUtils {

    public final String ACCESS_TOKEN_REQUEST_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    public MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
        parameterMap.forEach((key, values) -> {
            for (String value : values) {
                parameters.add(key, value);
            }
        });
        return parameters;
    }

    public void throwError(String errorCode, String parameterName, String errorUri) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri);
        throw new OAuth2AuthenticationException(error);
    }

    /**
     * 格式化输出token 信息
     *
     * @param authentication 用户认证信息
     * @param claims         扩展信息
     * @return OAuth2AccessTokenResponse
     */
    public OAuth2AccessTokenResponse sendAccessTokenResponse(OAuth2Authorization authentication, Map<String, Object> claims) {
        OAuth2AccessToken accessToken = authentication.getAccessToken().getToken();
        OAuth2RefreshToken refreshToken = Optional.ofNullable(authentication.getRefreshToken())
                .map(OAuth2Authorization.Token::getToken).orElse(null);

        OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                .tokenType(accessToken.getTokenType())
                .scopes(accessToken.getScopes());

        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
        }
        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }

        if (MapUtil.isNotEmpty(claims)) {
            builder.additionalParameters(claims);
        }
        return builder.build();
    }

}
