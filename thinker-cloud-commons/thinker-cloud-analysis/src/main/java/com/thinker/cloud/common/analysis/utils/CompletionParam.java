package com.thinker.cloud.common.analysis.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 趋势点补全参数
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionParam {
    /**
     * 补全初始值
     */
    private BigDecimal initValueY;
    /**
     * 时间倒序生成
     */
    private boolean reverseTime;
    /**
     * y值的小数点数(精度处理)
     */
    private Integer numberOfDecimals;
}
