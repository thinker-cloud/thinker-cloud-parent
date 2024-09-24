package com.thinker.cloud.security.constants;

/**
 * 公共常量类
 *
 * @author admin
 **/
public interface SecurityConstants {

    /**
     * 角色前缀
     */
    String AUTH_ROLE = "ROLE_";

    /**
     * 菜单前缀
     */
    String AUTH_MENU = "MENU_";

    /**
     * 数据权限前缀
     */
    String AUTH_DATA_SCOPE = "SCOPE_";

    /**
     * 前缀
     */
    String DIGIT_PREFIX = "digit_";

    /**
     * 授权码模式code key 前缀
     */
    String OAUTH_CODE_PREFIX = "oauth:code:";

    /**
     * token 相关前缀
     */
    String TOKEN_PREFIX = "token:";

    /**
     * oauth 相关前缀
     */
    String OAUTH_PREFIX = "oauth:";

    /**
     * 客户端模式
     */
    String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * 客户端ID
     */
    String CLIENT_ID = "clientId";

    /**
     * 会员登录URL
     */
    String MEMBER_TOKEN_URL = "/member/token";
    /**
     * 社交登录URL
     */
    String SOCIAL_TOKEN_URL = "/social/token";

    /**
     * 默认登录URL
     */
    String OAUTH_TOKEN_URL = "/oauth/token";

    /**
     * grant_type
     */
    String REFRESH_TOKEN = "refresh_token";

    /**
     * oauth 客户端信息
     */
    String CLIENT_DETAILS_KEY = "smarttech_oauth:client:details";

    /**
     * 资源服务器默认bean名称
     */
    String RESOURCE_SERVER_CONFIGURER = "resourceServerConfigurerAdapter";

    /**
     * {bcrypt} 加密的特征码
     */
    String SECURITY_BCRYPT = "{bcrypt}";
    String SECURITY_NOOP = "{noop}";

    /**
     * 用户ID字段
     */
    String DETAILS_USER_ID = "id";

    /**
     * 用户名
     */
    String DETAILS_USERNAME = "username";

    /**
     * 用户基本信息
     */
    String DETAILS_USER = "user_info";

    /**
     * 用户名phone
     */
    String DETAILS_PHONE = "phone";

    /**
     * 头像
     */
    String DETAILS_AVATAR = "avatar";

    /**
     * 用户类型字段
     */
    String DETAILS_TYPE = "type";

    /**
     * 用户数据权限
     */
    String DETAILS_DATA_SCOPE_TYPE = "dataScopeType";

    /**
     * 用户部门字段
     */
    String DETAILS_ORGANIZATION_ID = "organizationId";

    /**
     * 租户ID 字段
     */
    String DETAILS_TENANT_ID = "tenantId";

    /**
     * 协议字段
     */
    String DETAILS_LICENSE = "license";

    /**
     * 激活字段 兼容外围系统接入
     */
    String ACTIVE = "active";

    /**
     * 项目的license
     */
    String DIGIT_LICENSE = "made by digit-link";

    /**
     * sys_oauth_client_details 表的字段，不包括client_id、client_secret
     */
    String CLIENT_FIELDS = "client_id, CONCAT('{noop}',client_secret) as client_secret, resource_ids, scope, "
            + "grant_type, redirect_uri, resource_ids, access_token_validity_seconds, "
            + "refresh_token_validity_seconds, additional_information, auto_approve";

    /**
     * JdbcClientDetailsService 查询语句
     */
    String BASE_FIND_STATEMENT = "select " + CLIENT_FIELDS + " from oauth_client";

    /**
     * 按条件client_id 查询
     */
    String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ? and del_flag = 0";

    /**
     * 是否开启 redis json 格式化
     */
    boolean JSON_FORMAT = false;
}
