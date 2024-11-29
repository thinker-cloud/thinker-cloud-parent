package com.thinker.cloud.common.analysis.converter;

import com.thinker.cloud.common.analysis.model.query.AnalysisQuery;
import com.thinker.cloud.common.analysis.model.query.AnalysisTrendQuery;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 分析查询对象转换器
 *
 * @author admin
 **/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnalysisQueryConverter {

    AnalysisQueryConverter INSTANTS = Mappers.getMapper(AnalysisQueryConverter.class);

    /**
     * 复制 AnalysisQuery 对象
     *
     * @param query query
     * @return AnalysisQuery
     */
    AnalysisQuery copy(AnalysisQuery query);

    /**
     * 复制 AnalysisTrendQuery 对象
     *
     * @param query query
     * @return AnalysisTrendQuery
     */
    AnalysisTrendQuery copy(AnalysisTrendQuery query);
}
