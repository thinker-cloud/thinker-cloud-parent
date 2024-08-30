package com.thinker.cloud.rocketmq.annotation;

import java.lang.annotation.*;

/**
 * RocketMQ启用注解
 *
 * @author admin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableRocketMQ {
}
