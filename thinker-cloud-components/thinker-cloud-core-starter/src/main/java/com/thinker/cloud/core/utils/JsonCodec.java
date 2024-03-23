package com.thinker.cloud.core.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.thinker.cloud.core.jackson.serializers.datetime.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.TimeZone;


/**
 * @author admin
 */
@UtilityClass
public class JsonCodec {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());

        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.setTimeZone(TimeZone.getDefault());
        OBJECT_MAPPER.activateDefaultTypingAsProperty(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, "@_class");
    }

    public static String encodePretty(Object instance) {
        if (instance == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(instance);
        } catch (JsonProcessingException arg1) {
            throw new RuntimeException(arg1);
        }
    }

    public static String encode(Object instance) {
        if (instance == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(instance);
        } catch (JsonProcessingException arg1) {
            throw new RuntimeException(arg1);
        }
    }

    public static <T> T decode(String json, Class<T> clazz) {
        if (StrUtil.isEmpty(json)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException arg2) {
            throw new RuntimeException(arg2);
        }
    }

    public static ObjectMapper getJsonMapper() {
        return OBJECT_MAPPER;
    }
}
