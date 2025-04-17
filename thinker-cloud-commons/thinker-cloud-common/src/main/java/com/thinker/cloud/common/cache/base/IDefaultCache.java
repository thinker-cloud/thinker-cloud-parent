package com.thinker.cloud.common.cache.base;

/**
 * 默认缓存顶层抽象接口
 *
 * @author admin
 **/
public interface IDefaultCache<V> extends ICache<V> {

    /**
     * 构建默认缓存
     *
     * @return V
     */
    V buildDefaultCache();

    /**
     * 获取默认缓存
     *
     * @return V
     */
    V getDefaultCache();

    /**
     * 删除默认缓存
     *
     * @return 是否删除
     */
    boolean removeDefaultCache();
}
