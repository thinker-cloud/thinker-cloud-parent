package com.thinker.cloud.core.utils.dependhandle;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * 依赖属性查询工具
 *
 * @author admin
 */
public class DependsInfoTools {

    /**
     * 批量设置依赖信息
     *
     * @param handleList       需要处理的对象列表
     * @param foreignKey       需要处理的对象的外键（拿这个外键去查依赖）
     * @param selectDependList 根据所有外键查询依赖列表的过程
     * @param primaryKey       查询出依赖的主键（与外键对应）
     * @param setValueHandler  依赖属性设置过程
     * @param <T>              需要处理的对象类型
     * @param <D>              依赖对象类型
     * @param <K>              主键与外键的类型
     */
    public static <T, D, K> void batchSetDependInfo(List<T> handleList, Function<T, K> foreignKey
            , Function<List<K>, List<D>> selectDependList, Function<D, K> primaryKey
            , SetValueHandler<T, D> setValueHandler) {
        if (CollectionUtils.isEmpty(handleList)) {
            return;
        }
        List<K> idList = genIds(handleList, foreignKey);
        if (!idList.isEmpty()) {
            List<D> dependList = selectDependList.apply(idList);
            setDependProperty(handleList, foreignKey, dependList, primaryKey, setValueHandler);
        }
    }

    /**
     * 批量设置依赖信息
     *
     * @param handleList      需要处理的对象列表
     * @param foreignKey      需要处理的对象的外键（拿这个外键去查依赖）
     * @param selectDependMap 根据所有外键查询依赖列表的过程
     * @param setValueHandler 依赖属性设置过程
     * @param <T>             需要处理的对象类型
     * @param <D>             依赖对象类型
     * @param <K>             主键与外键的类型
     */
    public static <T, D, K> void batchSetMapDependInfo(List<T> handleList, Function<T, K> foreignKey
            , Function<List<K>, Map<K, D>> selectDependMap, SetValueHandler<T, D> setValueHandler) {
        if (CollectionUtils.isEmpty(handleList)) {
            return;
        }
        List<K> idList = genIds(handleList, foreignKey);
        if (!idList.isEmpty()) {
            Map<K, D> dependMap = selectDependMap.apply(idList);
            setDependMapProperty(handleList, foreignKey, dependMap, setValueHandler);
        }
    }

    /**
     * 批量设置依赖信息
     *
     * @param handleList       需要处理的对象列表
     * @param foreignKey       需要处理的对象的外键（拿这个外键去查依赖）
     * @param selectDependList 根据所有外键查询依赖列表的过程
     * @param primaryKey       查询出依赖的主键（与外键对应）
     * @param setValueHandler  依赖属性设置过程
     * @param <T>              需要处理的对象类型
     * @param <D>              依赖对象类型
     * @param <K>              主键与外键的类型
     */
    public static <T, D, K> void batchSetDependInfo(List<T> handleList, Function<T, K> foreignKey
            , Function<List<K>, List<D>> selectDependList, Function<D, K> primaryKey
            , SetValueHandler<T, D> setValueHandler, Consumer<T> defaultHandler) {
        if (CollectionUtils.isEmpty(handleList)) {
            return;
        }
        List<K> idList = genIds(handleList, foreignKey);
        if (!idList.isEmpty()) {
            List<D> dependList = selectDependList.apply(idList);
            setDependProperty(handleList, foreignKey, dependList, primaryKey, setValueHandler, defaultHandler);
        }
    }

    /**
     * 批量设置依赖信息
     *
     * @param handleList               需要处理的对象列表
     * @param selectDependList         根据所有外键查询依赖列表的过程
     * @param primaryKey               查询出依赖的主键（与外键对应）
     * @param keyValueHandlerRelations 外键与值处理的映射关系
     * @param <T>                      需要处理的对象类型
     * @param <D>                      依赖对象类型
     * @param <K>                      主键与外键的类型
     */
    public static <T, D, K> void batchSetDependInfo(List<T> handleList
            , Function<List<K>, List<D>> selectDependList, Function<D, K> primaryKey
            , List<KeyValueHandlerRelation<T, D, K>> keyValueHandlerRelations) {
        if (CollectionUtils.isEmpty(handleList)) {
            return;
        }
        List<Function<T, K>> foreignKeys
                = keyValueHandlerRelations.stream().map(KeyValueHandlerRelation::getForeignKey).collect(Collectors.toList());
        List<K> idList = genIds(handleList, foreignKeys);
        if (!idList.isEmpty()) {
            List<D> dependList = selectDependList.apply(idList);
            setDependProperty(handleList, dependList, primaryKey, keyValueHandlerRelations);
        }
    }

