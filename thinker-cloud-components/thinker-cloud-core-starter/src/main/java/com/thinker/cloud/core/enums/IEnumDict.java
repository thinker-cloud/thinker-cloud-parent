package com.thinker.cloud.core.enums;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson2.annotation.JSONType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thinker.cloud.core.serializer.jackson.enums.EnumDictDeserializer;
import com.thinker.cloud.core.serializer.jackson.enums.EnumDictSerializer;

import java.lang.reflect.Type;

/**
 * 枚举字典 支持将对象反序列化枚举,由于fastJson目前的版本还不支持从父类获取注解
 * 所以需要在实现类上注解:@JSONType(deserializer = EnumDictDeserializer.class)
 *
 * @author admin
 **/
@JSONType(deserializer = EnumDictDeserializer.class)
@JsonSerialize(using = EnumDictSerializer.class)
@JsonDeserialize(using = EnumDictDeserializer.class)
public interface IEnumDict<V> extends ObjectSerializer {

    /**
     * 枚举值
     *
     * @return V
     */
    V getValue();

    /**
     * 枚举描述
     *
     * @return String
     */
    String getDesc();

    /**
     * 是否在序列化为json的时候, 将枚举以对象方式序列化
     *
     * @return boolean
     */
    default boolean isWriteJsonObjectEnabled() {
        return true;
    }

    /**
     * fastJson 序列化
     *
     * @param jsonSerializer jsonSeriliazer
     * @param object         object
     * @param fieldName      fieldType
     * @param fieldType      fieldType
     * @param features       features
     */
    @Override
    default void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type fieldType, int features) {
        if (this.isWriteJsonObjectEnabled()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", this.getValue());
            jsonObject.put("desc", this.getDesc());
            jsonSerializer.write(jsonObject);
        } else {
            jsonSerializer.write(this.getValue());
        }
    }
}
