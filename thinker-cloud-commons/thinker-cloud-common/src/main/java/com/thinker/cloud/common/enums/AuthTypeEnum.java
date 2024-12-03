package com.thinker.cloud.common.enums;

import com.google.common.collect.Maps;
import com.thinker.cloud.common.utils.enums.EnumCacheUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * 授权认证类型
 *
 * @author admin
 **/
@Getter
@AllArgsConstructor
public enum AuthTypeEnum implements IEnumDict<String> {

    /**
     * 客户端admin（WEB B端）授权
     */
    ADMIN("/oauth2/token"),

    /**
     * 客户端member（APP C端）授权
     */
    MEMBER("/member/token"),
    ;

    private final String path;
    private static final Map<String, AuthTypeEnum> AUTH_PATH_MAP = Maps.newHashMap();

    static {
        for (AuthTypeEnum authTypeEnum : values()) {
            AUTH_PATH_MAP.put(authTypeEnum.getPath(), authTypeEnum);
        }
    }

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public String getDesc() {
        return "";
    }

    /**
     * 根据类型获取
     *
     * @param type type
     * @return AuthTypeEnum
     */
    public static AuthTypeEnum resolver(String type) {
        return EnumCacheUtil.loadEnumValue(AuthTypeEnum.class, type, ADMIN);
    }

    /**
     * 根据授权路径获取
     *
     * @param path path
     * @return AuthTypeEnum
     */
    public static AuthTypeEnum resolverByPath(String path) {
        return AUTH_PATH_MAP.get(path);
    }

    /**
     * 获取所有授权paths
     *
     * @return Set<String>
     */
    public static Set<String> getAuthPaths() {
        return AUTH_PATH_MAP.keySet();
    }
}
