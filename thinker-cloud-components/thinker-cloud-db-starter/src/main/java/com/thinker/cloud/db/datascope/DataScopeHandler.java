package com.thinker.cloud.db.datascope;

import java.util.Collection;

/**
 * data scope 判断处理器,抽象服务扩展
 *
 * @author admin
 */
public interface DataScopeHandler {

    /**
     * 获取用户权限类型
     *
     * @return Integer
     */
    Integer getDataScopeType();

    /**
     * 获取用户权限值
     *
     * @return Collection<String>
     */
    Collection<String> getDataScopeIds();

    /**
     * 计算用户数据权限
     *
     * @param originalSql 原始Sql
     * @param dataScope   数据权限
     */
    void calcScope(String originalSql, DataScope dataScope);
}
