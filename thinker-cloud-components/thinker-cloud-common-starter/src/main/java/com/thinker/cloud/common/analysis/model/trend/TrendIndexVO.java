package com.thinker.cloud.common.analysis.model.trend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 趋势图坐标点VO
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrendIndexVO {

    /**
     * x值
     */
    private BigDecimal xValue;

    /**
     * x文本值
     */
    private String xValueText;

    /**
     * x值(时间格式)
     */
    private Date xValueDateTime;

    /**
     * y值
     */
    private BigDecimal yValue;

    /**
     * y文本值
     */
    private String yValueText;

    public TrendIndexVO(BigDecimal xValue, BigDecimal yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public TrendIndexVO(String xValueText, BigDecimal yValue) {
        this.xValueText = xValueText;
        this.yValue = yValue;
    }

    public TrendIndexVO(String xValueText, Date xValueDateTime, BigDecimal yValue) {
        this.xValueText = xValueText;
        this.yValue = yValue;
        this.xValueDateTime = xValueDateTime;
    }
}
