package com.thinker.cloud.redis.idempotent.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * 幂等操作注解
 *
 * @author admin
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 幂等唯一标识，使用spring el表达式 用#来引用方法参数
     *
     * @return Spring-EL expression
     */
    String key() default "";

    /**
     * 缓存Key前缀
     *
     * @return String
     */
    String prefix() default "";

    /**
     * 有效期 默认：3 有效期要大于程序执行时间，否则请求还是可能会进来
     *
     * @return expireTime
     */
    int expireTime() default 3;

    /**
     * 时间单位 默认：s
     *
     * @return TimeUnit
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示信息，可自定义
     *
     * @return String
     */
    String info() default "重复请求，请稍后重试";

    /**
     * 是否在业务完成后删除key true:删除 false:不删除
     *
     * @return boolean
     */
    boolean delKey() default false;

}
