package com.thinker.cloud.security.password;

import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.model.Result;
import com.thinker.cloud.security.service.AuthUserDetailsService;
import com.thinker.cloud.security.userdetail.AuthUser;
import com.thinker.cloud.security.userdetail.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.stereotype.Component;

/**
 * 用户密码授权服务
 *
 * @author admin
 **/
@Slf4j
@Component
public class UsernamePasswordAuthServiceImpl implements AuthUserDetailsService {

    @Override
    public boolean support(OAuth2Authorization authorization) {
        return AuthUserDetailsService.super.support(authorization);
    }

    @Override
    public boolean support(String clientId, String grantType) {
        return AuthUserDetailsService.super.support(clientId, grantType);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setType(-1);
        userInfo.setUsername(username);
        userInfo.setPassword(new BCryptPasswordEncoder().encode("123456"));
        userInfo.setStatus(CommonConstants.STATUS_NORMAL);
        userInfo.setOrganizationId(-1L);
        userInfo.setTenantId(-1L);
        userInfo.setDataScopeType(1);
        return this.getUserDetails(Result.success(userInfo));
    }

    @Override
    public UserDetails loadUserByAuthParams(AuthUser user) {
        return this.loadUserByUsername(user.getUsername());
    }
}
