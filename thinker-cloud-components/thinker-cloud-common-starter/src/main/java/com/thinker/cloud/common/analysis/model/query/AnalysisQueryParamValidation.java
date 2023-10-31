package com.thinker.cloud.common.analysis.model.query;


import com.thinker.cloud.common.analysis.interfaces.common.AnalysisBaseCondition;
import com.thinker.cloud.core.exception.FailException;

/**
 * 统计分析查询参数验证
 *
 * @author admin
 */
public interface AnalysisQueryParamValidation extends AnalysisBaseCondition {
    /**
     * 检查开始结束时间是否为空
     */
    default void verificationAllDateTime() {
        if (getStartDateTime() == null && getEndDateTime() == null) {
            throw new FailException("请传入时间条件");
        }
    }

    /**
     * 检查开始时间是否为空
     */
    default void verificationStartDateTime() {
        if (this.getStartDateTime() == null) {
            throw new FailException("请传入开始时间");
        }
    }

    /**
     * 检查结束时间是否为空
     */
    default void verificationEndDateTime() {
        if (this.getEndDateTime() == null) {
            throw new FailException("请传入开始时间");
        }
    }
}
