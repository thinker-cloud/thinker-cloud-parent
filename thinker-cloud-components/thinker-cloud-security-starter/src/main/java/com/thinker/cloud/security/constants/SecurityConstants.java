package com.thinker.cloud.security.constants;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

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
     * oauth_access 前缀
     */
    String OAUTH_ACCESS_PREFIX = TOKEN_PREFIX + ":" + OAuth2ParameterNames.ACCESS_TOKEN;

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
     * APP登录URL
     */
    String APP_TOKEN_URL = "/app/token";

    /**
     * 默认登录URL
     */
    String OAUTH_TOKEN_URL = "/oauth2/token";

    /**
     * 社交登录URL
     */
    String SOCIAL_TOKEN_URL = "/social/token";

    /**
     * 请求映射处理器bean名称
     */
    String REQUEST_MAPPING_HANDLER = "requestMappingHandlerMapping";

    /**
     * {bcrypt} 加密的特征码
     */
    String SECURITY_BCRYPT = "{bcrypt}";
    String SECURITY_NOOP = "{noop}";

    /**
     * 用户名
     */
    String USERNAME = "username";

    /**
     * 用户信息
     */
    String DETAILS_USER = "user_info";

    /**
     * 用户ID
     */
    String DETAILS_USER_ID = "user_id";
}
