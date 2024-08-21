package com.thinker.cloud.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 判断是否有权限
 *
 * @author admin
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@pms.hasPermission('{value}'.split(','))")
public @interface HasPermission {

    /**
     * 权限字符串
     *
     * @return {@link String[] }
     */
    String[] value();

}
