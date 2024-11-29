package com.thinker.cloud.common.utils.date;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Maps;
import com.thinker.cloud.common.utils.range.DateTimeRange;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 时间工具类
 *
 * @author admin
 */
@Slf4j
@UtilityClass
public class LocalDateTimeUtil {

    /**
     * 默认时区
     */
    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();


    /**
     * LocalDateTime转换Date
     *
     * @param localDateTime localDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(SYSTEM_ZONE_ID).toInstant());
    }

    /**
     * LocalDateTime转换Instant
     *
     * @param date date
     * @return LocalDateTime
     */
    public static Instant toInstant(LocalDateTime date) {
        return date.atZone(SYSTEM_ZONE_ID).toInstant();
    }

    /**
     * Date转换LocalDateTime
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), SYSTEM_ZONE_ID);
    }

    /**
     * 获取一天的开始
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getTheStartOfTheDay() {
        return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 获取一天的结束
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getTheEndOfTheDay() {
        return LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0);
    }

    /**
     * 设置一天的开始
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime setTheStartOfTheDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 设置一天的结束
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime setTheEndOfTheDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
    }

    /**
     * 设置一天的开始
     *
     * @param date date
     * @return Date
     */
    public static Date setTheStartOfTheDay(Date date) {
        return toDate(setTheStartOfTheDay(toLocalDateTime(date)));
    }

    /**
     * 设置一天的结束
     *
     * @param date date
     * @return Date
     */
    public static Date setTheEndOfTheDay(Date date) {
        return toDate(setTheEndOfTheDay(toLocalDateTime(date)));
    }

    /**
     * 填满时间的月
     */
    public static Date fillMonth(Date date) {
        return toDate(toLocalDateTime(date).withMonth(12));
    }

    /**
     * 填满时间的日
     *
     * @param date date
     * @return Date
     */
    public static Date fillDay(Date date) {
        return toDate(toLocalDateTime(date).with(TemporalAdjusters.lastDayOfMonth()));
    }


    /**
     * 填满时间的月
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime fillMonth(LocalDateTime date) {
        return date.withMonth(12);
    }

    /**
     * 填满时间的日
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime fillDay(LocalDateTime date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }


    /**
     * 将LocalDateTime转为timestamp
     *
     * @param localDateTime localDateTime
     * @return long
     */
    public static long parseToTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * 设置时间的月的开始
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime setTheStartOfTheMonth(LocalDateTime localDateTime) {
        return setTheStartOfTheDay(localDateTime).withDayOfMonth(1);
    }

    /**
     * 设置时间的月的结束
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime setTheEndOfTheMonth(LocalDateTime localDateTime) {
        return setTheEndOfTheDay(localDateTime).withDayOfMonth(1).plusMonths(1).minusDays(1);
    }

    /**
     * 设置时间的年的开始
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime setTheStartOfTheYear(LocalDateTime localDateTime) {
        return setTheStartOfTheMonth(localDateTime.withMonth(1));
    }

    /**
     * 设置时间的年的结束
     *
     * @param localDateTime localDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime setTheEndOfTheYear(LocalDateTime localDateTime) {
        return setTheEndOfTheMonth(localDateTime.withMonth(12));
    }


    /**
     * 两个时间间隔的毫秒数
     *
     * @param subtractedTimestamp 被减时间
     * @param subtractTimestamp   减时间
     * @return 时间戳差值
     */
    public static long intervalTimestamp(LocalDateTime subtractedTimestamp, LocalDateTime subtractTimestamp) {
        return parseToTimestamp(subtractedTimestamp) - parseToTimestamp(subtractTimestamp);
    }

    /**
     * 两个时间间隔的分钟
     *
     * @param subtractedTimestamp 被减时间
     * @param subtractTimestamp   减时间
     * @return 分钟差值
     */
    public static long intervalMinute(LocalDateTime subtractedTimestamp, LocalDateTime subtractTimestamp) {
        return intervalTimestamp(subtractedTimestamp, subtractTimestamp) / 1000 / 60;
    }

    /**
     * 两个时间间隔的天数
     *
     * @param subtractedTimestamp 被减时间
     * @param subtractTimestamp   减时间
     * @return 间隔的天数
     */
    public static long intervalDay(LocalDateTime subtractedTimestamp, LocalDateTime subtractTimestamp) {
        return intervalMinute(subtractedTimestamp, subtractTimestamp) / 60 / 24;
    }

