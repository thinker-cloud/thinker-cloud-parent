package com.thinker.cloud.common.analysis.interfaces;


import com.thinker.cloud.common.analysis.model.query.AnalysisTrendQuery;

/**
 * 统计分析通用查询条件
 *
 * @author admin
 */
public interface AnalysisTrendCondition {

    /**
     * 统计分析通用属性
     *
     * @return 统计分析通用属性
     */
    AnalysisTrendQuery getAnalysis();
}
