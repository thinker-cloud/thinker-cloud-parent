package com.thinker.cloud.security.userdetail;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.io.Serial;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 授权用户信息
 *
 * @author admin
 */
@Getter
public class AuthUser extends User implements OAuth2AuthenticatedPrincipal {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    /**
     * 扩展属性，方便存放oauth 上下文相关信息
     */
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * 用户id
     */
    private final Long id;

    /**
     * 用户类型 -1.超级管理员 1.租户管理员 2.普通用户
     */
    private final Integer type;

    /**
     * 是否是超级管理员
     */
    private final boolean admin;

    /**
     * 手机号
     */
    private final String phone;

    /**
     * 所属部门id
     */
    private final Long organizationId;

    /**
     * 所属租户id
     */
    private final Long tenantId;

    /**
     * 数据权限类型 1.全部 2.本机及子级 3.本级 4.当前用户 10.自定义
     */
    private final Integer dataScopeType;

    public AuthUser(Long id, Integer type, String phone, boolean isAdmin,
                    Long organizationId, Long tenantId, Integer dataScopeType,
                    String username, String password, boolean enabled,
                    boolean accountNonExpired, boolean credentialsNonExpired,
                    boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.type = type;
        this.phone = phone;
        this.admin = isAdmin;
        this.organizationId = organizationId;
        this.tenantId = tenantId;
        this.dataScopeType = dataScopeType;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return super.getUsername();
    }
}
