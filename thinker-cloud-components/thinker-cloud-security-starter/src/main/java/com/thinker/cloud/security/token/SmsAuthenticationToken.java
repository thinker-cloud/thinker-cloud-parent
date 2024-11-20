package com.thinker.cloud.security.token;

import com.thinker.cloud.security.enums.LoginTypeEnum;
import com.thinker.cloud.security.model.AuthParams;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * 短信登录认证转换器
 *
 * @author admin
 **/
public class SmsAuthenticationToken extends AbstractAuthenticationToken {

    public static final AuthorizationGrantType GRANT_TYPE = new AuthorizationGrantType(LoginTypeEnum.SMS.getValue());

    public SmsAuthenticationToken(AuthParams authParams) {
        super(GRANT_TYPE, authParams);
    }

    public SmsAuthenticationToken(UserDetails principal) {
        super(GRANT_TYPE, principal, principal.getAuthorities());
    }
}
