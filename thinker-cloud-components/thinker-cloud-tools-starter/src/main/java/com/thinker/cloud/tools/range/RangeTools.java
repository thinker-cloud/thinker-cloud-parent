package com.thinker.cloud.tools.range;

import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 范围工具类
 *
 * @author admin
 **/
@UtilityClass
public class RangeTools {

    /**
     * 判断目标是否在范围内
     *
     * @param target 目标对象
     * @param range  范围
     * @return boolean
     */
    public static <T extends Comparable<T>> boolean isWithinRange(T target, Range<T> range) {
        return target.compareTo(range.getStart()) > -1 && target.compareTo(range.getEnd()) < 1;
    }

    /**
     * 判断目标是否在范围内
     *
     * @param target 目标对象
     * @param list   范围列表
     * @return boolean
     */
    public static <T extends Comparable<T>> boolean isWithinRange(T target, List<Range<T>> list) {
        if (CollectionUtils.isEmpty(list)) {
            return true;
        }

        return list.stream().anyMatch(range -> isWithinRange(target, range));
    }

}
