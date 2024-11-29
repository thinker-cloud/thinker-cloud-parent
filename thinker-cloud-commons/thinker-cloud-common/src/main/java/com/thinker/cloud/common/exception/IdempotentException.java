package com.thinker.cloud.common.exception;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.common.enums.IEnumDict;

import java.io.Serial;

/**
 * 幂等性异常类
 *
 * @author admin
 */
public class IdempotentException extends AbstractException {

    @Serial
    private static final long serialVersionUID = 6610083281801529147L;

    public IdempotentException(String message) {
        super(400, message);
    }

    public IdempotentException(Integer code, String message) {
        super(code, message);
    }

    public IdempotentException(IEnumDict<Integer> responseCode) {
        super(responseCode);
    }

    public static IdempotentException of(String message) {
        return new IdempotentException(message);
    }

    public static IdempotentException of(String message, Object... argArray) {
        return of(StrUtil.format(message, argArray));
    }

    public static IdempotentException of(Integer code, String message) {
        return new IdempotentException(code, message);
    }

    public static IdempotentException of(Integer code, String message, Object... argArray) {
        return of(code, StrUtil.format(message, argArray));
    }
}
