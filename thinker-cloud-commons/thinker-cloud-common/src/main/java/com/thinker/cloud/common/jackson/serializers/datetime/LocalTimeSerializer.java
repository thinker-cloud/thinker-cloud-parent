package com.thinker.cloud.common.jackson.serializers.datetime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thinker.cloud.common.utils.JacksonSerializeUtil;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author admin
 **/
@NoArgsConstructor
public class LocalTimeSerializer extends JsonSerializer<LocalTime> {

    public static final LocalTimeSerializer INSTANCE = new LocalTimeSerializer();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void serialize(LocalTime localTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // 检查是否被@JsonFormat修饰 并获取字段上@JsonFormat中定义的 pattern
        String pattern = JacksonSerializeUtil.getJsonFormatPattern(jsonGenerator);
        if (StringUtils.isNotEmpty(pattern)) {
            jsonGenerator.writeString(localTime.format(DateTimeFormatter.ofPattern(pattern)));
        } else {
            jsonGenerator.writeString(localTime.format(FORMATTER));
        }
    }
}
