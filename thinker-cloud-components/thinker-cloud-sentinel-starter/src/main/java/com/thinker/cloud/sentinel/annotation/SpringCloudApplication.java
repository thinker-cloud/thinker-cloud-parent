package com.thinker.cloud.sentinel.annotation;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * Spring Cloud 启动注解
 *
 * @author amdin
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableDiscoveryClient
@SpringBootApplication
public @interface SpringCloudApplication {

    /**
     * Exclude specific auto-configuration classes such that they will never be applied.
     *
     * @return the classes to exclude
     */
    @AliasFor(annotation = EnableAutoConfiguration.class, attribute = "exclude")
    Class<?>[] exclude() default {};

    /**
     * Base packages to scan for annotated components.
     * for a type-safe alternative to String-based package names.
     * <p>
     * <strong>Note:</strong> this setting is an alias for
     * {@link ComponentScan @ComponentScan} only. It has no effect on {@code @Entity}
     * scanning or Spring Data {@link Repository} scanning. For those you should add
     * {@link org.springframework.boot.autoconfigure.domain.EntityScan @EntityScan} and
     * {@code @Enable...Repositories} annotations.
     *
     * @return base packages to scan
     * @since 1.3.0
     */
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {"com.thinker.cloud"};
}
