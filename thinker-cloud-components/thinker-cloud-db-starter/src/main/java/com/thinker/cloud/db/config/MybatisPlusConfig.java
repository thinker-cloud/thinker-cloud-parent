package com.thinker.cloud.db.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.thinker.cloud.db.datascope.DataScopeHandler;
import com.thinker.cloud.db.datascope.DataScopeInterceptor;
import com.thinker.cloud.db.datascope.DefaultDataScopeHandler;
import com.thinker.cloud.db.handler.BaseMetaObjectHandler;
import com.thinker.cloud.db.injector.EnhanceSqlInjector;
import com.thinker.cloud.db.properties.DbConfigProperties;
import com.thinker.cloud.db.tenant.TenantMaintenanceHandler;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

/**
 * Mybatis-Plus 配置类
 *
 * @author admin
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, DbConfigProperties.class})
public class MybatisPlusConfig implements WebMvcConfigurer {

    @Resource
    private DbConfigProperties dbConfigProperties;

    /**
     * 数据权限处理器对象
     */
    @Bean
    @ConditionalOnMissingBean
    public DataScopeHandler dataScopeHandler() {
        return new DefaultDataScopeHandler();
    }

    /**
     * 创建租户维护处理器对象
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantMaintenanceHandler tenantMaintenanceHandler() {
        return new TenantMaintenanceHandler();
    }

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
    public EnhanceSqlInjector enhanceSqlInjector() {
        return new EnhanceSqlInjector();
    }

    /**
     * mybatis plus 拦截器配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户支持
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(tenantMaintenanceHandler());
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor);

        // 数据权限
        DataScopeInterceptor dataScopeInterceptor = new DataScopeInterceptor();
        dataScopeInterceptor.setDataScopeHandler(dataScopeHandler());
        interceptor.addInnerInterceptor(dataScopeInterceptor);

        // 分页支持
        DbType dbType = DbType.getDbType(dbConfigProperties.getDbType());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
        return interceptor;
    }
}
