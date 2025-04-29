package com.thinker.cloud.db.dynamic.datasource.plugins;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.enums.DdConstants;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 动态主从自动路由默认拦截器
 *
 * @author admin
 **/
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class DynamicMasterSlaveInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];

        if (StrUtil.isNotBlank(DynamicDataSourceContextHolder.peek())) {
            return invocation.proceed();
        }

        try {
            String dataSource;
            if (SqlCommandType.SELECT.equals(ms.getSqlCommandType())) {
                dataSource = DdConstants.SLAVE;
            } else {
                dataSource = DdConstants.MASTER;
            }
            DynamicDataSourceContextHolder.push(dataSource);
            return invocation.proceed();
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
    }
}
