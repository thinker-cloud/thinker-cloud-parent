package com.thinker.cloud.core.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Jackson序列化工具类
 *
 * @author admin
 **/
@UtilityClass
public class JacksonSerializeUtil {

    /**
     * 获取当前字段class
     *
     * @param jsonParser jsonParser
     * @return Class<?>
     */
    public static Class<?> getFieldClass(JsonParser jsonParser) {
        Class<?> clazz = jsonParser.getCurrentValue().getClass();
        try {
            String currentName = jsonParser.getCurrentName();
            Field declaredField = clazz.getDeclaredField(currentName);
            return declaredField.getType();
        } catch (NoSuchFieldException | IOException ignored) {
        }
        return null;
    }

    /**
     * 获取字段上@JsonFormat中定义的 pattern
     *
     * @param jsonParser jsonParser
     * @return String
     */
    public static String getJsonFormatPattern(JsonParser jsonParser) {
        // 获取value来源的类
        Class<?> aClass = jsonParser.getCurrentValue().getClass();

        try {
            // 获取字段名
            String currentName = jsonParser.getCurrentName();

            // 获取字段
            Field declaredField = aClass.getDeclaredField(currentName);

            // 是否被@JsonFormat修饰
            if (declaredField.isAnnotationPresent(JsonFormat.class)) {
                return declaredField.getAnnotation(JsonFormat.class).pattern();
            }
        } catch (NoSuchFieldException | IOException ignored) {
        }
        return null;
    }

    /**
     * 获取字段上@JsonFormat中定义的 pattern
     *
     * @param jsonGenerator jsonGenerator
     * @return String
     */
    public static String getJsonFormatPattern(JsonGenerator jsonGenerator) {
        // 获取value来源的类
        Class<?> aClass = jsonGenerator.getCurrentValue().getClass();

        // 获取字段名
        String currentName = jsonGenerator.getOutputContext().getCurrentName();

        try {
            // 获取字段
            Field declaredField = aClass.getDeclaredField(currentName);

            // 是否被@JsonFormat修饰
            if (declaredField.isAnnotationPresent(JsonFormat.class)) {
                return declaredField.getAnnotation(JsonFormat.class).pattern();
            }
        } catch (NoSuchFieldException ignored) {
        }
        return null;
    }
}
