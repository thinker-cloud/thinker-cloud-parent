package com.thinker.cloud.core.serializer.jackson.enums;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ObjectDeserializer;
import com.alibaba.fastjson2.JSONReader;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.thinker.cloud.core.enums.IEnumDict;
import com.thinker.cloud.core.exception.ValidationException;
import com.thinker.cloud.core.utils.enums.EnumDictUtil;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 枚举反序列化
 *
 * @author admin
 **/
@SuppressWarnings("all")
@Slf4j
@NoArgsConstructor
public class EnumDictDeserializer extends JsonDeserializer<Object> implements ObjectDeserializer {

    public static final EnumDictDeserializer INSTANCE = new EnumDictDeserializer();

    @Override
    @SneakyThrows
    public Object deserialize(JsonParser jsonParser, DeserializationContext context) {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String currentName = jsonParser.currentName();
        Object currentValue = jsonParser.getCurrentValue();
        Class findPropertyType;
        if (StringUtils.isEmpty(currentName) || StringUtils.isEmpty(currentValue)) {
            return null;
        } else {
            findPropertyType = BeanUtils.findPropertyType(currentName, currentValue.getClass());
        }
        Supplier<ValidationException> exceptionSupplier = () -> {
            List<Object> values = Stream.of(findPropertyType.getEnumConstants())
                    .map(Enum.class::cast)
                    .map(e -> {
                        if (e instanceof IEnumDict) {
                            return ((IEnumDict) e).getValue();
                        }
                        return e.name();
                    }).collect(Collectors.toList());

            return new ValidationException("参数[" + currentName + "]在选项中不存在");
        };
        if (IEnumDict.class.isAssignableFrom(findPropertyType) && findPropertyType.isEnum()) {
            if (node.isObject()) {
                return EnumDictUtil.findByValue(findPropertyType, node.get("value").textValue()).orElseThrow(exceptionSupplier);
            }
            if (node.isNumber()) {
                return EnumDictUtil.find(findPropertyType, node.numberValue()).orElseThrow(exceptionSupplier);
            }
            if (node.isTextual()) {
                return EnumDictUtil.find(findPropertyType, node.textValue()).orElseThrow(exceptionSupplier);
            }
            throw new ValidationException("参数[" + currentName + "]在选项中不存在");
        }
        if (findPropertyType.isEnum()) {
            return Stream.of(findPropertyType.getEnumConstants())
                    .filter(o -> {
                        if (node.isTextual()) {
                            return node.textValue().equalsIgnoreCase(((Enum) o).name());
                        }
                        if (node.isNumber()) {
                            return node.intValue() == ((Enum) o).ordinal();
                        }
                        return false;
                    })
                    .findAny()
                    .orElseThrow(exceptionSupplier);
        }
        log.warn("unsupported deserialize enum json : {}", node);
        return null;
    }

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        try {
            Object value;
            final JSONLexer lexer = parser.getLexer();
            final JSONReader reader = lexer.getReader();
            if (reader.isNumber()) {
                int intValue = lexer.intValue();
                lexer.nextToken(JSONToken.COMMA);
                return (T) EnumDictUtil.find((Class) type, intValue);
            } else if (reader.isString()) {
                String name = lexer.stringVal();
                lexer.nextToken(JSONToken.COMMA);

                if (name.isEmpty()) {
                    return null;
                }
                return (T) EnumDictUtil.find((Class) type, name).orElse(null);
            } else if (reader.isNull()) {
                lexer.nextToken(JSONToken.COMMA);
                return null;
            } else {
                value = parser.parse();
                if (value instanceof Map) {
                    return (T) EnumDictUtil.find(((Class) type), ((Map) value).get("value")).orElseGet(() ->
                            EnumDictUtil.find(((Class) type), ((Map) value).get("text")).orElse(null));
                }
            }
            throw new JSONException("parse enum " + type + " error, value : " + value);
        } catch (JSONException e) {
            throw e;
        } catch (Exception e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
