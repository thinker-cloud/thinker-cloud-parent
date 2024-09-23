package com.thinker.cloud.db.dynamic.datasource.config;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.thinker.cloud.db.dynamic.datasource.aspect.DynamicDataSourceAspect;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多数据源配置
 *
 * @author admin
 **/
@Configuration
@ConditionalOnBean(DataSourceAutoConfiguration.class)
@AutoConfigureAfter(DynamicDataSourceAutoConfiguration.class)
public class DynamicDataSourceConfiguration {

    /**
     * 多数源切面
     *
     * @return DynamicDataSourceAspect
     */
    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect() {
        return new DynamicDataSourceAspect();
    }
}
