package com.thinker.cloud.rocketmq.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Description：RocketMQ消费抽象基类
 *
 * @author admin
 */
public abstract class AbstractMQConsumer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMQConsumer.class);

    /**
     * 反序列化解析消息
     *
     * @param message 消息体
     * @return 序列化结果
     */
    protected T parseMessage(MessageExt message) {
        if (message == null || message.getBody() == null) {
            return null;
        }
        final Type type = this.getMessageType();
        if (type instanceof Class) {
            try {
                return JSON.parseObject(new String(message.getBody()), type);
            } catch (JSONException e) {
                LOGGER.error("parse message json fail : {}", e.getMessage());
            }
        } else {
            LOGGER.warn("Parse msg error. {}", message);
        }
        return null;
    }

    protected Map<String, Object> parseExtParam(MessageExt message) {
        Map<String, Object> extMap = new HashMap<>();

        // parse message property
        extMap.put(MessageExtConst.PROPERTY_TOPIC, message.getTopic());
        extMap.putAll(message.getProperties());

        // parse messageExt property
        extMap.put(MessageExtConst.PROPERTY_EXT_BORN_HOST, message.getBornHost());
        extMap.put(MessageExtConst.PROPERTY_EXT_BORN_TIMESTAMP, message.getBornTimestamp());
        extMap.put(MessageExtConst.PROPERTY_EXT_COMMIT_LOG_OFFSET, message.getCommitLogOffset());
        extMap.put(MessageExtConst.PROPERTY_EXT_MSG_ID, message.getMsgId());
        extMap.put(MessageExtConst.PROPERTY_EXT_PREPARED_TRANSACTION_OFFSET, message.getPreparedTransactionOffset());
        extMap.put(MessageExtConst.PROPERTY_EXT_QUEUE_ID, message.getQueueId());
        extMap.put(MessageExtConst.PROPERTY_EXT_QUEUE_OFFSET, message.getQueueOffset());
        extMap.put(MessageExtConst.PROPERTY_EXT_RECONSUME_TIMES, message.getReconsumeTimes());
        extMap.put(MessageExtConst.PROPERTY_EXT_STORE_HOST, message.getStoreHost());
        extMap.put(MessageExtConst.PROPERTY_EXT_STORE_SIZE, message.getStoreSize());
        extMap.put(MessageExtConst.PROPERTY_EXT_STORE_TIMESTAMP, message.getStoreTimestamp());
        extMap.put(MessageExtConst.PROPERTY_EXT_SYS_FLAG, message.getSysFlag());
        extMap.put(MessageExtConst.PROPERTY_EXT_BODY_CRC, message.getBodyCRC());

        return extMap;
    }

    /**
     * 解析消息类型
     *
     * @return 消息类型
     */
    protected Type getMessageType() {
        Type superType = this.getClass().getGenericSuperclass();
        if (superType instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Assert.isTrue(actualTypeArguments.length == 1, "Number of type arguments must be 1");
            return actualTypeArguments[0];
        } else {
            // 如果没有定义泛型，解析为Object
            return Object.class;
        }
    }
}
