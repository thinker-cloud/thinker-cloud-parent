package com.thinker.cloud.core.cache.base;

/**
 * 默认缓存顶层抽象接口
 *
 * @author admin
 **/
public interface IDefaultCache<T> extends ICache {

    /**
     * 构建默认缓存
     *
     * @return T
     */
    T buildDefaultCache();

    /**
     * 获取默认缓存
     *
     * @return T
     */
    T getDefaultCache();

    /**
     * 删除默认缓存
     *
     * @return 是否删除
     */
    boolean removeDefaultCache();
}
