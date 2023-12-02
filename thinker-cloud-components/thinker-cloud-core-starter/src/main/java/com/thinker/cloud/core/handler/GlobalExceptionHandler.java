package com.thinker.cloud.core.handler;

import com.thinker.cloud.core.enums.ResponseCode;
import com.thinker.cloud.core.exception.*;
import com.thinker.cloud.core.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 全局异常处理器
 *
 * @author admin
 */
@Slf4j
@RestController
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
        return Result.buildFailure(ResponseCode.SERVER_FAILURE);
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
        return Result.buildFailure(e.getCode(), e.getMessage());
    }

    /**
     * 验证异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ValidationException.class)
    public Result<Void> handleValidateException(ValidationException e) {
        log.error("验证异常信息，ex={}", e.getMessage());
        return Result.buildFailure(e.getCode(), e.getMessage());
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
        return Result.buildFailure(e.getData(), e.getCode(), e.getMessage());
    }

    /**
     * 锁异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(LockException.class)
    public Result<Void> handleLockException(LockException e) {
        log.error("锁异常信息，ex={}", e.getMessage());
        return Result.buildFailure(e.getCode(), e.getMessage());
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
        return Result.buildFailure(e.getCode(), e.getMessage());
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
        return Result.buildFailure(e.getCode(), e.getMessage());
    }

    /**
     * 参数绑定异常
     *
     * @param e exception
     * @return Result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleBodyValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.get(0).getDefaultMessage();
        log.error("参数绑定异常，ex={}", errorMessage);
        return Result.buildFailure(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    /**
     * 避免 404 重定向到 /error 导致NPE
     *
     * @return Result
     */
    @DeleteMapping("error")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> noHandlerFoundException() {
        return Result.buildFailure(ResponseCode.NOT_FOUND);
    }
}
