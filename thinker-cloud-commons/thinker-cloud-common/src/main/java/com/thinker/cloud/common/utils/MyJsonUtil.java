package com.thinker.cloud.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Json工具类
 *
 * @author admin
 */
@UtilityClass
public class MyJsonUtil {
    public static JSONObject toJsonObject(Object obj) {
        return Optional.ofNullable(obj).map(o -> JSONObject.parseObject(JSONObject.toJSONString(o))).orElse(null);
    }

    public static <T> T toJavaObject(JSON json, Class<T> clazz) {
        return Optional.ofNullable(json).map(j -> JSONObject.toJavaObject(j, clazz)).orElse(null);
    }

    public static <T> T toJavaObject(String jsonString, Class<T> clazz) {
        return Optional.ofNullable(jsonString).map(j -> JSONObject.parseObject(j, clazz)).orElse(null);
    }

    public static JSONArray toJsonArray(Object obj) {
        return Optional.ofNullable(obj).map(o -> JSONArray.parseArray(JSONArray.toJSONString(o))).orElse(null);
    }

    public static <T> List<T> toJavaList(JSONArray json, Class<T> clazz) {
        return Optional.ofNullable(json).filter(CollectionUtil::isNotEmpty).map(j -> j.toJavaList(clazz)).orElse(null);
    }

    public static <T, R extends T> List<T> toJavaList(String json, Class<R> clazz) {
        List<R> list = JSONArray.parseArray(json, clazz);
        return Objects.nonNull(list) ? Lists.newArrayList(list) : null;
    }

    public static <T, R extends T> List<T> toJavaList(List<String> jsons, Class<R> clazz) {
        return Objects.nonNull(jsons) ? jsons.stream().map(value -> toJavaObject(value, clazz)).collect(Collectors.toList()) : null;
    }
}
