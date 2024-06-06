package com.thinker.cloud.db.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库配置
 *
 * @author admin
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "thinker-cloud.db")
public class DbConfigProperties {

    /**
     * 数据库类型
     */
    private String dbType = DbType.MYSQL.getDb();

    /**
     * 租户配置
     */
    private TenantConfigProperties tenant = new TenantConfigProperties();

    /**
     * 租户配置
     */
    @Data
    public static class TenantConfigProperties {

        /**
         * 维护租户列名称
         */
        private String column = "tenant_id";

        /**
         * 忽略多租户的数据表集合
         */
        private List<String> ignoreTables = new ArrayList<>();

        /**
         * 忽略租户ids集合
         */
        private List<Long> ignoreTenantIds = new ArrayList<>();
    }
}
