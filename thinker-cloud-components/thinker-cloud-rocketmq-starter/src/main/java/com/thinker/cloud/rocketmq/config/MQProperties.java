package com.thinker.cloud.rocketmq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RocketMQ的配置参数
 *
 * @author admin
 */
@Data
@ConfigurationProperties(prefix = "spring.rocketmq")
public class MQProperties {
    /**
     * config name server address
     */
    private String nameServerAddress;
    /**
     * config producer group , default to DPG+RANDOM UUID like DPG-fads-3143-123d-1111
     */
    private String producerGroup;
    /**
     * config send message timeout
     */
    private Integer sendMsgTimeout = 3000;
    /**
     * switch of trace message consumer: send message consumer info to topic: rmq_sys_TRACE_DATA
     */
    private Boolean traceEnabled = Boolean.TRUE;

    /**
     * switch of send message with vip channel
     */
    private Boolean vipChannelEnabled = Boolean.TRUE;
}
