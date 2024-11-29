package com.thinker.cloud.common.utils.range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 日期范围
 *
 * @author admin
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DateRange implements IRange<LocalDate>, Serializable {

    @Serial
    private static final long serialVersionUID = -4675617900499897701L;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    @Override
    public LocalDate getStart() {
        return this.startDate;
    }

    @Override
    public LocalDate getEnd() {
        return this.endDate;
    }
}
