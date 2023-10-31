package com.thinker.cloud.common.analysis.model.trend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 统计趋势线VO
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrendLineVO {

    /**
     * 趋势线名称
     */
    private String name;

    /**
     * 趋势线code
     */
    private String code;

    /**
     * 单位
     */
    private String unit;

    /**
     * 趋势线总值
     */
    private BigDecimal total;

    /**
     * 趋势线坐标列表
     */
    private List<TrendIndexVO> list;

    /**
     * 根据点对象列表计算总值
     */
    public TrendLineVO calTotalValue() {
        this.total = Optional.ofNullable(list)
                .map(ls -> ls.stream()
                        .map(TrendIndexVO::getYValue)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
        return this;
    }
}
