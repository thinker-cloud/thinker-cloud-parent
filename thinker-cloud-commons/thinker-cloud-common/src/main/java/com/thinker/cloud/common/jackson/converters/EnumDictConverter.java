package com.thinker.cloud.common.jackson.converters;

import com.thinker.cloud.common.enums.IEnumDict;
import com.thinker.cloud.common.exception.ValidationException;
import com.thinker.cloud.common.utils.enums.EnumDictUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.NonNull;

/**
 * 枚举转化工厂
 *
 * @author admin
 */
public class EnumDictConverter implements ConverterFactory<String, IEnumDict<?>> {

    public static final EnumDictConverter INSTANCE = new EnumDictConverter();

    @NonNull
    @Override
    public <T extends IEnumDict<?>> Converter<String, T> getConverter(@NonNull Class<T> aClass) {
        return value -> EnumDictUtil.findByValue(aClass, value).orElseThrow(() -> new ValidationException("参数[" + value + "]在选项中不存在"));
    }

}
