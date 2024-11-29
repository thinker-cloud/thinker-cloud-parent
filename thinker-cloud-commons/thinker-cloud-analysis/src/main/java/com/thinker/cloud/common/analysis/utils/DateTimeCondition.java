package com.thinker.cloud.common.analysis.utils;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 分析时间对象
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
public class DateTimeCondition implements IDateTimeCondition {

    /**
     * 开始时间
     */
    private LocalDateTime startDateTime;

    /**
     * 结束时间
     */
    private LocalDateTime endDateTime;

    /**
     * 环比名称、或同比名称 昨日同期/上月同期/去年同期
     */
    private String lastPeriodTimeName;
}
