package com.thinker.cloud.db.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.thinker.cloud.db.datascope.DataScopeInterceptor;
import com.thinker.cloud.db.datascope.DefaultDataScopeHandler;
import com.thinker.cloud.db.handler.BaseMetaObjectHandler;
import com.thinker.cloud.db.injector.EnhanceSqlInjector;
import com.thinker.cloud.db.properties.DbConfigProperties;
import com.thinker.cloud.db.tenant.TenantMaintenanceHandler;
import com.thinker.cloud.db.tenant.TenantRequestInterceptor;
import com.thinker.cloud.db.typehandler.IntegerArrayTypeHandler;
import com.thinker.cloud.db.typehandler.LongArrayTypeHandler;
import com.thinker.cloud.db.typehandler.StringArrayTypeHandler;
import jakarta.annotation.Resource;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-Plus 配置类
 *
 * @author admin
 */
@Configuration
@AutoConfigureAfter(DbConfigProperties.class)
@ConditionalOnBean(DataSourceAutoConfiguration.class)
public class MybatisPlusConfig {

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

        // 分页支持
        DbType dbType = DbType.getDbType(dbConfigProperties.getType());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));

        // 乐观锁支持
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

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
        return interceptor;
    }

    /**
     * 自定义typeHandler全局自动注册
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCustomizer configurationCustomizer() {
        return mybatisConfiguration -> {
            TypeHandlerRegistry handlerRegistry = mybatisConfiguration.getTypeHandlerRegistry();

            // 逗号,隔开的字符或数字自动转为数组接收
            handlerRegistry.register(Long[].class, LongArrayTypeHandler.class);
            handlerRegistry.register(Integer[].class, IntegerArrayTypeHandler.class);
            handlerRegistry.register(String[].class, StringArrayTypeHandler.class);
        };
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

