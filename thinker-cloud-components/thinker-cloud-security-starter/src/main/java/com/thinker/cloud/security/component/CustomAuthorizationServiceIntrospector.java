package com.thinker.cloud.security.component;

import com.thinker.cloud.security.constants.SecurityConstants;
import com.thinker.cloud.security.exception.OAuthClientException;
import com.thinker.cloud.security.service.AuthUserDetailsService;
import com.thinker.cloud.security.userdetail.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 资源服务器token内省处理器
 *
 * @author admin
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationServiceIntrospector implements OpaqueTokenIntrospector {

    private final OAuth2AuthorizationService authorizationService;
    private final List<AuthUserDetailsService> authUserDetailsServices;


    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
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

        // 根据授权方式获取对应用户授权服务
        Optional<AuthUserDetailsService> authUserDetailsService = authUserDetailsServices.stream()
                .filter(service -> service.support(authorization))
                .max(Comparator.comparingInt(Ordered::getOrder));
        if (authUserDetailsService.isEmpty()) {
            throw new OAuthClientException("暂不支持此授权方式：{}" + authorizationGrantType.getValue());
        }

        try {
            // 获取授权关联凭证对象
            Object principal = authorization.getAttributes().get(Principal.class.getName());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
            Object tokenPrincipal = usernamePasswordAuthenticationToken.getPrincipal();

            // 注入客户端信息，方便上下文中获取
            AuthUser userDetails = (AuthUser) authUserDetailsService.get().loadUserByAuthParams((AuthUser) tokenPrincipal);
            userDetails.getAttributes().put(SecurityConstants.CLIENT_ID, authorization.getRegisteredClientId());
            return userDetails;
        } catch (UsernameNotFoundException e) {
            log.warn("用户不存在 {}", e.getLocalizedMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("资源服务器 introspect Token error {}", e.getLocalizedMessage(), e);
            throw new InvalidBearerTokenException("invalid_token: " + token);
        }
    }
}
