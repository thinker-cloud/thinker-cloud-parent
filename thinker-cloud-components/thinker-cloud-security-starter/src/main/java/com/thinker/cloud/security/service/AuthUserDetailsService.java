package com.thinker.cloud.security.service;

import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.model.Result;
import com.thinker.cloud.security.constants.SecurityConstants;
import com.thinker.cloud.security.userdetail.UserInfo;
import com.thinker.cloud.security.userdetail.AuthUser;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 用户统一授权顶层接口
 *
 * @author admin
 **/
public interface AuthUserDetailsService extends UserDetailsService, Ordered {

    /**
     * 是否支持此客户端校验
     *
     * @param authorization 授权对象
     * @return boolean
     */
    default boolean support(OAuth2Authorization authorization) {
        return true;
    }

    /**
     * 是否支持此客户端校验
     *
     * @param clientId  客户端id
     * @param grantType 授权类型
     * @return boolean
     */
    default boolean support(String clientId, String grantType) {
        return true;
    }

    /**
     * 获取认证主体用户信息
     *
     * @param user user
     * @return UserDetails
     */
    UserDetails loadUserByAuthParams(AuthUser user);

    /**
     * 构建UserDetails
     *
     * @param result 用户信息
     * @return UserDetails
     */
    default UserDetails getUserDetails(Result<UserInfo> result) {
        if (!result.isSuccess()) {
            throw new UsernameNotFoundException(result.getMessage());
        }

        UserInfo info = result.getData();
        Set<String> dbAuthsSet = new HashSet<>();

        // 关联角色
        info.getRoleIds().stream()
                .filter(Objects::nonNull)
                .map(roleId -> SecurityConstants.AUTH_ROLE + roleId)
                .forEach(dbAuthsSet::add);

        // 菜单权限
        info.getMenuIds().stream()
                .filter(Objects::nonNull)
                .map(menuId -> SecurityConstants.AUTH_MENU + menuId)
                .forEach(dbAuthsSet::add);

        // 数据权限
        info.getDataScopeIds().stream()
                .filter(Objects::nonNull)
                .map(dataScopeId -> SecurityConstants.AUTH_DATA_SCOPE + dataScopeId)
                .forEach(dbAuthsSet::add);

        // 权限集合
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils
                .createAuthorityList(dbAuthsSet.toArray(new String[0]));

        // 构造Security用户
        return new AuthUser(info.getId(), info.getType(), info.getPhone(),
                info.getOrganizationId(), info.getTenantId(), info.getDataScopeType(),
                info.getUsername(), SecurityConstants.SECURITY_BCRYPT + info.getPassword(),
                info.getStatus().equals(CommonConstants.STATUS_NORMAL),
                !CommonConstants.STATUS_EXPIRED.equals(info.getStatus()), true,
                !CommonConstants.STATUS_LOCK.equals(info.getStatus()), authorities);
    }

    /**
     * 排序值 默认取最大的
     *
     * @return 排序值
     */
    @Override
    default int getOrder() {
        return 0;
    }
}
