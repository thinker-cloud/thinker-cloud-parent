package com.thinker.cloud.common.analysis.model.total;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 分析统计 总计对象VO
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisTotalVO implements AnalysisTotal {

    /**
     * 统计对象名称
     */
    private String name;

    /**
     * 统计对象标记
     */
    private String code;

    /**
     * 统计值
     */
    private BigDecimal value;

    /**
     * 单位
     */
    private String unit;

    /**
     * 去年同比值
     */
    private BigDecimal sameTimeLastYearValue;

    /**
     * 去年同比增长率
     */
    private BigDecimal sameTimeLastYearGrowthRate;

    /**
     * 环比值
     */
    private BigDecimal chainLastPeriodValue;

    /**
     * 环比增长率
     */
    private BigDecimal chainLastPeriodGrowthRate;
}
