package com.thinker.cloud.core.utils.dependhandle;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

/**
 * 外键与设置过程关系
 *
 * @author admin
 */
@Data
@AllArgsConstructor
public class KeyValueHandlerRelation<T, D, K> {

    private Function<T, K> foreignKey;
    private SetValueHandler<T, D> setValueHandler;

    public static <T, D, K> KeyValueHandlerRelation<T, D, K> of(Function<T, K> foreignKey
            , SetValueHandler<T, D> setValueHandler) {
        return new KeyValueHandlerRelation<>(foreignKey, setValueHandler);
    }
}
