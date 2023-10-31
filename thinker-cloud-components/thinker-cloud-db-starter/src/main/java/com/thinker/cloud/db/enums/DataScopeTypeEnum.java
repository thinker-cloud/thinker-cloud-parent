package com.thinker.cloud.db.enums;

import com.thinker.cloud.core.enums.IEnumDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型
 *
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum DataScopeTypeEnum implements IEnumDict<Integer> {

    /**
     * 查询全部数据
     */
    ALL(1, "全部"),

    /**
     * 本级及子级
     */
    OWN_CHILD_LEVEL(2, "本级及子级"),

    /**
     * 本级
     */
    OWN_LEVEL(3, "本级"),

    /**
     * 当前用户
     */
    OWN_USER(4, "当前用户"),

    /**
     * 自定义
     */
    CUSTOM(2, "自定义"),
    ;

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String desc;


    @Override
    public Integer getValue() {
        return this.type;
    }
}
