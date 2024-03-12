package com.thinker.cloud.common.analysis.interfaces.common;

/**
 * 同环比条件
 *
 * @author admin
 */
public interface AnalysisSameTimeLastCondition extends AnalysisBaseCondition {

    /**
     * 是否查询同比
     *
     * @return 是否查询同比
     */
    Boolean getQuerySamePeriodLastYear();

    /**
     * 是否查询环比
     *
     * @return 是否查询环比
     */
    Boolean getQueryChainLastPeriod();

    /**
     * 环比周期
     *
     * @return 查询哪个周期的环比:hour、day、month、quarterly、year
     */
    String getQueryChainLastPeriodCycle();
}
