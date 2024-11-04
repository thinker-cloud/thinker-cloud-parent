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
public class StrLongTrimZeroConverter implements Converter<String, Long> {

    public static final StrLongTrimZeroConverter INSTANCE = new StrLongTrimZeroConverter();

    @Override
    public Long convert(@NonNull String textNumber) {
        if (textNumber.trim().isEmpty()) {
            return null;
        }

        // 去除字符串尾部多余的0
        if (NumberUtil.isNumber(textNumber)) {
            BigDecimal number = new BigDecimal(textNumber);
            textNumber = number.stripTrailingZeros().toPlainString();
        }

        // 默认处理
        return NumberUtils.parseNumber(textNumber, Long.class);
    }
}