    @SafeVarargs
    public static <T, D, K> void batchSetDependInfo(List<T> handleList
            , Function<List<K>, List<D>> selectDependList, Function<D, K> primaryKey
            , KeyValueHandlerRelation<T, D, K>... keyValueHandlerRelations) {
        batchSetDependInfo(handleList, selectDependList, primaryKey, Lists.newArrayList(keyValueHandlerRelations));
    }

    private static <T, K> List<K> genIds(List<T> handleList, Function<T, K> foreignKey) {
        return handleList.isEmpty() ? Lists.newArrayList() : handleList.parallelStream()
                .map(foreignKey)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private static <T, K> List<K> genIds(List<T> handleList, List<Function<T, K>> foreignKeys) {
        Set<K> ids = Sets.newCopyOnWriteArraySet();
        if (!handleList.isEmpty()) {
            handleList.parallelStream().forEach(handle ->
                    foreignKeys.forEach(tkFunction ->
                            Optional.ofNullable(tkFunction.apply(handle)).ifPresent(ids::add)));
        }
        return new ArrayList<>(ids);
    }

    private static <T, D, K> void setDependMapProperty(List<T> handleList, Function<T, K> foreignKey
            , Map<K, D> dependMap, SetValueHandler<T, D> setValueHandler) {
        if (MapUtil.isEmpty(dependMap)) {
            return;
        }
        dependMap.forEach((key, value) -> {
            handleList.parallelStream().forEach(setObject ->
                    Optional.ofNullable(foreignKey.apply(setObject)).ifPresent(foreignId -> {
                        if (foreignId.equals(key)) {
                            setValueHandler.set(setObject, value);
                        }
                    })
            );
        });
    }

    private static <T, D, K> void setDependProperty(List<T> handleList, Function<T, K> foreignKey
            , List<D> dependList, Function<D, K> primaryKey
            , SetValueHandler<T, D> setValueHandler) {
        if (CollectionUtils.isEmpty(dependList)) {
            return;
        }
        dependList.forEach(depend -> {
            K primaryId = primaryKey.apply(depend);
            handleList.parallelStream().forEach(setObject ->
                    Optional.ofNullable(foreignKey.apply(setObject)).ifPresent(foreignId -> {
                        if (foreignId.equals(primaryId)) {
                            setValueHandler.set(setObject, depend);
                        }
                    })
            );
        });
    }

    private static <T, D, K> void setDependProperty(List<T> handleList, Function<T, K> foreignKey
            , List<D> dependList, Function<D, K> primaryKey
            , SetValueHandler<T, D> setValueHandler, Consumer<T> defaultHandler) {
        handleList.forEach(handle -> {
            K primaryId = foreignKey.apply(handle);
            AtomicBoolean matchFlag = new AtomicBoolean(false);
            dependList.parallelStream().forEach(depend -> {
                K foreignId = primaryKey.apply(depend);
                if (Objects.nonNull(foreignId) && foreignId.equals(primaryId)) {
                    matchFlag.set(true);
                    setValueHandler.set(handle, depend);
                }
            });

            // 如果都没有匹配到则使用默认处理
            if (matchFlag.get() && Objects.nonNull(defaultHandler)) {
                defaultHandler.accept(handle);
            }
        });
    }

    private static <T, D, K> void setDependProperty(List<T> handleList
            , List<D> dependList, Function<D, K> primaryKey
            , List<KeyValueHandlerRelation<T, D, K>> keyValueHandlerRelations) {
        Map<K, D> idDependsList = dependList.parallelStream().collect(Collectors.toMap(primaryKey, UnaryOperator.identity()));
        handleList.parallelStream().forEach(setObject -> {
            keyValueHandlerRelations.forEach(tdkKeyValueHandlerRelation -> {
                K dependId = tdkKeyValueHandlerRelation.getForeignKey().apply(setObject);
                Optional.ofNullable(idDependsList.get(dependId))
                        .ifPresent(depend -> tdkKeyValueHandlerRelation.getSetValueHandler().set(setObject, depend));
            });
        });
    }
}
