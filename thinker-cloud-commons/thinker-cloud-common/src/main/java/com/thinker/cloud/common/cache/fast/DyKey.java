package com.thinker.cloud.common.cache.fast;

import com.alibaba.fastjson.JSON;
import com.thinker.cloud.common.utils.DeflaterUtil;
import lombok.ToString;

/**
 * 动态key
 * 用户多个缓存条件进行组合的构建器
 *
 * @author admin
 */
@ToString
public class DyKey implements IDyKey {

    private DyKey(String key) {
        this.key = key;
    }

    private final String key;

    public static DyKey join(Object... keys) {
        return builder().join(keys).build();
    }

    public static DyKeyBuilder builder() {
        return new DyKeyBuilder();
    }

    @Override
    public String getKey() {
        return key;
    }

    /**
     * 构建器
     */
    public static class DyKeyBuilder {
        private final StringBuilder keyBuild = new StringBuilder();

        public DyKeyBuilder join(Object... keys) {
            for (Object k : keys) {
                join(k, "-");
            }
            return this;
        }

        public DyKeyBuilder join(Object key, String separator) {
            if (!keyBuild.isEmpty()) {
                keyBuild.append(separator);
            }
            keyBuild.append(DeflaterUtil.zipString(JSON.toJSONString(key)));
            return this;
        }

        public DyKey build() {
            return new DyKey(keyBuild.toString());
        }
    }
}
