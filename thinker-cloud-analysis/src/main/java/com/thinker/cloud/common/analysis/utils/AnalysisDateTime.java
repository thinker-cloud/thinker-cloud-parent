package com.thinker.cloud.common.analysis.utils;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 分析时间对象
 * <p>
 * 名词解释
 * 同比：去年同期
 * 环比：时间维度为日的，为昨日、时间维度为月的，为上月、时间维度为年的，为去年
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
public class AnalysisDateTime {

    /**
     * 同比:开始时间
     */
    private LocalDateTime startSamePeriodLastYearTime;

    /**
     * 同比:结束时间
     */
    private LocalDateTime endSamePeriodLastYearTime;

    /**
     * 环比:开始时间
     */
    private LocalDateTime startChainLastPeriodTime;

    /**
     * 环比:结束时间
     */
    private LocalDateTime endChainLastPeriodTime;

    /**
     * 符合环比条件，这个值为true时，才会有环比的开始与结束时间
     */
    private Boolean isCorrectChainLastPeriod;

    /**
     * 环比名称 昨日同期/上月同期/去年同期
     */
    private String chainLastPeriodTimeName;
}