    /**
     * 最近7天开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime last7DaysStartTime() {
        return LocalDateTime.of(LocalDate.now().minusDays(6), LocalTime.MIN);
    }

    /**
     * 最近7天结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime last7DaysEndTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }

    /**
     * 最近30天开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime last30DaysStartTime() {
        return LocalDateTime.of(LocalDate.now().minusDays(29), LocalTime.MIN);
    }

    /**
     * 最近30天结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime last30DaysEndTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }

    /**
     * 最近一年开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime last1YearStartTime() {
        return LocalDateTime.of(LocalDate.now().minusYears(1).plusDays(1), LocalTime.MIN);
    }

    /**
     * 最近一年结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime last1YearEndTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }

    /**
     * 本周开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime weekStartTime() {
        LocalDate now = LocalDate.now();
        return LocalDateTime.of(now.minusDays(now.getDayOfWeek().getValue() - 1), LocalTime.MIN);
    }

    /**
     * 本周结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime weekEndTime() {
        LocalDate now = LocalDate.now();
        return LocalDateTime.of(now.plusDays(7 - now.getDayOfWeek().getValue()), LocalTime.MAX);
    }

    /**
     * 本月开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime monthStartTime() {
        return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
    }

    /**
     * 本月结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime monthEndTime() {
        return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
    }

    /**
     * 本季度开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime quarterStartTime() {
        LocalDate now = LocalDate.now();
        Month month = Month.of(now.getMonth().firstMonthOfQuarter().getValue());
        return LocalDateTime.of(LocalDate.of(now.getYear(), month, 1), LocalTime.MIN);
    }

    /**
     * 本季度结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime quarterEndTime() {
        LocalDate now = LocalDate.now();
        Month month = Month.of(now.getMonth().firstMonthOfQuarter().getValue()).plus(2);
        return LocalDateTime.of(LocalDate.of(now.getYear(), month, month.length(now.isLeapYear())), LocalTime.MAX);
    }

    /**
     * 本半年开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime halfYearStartTime() {
        LocalDate now = LocalDate.now();
        Month month = (now.getMonthValue() > 6) ? Month.JULY : Month.JANUARY;
        return LocalDateTime.of(LocalDate.of(now.getYear(), month, 1), LocalTime.MIN);
    }

    /**
     * 本半年结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime halfYearEndTime() {
        LocalDate now = LocalDate.now();
        Month month = (now.getMonthValue() > 6) ? Month.DECEMBER : Month.JUNE;
        return LocalDateTime.of(LocalDate.of(now.getYear(), month, month.length(now.isLeapYear())), LocalTime.MAX);
    }

    /**
     * 本年开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime yearStartTime() {
        return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN);
    }

    /**
     * 本年结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime yearEndTime() {
        return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()), LocalTime.MAX);
    }

    /**
     * 上周开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastWeekStartTime() {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        return LocalDateTime.of(lastWeek.minusDays(lastWeek.getDayOfWeek().getValue() - 1), LocalTime.MIN);
    }

    /**
     * 上周结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastWeekEndTime() {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        return LocalDateTime.of(lastWeek.plusDays(7 - lastWeek.getDayOfWeek().getValue()), LocalTime.MAX);
    }

    /**
     * 上月开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastMonthStartTime() {
        return LocalDateTime.of(LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
    }

    /**
     * 上月结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastMonthEndTime() {
        return LocalDateTime.of(LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
    }

    /**
     * 上季度开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastQuarterStartTime() {
        LocalDate now = LocalDate.now();
        Month firstMonthOfQuarter = Month.of(now.getMonth().firstMonthOfQuarter().getValue());
        Month firstMonthOfLastQuarter = firstMonthOfQuarter.minus(3);
        int yearOfLastQuarter = firstMonthOfQuarter.getValue() < 4 ? now.getYear() - 1 : now.getYear();
        return LocalDateTime.of(LocalDate.of(yearOfLastQuarter, firstMonthOfLastQuarter, 1), LocalTime.MIN);
    }

    /**
     * 上季度结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastQuarterEndTime() {
        LocalDate now = LocalDate.now();
        Month firstMonthOfQuarter = Month.of(now.getMonth().firstMonthOfQuarter().getValue());
        Month firstMonthOfLastQuarter = firstMonthOfQuarter.minus(1);
        int yearOfLastQuarter = firstMonthOfQuarter.getValue() < 4 ? now.getYear() - 1 : now.getYear();
        return LocalDateTime.of(LocalDate.of(yearOfLastQuarter, firstMonthOfLastQuarter, firstMonthOfLastQuarter.maxLength()), LocalTime.MAX);
    }

    /**
     * 上半年开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastHalfYearStartTime() {
        LocalDate now = LocalDate.now();
        int lastHalfYear = (now.getMonthValue() > 6) ? now.getYear() : now.getYear() - 1;
        Month firstMonthOfLastHalfYear = (now.getMonthValue() > 6) ? Month.JANUARY : Month.JULY;
        return LocalDateTime.of(LocalDate.of(lastHalfYear, firstMonthOfLastHalfYear, 1), LocalTime.MIN);
    }

    /**
     * 上半年结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastHalfYearEndTime() {
        LocalDate now = LocalDate.now();
        int lastHalfYear = (now.getMonthValue() > 6) ? now.getYear() : now.getYear() - 1;
        Month lastMonthOfLastHalfYear = (now.getMonthValue() > 6) ? Month.JUNE : Month.DECEMBER;
        return LocalDateTime.of(LocalDate.of(lastHalfYear, lastMonthOfLastHalfYear, lastMonthOfLastHalfYear.maxLength()), LocalTime.MAX);
    }

    /**
     * 上一年开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastYearStartTime() {
        return LocalDateTime.of(LocalDate.now().minusYears(1L).with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN);
    }

    /**
     * 上一年结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime lastYearEndTime() {
        return LocalDateTime.of(LocalDate.now().minusYears(1L).with(TemporalAdjusters.lastDayOfYear()), LocalTime.MAX);
    }

    /**
     * 下周开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextWeekStartTime() {
        LocalDate nextWeek = LocalDate.now().plusWeeks(1L);
        return LocalDateTime.of(nextWeek.minusDays(nextWeek.getDayOfWeek().getValue() - 1), LocalTime.MIN);
    }

    /**
     * 下周结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextWeekEndTime() {
        LocalDate nextWeek = LocalDate.now().plusWeeks(1L);
        return LocalDateTime.of(nextWeek.plusDays(7 - nextWeek.getDayOfWeek().getValue()), LocalTime.MAX);
    }

    /**
     * 下月开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextMonthStartTime() {
        return LocalDateTime.of(LocalDate.now().plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
    }

    /**
     * 下月结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextMonthEndTime() {
        return LocalDateTime.of(LocalDate.now().plusMonths(1L).with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
    }

    /**
     * 下季度开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextQuarterStartTime() {
        LocalDate now = LocalDate.now();
        Month firstMonthOfQuarter = Month.of(now.getMonth().firstMonthOfQuarter().getValue());
        Month firstMonthOfNextQuarter = firstMonthOfQuarter.plus(3);
        int yearOfNextQuarter = firstMonthOfQuarter.getValue() > 9 ? now.getYear() + 1 : now.getYear();
        return LocalDateTime.of(LocalDate.of(yearOfNextQuarter, firstMonthOfNextQuarter, 1), LocalTime.MIN);
    }

    /**
     * 下季度结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextQuarterEndTime() {
        LocalDate now = LocalDate.now();
        Month firstMonthOfQuarter = Month.of(now.getMonth().firstMonthOfQuarter().getValue());
        Month firstMonthOfNextQuarter = firstMonthOfQuarter.plus(5);
        int yearOfNextQuarter = firstMonthOfQuarter.getValue() > 9 ? now.getYear() + 1 : now.getYear();
        return LocalDateTime.of(LocalDate.of(yearOfNextQuarter, firstMonthOfNextQuarter, firstMonthOfNextQuarter.maxLength()), LocalTime.MAX);
    }

    /**
     * 上半年开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextHalfYearStartTime() {
        LocalDate now = LocalDate.now();
        int nextHalfYear = (now.getMonthValue() > 6) ? now.getYear() + 1 : now.getYear();
        Month firstMonthOfNextHalfYear = (now.getMonthValue() > 6) ? Month.JANUARY : Month.JULY;
        return LocalDateTime.of(LocalDate.of(nextHalfYear, firstMonthOfNextHalfYear, 1), LocalTime.MIN);
    }

    /**
     * 上半年结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextHalfYearEndTime() {
        LocalDate now = LocalDate.now();
        int lastHalfYear = (now.getMonthValue() > 6) ? now.getYear() + 1 : now.getYear();
        Month lastMonthOfNextHalfYear = (now.getMonthValue() > 6) ? Month.JUNE : Month.DECEMBER;
        return LocalDateTime.of(LocalDate.of(lastHalfYear, lastMonthOfNextHalfYear, lastMonthOfNextHalfYear.maxLength()), LocalTime.MAX);
    }

    /**
     * 下一年开始时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextYearStartTime() {
        return LocalDateTime.of(LocalDate.now().plusYears(1L).with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN);
    }

    /**
     * 下一年结束时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nextYearEndTime() {
        return LocalDateTime.of(LocalDate.now().plusYears(1L).with(TemporalAdjusters.lastDayOfYear()), LocalTime.MAX);
    }

    /**
     * 根据开始时间和结束时间生成时间段
     *
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     * @param duration      定义间隔
     * @return Map<LocalDateTime, LocalDateTime>
     */
    public static Map<LocalDateTime, LocalDateTime> genTimeSlot(LocalDateTime startDateTime, LocalDateTime endDateTime, Duration duration) {
        Map<LocalDateTime, LocalDateTime> timeSlotMap = Maps.newTreeMap();
        if (startDateTime.isAfter(endDateTime)) {
            return timeSlotMap;
        }

        // 格式化日期
        startDateTime = localDateTimeFormat(startDateTime);
        endDateTime = localDateTimeFormat(endDateTime);

        // 生成时间段
        while (endDateTime.isAfter(startDateTime)) {
            LocalDateTime localDateTime = startDateTime.plus(duration);

            // 最后跨度太大超过结束时间 则直接用结束时间
            if (localDateTime.isAfter(endDateTime)) {
                timeSlotMap.put(startDateTime, endDateTime);
                break;
            }
            timeSlotMap.put(startDateTime, localDateTime);
            startDateTime = localDateTime;
        }
        return timeSlotMap;
    }

