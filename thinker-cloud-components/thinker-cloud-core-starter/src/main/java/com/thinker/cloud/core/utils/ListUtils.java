package com.thinker.cloud.core.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * List相关工具类
 *
 * @author admin
 **/
public class ListUtils {

    /**
     * 将列表转为Map键值对映射
     *
     * @param list       list
     * @param foreignKey foreignKey
     */
    public static <T, K> Map<K, T> toMap(List<T> list, Function<T, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream().collect(Collectors.toMap(foreignKey, value -> value, (key1, key2) -> key2));
    }

    /**
     * 将列表转为Map键值对映射
     *
     * @param list       list
     * @param filter     filter
     * @param foreignKey foreignKey
     */
    public static <T, K> Map<K, T> toMap(List<T> list, Predicate<T> filter, Function<T, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream().filter(filterPredicate(filter))
                .collect(Collectors.toMap(foreignKey, value -> value, (key1, key2) -> key2));
    }

    /**
     * 将列表转为Map键值对映射
     *
     * @param list        list
     * @param foreignKey  foreignKey
     * @param foreignKey1 foreignKey1
     */
    public static <T, M, K> Map<K, M> toMap(List<T> list, Function<T, M> foreignKey, Function<M, K> foreignKey1) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream()
                .map(foreignKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(foreignKey1, value -> value, (key1, key2) -> key2));
    }

    /**
     * 将列表转为Map键值对映射
     *
     * @param list        list
     * @param foreignKey1 foreignKey
     * @param filter      filter
     * @param foreignKey2 foreignKey1
     */
    public static <T, M, K> Map<K, M> toMap(List<T> list, Predicate<T> filter, Function<T, M> foreignKey1, Function<M, K> foreignKey2) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream()
                .filter(filterPredicate(filter))
                .map(foreignKey1)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(foreignKey2, value -> value, (key1, key2) -> key2));
    }

    /**
     * 将列表转为Map键值对映射
     *
     * @param list        list
     * @param filter      filter
     * @param foreignKey1 foreignKey1
     * @param foreignKey2 foreignKey2
     */
    public static <T, M, K> Map<K, M> toMap(List<T> list, Function<T, M> foreignKey1, Predicate<M> filter, Function<M, K> foreignKey2) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream()
                .map(foreignKey1)
                .filter(Objects::nonNull)
                .filter(filterPredicate(filter))
                .collect(Collectors.toMap(foreignKey2, value -> value, (key1, key2) -> key2));
    }

    /**
     * 将列表转为Map键值对映射
     *
     * @param list        list
     * @param filter1     filter1
     * @param foreignKey1 foreignKey1
     * @param filter2     filter2
     * @param foreignKey2 foreignKey2
     */
    public static <T, M, K> Map<K, M> toMap(List<T> list, Predicate<T> filter1, Function<T, M> foreignKey1
            , Predicate<M> filter2, Function<M, K> foreignKey2) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream()
                .filter(filterPredicate(filter1))
                .map(foreignKey1)
                .filter(Objects::nonNull)
                .filter(filterPredicate(filter2))
                .collect(Collectors.toMap(foreignKey2, value -> value, (key1, key2) -> key2));
    }

    /**
     * 将列表转为Map键值对映射
     *
     * @param list        list
     * @param keyMapper   key映射
     * @param valueMapper value映射
     */
    public static <T, K, M> Map<K, M> toMapValue(List<T> list, Function<T, K> keyMapper, Function<T, M> valueMapper) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper, (key1, key2) -> key2));
    }

    /**
     * 将列表中的某个字段合并成一个list
     *
     * @param list       list
     * @param foreignKey foreignKey
     */
    public static <T, K> List<K> toList(List<T> list, Function<T, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .map(foreignKey)
                .filter(ObjectUtil::isNotEmpty)
                .collect(Collectors.toList());
    }

    /**
     * 将列表中的某个字段合并成一个list
     *
     * @param list       list
     * @param filter     filter
     * @param foreignKey foreignKey
     */
    public static <T, K> List<K> toList(List<T> list, Predicate<T> filter, Function<T, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .filter(filterPredicate(filter))
                .map(foreignKey)
                .filter(ObjectUtil::isNotEmpty)
                .collect(Collectors.toList());
    }

    /**
     * 将列表中的某个字段合并成一个去重list
     *
     * @param list       list
     * @param foreignKey foreignKey
     */
    public static <T, K> List<K> toDistinctList(List<T> list, Function<T, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .map(foreignKey)
                .filter(ObjectUtil::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 将列表中的某个list合并转换为Map键值对映射
     *
     * @param list       list
     * @param mergeList  mergeFunc
     * @param foreignKey foreignKey
     */
    public static <T, M, K> Map<K, M> toMergeMap(List<T> list, Function<T, List<M>> mergeList, Function<M, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream()
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(foreignKey, value -> value));
    }

    /**
     * 将列表中的某个list合并转换为Map键值对映射
     *
     * @param list       list
     * @param filter     filter
     * @param mergeList  mergeList
     * @param foreignKey foreignKey
     */
    public static <T, M, K> Map<K, M> toMergeMap(List<T> list, Predicate<T> filter
            , Function<T, List<M>> mergeList, Function<M, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream()
                .filter(filterPredicate(filter))
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(foreignKey, value -> value));
    }

    /**
     * 将列表中的某个list合并转换为Map键值对映射
     *
     * @param list       list
     * @param mergeList  mergeList
     * @param foreignKey foreignKey
     * @param filter     filter
     */
    public static <T, M, K> Map<K, M> toMergeMap(List<T> list, Function<T, List<M>> mergeList
            , Function<M, K> foreignKey, Predicate<M> filter) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream()
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(filterPredicate(filter))
                .collect(Collectors.toMap(foreignKey, value -> value));
    }

    /**
     * 将列表中的某个list合并转换为Map键值对映射
     *
     * @param list       list
     * @param filter1    filter1
     * @param mergeList  mergeList
     * @param foreignKey foreignKey
     * @param filter2    filter2
     */
    public static <T, M, K> Map<K, M> toMergeMap(List<T> list, Predicate<T> filter1
            , Function<T, List<M>> mergeList, Function<M, K> foreignKey, Predicate<M> filter2) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }

        return list.stream()
                .filter(filterPredicate(filter1))
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(filterPredicate(filter2))
                .collect(Collectors.toMap(foreignKey, value -> value));
    }

    /**
     * 将列表中的某个list合并成一个list
     *
     * @param list      list
     * @param mergeList mergeList
     */
    public static <T, M> List<M> toMergeList(List<T> list, Function<T, List<M>> mergeList) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 将列表中的某个list合并成一个list
     *
     * @param list       list
     * @param mergeList  mergeList
     * @param foreignKey foreignKey
     */
    public static <T, M, K> List<K> toMergeList(List<T> list, Function<T, List<M>> mergeList, Function<M, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(foreignKey)
                .collect(Collectors.toList());
    }


    /**
     * 将列表中的某个list合并成一个list
     *
     * @param list      list
     * @param filter    filter
     * @param mergeList mergeList
     */
    public static <T, M> List<M> toMergeList(List<T> list, Predicate<T> filter, Function<T, List<M>> mergeList) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .filter(filterPredicate(filter))
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 将列表中的某个list合并成一个list
     *
     * @param list       list
     * @param filter     filter
     * @param mergeList  mergeList
     * @param foreignKey foreignKey
     */
    public static <T, M, K> List<K> toMergeList(List<T> list, Predicate<T> filter, Function<T, List<M>> mergeList, Function<M, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .filter(filterPredicate(filter))
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(foreignKey)
                .collect(Collectors.toList());
    }


    /**
     * 将列表中的某个list合并成一个list
     *
     * @param list      list
     * @param mergeList mergeList
     * @param filter    filter2
     */
    public static <T, M> List<M> toMergeList(List<T> list, Function<T, List<M>> mergeList, Predicate<M> filter) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(filterPredicate(filter))
                .collect(Collectors.toList());
    }

    /**
     * 将列表中的某个list合并成一个list
     *
     * @param list       list
     * @param mergeList  mergeList
     * @param filter     filter
     * @param foreignKey foreignKey
     */
    public static <T, M, K> List<K> toMergeList(List<T> list, Function<T, List<M>> mergeList, Predicate<M> filter, Function<M, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(filterPredicate(filter))
                .map(foreignKey)
                .collect(Collectors.toList());
    }

    /**
     * 将列表中的某个list合并成一个list
     *
     * @param list      list
     * @param filter1   filter1
     * @param mergeList mergeList
     * @param filter2   filter2
     */
    public static <T, M> List<M> toMergeList(List<T> list, Predicate<T> filter1, Function<T, List<M>> mergeList, Predicate<M> filter2) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .filter(filterPredicate(filter1))
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(filterPredicate(filter2))
                .collect(Collectors.toList());
    }

    /**
     * 将列表中的某个list合并成一个list
     *
     * @param list       list
     * @param filter1    filter1
     * @param mergeList  mergeList
     * @param filter2    filter2
     * @param foreignKey foreignKey
     */
    public static <T, M, K> List<K> toMergeList(List<T> list, Predicate<T> filter1, Function<T, List<M>> mergeList, Predicate<M> filter2, Function<M, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        return list.stream()
                .filter(filterPredicate(filter1))
                .map(mergeList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(filterPredicate(filter2))
                .map(foreignKey)
                .collect(Collectors.toList());
    }

    /**
     * 将 list 根据某个字段分组
     *
     * @param list       list
     * @param foreignKey foreignKey
     * @return Map<K, List < T>>
     */
    public static <T, K> Map<K, List<T>> toGroupMap(List<T> list, Function<T, K> foreignKey) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.groupingBy(foreignKey));
    }

    /**
     * 将 list 根据某个字段分组
     *
     * @param list        list
     * @param foreignKey1 分组键1
     * @param foreignKey2 分组键2
     * @return Map<K, List < T>>
     */
    public static <T, K, M> Map<K, List<M>> toGroupMap(List<T> list, Function<T, K> foreignKey1, Function<T, M> foreignKey2) {
        if (CollectionUtil.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream()
                .collect(Collectors.groupingBy(foreignKey1
                        , Collectors.mapping(foreignKey2, Collectors.toList())));
    }

    private static <T> Predicate<T> filterPredicate(Predicate<T> filter1) {
        return var -> Objects.isNull(filter1) || filter1.test(var);
    }
}
