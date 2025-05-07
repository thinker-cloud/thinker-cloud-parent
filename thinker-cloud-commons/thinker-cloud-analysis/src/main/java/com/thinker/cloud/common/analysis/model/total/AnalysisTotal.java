package com.thinker.cloud.common.analysis.model.total;

import org.springframework.lang.NonNull;

import java.math.BigDecimal;

/**
 * 分析统计 总计对象
 *
 * @author admin
 */
public interface AnalysisTotal extends Comparable<AnalysisTotal> {

    /**
     * 统计对象名称
     *
     * @return 统计值
     */
    String getName();

    /**
     * set 统计对象名称
     *
     * @param name 对象名称
     * @return this
     */
    AnalysisTotal setName(String name);

    /**
     * 统计对象code
     *
     * @return 统计对象code
     */
    String getCode();

    /**
     * set 统计对象code
     *
     * @param code 统计对象code
     * @return this
     */
    AnalysisTotal setCode(String code);

    /**
     * 统计值
     *
     * @return 统计值
     */
    BigDecimal getValue();

    /**
     * set统计值
     *
     * @param value 统计值
     * @return this
     */
    AnalysisTotal setValue(BigDecimal value);

    /**
     * 同比值
     *
     * @return 同比值
     */
    BigDecimal getSameTimeLastYearValue();

    /**
     * set 同比值
     *
     * @param sameTimeLastYearValue 同比值
     * @return this
     */
    AnalysisTotal setSameTimeLastYearValue(BigDecimal sameTimeLastYearValue);

    /**
     * 获取同比增长率
     *
     * @return 同比增长率
     */
    BigDecimal getSameTimeLastYearGrowthRate();

    /**
     * set 同比增长率
     *
     * @param sameTimeLastYearGrowthRate 同比增长率
     * @return this
     */
    AnalysisTotal setSameTimeLastYearGrowthRate(BigDecimal sameTimeLastYearGrowthRate);

    /**
     * 环比值
     *
     * @return 环比值
     */
    BigDecimal getChainLastPeriodValue();

    /**
     * set 环比值
     *
     * @param chainLastPeriodValue 环比值
     * @return this
     */
    AnalysisTotal setChainLastPeriodValue(BigDecimal chainLastPeriodValue);

    /**
     * 环比增长率
     *
     * @return 环比增长率
     */
    BigDecimal getChainLastPeriodGrowthRate();

    /**
     * set 环比增长率
     *
     * @param chainLastPeriodGrowthRate 环比增长率
     * @return this
     */
    AnalysisTotal setChainLastPeriodGrowthRate(BigDecimal chainLastPeriodGrowthRate);

    /**
     * 排序比较
     *
     * @param total 比较对象
     * @return 排序号
     */
    @Override
    default int compareTo(@NonNull AnalysisTotal total) {
        try {
            int valueSort = total.getValue().compareTo(this.getValue());
            if (valueSort == 0) {
                return this.getName().compareTo(total.getName());
            }
            return valueSort;
        } catch (NullPointerException e) {
            return Integer.MAX_VALUE;
        }
    }
}
