package com.thinker.cloud.rocketmq.trace.common;


import com.thinker.cloud.rocketmq.trace.utils.MixUtils;
import lombok.Data;
import org.apache.rocketmq.common.message.MessageType;

@Data
public class OnsTraceBean {

    private static String LOCAL_ADDRESS = MixUtils.getLocalAddress();
    private String topic = "";
    private String msgId = "";
    private String offsetMsgId = "";
    private String tags = "";
    private String keys = "";
    private String storeHost = LOCAL_ADDRESS;
    private String clientHost = LOCAL_ADDRESS;
    private long storeTime;
    private int retryTimes;
    private int bodyLength;
    private MessageType msgType;
}
