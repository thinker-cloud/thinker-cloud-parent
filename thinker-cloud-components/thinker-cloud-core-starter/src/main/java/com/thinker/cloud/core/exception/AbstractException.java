package com.thinker.cloud.core.exception;

import com.thinker.cloud.core.enums.IEnumDict;
import lombok.Getter;

import java.io.Serial;

/**
 * 异常抽象基类
 *
 * @author admin
 **/
@Getter
public abstract class AbstractException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2709941529299230717L;

    private Integer code = 500;

    public AbstractException(String message) {
        super(message);
    }

    public AbstractException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public AbstractException(IEnumDict<Integer> responseCode) {
        super(responseCode.getDesc());
        this.code = responseCode.getValue();
    }

    public AbstractException(Throwable cause) {
        super(cause);
    }

    public AbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
