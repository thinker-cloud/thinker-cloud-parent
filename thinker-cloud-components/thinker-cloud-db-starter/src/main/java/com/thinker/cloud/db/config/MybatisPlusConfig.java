package com.thinker.cloud.db.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.thinker.cloud.db.datascope.DataScopeHandle;
import com.thinker.cloud.db.datascope.DataScopeInterceptor;
import com.thinker.cloud.db.datascope.DefaultDataScopeHandle;
import com.thinker.cloud.db.handler.MyMetaObjectHandler;
import com.thinker.cloud.db.properties.DbConfigProperties;
import com.thinker.cloud.db.tenant.TenantMaintenanceHandler;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
@EnableAutoConfiguration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, DbConfigProperties.class})
public class MybatisPlusConfig implements WebMvcConfigurer {

    @Resource
    private DbConfigProperties dbConfigProperties;

    @Bean
    @ConditionalOnMissingBean
    public DataScopeHandle dataScopeHandle() {
        return new DefaultDataScopeHandle();
    }


    /**
     * mybatis plus 拦截器配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户支持
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(digitTenantHandler());
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor);

        // 数据权限
        DataScopeInterceptor dataScopeInterceptor = new DataScopeInterceptor();
        dataScopeInterceptor.setDataScopeHandle(dataScopeHandle());
        interceptor.addInnerInterceptor(dataScopeInterceptor);

        // 分页支持
        DbType dbType = DbType.getDbType(dbConfigProperties.getDbType());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
        return interceptor;
    }

    /**
     * 创建租户维护处理器对象
     *
     * @return 处理后的租户维护处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantMaintenanceHandler digitTenantHandler() {
        return new TenantMaintenanceHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public MyMetaObjectHandler myMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }
}
