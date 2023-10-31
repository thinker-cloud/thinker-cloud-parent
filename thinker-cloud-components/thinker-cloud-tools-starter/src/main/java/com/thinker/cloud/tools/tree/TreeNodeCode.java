package com.thinker.cloud.tools.tree;

import java.util.List;

/**
 * 树节点
 *
 * @author admin
 */
public interface TreeNodeCode<T extends TreeNodeCode<T>> {
    /**
     * 节点id
     *
     * @return id
     */
    Long getId();

    /**
     * 节点code
     *
     * @return code
     */
    String getCode();

    /**
     * 父级id
     *
     * @return parentId
     */
    Long getParentId();

    /**
     * 设置子列表
     *
     * @param subNodeList 子列表
     * @return this
     */
    T setSubList(List<T> subNodeList);

    /**
     * 获取子列表
     *
     * @return 子列表
     */
    List<T> getSubList();
}
