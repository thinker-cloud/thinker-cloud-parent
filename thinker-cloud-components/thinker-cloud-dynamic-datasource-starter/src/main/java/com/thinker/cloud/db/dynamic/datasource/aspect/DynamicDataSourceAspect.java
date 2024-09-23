package com.thinker.cloud.db.dynamic.datasource.aspect;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.thinker.cloud.core.exception.AbstractException;
import com.thinker.cloud.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
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
    public Object around(ProceedingJoinPoint joinPoint) {
        // 获取需要切换的数据源
        DS ds = this.getDataSource(joinPoint);
        if (Objects.nonNull(ds) && StrUtil.isNotBlank(ds.value())) {
            DynamicDataSourceContextHolder.push(ds.value());
        } else {
            DynamicDataSourceContextHolder.push(primary);
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("多数据切换失败，ds:{}，ex={}", ds, e.getMessage(), e);
            throw new ServiceException("数据源切换失败", e);
        } finally {
            // 在执行方法之后 销毁数据源
            DynamicDataSourceContextHolder.clear();
        }
    }

    @AfterThrowing(value = "dsPointCut()", throwing = "ex")
    public void afterThrowing(Throwable ex) {
        if (ex instanceof AbstractException) {
            throw (AbstractException) ex;
        }

        log.error("数据源切换未知异常，ex={}", ex.getMessage(), ex);
        throw new ServiceException("未知异常，请联系管理员");
    }

    /**
     * 获取需要切换的数据源
     */
    public DS getDataSource(ProceedingJoinPoint joinPoint) {
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
