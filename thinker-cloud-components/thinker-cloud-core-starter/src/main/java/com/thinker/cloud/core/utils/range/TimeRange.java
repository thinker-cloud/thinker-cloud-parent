package com.thinker.cloud.core.utils.range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

/**
 * 时间范围
 *
 * @author admin
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TimeRange implements IRange<LocalTime>, Serializable {

    @Serial
    private static final long serialVersionUID = -7814297716085186543L;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    @Override
    public LocalTime getStart() {
        return this.startTime;
    }

    @Override
    public LocalTime getEnd() {
        return this.endTime;
    }
}
