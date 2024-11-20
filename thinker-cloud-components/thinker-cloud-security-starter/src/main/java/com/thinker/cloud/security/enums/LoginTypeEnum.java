package com.thinker.cloud.security.enums;

import com.thinker.cloud.core.enums.IEnumDict;
import com.thinker.cloud.core.utils.enums.EnumCacheUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录认证类型
 *
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum implements IEnumDict<String> {

    //
    PASSWORD("password", "账号密码登录"),
    SMS("sms", "验证码登录"),
    WX("wx", "微信登录"),
    WX_MINI("wx_mini", "微信小程序登录");

    private final String value;
    private final String desc;

    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * 根据类型获取枚举
     *
     * @param type type
     * @return AuthTypeEnum
     */
    public static LoginTypeEnum resolver(String type) {
        return EnumCacheUtil.loadEnumValue(LoginTypeEnum.class, type, null);
    }
}
