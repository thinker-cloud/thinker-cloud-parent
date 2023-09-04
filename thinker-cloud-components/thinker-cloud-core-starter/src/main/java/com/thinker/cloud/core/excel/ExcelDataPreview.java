package com.thinker.cloud.core.excel;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * excel数据预览信息
 *
 * @author admin
 **/
@Data
@Accessors(chain = true)
public class ExcelDataPreview implements Serializable {

    @Serial
    private static final long serialVersionUID = 8075552968576833281L;

    /**
     * 正常数据条数
     */
    private Integer normalCount;

    /**
     * 异常数据条数
     */
    private Integer abnormalCount;

    /**
     * 异常信息列表
     */
    private List<String> abnormalInfos;
}
