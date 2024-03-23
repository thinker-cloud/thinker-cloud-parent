package com.thinker.cloud.core.jackson.serializers.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thinker.cloud.core.enums.IEnumDict;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 枚举序列化
 *
 * @author admin
 **/
@NoArgsConstructor
public class EnumDictSerializer extends JsonSerializer<IEnumDict<?>> {

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
}
