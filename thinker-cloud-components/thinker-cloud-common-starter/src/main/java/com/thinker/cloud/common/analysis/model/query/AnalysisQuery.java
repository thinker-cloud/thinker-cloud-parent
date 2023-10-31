package com.thinker.cloud.common.analysis.model.query;


import com.thinker.cloud.common.analysis.constants.AnalysisCycle;
import com.thinker.cloud.common.analysis.interfaces.common.AnalysisBaseCondition;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 通用分析查询条件
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
public class AnalysisQuery implements AnalysisBaseCondition, AnalysisQueryParamValidation {

    {
        this.groupBy = AnalysisCycle.hour.getValue();
        this.groupInterval = 1;
    }

    /**
     * 开始时间
     */
    private LocalDateTime startDateTime;

    /**
     * 结束时间
     */
    private LocalDateTime endDateTime;

    /**
     * 分组类型 minute,hour,day,week,month,year 默认为hour
     */
    private String groupBy;

    /**
     * 分组间隔 默认为1
     */
    private Integer groupInterval;
}
