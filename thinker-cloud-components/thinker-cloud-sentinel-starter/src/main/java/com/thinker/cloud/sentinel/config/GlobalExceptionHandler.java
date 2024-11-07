package com.thinker.cloud.sentinel.config;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.enums.ResponseCode;
import com.thinker.cloud.core.exception.*;
import com.thinker.cloud.core.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * 全局异常处理器
 *
 * @author admin
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常.
     *
     * @param e exception
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleGlobalException(Exception e) {
        log.error("全局异常信息，ex={}", e.getMessage(), e);
        return Result.failure(ResponseCode.SERVER_FAILURE);
    }

    /**
     * 统一业务异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(AbstractException.class)
    public Result<Void> handleValidateException(AbstractException e) {
        log.error("统一业务异常，ex={}", e.getMessage());
        return Result.failure(e.getCode(), e.getMessage());
    }

    /**
     * 验证异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Result<Void> handleValidateException(ValidationException e) {
        log.error("验证异常信息，ex={}", e.getMessage());
        return Result.failure(e.getCode(), e.getMessage());
    }

    /**
     * 业务异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FailException.class)
    public Result<Object> handleFailException(FailException e) {
        log.error("业务异常信息，ex={}", e.getMessage());
        return Result.failure(e.getData(), e.getCode(), e.getMessage());
    }

    /**
     * 锁异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(LockException.class)
    public Result<Void> handleLockException(LockException e) {
        log.error("锁异常信息，ex={}", e.getMessage());
        return Result.failure(e.getCode(), e.getMessage());
    }

    /**
     * 幂等异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IdempotentException.class)
    public Result<Void> handleIdempotentException(IdempotentException e) {
        log.error("幂等异常信息，ex={}", e.getMessage());
        return Result.failure(e.getCode(), e.getMessage());
    }

    /**
     * 缓存异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CacheableException.class)
    public Result<Void> handleCacheableException(CacheableException e) {
        log.error("缓存异常信息，ex={}", e.getMessage());
        return Result.failure(e.getCode(), e.getMessage());
    }

    /**
     * 参数绑定异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.get(0).getDefaultMessage();
        log.error("参数绑定异常，ex={}", errorMessage);
        return Result.failure(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    /**
     * 参数类型转换异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public Result<Void> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("参数类型转换异常，ex={}", e.getMessage(), e);
        String message = StrUtil.format("参数类型错误，参数：[{}]:[{}]，错误：{}"
                , e.getName(), e.getRequiredType(), e.getMessage());
        return Result.failure(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * 参数异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常，ex={}", e.getMessage(), e);
        return Result.failure("参数不合法，请检查");
    }

    /**
     * 数字格式化异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NumberFormatException.class})
    public Result<Void> handleNumberFormatException(NumberFormatException e) {
        log.error("数字格式化异常，ex={}", e.getMessage(), e);
        return Result.failure(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * 保持和低版本请求路径不存在的行为一致
     * <p>
     * <a href="https://github.com/spring-projects/spring-boot/issues/38733">
     * [Spring Boot3.2.0] 404 Not Found behavior #38733
     * </a>
     *
     * @param e exception
     * @return R
     */
    @ExceptionHandler({NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> bindExceptionHandler(NoResourceFoundException e) {
        log.debug("请求路径 404 {}", e.getMessage(), e);
        return Result.failure(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
