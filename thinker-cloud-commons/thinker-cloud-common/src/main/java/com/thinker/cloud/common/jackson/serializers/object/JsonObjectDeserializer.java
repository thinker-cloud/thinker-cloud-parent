package com.thinker.cloud.common.jackson.serializers.object;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Optional;

/**
 * @author admin
 **/
@NoArgsConstructor
@AllArgsConstructor
public class JsonObjectDeserializer extends org.springframework.boot.jackson.JsonObjectDeserializer<Object> implements ContextualDeserializer {

    private JavaType type;

    @Override
    protected Object deserializeObject(JsonParser jsonParser, DeserializationContext context, ObjectCodec codec, JsonNode tree) throws IOException {
        return this.nullSafeValue(tree, (ObjectMapper) codec);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty beanProperty) {
        JavaType type = Optional.ofNullable(context.getContextualType()).orElseGet(() -> beanProperty.getMember().getType());
        return new JsonObjectDeserializer(type);
    }

    /**
     * 帮助程序方法，用于从给定 jsonNode 值中提取值或在 null 节点本身为 nul
     *
     * @param jsonNode 源节点（可能是 null）
     * @param mapper   ObjectMapper
     * @return 节点值或 null
     */
    private Object nullSafeValue(JsonNode jsonNode, ObjectMapper mapper) throws JsonProcessingException {
        Assert.notNull(type, "Type must not be null");
        if (jsonNode == null) {
            return null;
        }
        Class<?> rawClass = type.getRawClass();
        String value = jsonNode.asText();
        if (rawClass == String.class) {
            return value;
        }
        if (rawClass == Boolean.class) {
            return Boolean.valueOf(value);
        }
        if (rawClass == Long.class) {
            return Long.valueOf(value);
        }
        if (rawClass == Integer.class) {
            return Integer.valueOf(value);
        }
        if (rawClass == Short.class) {
            return Short.valueOf(value);
        }
        if (rawClass == Double.class) {
            return Double.valueOf(value);
        }
        if (rawClass == Float.class) {
            return Float.valueOf(value);
        }
        if (rawClass == BigDecimal.class) {
            return new BigDecimal(value);
        }
        if (rawClass == BigInteger.class) {
            return new BigInteger(value);
        }

        // 数组、对象处理
        if (!jsonNode.isEmpty()) {
            if (jsonNode.isArray()) {
                Iterator<JsonNode> iterator = jsonNode.elements();
                while (iterator.hasNext()) {
                    JsonNode next = iterator.next();
                    if (next.isEmpty()) {
                        iterator.remove();
                    }
                }
            }

            String content = jsonNode.toString();
            return mapper.readValue(content, type);
        }

        return null;
    }
}
