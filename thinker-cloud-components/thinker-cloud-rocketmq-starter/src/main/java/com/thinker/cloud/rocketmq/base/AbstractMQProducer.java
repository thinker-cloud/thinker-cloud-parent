package com.thinker.cloud.rocketmq.base;

import com.alibaba.fastjson.JSON;
import com.thinker.cloud.rocketmq.exception.MQException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;

/**
 * RocketMQ的生产者的抽象基类
 *
 * @author admin
 */
@Slf4j
public abstract class AbstractMQProducer {

    private final static MessageQueueSelector messageQueueSelector = new SelectMessageQueueByHash();

    public AbstractMQProducer() {
    }

    private DefaultMQProducer producer;

    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    public DefaultMQProducer getProducer() {
        return producer;
    }

    /**
     * 同步发送消息
     *
     * @param message 消息体
     * @throws MQException 消息异常
     */
    public void syncSend(Message message) throws MQException {
        try {
            SendResult sendResult = producer.send(message);
            log.debug("send rocketmq message ,messageId : {}", sendResult.getMsgId());
            this.doAfterSyncSend(message, sendResult);
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", message.getTopic(), JSON.toJSONString(message));
            throw new MQException("消息发送失败，topic :" + message.getTopic() + ",e:" + e.getMessage());
        }
    }


    /**
     * 同步发送消息
     *
     * @param message 消息体
     * @param hashKey 用于hash后选择queue的key
     * @throws MQException 消息异常
     */
    public void syncSendOrderly(Message message, String hashKey) throws MQException {
        if (StringUtils.isEmpty(hashKey)) {
            // fall back to normal
            syncSend(message);
        }
        try {
            SendResult sendResult = producer.send(message, messageQueueSelector, hashKey);
            log.debug("send rocketmq message orderly ,messageId : {}", sendResult.getMsgId());
            this.doAfterSyncSend(message, sendResult);
        } catch (Exception e) {
            log.error("顺序消息发送失败，topic : {}, msgObj {}", message.getTopic(), message);
            throw new MQException("顺序消息发送失败，topic :" + message.getTopic() + ",e:" + e.getMessage());
        }
    }

    /**
     * 重写此方法处理发送后的逻辑
     *
     * @param message    发送消息体
     * @param sendResult 发送结果
     */
    public void doAfterSyncSend(Message message, SendResult sendResult) {
    }

    /**
     * 异步发送消息
     *
     * @param message      msgObj
     * @param sendCallback 回调
     * @throws MQException 消息异常
     */
    public void asyncSend(Message message, SendCallback sendCallback) throws MQException {
        try {
            producer.send(message, sendCallback);
            log.debug("send rocketmq message async");
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", message.getTopic(), JSON.toJSONString(message));
            throw new MQException("消息发送失败，topic :" + message.getTopic() + ",e:" + e.getMessage());
        }
    }
}