    /**
     * 根据开始时间和结束时间生成时间段
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param duration  定义间隔
     * @return Map<Date, Date>
     */
    public static Map<Date, Date> genTimeSlotDate(Date startDate, Date endDate, Duration duration) {
        LocalDateTime startDateTime = DateUtil.toLocalDateTime(startDate);
        LocalDateTime endDateTime = DateUtil.toLocalDateTime(endDate);
        Map<Date, Date> timeSlotMap = Maps.newTreeMap();
        if (startDateTime.isAfter(endDateTime)) {
            return timeSlotMap;
        }

        // 格式化日期
        startDateTime = localDateTimeFormat(startDateTime);
        endDateTime = localDateTimeFormat(endDateTime);

        // 生成时间段
        while (endDateTime.isAfter(startDateTime)) {
            LocalDateTime localDateTime = startDateTime.plus(duration);

            // 最后跨度太大超过结束时间 则直接用结束时间
            if (localDateTime.isAfter(endDateTime)) {
                timeSlotMap.put(DateUtil.date(startDateTime), DateUtil.date(endDateTime));
                break;
            }
            timeSlotMap.put(DateUtil.date(startDateTime), DateUtil.date(localDateTime));
            startDateTime = localDateTime;
        }
        return timeSlotMap;
    }

    private static LocalDateTime localDateTimeFormat(LocalDateTime startDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        startDateTime = DateUtil.parseLocalDateTime(startDateTime.format(formatter));
        return startDateTime.withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 判断当前时间是否在时间范围内
     *
     * @param dateTimeRanges 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRangeNow(List<DateTimeRange> dateTimeRanges) {
        return isWithinTimeRange(LocalDateTime.now(), dateTimeRanges);
    }

    /**
     * 判断时间是否在时间范围内
     *
     * @param targetTime     目标时间
     * @param dateTimeRanges 时间范围
     * @return boolean
     */
    public static boolean isWithinTimeRange(LocalDateTime targetTime, List<DateTimeRange> dateTimeRanges) {
        if (CollectionUtils.isEmpty(dateTimeRanges)) {
            return true;
        }

        for (DateTimeRange dateTimeRange : dateTimeRanges) {
            LocalDateTime startDateTime = dateTimeRange.getStartDateTime();
            LocalDateTime endDateTime = dateTimeRange.getEndDateTime();
            if (startDateTime.isBefore(targetTime) && endDateTime.isAfter(targetTime)) {
                return true;
            }
        }

        return false;
    }
}
