package com.thinker.cloud.redis.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * 缓存注解
 *
 * @author admin
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Cacheable {

    /**
     * 缓存唯一标识，使用spring el表达式 用#来引用方法参数
     *
     * @return Spring-EL expression
     */
    String[] keys() default {};

    /**
     * 缓存Key前缀
     *
     * @return String
     */
    String prefix() default "";

    /**
     * 缓存过期时间 默认：1分钟
     *
     * @return expireTime
     */
    int expireTime() default 1;

    /**
     * 时间单位 默认：分钟
     *
     * @return TimeUnit
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * Keys为空时生效，在使用所有参数生成缓存key时排除定义字段
     *
     * @return String[]
     */
    String[] ignoreKeys() default {};
}
