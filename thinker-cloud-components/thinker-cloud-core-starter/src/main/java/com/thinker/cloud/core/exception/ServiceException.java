package com.thinker.cloud.core.exception;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.enums.IEnumDict;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 自定义服务异常类
 *
 * @author admin
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends AbstractException {
    @Serial
    private static final long serialVersionUID = -3400616766096741409L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Integer code, String message) {
        super(code, message);
    }

    public ServiceException(IEnumDict<Integer> responseCode) {
        super(responseCode);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ServiceException of(String message) {
        return new ServiceException(message);
    }

    public static ServiceException of(String message, Object... argArray) {
        return of(StrUtil.format(message, argArray));
    }

    public static ServiceException of(Integer code, String message) {
        return new ServiceException(code, message);
    }

    public static ServiceException of(Integer code, String message, Object... argArray) {
        return of(code, StrUtil.format(message, argArray));
    }
}
