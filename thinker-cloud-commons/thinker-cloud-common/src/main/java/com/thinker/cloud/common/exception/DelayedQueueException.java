package com.thinker.cloud.common.exception;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.common.enums.IEnumDict;

import java.io.Serial;

/**
 * 延迟队列异常类
 *
 * @author admin
 */
public class DelayedQueueException extends AbstractException {

    @Serial
    private static final long serialVersionUID = -3080503748380373368L;

    public DelayedQueueException(String message) {
        super(message);
    }

    public DelayedQueueException(Integer code, String message) {
        super(code, message);
    }

    public DelayedQueueException(IEnumDict<Integer> responseCode) {
        super(responseCode);
    }

    public static DelayedQueueException of(String message) {
        return new DelayedQueueException(message);
    }

    public static DelayedQueueException of(String message, Object... argArray) {
        return of(StrUtil.format(message, argArray));
    }

    public static DelayedQueueException of(Integer code, String message) {
        return new DelayedQueueException(code, message);
    }

    public static DelayedQueueException of(Integer code, String message, Object... argArray) {
        return of(code, StrUtil.format(message, argArray));
    }
}
