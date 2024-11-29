package com.thinker.cloud.common.analysis.utils;

import java.time.LocalDateTime;

/**
 * 分析时间对象
 *
 * @author admin
 */
public interface IDateTimeCondition {

    /**
     * 开始时间
     *
     * @return LocalDateTime
     */
    LocalDateTime getStartDateTime();

    /**
     * 结束时间
     *
     * @return LocalDateTime
     */
    LocalDateTime getEndDateTime();

    /**
     * 环比名称、或同比名称 昨日同期/上月同期/去年同期
     *
     * @return String
     */
    String getLastPeriodTimeName();
}
