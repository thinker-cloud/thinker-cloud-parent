package com.thinker.cloud.core.cache.fast;

/**
 * 动态key
 *
 * @author admin
 */
public interface IDyKey {

    /**
     * 获取最终的key
     *
     * @return 最终的key
     */
    String getKey();
}
