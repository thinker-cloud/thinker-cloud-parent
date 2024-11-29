package com.thinker.cloud.common.utils.date;

import com.thinker.cloud.common.utils.range.DateRange;
import com.thinker.cloud.common.utils.range.Range;
import com.thinker.cloud.common.utils.range.RangeTools;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * LocalDate 工具类
 *
 * @author admin
 **/
@UtilityClass
public class LocalDateUtil {

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param dateRanges 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeNow(List<DateRange> dateRanges) {
        return isWithinTimeRange(LocalDate.now(), dateRanges);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param dayRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeDayNow(Range<Integer> dayRange) {
        return RangeTools.isWithinRange(LocalDate.now().getDayOfMonth(), dayRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param daysRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeDayNow(List<Range<Integer>> daysRange) {
        return RangeTools.isWithinRange(LocalDate.now().getDayOfMonth(), daysRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param monthRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeMonthNow(Range<Integer> monthRange) {
        return RangeTools.isWithinRange(LocalDate.now().getMonthValue(), monthRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param monthsRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeMonthNow(List<Range<Integer>> monthsRange) {
        return RangeTools.isWithinRange(LocalDate.now().getMonthValue(), monthsRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param yearRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeYearNow(Range<Integer> yearRange) {
        return RangeTools.isWithinRange(LocalDate.now().getYear(), yearRange);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param yearsRange 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeYearNow(List<Range<Integer>> yearsRange) {
        return RangeTools.isWithinRange(LocalDate.now().getYear(), yearsRange);
    }

    /**
     * 判断时间是否在时间范围内
     *
     * @param targetTime 目标时间
     * @param dateRanges 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRange(LocalDate targetTime, List<DateRange> dateRanges) {
        if (CollectionUtils.isEmpty(dateRanges)) {
            return true;
        }

        for (DateRange dateRange : dateRanges) {
            LocalDate startDate = dateRange.getStartDate();
            LocalDate endDate = dateRange.getEndDate();
            if (startDate.isBefore(targetTime) && endDate.isAfter(targetTime)) {
                return true;
            }
        }

        return false;
    }
}
