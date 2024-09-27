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
    String DIGIT_PREFIX = "THINKER_";

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
     * 客户端ID
     */
    String CLIENT_ID = "clientId";

    /**
     * grant_type
     */
    String REFRESH_TOKEN = "refresh_token";

    /**
     * 请求映射处理器bean名称
     */
    String REQUEST_MAPPING_HANDLER = "requestMappingHandlerMapping";

    /**
     * {bcrypt} 加密的特征码
     */
    String SECURITY_BCRYPT = "{bcrypt}";
    String SECURITY_NOOP = "{noop}";
}
