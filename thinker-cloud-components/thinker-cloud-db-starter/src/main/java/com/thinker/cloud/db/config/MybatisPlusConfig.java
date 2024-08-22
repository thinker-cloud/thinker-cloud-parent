package com.thinker.cloud.db.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.thinker.cloud.db.datascope.DataScopeInterceptor;
import com.thinker.cloud.db.datascope.DefaultDataScopeHandler;
import com.thinker.cloud.db.handler.BaseMetaObjectHandler;
import com.thinker.cloud.db.injector.EnhanceSqlInjector;
import com.thinker.cloud.db.properties.DbConfigProperties;
import com.thinker.cloud.db.tenant.TenantMaintenanceHandler;
import com.thinker.cloud.db.tenant.TenantRequestInterceptor;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Mybatis-Plus 配置类
 *
 * @author admin
 */
@Configuration
@AutoConfigureAfter(DbConfigProperties.class)
@ConditionalOnBean(DataSourceAutoConfiguration.class)
public class MybatisPlusConfig implements WebMvcConfigurer {

    @Resource
    private DbConfigProperties dbConfigProperties;

    /**
     * 字段自动填充处理器对象
     */
    @Bean
    @ConditionalOnMissingBean
    public BaseMetaObjectHandler baseMetaObjectHandler() {
        return new BaseMetaObjectHandler();
    }

    /**
     * 增强自定义方法注入对象
     */
    @Bean
    @ConditionalOnMissingBean
    public EnhanceSqlInjector enhanceSqlInjector() {
        return new EnhanceSqlInjector();
    }

    /**
     * mybatis plus 拦截器配置
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 多租户支持
        if (dbConfigProperties.getTenant().getEnable()) {
            TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
            tenantLineInnerInterceptor.setTenantLineHandler(new TenantMaintenanceHandler(dbConfigProperties.getTenant()));
            interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        }

        // 数据权限支持
        if (dbConfigProperties.getDataScope().getEnable()) {
            DefaultDataScopeHandler dataScopeHandler = new DefaultDataScopeHandler(dbConfigProperties.getDataScope());
            DataScopeInterceptor dataScopeInterceptor = new DataScopeInterceptor(dataScopeHandler);
            interceptor.addInnerInterceptor(dataScopeInterceptor);
        }

        // 分页支持
        DbType dbType = DbType.getDbType(dbConfigProperties.getDbType());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
        return interceptor;
    }

    /**
     * 传递请求的租户ID
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantRequestInterceptor tenantRequestInterceptor() {
        return new TenantRequestInterceptor();
    }
}

