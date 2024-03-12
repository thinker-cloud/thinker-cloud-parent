package com.thinker.cloud.common.analysis.model.count;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 数量统计VO
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountIntegrationVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5235899086991986929L;

    {
        this.total = 0;
        this.totalValue = BigDecimal.ZERO;
    }

    /**
     * 统计总数
     */
    private Integer total;

    /**
     * 统计总数值
     */
    private BigDecimal totalValue;

    /**
     * 统计项列表
     */
    private List<CountItemVO> countItems;

    /**
     * 计算总数和百分比
     *
     * @return CountIntegrationVO
     */
    public CountIntegrationVO calculateAll() {
        calculateTotal();
        calculateTotalValue();
        calculateThePercentage();
        return this;
    }

    /**
     * 设置统计项并计算总数和百分比
     *
     * @param countItemList 统计项列表
     * @return CountIntegrationVO
     */
    public CountIntegrationVO calculateAll(List<CountItemVO> countItemList) {
        this.countItems = countItemList;
        calculateTotal();
        calculateTotalValue();
        calculateThePercentage();
        return this;
    }

    /**
     * 计算总数
     *
     * @return CountIntegrationVO
     */
    public CountIntegrationVO calculateTotal() {
        this.total = countItems.stream()
                .mapToInt(CountItemVO::getCount)
                .sum();
        return this;
    }

    /**
     * 计算总数值
     *
     * @return CountIntegrationVO
     */
    public CountIntegrationVO calculateTotalValue() {
        this.totalValue = countItems.stream()
                .map(CountItemVO::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this;
    }

    /**
     * 计算占比
     *
     * @return CountIntegrationVO
     */
    public CountIntegrationVO calculateThePercentage() {
        if (total != null && total != 0) {
            BigDecimal totalValue = new BigDecimal(total);
            countItems.forEach(countItemVO -> {
                BigDecimal scale = new BigDecimal(countItemVO.getCount())
                        .divide(totalValue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                countItemVO.setScale(scale);
            });
        }
        return this;
    }


    /**
     * 数量统计项VO
     *
     * @author admin
     */
    @Data
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CountItemVO implements Serializable {

        @Serial
        private static final long serialVersionUID = -2480767290309279393L;

        {
            this.count = 0;
            this.value = BigDecimal.ZERO;
            this.scale = BigDecimal.ZERO;
        }

        /**
         * 编号
         */
        private String code;

        /**
         * 名称
         */
        private String name;

        /**
         * 数量
         */
        private Integer count;

        /**
         * 数值
         */
        private BigDecimal value;

        /**
         * 占总数比例
         */
        private BigDecimal scale;
    }
}
