package com.thinker.cloud.core.sensitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.thinker.cloud.core.utils.DesensitizedUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Objects;

/**
 * 脱敏序列化
 *
 * @author admin
 */
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("all")
public class SensitiveSerialize extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveTypeEnum type;
    private Integer prefixNoMaskLen;
    private Integer suffixNoMaskLen;
    private String maskStr;

    @Override
    public void serialize(final String origin, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        switch (type) {
            case CHINESE_NAME -> jsonGenerator.writeString(DesensitizedUtil.chineseName(origin));
            case ID_CARD -> jsonGenerator.writeString(DesensitizedUtil.idCardNum(origin));
            case FIXED_PHONE -> jsonGenerator.writeString(DesensitizedUtil.fixedPhone(origin));
            case MOBILE_PHONE -> jsonGenerator.writeString(DesensitizedUtil.mobilePhone(origin));
            case ADDRESS -> jsonGenerator.writeString(DesensitizedUtil.address(origin));
            case EMAIL -> jsonGenerator.writeString(DesensitizedUtil.email(origin));
            case BANK_CARD -> jsonGenerator.writeString(DesensitizedUtil.bankCard(origin));
            case PASSWORD -> jsonGenerator.writeString(DesensitizedUtil.password(origin));
            case KEY -> jsonGenerator.writeString(DesensitizedUtil.key(origin));
            case CUSTOMER ->
                    jsonGenerator.writeString(DesensitizedUtil.desValue(origin, prefixNoMaskLen, suffixNoMaskLen, maskStr));
            default -> throw new IllegalArgumentException("Unknow sensitive type enum " + type);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializerProvider,
                                              final BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                Sensitive sensitive = beanProperty.getAnnotation(Sensitive.class);
                if (sensitive == null) {
                    sensitive = beanProperty.getContextAnnotation(Sensitive.class);
                }
                if (sensitive != null) {
                    return new SensitiveSerialize(sensitive.type(), sensitive.prefixNoMaskLen(), sensitive.suffixNoMaskLen(), sensitive.maskStr());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }
}

