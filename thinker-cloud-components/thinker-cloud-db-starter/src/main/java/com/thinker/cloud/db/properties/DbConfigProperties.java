package com.thinker.cloud.db.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Set;

/**
 * 数据库配置
 *
 * @author admin
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "thinker.cloud.db")
public class DbConfigProperties {

    /**
     * 数据库类型
     */
    private String type = "mysql";

    /**
     * 数据权限配置
     */
    private DataScopeProperties dataScope = new DataScopeProperties();

    /**
     * 租户配置
     */
    private TenantConfigProperties tenant = new TenantConfigProperties();

    /**
     * 数据权限配置
     */
    @Data
    public static class DataScopeProperties {

        /**
         * 是否开启数据权限
         */
        private Boolean enable = true;

        /**
         * 限制范围的字段名称
         */
        private String scopeName = "organization_id";

        /**
         * 限制用户权限范围的字段名称
         */
        private String userScopeName = "create_by";

        /**
         * 忽略数据权限过滤的数据表集合
         */
        private Set<String> ignoreTables = Collections.emptySet();
    }

    /**
     * 租户配置
     */
    @Data
    public static class TenantConfigProperties {

        /**
         * 是否开启租户隔离
         */
        private Boolean enable = true;

        /**
         * 维护租户列名称
         */
        private String column = "tenant_id";

        /**
         * 忽略多租户的数据表集合
         */
        private Set<String> ignoreTables = Collections.emptySet();

        /**
         * 忽略租户ids集合
         */
        private Set<Long> ignoreTenantIds = Collections.emptySet();
    }
}
