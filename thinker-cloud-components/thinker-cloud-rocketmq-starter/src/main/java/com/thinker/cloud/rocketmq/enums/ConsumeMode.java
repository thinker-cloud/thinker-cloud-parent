package com.thinker.cloud.rocketmq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消费模式
 *
 * @author admin
 */

@Getter
@AllArgsConstructor
public enum ConsumeMode {

    /**
     * CONCURRENTLY
     * 使用线程池并发消费
     */
    CONCURRENTLY("CONCURRENTLY"),

    /**
     * ORDERLY
     * 单线程消费
     */
    ORDERLY("ORDERLY");

    private final String mode;

}
