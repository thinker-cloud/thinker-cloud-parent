package com.thinker.cloud.core.utils;

import com.google.common.collect.Maps;
import com.thinker.cloud.core.enums.IEnumDict;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * 基于内存缓存 枚举工具类
 *
 * @author admin
 **/
@Slf4j
@UtilityClass
public class EnumCacheUtil {

    /**
     * 枚举类和枚举类型关系映射MAP
     */
    private static final Map<Class<?>, Map<Object, IEnumDict<?>>> MAP = Maps.newHashMap();

    /**
     * 加载枚举到内存中
     *
     * @param values 枚举定义类型数据
     */
    public static void loadEnumCache(IEnumDict<?>[] values) {
        for (IEnumDict<?> value : values) {
            MAP.computeIfAbsent(value.getClass(), key -> Maps.newHashMap()).put(value.getValue(), value);
        }
    }

    /**
     * 根据 枚举Class 和 Value 从内存中获取枚举对象
     *
     * @param enumClass 枚举Class
     * @param value     value
     * @param <T>       枚举类型
     * @param <V>       value 类型
     * @return T
     */
    public static <T extends IEnumDict<V>, V> T formEnumValue(Class<T> enumClass, V value) {
        Map<Object, IEnumDict<?>> enumDictMap = MAP.get(enumClass);
        Objects.requireNonNull(enumDictMap, () -> String.format("枚举[%s]未加载到内存中，请加载内存", enumClass));
        IEnumDict<?> enumDict = enumDictMap.get(value);
        Objects.requireNonNull(enumDict, () -> String.format("枚举[%s]：类型[%s]未定义，请检查", enumClass, value));
        return (T) enumDict;
    }

    /**
     * 根据 枚举Class 和 Value 从内存中获取枚举对象
     *
     * @param enumClass    枚举Class
     * @param value        value
     * @param defaultValue 默认值
     * @param <T>          枚举类型
     * @param <V>          value 类型
     * @return T
     */
    public static <T extends IEnumDict<V>, V> T formEnumValue(Class<T> enumClass, V value, T defaultValue) {
        try {
            return formEnumValue(enumClass, value);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return defaultValue;
    }

    /**
     * 根据 枚举Class 和 Value 获取枚举对象
     * <p>
     * 如果内存中没有枚举信息，则先加载枚举到内存中在查找
     *
     * @param enumClass 枚举Class
     * @param value     value
     * @param <T>       枚举类型
     * @param <V>       value 类型
     * @return T
     */
    public static <T extends IEnumDict<V>, V> T loadEnumValue(Class<T> enumClass, V value) {
        // 内存中存在则直接取内存
        if (MAP.containsKey(enumClass)) {
            return formEnumValue(enumClass, value);
        }

        // 加载到内存中 在查找
        loadEnumCache(enumClass.getEnumConstants());
        return formEnumValue(enumClass, value);
    }

    /**
     * 根据 枚举Class 和 Value 获取枚举对象
     * <p>
     * 如果内存中没有枚举信息，则先加载枚举到内存中在查找
     *
     * @param enumClass    枚举Class
     * @param value        value
     * @param defaultValue 默认值
     * @param <T>          枚举类型
     * @param <V>          value 类型
     * @return T
     */
    public static <T extends IEnumDict<V>, V> T loadEnumValue(Class<T> enumClass, V value, T defaultValue) {
        try {
            return loadEnumValue(enumClass, value);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return defaultValue;
    }
}
