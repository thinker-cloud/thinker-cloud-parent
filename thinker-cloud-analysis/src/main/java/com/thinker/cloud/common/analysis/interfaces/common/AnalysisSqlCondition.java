package com.thinker.cloud.common.analysis.interfaces.common;

import java.time.LocalDateTime;

/**
 * @author admin
 */
public interface AnalysisSqlCondition extends AnalysisBaseCondition {
    /**
     * 设置组
     *
     * @param groupBy 分组
     * @return this
     */
    AnalysisSqlCondition setGroupBy(String groupBy);

    /**
     * 设置开始时间
     *
     * @param startDateTime 开始时间
     * @return this
     */
    AnalysisSqlCondition setStartDateTime(LocalDateTime startDateTime);

    /**
     * 设置结束时间
     *
     * @param endDateTime 结束时间
     * @return this
     */
    AnalysisSqlCondition setEndDateTime(LocalDateTime endDateTime);

    /**
     * 设置每组间隔
     *
     * @param groupInterval 每组间隔
     * @return this
     */
    AnalysisSqlCondition setGroupInterval(Integer groupInterval);
}
