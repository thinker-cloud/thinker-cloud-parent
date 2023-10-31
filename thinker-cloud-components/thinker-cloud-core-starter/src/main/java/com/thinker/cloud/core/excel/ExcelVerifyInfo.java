package com.thinker.cloud.core.excel;

import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * Excel导入验证信息
 *
 * @author admin
 **/
@Data
public class ExcelVerifyInfo implements IExcelModel, IExcelDataModel, Serializable {

    @Serial
    private static final long serialVersionUID = 998442201819672878L;

    /**
     * 当前行数
     */
    private Integer rowNum;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 设置当前行数 默认从0开始
     *
     * @param rowNum 行数
     */
    @Override
    public void setRowNum(Integer rowNum) {
        Optional.ofNullable(rowNum).ifPresent(num -> this.rowNum = num + 1);
    }
}
