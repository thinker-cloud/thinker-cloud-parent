package com.thinker.cloud.security.token;

import com.thinker.cloud.security.enums.LoginTypeEnum;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * 密码登录认证转换器
 *
 * @author admin
 **/
public class PasswordAuthenticationToken extends AbstractAuthenticationToken {

    public static final AuthorizationGrantType GRANT_TYPE = new AuthorizationGrantType(LoginTypeEnum.PASSWORD.getValue());

    private final String credentials;

    public PasswordAuthenticationToken(String username, String password) {
        super(GRANT_TYPE, username);
        this.credentials = password;
    }

    public PasswordAuthenticationToken(UserDetails principal) {
        super(GRANT_TYPE, principal, principal.getAuthorities());
        this.credentials = principal.getPassword();
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }
}
