package com.thinker.cloud.core.model;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thinker.cloud.common.enums.IEnumDict;
import com.thinker.cloud.common.enums.ResponseCode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 返回结果类
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -8503156186330100229L;

    /**
     * 状态
     */
    private boolean success;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 自定义状态码
     */
    private Integer code;

    /**
     * 返回数据
     */
    private T data;

    private static <T> Result<T> build(Boolean success, T data, Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        result.setMessage(message);
        result.setSuccess(success);
        return result;
    }

    public static <T> Result<T> success() {
        return success(ResponseCode.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return success(data, ResponseCode.SUCCESS);
    }

    public static <T> Result<T> success(Integer code, String message) {
        return success(null, code, message);
    }

    public static <T> Result<T> success(IEnumDict<Integer> responseCode) {
        return success(responseCode.getValue(), responseCode.getDesc());
    }

    public static <T> Result<T> success(Integer code, String message, Object... agsArray) {
        return success(code, StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> success(T data, String message) {
        return success(data, ResponseCode.SUCCESS.getCode(), message);
    }

    public static <T> Result<T> success(T data, String message, Object... agsArray) {
        return success(data, StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> success(T data, IEnumDict<Integer> responseCode) {
        return success(data, responseCode.getValue(), responseCode.getDesc());
    }

    public static <T> Result<T> success(T data, Integer code, String message) {
        return build(Boolean.TRUE, data, code, message);
    }

    public static <T> Result<T> failure() {
        return failure(ResponseCode.SERVER_FAILURE);
    }

    public static <T> Result<T> failure(String message) {
        return failure(ResponseCode.SERVER_FAILURE.getCode(), message);
    }

    public static <T> Result<T> failure(Integer code, String message) {
        return failure(null, code, message);
    }

    public static <T> Result<T> failure(IEnumDict<Integer> responseCode) {
        return failure(responseCode.getValue(), responseCode.getDesc());
    }

    public static <T> Result<T> failure(String message, Object... agsArray) {
        return failure(ResponseCode.SERVER_FAILURE.getCode(), StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> failure(Integer code, String message, Object... agsArray) {
        return failure(code, StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> failure(T data, String message) {
        return failure(data, ResponseCode.SERVER_FAILURE.getCode(), message);
    }

    public static <T> Result<T> failure(T data, String message, Object... agsArray) {
        return failure(data, StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> failure(T data, IEnumDict<Integer> responseCode) {
        return failure(data, responseCode.getValue(), responseCode.getDesc());
    }

    public static <T> Result<T> failure(T data, Integer code, String message) {
        return build(Boolean.FALSE, data, code, message);
    }
}
