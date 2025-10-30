package com.thinker.cloud.common.jackson.serializers.enums;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thinker.cloud.common.enums.IEnumDict;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 枚举序列化
 *
 * @author admin
 **/
@NoArgsConstructor
public class EnumDictSerializer extends JsonSerializer<IEnumDict<?>> implements ObjectSerializer {

    public static final EnumDictSerializer INSTANCE = new EnumDictSerializer();

    @Override
    public void serialize(IEnumDict<?> iEnumDict, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (iEnumDict.isWriteJsonObjectEnabled()) {
            gen.writeStartObject();
            gen.writeStringField("desc", iEnumDict.getDesc());
            gen.writeObjectField("value", iEnumDict.getValue());
            gen.writeEndObject();
        } else {
            gen.writeObject(iEnumDict.getValue());
        }
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
    public void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type fieldType, int features) {
        if (object instanceof IEnumDict<?> iEnumDict) {
            if (iEnumDict.isWriteJsonObjectEnabled()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("value", iEnumDict.getValue());
                jsonObject.put("desc", iEnumDict.getDesc());
                jsonSerializer.write(jsonObject);
            } else {
                jsonSerializer.write(iEnumDict.getValue());
            }
        }
    }
}
