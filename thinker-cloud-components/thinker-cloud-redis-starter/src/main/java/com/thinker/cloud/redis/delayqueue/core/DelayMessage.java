package com.thinker.cloud.redis.delayqueue.core;


import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 延迟消息对象
 *
 * @author admin
 */
@ToString
public abstract class DelayMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
