package com.thinker.cloud.core.utils.date;

import com.thinker.cloud.tools.range.Range;
import com.thinker.cloud.tools.range.RangeTools;
import com.thinker.cloud.tools.range.TimeRange;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.List;

/**
 * LocalTime 工具类
 *
 * @author admin
 **/
@UtilityClass
public class LocalTimeUtil {

    /**
     * 判断当前时间是否在区间内
     *
     * @param timeRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeNow(TimeRange timeRange) {
        return isWithinTimeRangeNow(timeRange.getStartTime(), timeRange.getEndTime());
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param timeRanges 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeNow(List<TimeRange> timeRanges) {
        return isWithinTimeRange(LocalTime.now(), timeRanges);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param secondRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeSecondNow(Range<Integer> secondRange) {
        return RangeTools.isWithinRange(LocalTime.now().getSecond(), secondRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param secondsRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeSecondNow(List<Range<Integer>> secondsRange) {
        return RangeTools.isWithinRange(LocalTime.now().getSecond(), secondsRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param minuteRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeMinuteNow(Range<Integer> minuteRange) {
        return RangeTools.isWithinRange(LocalTime.now().getMinute(), minuteRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param minutesRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeMinuteNow(List<Range<Integer>> minutesRange) {
        return RangeTools.isWithinRange(LocalTime.now().getMinute(), minutesRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param hourRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeHourNow(Range<Integer> hourRange) {
        return RangeTools.isWithinRange(LocalTime.now().getHour(), hourRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param hoursRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeHourNow(List<Range<Integer>> hoursRange) {
        return RangeTools.isWithinRange(LocalTime.now().getHour(), hoursRange);
    }

    /**
     * 判断当前时间是否在区间内
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return boolean
     */
    public static boolean isWithinTimeRangeNow(LocalTime startTime, LocalTime endTime) {
        return isWithinTimeRange(startTime, endTime, LocalTime.now());
    }

    /**
     * 判断时间是否在区间内
     *
     * @param targetTime 目标时间
     * @param timeRange  时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRange(LocalTime targetTime, TimeRange timeRange) {
        return isWithinTimeRange(timeRange.getStartTime(), timeRange.getEndTime(), targetTime);
    }

    /**
     * 判断时间是否在区间内
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param targetTime 目标时间
     * @return boolean
     */
    public static boolean isWithinTimeRange(LocalTime startTime, LocalTime endTime, LocalTime targetTime) {
        return startTime.isBefore(targetTime) && endTime.isAfter(targetTime);
    }

    /**
     * 判断时间是否在时间范围内
     *
     * @param targetTime 目标时间
     * @param timeRanges 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRange(LocalTime targetTime, List<TimeRange> timeRanges) {
        if (CollectionUtils.isEmpty(timeRanges)) {
            return true;
        }

        for (TimeRange timeRange : timeRanges) {
            LocalTime startTime = timeRange.getStartTime();
            LocalTime endTime = timeRange.getEndTime();
            if (startTime.isBefore(targetTime) && endTime.isAfter(targetTime)) {
                return true;
            }
        }

        return false;
    }
}
