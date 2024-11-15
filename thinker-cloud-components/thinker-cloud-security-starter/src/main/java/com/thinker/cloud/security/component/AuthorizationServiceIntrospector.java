package com.thinker.cloud.security.component;

import com.thinker.cloud.security.constants.SecurityConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

/**
 * 资源服务器token内省处理器
 * <p>
 * 校验token的有效性
 *
 * @author admin
 */
@Slf4j
@AllArgsConstructor
public class AuthorizationServiceIntrospector implements OpaqueTokenIntrospector {

    private final OAuth2AuthorizationService authorizationService;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        try {
            OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
            if (Objects.isNull(authorization)) {
                throw new InvalidBearerTokenException("invalid_token: " + token);
            }

            // 客户端模式默认返回
            AuthorizationGrantType authorizationGrantType = authorization.getAuthorizationGrantType();
            if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(authorizationGrantType)) {
                return new DefaultOAuth2AuthenticatedPrincipal(authorization.getPrincipalName()
                        , authorization.getAttributes(), AuthorityUtils.NO_AUTHORITIES);
            }

            // 获取授权关联凭证对象
            Map<String, Object> attributes = authorization.getAttributes();
            Authentication authentication = (Authentication) attributes.get(Principal.class.getName());
            OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

            // 注入客户端信息，方便上下文中获取
            principal.getAttributes().put(SecurityConstants.CLIENT_ID, authorization.getRegisteredClientId());
            return principal;
        } catch (Exception e) {
            log.error("资源服务器 introspect Token error {}", e.getLocalizedMessage(), e);
            throw new InvalidBearerTokenException("invalid_token: " + token);
        }
    }
}
