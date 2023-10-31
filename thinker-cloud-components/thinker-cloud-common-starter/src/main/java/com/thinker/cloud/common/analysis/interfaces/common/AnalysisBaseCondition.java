package com.thinker.cloud.common.analysis.interfaces.common;

import java.time.LocalDateTime;

/**
 * 统计分析通用查询条件
 *
 * @author admin
 */
public interface AnalysisBaseCondition {

    /**
     * 获取开始时间
     *
     * @return 开始时间
     */
    LocalDateTime getStartDateTime();

    /**
     * 获取结束时间
     *
     * @return 开始时间
     */
    LocalDateTime getEndDateTime();

    /**
     * 获取分组
     *
     * @return 开始时间
     */
    String getGroupBy();

    /**
     * 按分组类型的间隔
     * 例如组合为：1分钟、5分钟 等...
     *
     * @return 分组间隔
     */
    default Integer getGroupInterval() {
        return 1;
    }
}
