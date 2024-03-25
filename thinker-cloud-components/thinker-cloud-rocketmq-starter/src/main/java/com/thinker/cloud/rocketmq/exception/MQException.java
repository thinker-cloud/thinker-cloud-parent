package com.thinker.cloud.rocketmq.exception;

import com.thinker.cloud.core.exception.AbstractException;

/**
 * RocketMQ的自定义异常
 */
public class MQException extends AbstractException {
    public MQException(String msg) {
        super(msg);
    }
}
