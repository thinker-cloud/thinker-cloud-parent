package com.thinker.cloud.db.dynamic.datasource.aspect;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Objects;

/**
 * 动态数据源切面
 *
 * @author admin
 **/
@Slf4j
@Aspect
public class DynamicDataSourceAspect {

    @Value("${spring.datasource.dynamic.primary:master}")
    private String primary;

    @Pointcut("@annotation(com.thinker.cloud.db.dynamic.datasource.annotation.Master) "
            + "|| @annotation(com.thinker.cloud.db.dynamic.datasource.annotation.Slave)")
    public void dsPointCut() {

    }

    @Around(value = "dsPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取需要切换的数据源
        DS ds = this.getDataSource(joinPoint);
        if (Objects.nonNull(ds) && StrUtil.isNotBlank(ds.value())) {
            if (Objects.equals(ds.value(), DynamicDataSourceContextHolder.peek())) {
                return joinPoint.proceed();
            }
            DynamicDataSourceContextHolder.push(ds.value());
        }

        try {
            return joinPoint.proceed();
        } finally {
            // 在执行方法之后 销毁数据源
            DynamicDataSourceContextHolder.clear();
        }
    }

    /**
     * 获取需要切换的数据源
     */
    private DS getDataSource(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DS dataSource = AnnotationUtils.findAnnotation(signature.getMethod(), DS.class);
        if (Objects.nonNull(dataSource)) {
            return dataSource;
        }

        // Check if the method is declared in an interface
        Class<?> declaringType = signature.getDeclaringType();
        if (declaringType.isInterface()) {
            // Find annotation on the interface
            dataSource = AnnotationUtils.findAnnotation(declaringType, DS.class);
            if (Objects.nonNull(dataSource)) {
                return dataSource;
            }
        }

        // Find annotation on the class (if exists)
        return AnnotationUtils.findAnnotation(declaringType, DS.class);
    }
}
