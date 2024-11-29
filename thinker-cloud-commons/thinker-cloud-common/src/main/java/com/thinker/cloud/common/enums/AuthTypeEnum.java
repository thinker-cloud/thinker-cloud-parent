package com.thinker.cloud.common.enums;

import com.thinker.cloud.common.utils.enums.EnumCacheUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

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
     * 获取所有授权paths
     *
     * @return List<String>
     */
    public static List<String> getAuthPaths() {
        return Arrays.stream(values()).map(AuthTypeEnum::getPath).toList();
    }
}
