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
 * 用户信息
 *
 * @author admin
 */
@Getter
public class DigitUser extends User implements OAuth2AuthenticatedPrincipal {

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
     * 类型
     */
    private final Long type;

    /**
     * 名称
     */
    private final String name;

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
     * 数据权限类型
     */
    private final Integer dataScopeType;

    public DigitUser(Long id, Long type, String phone, String name, Long organizationId, Long tenantId, Integer dataScopeType
            , String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired
            , boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.type = type;
        this.name = name;
        this.phone = phone;
        this.organizationId = organizationId;
        this.tenantId = tenantId;
        this.dataScopeType = dataScopeType;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return this.getUsername();
    }
}
