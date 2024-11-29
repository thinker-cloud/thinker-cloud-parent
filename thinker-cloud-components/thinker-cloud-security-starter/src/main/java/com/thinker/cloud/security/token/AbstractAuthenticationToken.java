package com.thinker.cloud.security.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 自定义授权模式抽象
 *
 * @author admin
 */
@Getter
@Setter
public abstract class AbstractAuthenticationToken extends org.springframework.security.authentication.AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final Object principal;
    private final AuthorizationGrantType grantType;
    private Set<String> scopes = Collections.emptySet();
    private Map<String, Object> additionalParameters = Collections.emptyMap();

    public AbstractAuthenticationToken(AuthorizationGrantType grantType, Object principal) {
        super(Collections.emptyList());
        Assert.notNull(grantType, "grantType cannot be null");
        Assert.notNull(principal, "principal cannot be null");

        this.grantType = grantType;
        this.principal = principal;
    }

    public AbstractAuthenticationToken(AuthorizationGrantType grantType, Object principal,
                                       Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.notNull(grantType, "grantType cannot be null");
        Assert.notNull(principal, "principal cannot be null");

        this.grantType = grantType;
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * 获取直接参数
     *
     * @param key key
     * @return Object
     */
    public Object getParameters(String key) {
        return this.additionalParameters.get(key);
    }
}
