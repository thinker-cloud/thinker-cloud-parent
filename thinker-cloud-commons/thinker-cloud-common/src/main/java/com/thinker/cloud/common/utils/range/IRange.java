package com.thinker.cloud.common.utils.range;

/**
 * 范围
 *
 * @author admin
 **/
public interface IRange<T> {

    /**
     * 开始
     *
     * @return T
     */
    T getStart();

    /**
     * 结束
     *
     * @return T
     */
    T getEnd();

    /**
     * 格式化
     *
     * @return String
     */
    default String format() {
        return this.getStart() + "-" + this.getEnd();
    }
}
