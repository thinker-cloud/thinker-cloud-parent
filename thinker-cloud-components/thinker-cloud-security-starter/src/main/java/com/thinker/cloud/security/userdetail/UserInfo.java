package com.thinker.cloud.security.userdetail;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 认证用户主体
 *
 * @author admin
 */
@Data
public class UserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 姓名
     */
    private String name;

    /**
     * 用户类型
     */
    private Integer type;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 组织机构id
     */
    private Long organizationId;

    /**
     * 权限类型
     */
    private Integer dataScopeType;

    /**
     * 角色列表
     */
    private Collection<String> roleIds = new ArrayList<>();

    /**
     * 菜单权限列表
     */
    private Collection<String> menuIds = new ArrayList<>();

    /**
     * 数据权限列表
     */
    private Collection<String> dataScopeIds = new ArrayList<>();
}
