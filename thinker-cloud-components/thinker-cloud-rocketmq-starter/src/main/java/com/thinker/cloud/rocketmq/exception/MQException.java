package com.thinker.cloud.rocketmq.exception;

import com.thinker.cloud.common.exception.AbstractException;

/**
 * RocketMQ的自定义异常
 *
 * @author admin
 */
public class MQException extends AbstractException {

    public MQException(String msg) {
        super(msg);
    }

}
