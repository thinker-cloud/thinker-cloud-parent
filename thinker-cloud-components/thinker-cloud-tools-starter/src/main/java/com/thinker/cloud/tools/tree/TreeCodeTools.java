package com.thinker.cloud.tools.tree;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 树级结构生成器
 *
 * @author admin
 * @since 2020/12/31 18:05
 */
public class TreeCodeTools {

    /**
     * 生成树级结构
     *
     * @param allNodeList 所有节点列表
     * @param <T>         实现的业务对象类型
     * @return 结果树
     */
    public static <T extends TreeNodeCode<T>> List<T> genTree(List<T> allNodeList) {
        // 获取根极节点
        List<T> rootList = getRootList(allNodeList);

        // 根据父级id分组
        Map<Object, List<T>> parentIdTreeNodeListMap = allNodeList.parallelStream()
                .collect(Collectors.groupingBy(TreeNodeCode::getParentId));
        setRecursionSubList(parentIdTreeNodeListMap, rootList);
        return rootList;
    }

    /**
     * 生成树级结构
     *
     * @param allNodeList 所有节点列表
     * @param <T>         实现的业务对象类型
     * @return 结果树
     */
    public static <T extends TreeNodeCode<T>> List<T> genTreeCode(List<T> allNodeList) {
        // 获取根极节点
        List<T> rootList = getRootList(allNodeList);

        // 根据父级id分组
        Map<Object, List<T>> parentIdTreeNodeListMap = allNodeList.parallelStream()
                .collect(Collectors.groupingBy(TreeNodeCode::getParentId));
        setRecursionSubCodeList(parentIdTreeNodeListMap, rootList);
        return rootList;
    }

    /**
     * 获取根级节点
     *
     * @param allNodeList allNodeList
     * @param <T>         T
     * @return 根级节点
     */
    private static <T extends TreeNodeCode<T>> List<T> getRootList(List<T> allNodeList) {
        Set<Object> ids = allNodeList.parallelStream().map(TreeNodeCode::getId).collect(Collectors.toSet());
        return allNodeList.parallelStream()
                // 列表中找不到父级为根
                .filter(treeNode -> !ids.contains(treeNode.getParentId()))
                .collect(Collectors.toList());
    }

    /**
     * 递归设置子组织架构
     *
     * @param idTreeNodeMap    所有节点id与对象映射
     * @param rootTreeNodeList 当前根节点列表
     * @param <T>              实现的业务对象类型
     */
    private static <T extends TreeNodeCode<T>> void setRecursionSubCodeList(Map<Object, List<T>> idTreeNodeMap, List<T> rootTreeNodeList) {
        rootTreeNodeList.forEach(treeNode ->
                Optional.ofNullable(idTreeNodeMap.get(treeNode.getCode())).ifPresent(treeNodeList -> {
                    setRecursionSubCodeList(idTreeNodeMap, treeNodeList);
                    treeNode.setSubList(treeNodeList);
                })
        );
    }

    /**
     * 递归设置子组织架构
     *
     * @param idTreeNodeMap    所有节点id与对象映射
     * @param rootTreeNodeList 当前根节点列表
     * @param <T>              实现的业务对象类型
     */
    private static <T extends TreeNodeCode<T>> void setRecursionSubList(Map<Object, List<T>> idTreeNodeMap, List<T> rootTreeNodeList) {
        rootTreeNodeList.forEach(treeNode ->
                Optional.ofNullable(idTreeNodeMap.get(treeNode.getId())).ifPresent(treeNodeList -> {
                    setRecursionSubList(idTreeNodeMap, treeNodeList);
                    treeNode.setSubList(treeNodeList);
                })
        );
    }


    public static <T extends TreeNodeCode<T>> void recursionHandler(List<T> allNodeList, Consumer<T> handler) {
        allNodeList.forEach(treeNode -> {
            handler.accept(treeNode);
            Optional.ofNullable(treeNode.getSubList()).ifPresent(subList -> recursionHandler(subList, handler));
        });
    }
}
