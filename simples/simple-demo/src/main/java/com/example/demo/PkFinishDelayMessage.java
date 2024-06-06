package com.example.demo;


import com.thinker.cloud.redis.delayqueue.core.DelayMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 91851
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PkFinishDelayMessage extends DelayMessage {

    private Long pkId;
}
