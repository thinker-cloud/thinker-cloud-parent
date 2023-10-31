package com.thinker.cloud.db.datascope;

import com.thinker.cloud.db.enums.DataScopeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * 数据权限查询参数
 *
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataScope extends HashMap<String, Object> {

    /**
     * 限制范围的字段名称
     */
    private String scopeName = "organization_id";

    /**
     * 具体的数据权限范围
     */
    private Collection<String> dataScopeIds = new ArrayList<>();

    /**
     * 数据权限类型
     */
    private Integer type = DataScopeTypeEnum.ALL.getType();
}
