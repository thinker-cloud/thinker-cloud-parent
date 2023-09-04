package com.thinker.cloud.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码
 *
 * @author admin
 **/
@Getter
@AllArgsConstructor
public enum ResponseCode implements IEnumDict<Integer> {

    //
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "身份未认证"),
    INVALID_TOKEN(401, "Token失效，请重新登录"),
    NOT_FOUND(404, "请求路径不存在"),
    FAILURE(500, "服务器异常，请联系管理员"),
    UNAVAILABLE(503, "依赖服务异常，请稍后再试"),
    ;

    private final Integer code;
    private final String desc;

    @Override
    public Integer getValue() {
        return this.code;
    }
}
