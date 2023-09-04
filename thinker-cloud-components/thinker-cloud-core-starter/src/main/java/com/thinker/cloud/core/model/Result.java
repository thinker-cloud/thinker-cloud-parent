package com.thinker.cloud.core.model;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thinker.cloud.core.enums.IEnumDict;
import com.thinker.cloud.core.enums.ResponseCode;
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

    public static <T> Result<T> buildSuccess() {
        return buildSuccess(ResponseCode.SUCCESS);
    }

    public static <T> Result<T> buildSuccess(T data) {
        return buildSuccess(data, ResponseCode.SUCCESS);
    }

    public static <T> Result<T> buildSuccess(Integer code, String message) {
        return buildSuccess(null, code, message);
    }

    public static <T> Result<T> buildSuccess(IEnumDict<Integer> responseCode) {
        return buildSuccess(responseCode.getValue(), responseCode.getDesc());
    }

    public static <T> Result<T> buildSuccess(Integer code, String message, Object... agsArray) {
        return buildSuccess(code, StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> buildSuccess(T data, String message) {
        return buildSuccess(data, ResponseCode.SUCCESS.getCode(), message);
    }

    public static <T> Result<T> buildSuccess(T data, String message, Object... agsArray) {
        return buildSuccess(data, StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> buildSuccess(T data, IEnumDict<Integer> responseCode) {
        return buildSuccess(data, responseCode.getValue(), responseCode.getDesc());
    }

    public static <T> Result<T> buildSuccess(T data, Integer code, String message) {
        return build(Boolean.TRUE, data, code, message);
    }

    public static <T> Result<T> buildFailure() {
        return buildFailure(ResponseCode.FAILURE);
    }

    public static <T> Result<T> buildFailure(String message) {
        return buildFailure(ResponseCode.FAILURE.getCode(), message);
    }

    public static <T> Result<T> buildFailure(Integer code, String message) {
        return buildFailure(null, code, message);
    }

    public static <T> Result<T> buildFailure(IEnumDict<Integer> responseCode) {
        return buildFailure(responseCode.getValue(), responseCode.getDesc());
    }

    public static <T> Result<T> buildFailure(String message, Object... agsArray) {
        return buildFailure(ResponseCode.FAILURE.getCode(), StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> buildFailure(Integer code, String message, Object... agsArray) {
        return buildFailure(code, StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> buildFailure(T data, String message) {
        return buildFailure(data, ResponseCode.FAILURE.getCode(), message);
    }

    public static <T> Result<T> buildFailure(T data, String message, Object... agsArray) {
        return buildFailure(data, StrUtil.format(message, agsArray));
    }

    public static <T> Result<T> buildFailure(T data, IEnumDict<Integer> responseCode) {
        return buildFailure(data, responseCode.getValue(), responseCode.getDesc());
    }

    public static <T> Result<T> buildFailure(T data, Integer code, String message) {
        return build(Boolean.FALSE, data, code, message);
    }
}
