package com.thinker.cloud.core.jackson.serializers.datetime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thinker.cloud.core.utils.JacksonSerializeUtil;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author admin
 **/
@NoArgsConstructor
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    public static final LocalDateSerializer INSTANCE = new LocalDateSerializer();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // 检查是否被@JsonFormat修饰 并获取字段上@JsonFormat中定义的 pattern
        String pattern = JacksonSerializeUtil.getJsonFormatPattern(jsonGenerator);
        if (StringUtils.isNotEmpty(pattern)) {
            jsonGenerator.writeString(localDate.format(DateTimeFormatter.ofPattern(pattern)));
        } else {
            jsonGenerator.writeString(FORMATTER.format(localDate));
        }
    }
}
