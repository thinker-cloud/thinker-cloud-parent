package com.thinker.cloud.core.aspect.expression;

import org.aspectj.lang.JoinPoint;

/**
 * 唯一标志处理器
 *
 * @author admin
 */
public interface KeyResolver {

    /**
     * 解析处理 key
     *
     * @param point 接口切点信息
     * @param key   接口注解标识
     * @return 处理结果
     */
    String resolver(JoinPoint point, String key);

    /**
     * 解析处理 keys
     *
     * @param point 接口切点信息
     * @param keys  接口注解标识
     * @return 处理结果
     */
    default String resolver(JoinPoint point, String... keys) {
        return this.resolver(point, ".", keys);
    }

    /**
     * 解析处理 keys
     *
     * @param point     接口切点信息
     * @param separator 分隔符
     * @param key       接口注解标识
     * @return 处理结果
     */
    String resolver(JoinPoint point, String separator, String... key);
}
