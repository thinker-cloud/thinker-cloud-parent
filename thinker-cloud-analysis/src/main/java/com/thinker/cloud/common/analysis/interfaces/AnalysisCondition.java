package com.thinker.cloud.common.analysis.interfaces;


import com.thinker.cloud.common.analysis.model.query.AnalysisQuery;

/**
 * 统计分析通用查询条件
 *
 * @author admin
 */
public interface AnalysisCondition {

    /**
     * 统计分析通用属性
     *
     * @return 统计分析通用属性
     */
    AnalysisQuery getAnalysis();
}
