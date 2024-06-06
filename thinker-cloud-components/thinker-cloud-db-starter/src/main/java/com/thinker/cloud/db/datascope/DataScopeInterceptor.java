package com.thinker.cloud.db.datascope;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Map;


/**
 * 数据权限拦截器
 *
 * @author admin
 */
@Slf4j
@AllArgsConstructor
public class DataScopeInterceptor implements InnerInterceptor {

    private final DataScopeHandler dataScopeHandler;

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                            ResultHandler resultHandler, BoundSql boundSql) {
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);

        String originalSql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();

        // 查找参数中包含DataScope类型的参数
        DataScope dataScope = this.findDataScopeObject(parameterObject);
        String sql = dataScopeHandler.calcScope(originalSql, dataScope);
        mpBs.sql(sql);
    }

    /**
     * 查找参数是否包括DataScope对象
     *
     * @param parameterObj 参数列表
     * @return DataScope
     */
    private DataScope findDataScopeObject(Object parameterObj) {
        if (parameterObj instanceof DataScope) {
            return (DataScope) parameterObj;
        } else if (parameterObj instanceof Map) {
            for (Object val : ((Map<?, ?>) parameterObj).values()) {
                if (val instanceof DataScope) {
                    return (DataScope) val;
                }
            }
        }
        return null;
    }
}
