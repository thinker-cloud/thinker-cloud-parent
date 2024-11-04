package com.thinker.cloud.redis.cache.interfaces;

import java.util.Optional;

/**
 * 禁用缓存
 *
 * @author admin
 **/
public interface DisableCache {

    /**
     * 获取是否禁用缓存
     *
     * @return Boolean
     */
    Boolean getDisableCache();

    /**
     * 设置是否禁用缓存
     *
     * @param disableCache disableCache
     * @return DisableCache
     */
    DisableCache setDisableCache(Boolean disableCache);

    /**
     * 是否禁用缓存
     *
     * @return boolean
     */
    default boolean disableCache() {
        return Optional.ofNullable(this.getDisableCache()).orElse(false);
    }
}
