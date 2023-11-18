package com.thinker.cloud.db.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.thinker.cloud.db.properties.DbConfigProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;

/**
 * 租户维护处理器
 *
 * @author admin
 */
@Slf4j
public class TenantMaintenanceHandler implements TenantLineHandler {

    @Resource
    private DbConfigProperties properties;

    /**
     * 获取租户值
     *
     * @return 租户值
     */
    @Override
    public Expression getTenantId() {
        Long tenantId = TenantContextHolder.getTenantId();
        log.debug("当前租户为 >> {}", tenantId);

        if (tenantId == null) {
            return new NullValue();
        }
        return new LongValue(tenantId);
    }

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return properties.getTenant().getColumn();
    }

    /**
     * 根据表名或租户id，判断是否进行过滤
     *
     * @param tableName 表名
     * @return 是否进行过滤
     */
    @Override
    public boolean ignoreTable(String tableName) {
        Long tenantId = TenantContextHolder.getTenantId();

        // 租户配置
        DbConfigProperties.TenantConfigProperties tenant = properties.getTenant();

        // 忽略租户id
        if (tenantId != null && tenant.getIgnoreTenantIds().contains(tenantId)) {
            return Boolean.TRUE;
        }

        // 忽略表
        return tenant.getIgnoreTables().contains(tableName);
    }
}
