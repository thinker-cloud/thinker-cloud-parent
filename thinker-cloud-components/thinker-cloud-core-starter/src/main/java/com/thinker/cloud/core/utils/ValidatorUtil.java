package com.thinker.cloud.core.utils;


import com.thinker.cloud.common.exception.FailException;
import com.thinker.cloud.common.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import lombok.experimental.UtilityClass;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

/**
 * hibernate-validator校验工具类
 *
 * @author admin
 */
@UtilityClass
public class ValidatorUtil {

    private static final Validator VALIDATOR;

    static {
        VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     *
     * @param object    待校验对象
     * @param separator 分隔符
     * @param groups    待校验的组
     * @throws FailException 校验不通过
     */
    public static void validate(Object object, String separator, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for (ConstraintViolation<Object> constraint : constraintViolations) {
                msg.append(constraint.getMessage()).append(separator);
            }
            msg.deleteCharAt(msg.length() - 1);
            throw new ValidationException(msg.toString());
        }
    }
}
