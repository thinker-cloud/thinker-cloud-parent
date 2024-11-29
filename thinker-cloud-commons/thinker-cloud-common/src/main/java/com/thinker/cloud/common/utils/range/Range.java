package com.thinker.cloud.common.utils.range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 范围
 *
 * @author admin
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Range<T extends Comparable<T>> implements IRange<T>, Serializable {

    @Serial
    private static final long serialVersionUID = -4757143825685635721L;

    /**
     * 开始
     */
    private T start;

    /**
     * 结束
     */
    private T end;
}
