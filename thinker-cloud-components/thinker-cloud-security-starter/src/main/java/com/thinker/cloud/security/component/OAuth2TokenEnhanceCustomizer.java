package com.thinker.cloud.security.component;

import com.thinker.cloud.security.constants.SecurityConstants;
import com.thinker.cloud.security.model.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.time.Instant;
import java.util.Date;

/**
 * token 增强
 *
 * @author admin
 */
@Slf4j
public class OAuth2TokenEnhanceCustomizer implements OAuth2TokenCustomizer<OAuth2TokenClaimsContext> {

    /**
     * Customize the OAuth 2.0 Token attributes.
     *
     * @param context the context containing the OAuth 2.0 Token attributes
     */
    @Override
    public void customize(OAuth2TokenClaimsContext context) {
        OAuth2TokenClaimsSet.Builder claims = context.getClaims();
        String clientId = context.getAuthorizationGrant().getName();
        claims.claim(SecurityConstants.CLIENT_ID, clientId);

        // 客户端模式不返回具体用户信息
        AuthorizationGrantType grantType = context.getAuthorizationGrantType();
        if (SecurityConstants.CLIENT_CREDENTIALS.equals(grantType.getValue())) {
            return;
        }

        if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
            AuthUser authUser = (AuthUser) context.getPrincipal().getPrincipal();
            claims.claim(SecurityConstants.DETAILS_USER, authUser);
            claims.claim(SecurityConstants.DETAILS_USER_ID, authUser.getId());
            claims.claim(SecurityConstants.USERNAME, authUser.getUsername());
        } else if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
            claims.claim(IdTokenClaimNames.AUTH_TIME, Date.from(Instant.now()));
            StandardSessionIdGenerator standardSessionIdGenerator = new StandardSessionIdGenerator();
            claims.claim("sid", standardSessionIdGenerator.generateSessionId());
        }
    }
}
