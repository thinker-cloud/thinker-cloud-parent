package com.thinker.cloud.rocketmq.trace.common;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OnsTraceContext implements Comparable<OnsTraceContext> {
    /**
     * 轨迹数据的类型，Pub,SubBefore,SubAfter
     */
    private OnsTraceType traceType;
    /**
     * 记录时间
     */
    private long timeStamp = System.currentTimeMillis();
    /**
     * Region信息
     */
    private String regionId = "";
    /**
     * 发送组或者消费组名
     */
    private String groupName = "";
    /**
     * 耗时，单位ms
     */
    private int costTime = 0;
    /**
     * 消费状态，成功与否
     */
    private boolean isSuccess = true;
    /**
     * UUID,用于匹配消费前和消费后的数据
     */
    private String requestId = UUID.randomUUID().toString().replaceAll("-", "");
    /**
     * 针对每条消息的轨迹数据
     */
    private List<OnsTraceBean> traceBeans;

    @Override
    public int compareTo(OnsTraceContext o) {
        return (int) (this.timeStamp - o.getTimeStamp());
    }
}
