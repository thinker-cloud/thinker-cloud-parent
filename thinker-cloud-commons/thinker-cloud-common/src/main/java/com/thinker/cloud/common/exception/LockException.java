package com.thinker.cloud.common.exception;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.common.enums.IEnumDict;

import java.io.Serial;

/**
 * 加锁异常类
 *
 * @author admin
 */
public class LockException extends AbstractException {

    @Serial
    private static final long serialVersionUID = 6610083281801529147L;

    public LockException(String message) {
        super(message);
    }

    public LockException(Integer code, String message) {
        super(code, message);
    }

    public LockException(IEnumDict<Integer> responseCode) {
        super(responseCode);
    }

    public LockException(Throwable cause) {
        super(cause);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public static LockException of(String message) {
        return new LockException(message);
    }

    public static LockException of(String message, Object... argArray) {
        return of(StrUtil.format(message, argArray));
    }

    public static LockException of(Integer code, String message) {
        return new LockException(code, message);
    }

    public static LockException of(Integer code, String message, Object... argArray) {
        return of(code, StrUtil.format(message, argArray));
    }
}
