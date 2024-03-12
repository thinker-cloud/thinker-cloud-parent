package com.thinker.cloud.common.analysis.utils;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.utils.date.LocalDateTimeUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 根据一个时间，转换成指定类型的开始时间~结束时间
 *
 * @author admin
 */
@Getter
@Setter
@NoArgsConstructor
public class DateTimeSlot {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 时段类型 (1、日 2、月 3、年 4、一周前至今)
     */
    public static final Integer DAY = 1;
    public static final Integer MONTH = 2;
    public static final Integer YEAR = 3;
    public static final Integer A_WEEK_AGO = 4;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    /**
     * 该时段的时间范围（例如：2018、2018-11、2018-11-28）
     */
    private String dateTimeRangeText;

    /**
     * 转成一个时段
     *
     * @param timeSlotType 目标时段类型(1、日 2、月 3、年) 默认日
     * @param date         默认当前时间
     */
    public static DateTimeSlot getTimeSlot(@Nullable Integer timeSlotType, @Nullable LocalDateTime date) {
        if (timeSlotType == null) {
            timeSlotType = DAY;
        }
        if (date == null) {
            date = LocalDateTime.now();
        }
        DateTimeSlot dateTimeSlot = new DateTimeSlot();
        // 清除基础的时分秒
        date = LocalDateTimeUtil.setTheStartOfTheDay(date);
        // 年
        if (YEAR.equals(timeSlotType)) {
            // 清除月
            date = date.withDayOfMonth(1);
            date = date.withMonth(1);
            dateTimeSlot.setStartDateTime(date);
            dateTimeSlot.setEndDateTime(LocalDateTimeUtil
                    .fillMonth(LocalDateTimeUtil.fillDay(LocalDateTimeUtil.setTheEndOfTheDay(date))));
            dateTimeSlot.setDateTimeRangeText(String.valueOf(date.getYear()));
            // 月
        } else if (MONTH.equals(timeSlotType)) {
            // 清除日
            date = date.withDayOfMonth(1);
            dateTimeSlot.setStartDateTime(date);
            dateTimeSlot.setEndDateTime(LocalDateTimeUtil.fillDay(LocalDateTimeUtil.setTheEndOfTheDay(date)));
            dateTimeSlot.setDateTimeRangeText(date.getYear() + "-" + date.getMonthValue());
            // 日
        } else if (DAY.equals(timeSlotType)) {
            dateTimeSlot.setStartDateTime(date);
            dateTimeSlot.setEndDateTime(LocalDateTimeUtil.setTheEndOfTheDay(date));
            dateTimeSlot.setDateTimeRangeText(date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth());
        } else if (A_WEEK_AGO.equals(timeSlotType)) {
            // 6天前加今天为一周前
            dateTimeSlot.setEndDateTime(LocalDateTimeUtil.setTheEndOfTheDay(date));
            date = date.minusDays(6);
            dateTimeSlot.setStartDateTime(date);
            dateTimeSlot.setDateTimeRangeText(date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth());
        } else {
            throw new IllegalStateException("时段类型错误");
        }
        return dateTimeSlot;
    }

    /**
     * 转成一个时段
     *
     * @param dateStr yyyy yyyy-MM yyyy-MM-dd
     * @return DateTimeSlot
     */
    public static DateTimeSlot getTimeSlot(@Nullable String dateStr) {
        try {
            assert dateStr != null;
            if (!dateStr.contains(StrUtil.DASHED)) {
                LocalDateTime dateTime = LocalDateTime.parse(dateStr + "-01-01 00:00:00", DF);
                return getTimeSlot(YEAR, dateTime);
            } else if (dateStr.split(StrUtil.DASHED).length == 2) {
                LocalDateTime dateTime = LocalDateTime.parse(dateStr + "-01 00:00:00", DF);
                return getTimeSlot(MONTH, dateTime);
            } else if (dateStr.split(StrUtil.DASHED).length == 3) {
                LocalDateTime dateTime = LocalDateTime.parse(dateStr + " 00:00:00", DF);
                return getTimeSlot(DAY, dateTime);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("时间格式错误:" + dateStr);
        }
        return null;
    }
}
