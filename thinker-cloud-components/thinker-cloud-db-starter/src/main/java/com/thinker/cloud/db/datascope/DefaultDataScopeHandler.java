package com.thinker.cloud.db.datascope;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.db.enums.DataScopeTypeEnum;
import com.thinker.cloud.db.properties.DbConfigProperties;
import com.thinker.cloud.security.constants.SecurityConstants;
import com.thinker.cloud.security.userdetail.DigitUser;
import com.thinker.cloud.security.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.compress.utils.Sets;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 默认data scope 判断处理器
 *
 * @author admin
 */
@Slf4j
@AllArgsConstructor
public class DefaultDataScopeHandler implements DataScopeHandler {

    private final DbConfigProperties.DataScopeProperties properties;

    /**
     * 数据权限忽略类型定义
     */
    private static final List<Integer> IGNORE_DATA_SCOPE_TYPES =
            Arrays.asList(DataScopeTypeEnum.ALL.getType(), DataScopeTypeEnum.CUSTOM.getType());

    @Override
    public Integer getDataScopeType() {
        DigitUser digitUser = SecurityUtils.getUser();
        if (ObjectUtil.isNotEmpty(digitUser)) {
            return digitUser.getDataScopeType();
        }
        return DataScopeTypeEnum.OWN_USER.getType();
    }

    @Override
    public Collection<String> getDataScopeIds() {
        DigitUser digitUser = SecurityUtils.getUser();
        if (ObjectUtil.isNotEmpty(digitUser)) {
            return digitUser.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.startsWith(SecurityConstants.AUTH_DATA_SCOPE))
                    .map(authority -> authority.replace(SecurityConstants.AUTH_DATA_SCOPE, StrUtil.EMPTY))
                    .collect(Collectors.toSet());
        }
        return null;
    }

    /**
     * 计算用户数据权限
     */
    @Override
    public String calcScope(String originalSql, DataScope dataScope) {
        // 数据权限忽略检查
        if (this.ignoreDataScope(originalSql, dataScope)) {
            return originalSql;
        }

        // 当前用户
        if (DataScopeTypeEnum.OWN_USER.getType().equals(dataScope.getType())) {
            if (StrUtil.isBlank(dataScope.getScopeName())) {
                dataScope.setScopeName(properties.getUserScopeName());
            }

            if (StrUtil.containsIgnoreCase(originalSql, dataScope.getScopeName())) {
                return String.format("select * from (%s) temp_data_scope where temp_data_scope.%s = %s"
                        , originalSql, dataScope.getScopeName(), SecurityUtils.getUserId());
            }

            return originalSql;
        }

        // 本级或本级及子级
        if (StrUtil.isBlank(dataScope.getScopeName())) {
            dataScope.setScopeName(properties.getScopeName());
        }

        if (StrUtil.containsIgnoreCase(originalSql, dataScope.getScopeName())
                && CollUtil.isNotEmpty(dataScope.getDataScopeIds())) {
            String join = CollectionUtil.join(dataScope.getDataScopeIds(), ",");
            return String.format("select * from (%s) temp_data_scope where temp_data_scope.%s in (%s)"
                    , originalSql, dataScope.getScopeName(), join);
        }
        return originalSql;
    }

    /**
     * 忽略数据权限检查
     *
     * @param originalSql originalSql
     * @param dataScope   dataScope
     * @return boolean
     */
    public boolean ignoreDataScope(String originalSql, DataScope dataScope) {
        if (ObjectUtil.isEmpty(SecurityUtils.getUser())) {
            return true;
        }

        if (Objects.isNull(dataScope)) {
            dataScope = new DataScope();
            dataScope.setType(getDataScopeType());
            dataScope.setDataScopeIds(getDataScopeIds());
        }

        // 查询全部或自定义权限则忽略
        if (IGNORE_DATA_SCOPE_TYPES.contains(dataScope.getType())) {
            return true;
        }

        // 没有指定表则不处理
        if (properties.getIgnoreTables().isEmpty()) {
            return false;
        }

        Set<String> tables = Sets.newHashSet();
        try {
            tables.addAll(TablesNamesFinder.findTables(originalSql));
        } catch (JSQLParserException e) {
            log.error("获取表名失败，Sql解析异常，ex={}" , e.getMessage(), e);
        }

        // 存在指定表忽略权限处理
        return tables.stream().anyMatch(tableName -> properties.getIgnoreTables().contains(tableName));
    }
}
