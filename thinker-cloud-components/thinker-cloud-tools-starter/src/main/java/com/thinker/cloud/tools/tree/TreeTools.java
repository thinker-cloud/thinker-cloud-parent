package com.thinker.cloud.tools.tree;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 树级结构生成器
 *
 * @author admin
 * @since 2020/12/31 18:05
 */
public class TreeTools {

    /**
     * 生成树级结构
     *
     * @param allNodeList 所有节点列表
     * @param <Node>         实现的业务对象类型
     * @return 结果树
     */
    public static <Node extends TreeNode<Node>> List<Node> genTree(List<Node> allNodeList) {
        List<Node> rootList = listRoot(allNodeList);
        // 根据父级id分组
        Map<Object, List<Node>> parentIdTreeNodeListMap = allNodeList.parallelStream()
                .collect(Collectors.groupingBy(TreeNode::getParentId));
        setRecursionSubList(parentIdTreeNodeListMap, rootList);
        return rootList;
    }

    /**
     * 查找根级树
     *
     * @param allNodeList 所有节点列表
     * @param <Node>         实现的业务对象类型
     * @return 结果树
     */
    public static <Node extends TreeNode<Node>> List<Node> listRoot(List<Node> allNodeList) {
        Set<Object> ids = allNodeList.parallelStream().map(TreeNode::getId).collect(Collectors.toSet());
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
     * @param <Node>           实现的业务对象类型
     */
    private static <Node extends TreeNode<Node>> void
    setRecursionSubList(Map<Object, List<Node>> idTreeNodeMap, List<Node> rootTreeNodeList) {
        rootTreeNodeList.forEach(treeNode ->
                Optional.ofNullable(idTreeNodeMap.get(treeNode.getId())).ifPresent(treeNodeList -> {
                    setRecursionSubList(idTreeNodeMap, treeNodeList);
                    treeNode.setSubList(treeNodeList);
                })
        );
    }


    public static <Node extends TreeNode<Node>> void recursionHandler(List<Node> tree, Consumer<Node> handler) {
        tree.forEach(treeNode -> {
            handler.accept(treeNode);
            Optional.ofNullable(treeNode.getSubList()).ifPresent(subList -> recursionHandler(subList, handler));
        });
    }

    /**
     * 关联依赖列表
     */
    public static <Node extends TreeNode<Node>, Fruit> void associateFruitList(List<Node> tree, List<Fruit> fruitList
            , Function<Fruit, Long> getTreeNodeId, BiConsumer<Node, List<Fruit>> handler) {
        Map<Long, List<Fruit>> nodeIdAndFruitListMap = fruitList.parallelStream()
                .filter(fruit -> getTreeNodeId.apply(fruit) != null)
                .collect(Collectors.groupingBy(getTreeNodeId));
        recursionHandler(tree, node ->
                Optional.ofNullable(nodeIdAndFruitListMap.get(node.getId()))
                        .ifPresent(assFruitList -> handler.accept(node, assFruitList))
        );
    }


    /**
     * 从末至根过滤,把不符合条件的删掉
     *
     * @param tree            需要修剪的树
     * @param filterCondition 过滤条件，保留这个节点的条件
     * @param <Node>          树节点
     * @return 当前根级列表为空
     */
    public static <Node extends TreeNode<Node>> boolean trim(List<Node> tree, Predicate<? super Node> filterCondition) {
        if (tree == null) {
            return true;
        }
        List<Long> notMatchId = tree.stream()
                .filter(node -> trim(node.getSubList(), filterCondition) & !filterCondition.test(node))
                .map(Node::getId).collect(Collectors.toList());
        tree.removeIf(node -> notMatchId.contains(node.getId()));
        return tree.isEmpty();
    }
}
