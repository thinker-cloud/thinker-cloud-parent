package com.thinker.cloud.core.exception;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.enums.IEnumDict;

import java.io.Serial;

/**
 * 验证异常类
 *
 * @author admin
 **/
public class ValidationException extends AbstractException {

    @Serial
    private static final long serialVersionUID = -4628224968406606610L;

    public ValidationException(String message) {
        super(400, message);
    }

    public ValidationException(Integer code, String message) {
        super(code, message);
    }

    public ValidationException(IEnumDict<Integer> responseCode) {
        super(responseCode);
    }

    public static ValidationException of(String message) {
        return new ValidationException(message);
    }

    public static ValidationException of(String message, Object... argArray) {
        return of(StrUtil.format(message, argArray));
    }

    public static ValidationException of(Integer code, String message) {
        return new ValidationException(code, message);
    }

    public static ValidationException of(Integer code, String message, Object... argArray) {
        return of(code, StrUtil.format(message, argArray));
    }
}
