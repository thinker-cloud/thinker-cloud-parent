package com.thinker.cloud.db.dynamic.datasource.annotation;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.lang.annotation.*;

/**
 * 从库数据源
 *
 * @author admin
 */
@DS("slave")
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Slave {

}
