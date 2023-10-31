package com.thinker.cloud.common.analysis.constants;


import com.thinker.cloud.core.enums.IEnumDict;
import com.thinker.cloud.core.utils.enums.EnumCacheUtil;
import org.springframework.lang.NonNull;

/**
 * 分析周期
 *
 * @author admin
 */
public enum AnalysisCycle implements IEnumDict<String> {
    //
    minute,
    hour,
    day,
    week,
    month,
    quarterly,
    year,
    ;


    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public String getDesc() {
        return null;
    }

    @Override
    public boolean isWriteJsonObjectEnabled() {
        return false;
    }


    public boolean equals(String value) {
        return this.name().equals(value);
    }

    public static AnalysisCycle resolve(@NonNull String value) {
        return EnumCacheUtil.loadEnumValue(AnalysisCycle.class, value);
    }
}
