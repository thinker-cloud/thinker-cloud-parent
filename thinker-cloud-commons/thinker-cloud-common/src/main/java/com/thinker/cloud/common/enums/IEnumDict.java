package com.thinker.cloud.common.enums;

import com.alibaba.fastjson2.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thinker.cloud.common.jackson.serializers.enums.EnumDictDeserializer;
import com.thinker.cloud.common.jackson.serializers.enums.EnumDictSerializer;

/**
 * 枚举字典 支持将对象反序列化枚举,由于fastJson目前的版本还不支持从父类获取注解
 * 所以需要在实现类上注解:@JSONType(deserializer = EnumDictDeserializer.class)
 *
 * @author admin
 **/
@JSONType(serializer = EnumDictSerializer.class, deserializer = EnumDictDeserializer.class)
@JsonSerialize(using = EnumDictSerializer.class)
@JsonDeserialize(using = EnumDictDeserializer.class)
public interface IEnumDict<V> {

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
    @JsonIgnore
    default boolean isWriteJsonObjectEnabled() {
        return true;
    }

}
