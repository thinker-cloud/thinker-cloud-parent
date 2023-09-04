package com.thinker.cloud.core.serializer.jackson.datetime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thinker.cloud.core.utils.JacksonSerializeUtil;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 序列化
 *
 * @author admin
 */
@NoArgsConstructor
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // 检查是否被@JsonFormat修饰 并获取字段上@JsonFormat中定义的 pattern
        String pattern = JacksonSerializeUtil.getJsonFormatPattern(jsonGenerator);
        if (StringUtils.isNotEmpty(pattern)) {
            jsonGenerator.writeString(localDateTime.format(DateTimeFormatter.ofPattern(pattern)));
        } else {
            jsonGenerator.writeString(FORMATTER.format(localDateTime));
        }
    }
}
