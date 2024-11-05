package com.thinker.cloud.security.service;

import com.thinker.cloud.security.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis OAuth2 授权服务
 * <p>
 * 实现 Redis 存储 OAuth2Authorization 对象
 *
 * @author admin
 */
@RequiredArgsConstructor
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final static Long TIMEOUT = 10L;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");

        if (isState(authorization)) {
            String token = authorization.getAttribute("state");
            redisTemplate.setValueSerializer(RedisSerializer.java());

            String key = buildKey(OAuth2ParameterNames.STATE, token);
            redisTemplate.opsForValue().set(key, authorization, TIMEOUT, TimeUnit.MINUTES);
        }

        if (isCode(authorization)) {
            OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                    .getToken(OAuth2AuthorizationCode.class);
            Assert.notNull(authorizationCode, "authorizationCode cannot be null");

            OAuth2AuthorizationCode authorizationCodeToken = authorizationCode.getToken();
            Instant issuedAt = authorizationCodeToken.getIssuedAt();
            Assert.notNull(issuedAt, "authorizationCode token issuedAt cannot be null");

            long between = ChronoUnit.MINUTES.between(issuedAt, authorizationCodeToken.getExpiresAt());
            redisTemplate.setValueSerializer(RedisSerializer.java());

            String key = buildKey(OAuth2ParameterNames.CODE, authorizationCodeToken.getTokenValue());
            redisTemplate.opsForValue().set(key, authorization, between, TimeUnit.MINUTES);
        }

        if (isRefreshToken(authorization)) {
            OAuth2Authorization.Token<OAuth2RefreshToken> authorizationRefresh = authorization.getRefreshToken();
            Assert.notNull(authorizationRefresh, "authorizationRefresh cannot be null");

            OAuth2RefreshToken refreshToken = authorizationRefresh.getToken();
            Instant issuedAt = refreshToken.getIssuedAt();
            Assert.notNull(issuedAt, "authorizationCode token issuedAt cannot be null");

            long between = ChronoUnit.SECONDS.between(issuedAt, refreshToken.getExpiresAt());
            redisTemplate.setValueSerializer(RedisSerializer.java());

            String key = buildKey(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue());
            redisTemplate.opsForValue().set(key, authorization, between, TimeUnit.SECONDS);
        }

        if (isAccessToken(authorization)) {
            OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
            Instant issuedAt = accessToken.getIssuedAt();
            Assert.notNull(issuedAt, "authorization AccessToken issuedAt cannot be null");

            long between = ChronoUnit.SECONDS.between(issuedAt, accessToken.getExpiresAt());
            redisTemplate.setValueSerializer(RedisSerializer.java());

            String key = buildKey(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getTokenValue());
            redisTemplate.opsForValue().set(key, authorization, between, TimeUnit.SECONDS);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");

        List<String> keys = new ArrayList<>();
        if (isState(authorization)) {
            String token = authorization.getAttribute("state");
            keys.add(buildKey(OAuth2ParameterNames.STATE, token));
        }

        if (isCode(authorization)) {
            OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                    .getToken(OAuth2AuthorizationCode.class);
            Assert.notNull(authorizationCode, "authorizationCode cannot be null");

            OAuth2AuthorizationCode authorizationCodeToken = authorizationCode.getToken();
            keys.add(buildKey(OAuth2ParameterNames.CODE, authorizationCodeToken.getTokenValue()));
        }

        if (isRefreshToken(authorization)) {
            OAuth2Authorization.Token<OAuth2RefreshToken> authorizationRefreshToken = authorization.getRefreshToken();
            Assert.notNull(authorizationRefreshToken, "authorizationRefreshToken cannot be null");

            OAuth2RefreshToken refreshToken = authorizationRefreshToken.getToken();
            keys.add(buildKey(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue()));
        }

        if (isAccessToken(authorization)) {
            OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
            keys.add(buildKey(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getTokenValue()));
        }
        redisTemplate.delete(keys);
    }

    @Override
    @Nullable
    public OAuth2Authorization findById(String id) {
        return this.findByToken(id, OAuth2TokenType.ACCESS_TOKEN);
    }

    @Override
    @Nullable
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");

        redisTemplate.setValueSerializer(RedisSerializer.java());
        String key = buildKey(Optional.ofNullable(tokenType)
                .orElse(OAuth2TokenType.ACCESS_TOKEN)
                .getValue(), token);
        return (OAuth2Authorization) redisTemplate.opsForValue().get(key);
    }

    private static String buildKey(String type, String id) {
        return String.format("%s:%s::%s", SecurityConstants.TOKEN_PREFIX, type, id);
    }

    private static boolean isState(OAuth2Authorization authorization) {
        return Objects.nonNull(authorization.getAttribute("state"));
    }

    private static boolean isCode(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                .getToken(OAuth2AuthorizationCode.class);
        return Objects.nonNull(authorizationCode);
    }

    private static boolean isRefreshToken(OAuth2Authorization authorization) {
        return Objects.nonNull(authorization.getRefreshToken());
    }

    private static boolean isAccessToken(OAuth2Authorization authorization) {
        return Objects.nonNull(authorization.getAccessToken());
    }

}
