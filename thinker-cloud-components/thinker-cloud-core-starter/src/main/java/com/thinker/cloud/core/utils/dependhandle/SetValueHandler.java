package com.thinker.cloud.core.utils.dependhandle;

/**
 * 值设置过程
 *
 * @author admin
 */
@FunctionalInterface
public interface SetValueHandler<T, D> {
    /**
     * 依赖设置过程
     *
     * @param setObject 被设置属性的对象
     * @param value     待设置的值
     */
    void set(T setObject, D value);
}
