package com.thinker.cloud.common.analysis.model.query;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统计分析通用扩展查询参数
 *
 * @author admin
 **/
@Data
public class AnalysisExtendQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = -4764377970038820434L;

    {
        this.disableCache = false;
    }

    /**
     * 禁用缓存
     */
    private Boolean disableCache;
}
