package com.thinker.cloud.db.datascope;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.db.enums.DataScopeTypeEnum;
import com.thinker.cloud.security.constants.SecurityConstants;
import com.thinker.cloud.security.userdetail.DigitUser;
import com.thinker.cloud.security.utils.SecurityUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 默认data scope 判断处理器
 *
 * @author admin
 */
public class DefaultDataScopeHandle implements DataScopeHandle {

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
            return digitUser.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.startsWith(SecurityConstants.AUTH_DATA_SCOPE))
                    .map(item -> item.replace(SecurityConstants.AUTH_DATA_SCOPE, StrUtil.EMPTY))
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 计算用户数据权限
     */
    @Override
    public void calcScope(String originalSql, DataScope dataScope) {
        DigitUser digitUser = SecurityUtils.getUser();
        if (ObjectUtil.isEmpty(digitUser)) {
            return;
        }

        if (ObjectUtil.isEmpty(dataScope)) {
            dataScope = new DataScope();
            dataScope.setType(getDataScopeType());
            dataScope.setDataScopeIds(getDataScopeIds());
        }

        // 查询全部
        if (DataScopeTypeEnum.ALL.getType().equals(dataScope.getType())) {
            return;
        }

        // 当前用户
        if (DataScopeTypeEnum.OWN_USER.getType().equals(dataScope.getType())
                && StrUtil.containsIgnoreCase(originalSql, "create_by")) {
            originalSql = String.format("SELECT * FROM (%s) temp_data_scope WHERE temp_data_scope.create_by = %s",
                    originalSql, digitUser.getId());
            return;
        }

        if (StrUtil.containsIgnoreCase(originalSql, dataScope.getScopeName())
                && CollUtil.isNotEmpty(dataScope.getDataScopeIds())) {
            String join = CollectionUtil.join(dataScope.getDataScopeIds(), ",");
            originalSql = String.format("SELECT * FROM (%s) temp_data_scope WHERE temp_data_scope.%s IN (%s)",
                    originalSql, dataScope.getScopeName(), join);
        }
    }
}
