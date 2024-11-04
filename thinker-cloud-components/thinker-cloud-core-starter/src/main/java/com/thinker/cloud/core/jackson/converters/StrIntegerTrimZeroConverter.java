package com.thinker.cloud.core.jackson.converters;

import cn.hutool.core.util.NumberUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;

/**
 * 去除字符串数字.后面的0
 *
 * @author admin
 **/
public class StrIntegerTrimZeroConverter implements Converter<String, Integer> {

    public static final StrIntegerTrimZeroConverter INSTANCE = new StrIntegerTrimZeroConverter();

    @Override
    public Integer convert(@NonNull String text) {
        if (text.trim().isEmpty()) {
            return null;
        }

        // 去除字符串尾部多余的0
        if (NumberUtil.isNumber(text)) {
            BigDecimal number = new BigDecimal(text);
            text = number.stripTrailingZeros().toPlainString();
        }

        // 默认处理
        return NumberUtils.parseNumber(text, Integer.class);
    }
}
