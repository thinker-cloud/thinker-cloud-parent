package com.thinker.cloud.common.utils.range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 日期时间范围
 *
 * @author admin
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DateTimeRange implements IRange<LocalDateTime>, Serializable {

    @Serial
    private static final long serialVersionUID = 1408469165377900109L;

    /**
     * 开始时间
     */
    private LocalDateTime startDateTime;

    /**
     * 结束时间
     */
    private LocalDateTime endDateTime;

    @Override
    public LocalDateTime getStart() {
        return this.startDateTime;
    }

    @Override
    public LocalDateTime getEnd() {
        return this.endDateTime;
    }
}
