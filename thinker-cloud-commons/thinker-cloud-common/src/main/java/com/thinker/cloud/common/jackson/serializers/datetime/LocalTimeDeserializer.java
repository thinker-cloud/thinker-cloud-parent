package com.thinker.cloud.common.jackson.serializers.datetime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author admin
 **/
@NoArgsConstructor
public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    public static final LocalTimeDeserializer INSTANCE = new LocalTimeDeserializer();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FORMATTER1 = DateTimeFormatter.ofPattern("HH:mm:ss.n");

    @Override
    public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String text = jsonParser.getText();
        return text.contains(".") ? LocalTime.parse(text, FORMATTER1) : LocalTime.parse(text, FORMATTER);
    }
}
