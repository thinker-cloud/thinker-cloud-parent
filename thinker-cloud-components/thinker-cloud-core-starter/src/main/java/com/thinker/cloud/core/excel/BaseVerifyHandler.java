package com.thinker.cloud.core.excel;

import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import cn.afterturn.easypoi.handler.inter.IExcelVerifyHandler;
import com.thinker.cloud.common.exception.ValidationException;
import com.thinker.cloud.core.utils.ValidatorUtil;

/**
 * 基础校验处理器
 *
 * @author admin
 **/
public class BaseVerifyHandler<T> implements IExcelVerifyHandler<T> {

    @Override
    public ExcelVerifyHandlerResult verifyHandler(T obj) {
        try {
            ValidatorUtil.validate(obj, ",");
            return new ExcelVerifyHandlerResult(true);
        } catch (ValidationException e) {
            return new ExcelVerifyHandlerResult(false, e.getMessage());
        }
    }
}
