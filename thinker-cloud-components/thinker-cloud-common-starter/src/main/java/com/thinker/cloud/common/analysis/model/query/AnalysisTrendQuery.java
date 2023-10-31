package com.thinker.cloud.common.analysis.model.query;

import com.thinker.cloud.common.analysis.interfaces.common.AnalysisSameTimeLastCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 分析趋势查询vo
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AnalysisTrendQuery extends AnalysisQuery implements AnalysisSameTimeLastCondition {

    {
        this.querySamePeriodLastYear = false;
        this.queryChainLastPeriod = false;
    }

    /**
     * 是否查询同比
     */
    private Boolean querySamePeriodLastYear;

    /**
     * 是否查询环比
     */
    private Boolean queryChainLastPeriod;

    /**
     * 查询环比周期 hour、day、week、month、quarterly、year
     */
    private String queryChainLastPeriodCycle;
}
