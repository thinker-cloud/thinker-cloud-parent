package com.thinker.cloud.rocketmq.trace.common;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;


@Data
public class OnsTraceTransferBean {

    private String transData;

    private Set<String> transKey = new HashSet<String>();
}
