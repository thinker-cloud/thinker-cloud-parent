package com.thinker.cloud.common.exception;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.common.enums.IEnumDict;

import java.io.Serial;

/**
 * 缓存异常类
 *
 * @author admin
 */
public class CacheableException extends AbstractException {

    @Serial
    private static final long serialVersionUID = 6610083281801529147L;

    public CacheableException(String message) {
        super(400, message);
    }

    public CacheableException(Integer code, String message) {
        super(code, message);
    }

    public CacheableException(IEnumDict<Integer> responseCode) {
        super(responseCode);
    }

    public static CacheableException of(String message) {
        return new CacheableException(message);
    }

    public static CacheableException of(String message, Object... argArray) {
        return of(StrUtil.format(message, argArray));
    }

    public static CacheableException of(Integer code, String message) {
        return new CacheableException(code, message);
    }

    public static CacheableException of(Integer code, String message, Object... argArray) {
        return of(code, StrUtil.format(message, argArray));
    }
}
