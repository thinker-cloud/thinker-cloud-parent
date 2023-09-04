package com.thinker.cloud.core.utils;

import com.thinker.cloud.core.enums.IEnumDict;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * IEnumDict 枚举工具类
 *
 * @author admin
 **/
@UtilityClass
public class EnumDictUtil {

    /**
     * 根据枚举的{@link IEnumDict#getValue()},{@link IEnumDict#getDesc()}来查找.
     *
     * @see this#find(Class, Predicate)
     */
    public static <T extends Enum<?> & IEnumDict<?>> Optional<T> find(Class<T> type, Object target) {
        return find(type, v -> eq(v, target));
    }

    /**
     * 从指定的枚举类中查找想要的枚举,并返回一个{@link Optional},如果未找到,则返回一个{@link Optional#empty()}
     *
     * @param type      实现了{@link IEnumDict}的枚举类
     * @param predicate 判断逻辑
     * @param <T>       枚举类型
     * @return 查找到的结果
     */
    public static <T extends Enum<?> & IEnumDict<?>> Optional<T> find(Class<T> type, Predicate<T> predicate) {
        if (type.isEnum()) {
            for (T enumDict : type.getEnumConstants()) {
                if (predicate.test(enumDict)) {
                    return Optional.of(enumDict);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 根据枚举的{@link IEnumDict#getValue()}来查找.
     *
     * @see this#find(Class, Predicate)
     */
    public static <T extends Enum<?> & IEnumDict<?>> Optional<T> findByValue(Class<T> type, Object value) {
        return find(type, e -> e.getValue() == value || e.getValue().equals(value) || String.valueOf(e.getValue()).equalsIgnoreCase(String.valueOf(value)));
    }


    /**
     * 对比是否和value相等,对比地址,值,value转为string忽略大小写对比,text忽略大小写对比
     *
     * @param value value
     * @return 是否相等
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Enum<?> & IEnumDict<?>> boolean eq(T enumDict, Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Object[]) {
            value = Collections.singletonList(value);
        }
        if (value instanceof Collection) {
            return ((Collection) value).stream().anyMatch(object -> eq(enumDict, object));
        }
        if (value instanceof Map) {
            value = ((Map) value).getOrDefault("value", ((Map) value).get("desc"));
        }
        return enumDict == value
                || enumDict.getValue() == value
                || enumDict.getValue().equals(value)
                || String.valueOf(enumDict.getValue()).equalsIgnoreCase(String.valueOf(value))
                || enumDict.getDesc().equalsIgnoreCase(String.valueOf(value));
    }
}
