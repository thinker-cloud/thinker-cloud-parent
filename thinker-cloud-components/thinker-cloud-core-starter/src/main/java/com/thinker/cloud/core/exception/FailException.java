package com.thinker.cloud.core.exception;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.enums.IEnumDict;
import lombok.Getter;

import java.io.Serial;

/**
 * 自定义业务异常类
 *
 * @author admin
 */
@Getter
public class FailException extends AbstractException {

    @Serial
    private static final long serialVersionUID = -3400616766096741409L;

    private Object data;

    public FailException(String message) {
        super(message);
    }

    public FailException(Integer code, String message) {
        super(code, message);
    }

    public FailException(IEnumDict<Integer> responseCode) {
        super(responseCode);
    }

    public FailException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailException(Object data, Integer code, String message) {
        super(code, message);
        this.data = data;
    }

    public static FailException of(String message) {
        return new FailException(message);
    }

    public static FailException of(String message, Object... argArray) {
        return of(StrUtil.format(message, argArray));
    }

    public static FailException of(Integer code, String message) {
        return new FailException(code, message);
    }

    public static FailException of(Integer code, String message, Object... argArray) {
        return of(code, StrUtil.format(message, argArray));
    }

    public static FailException of(Object data, Integer code, String message) {
        return new FailException(data, code, message);
    }

    public static FailException of(Object data, Integer code, String message, Object... argArray) {
        return of(data, code, StrUtil.format(message, argArray));
    }
}
