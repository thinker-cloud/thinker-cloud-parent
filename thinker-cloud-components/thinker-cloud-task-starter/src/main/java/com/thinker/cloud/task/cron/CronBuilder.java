package com.thinker.cloud.task.cron;

import com.thinker.cloud.common.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * cron 表达式构建器
 *
 * <p>
 * Cron表达式是一个字符串，字符串以5或6个空格隔开，分为6或7个域，每一个域代表一个含义，
 * Cron有如下两种语法格式：
 * 　　（1） Seconds Minutes Hours DayofMonth Month DayofWeek Year
 * 　　（2）Seconds Minutes Hours DayofMonth Month DayofWeek
 * 1. 结构：
 * corn从左到右（用空格隔开）：秒 分 小时 月份中的日期 月份 星期中的日期 年份
 * 2. 各字段含义：
 * 字段	            允许值，允许的特殊字符
 * 秒（Seconds）	0~59的整数, - * /    四个字符
 * 分（Minutes）	0~59的整数, - * /    四个字符
 * 小时（Hours）	0~23的整数, - * /    四个字符
 * 日期（DayofMonth）	1~31的整数（但是你需要考虑你月的天数）,- * ? / L W C     八个字符
 * 月份（Month）	1~12的整数或者 JAN-DEC , - * /    四个字符
 * 星期（DayofWeek）	1~7的整数或者 SUN-SAT （1=SUN）, - * ? / L C #     八个字符
 * 年(可选，留空)（Year）	1970~2099, - * /    四个字符
 * <p>
 * 3. 注意事项：
 * 每一个域都使用数字，但还可以出现如下特殊字符，它们的含义是：
 * 　　（1）*：表示匹配该域的任意值。假如在Minutes域使用*, 即表示每分钟都会触发事件。
 * 　　（2）?：只能用在DayofMonth和DayofWeek两个域。它也匹配域的任意值，但实际不会。因为DayofMonth和DayofWeek会相互影响。例如想在每月的20日触发调度，不管20日到底是星期几，则只能使用如下写法： 13 13 15 20 * ?, 其中最后一位只能用？，而不能使用*，如果使用*表示不管星期几都会触发，实际上并不是这样。
 * 　　（3）-：表示范围。例如在Minutes域使用5-20，表示从5分到20分钟每分钟触发一次
 * 　　（4）/：表示起始时间开始触发，然后每隔固定时间触发一次。例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次.
 * 　　（5）,：表示列出枚举值。例如：在Minutes域使用5,20，则意味着在5和20分每分钟触发一次。
 * 　　（6）L：表示最后，只能出现在DayofWeek和DayofMonth域。如果在DayofWeek域使用5L,意味着在最后的一个星期四触发。
 * 　　（7）W:表示有效工作日(周一到周五),只能出现在DayofMonth域，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。如果5日是星期天，则在6日(周一)触发；如果5日在星期一到星期五中的一天，则就在5日触发。另外一点，W的最近寻找不会跨过月份 。
 * 　　（8）LW:这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五。
 * 　　（9）#:用于确定每个月第几个星期几，只能出现在DayofWeek域。例如在4#2，表示某月的第二个星期三。
 * <p>
 * 注：
 * 　　（1）有些子表达式能包含一些范围或列表
 * 　　       例如：子表达式（天（星期））可以为 “MON-FRI”，“MON，WED，FRI”，“MON-WED,SAT”  “*”字符代表所有可能的值
 * 　　       因此，“*”在子表达式（月）里表示每个月的含义，“*”在子表达式（天（星期））表示星期的每一天
 * 　　 “/”字符用来指定数值的增量
 * 　　       例如：在子表达式（分钟）里的“0/15”表示从第0分钟开始，每15分钟在子表达式（分钟）里的“3/20”表示从第3分钟开始，每20分钟（它和“3，23，43”）的含义一样
 * 　  “？”字符仅被用于天（月）和天（星期）两个子表达式，表示不指定值
 * 　　       当2个子表达式其中之一被指定了值以后，为了避免冲突，需要将另一个子表达式的值设为“？”
 * 　　“L” 字符仅被用于天（月）和天（星期）两个子表达式，它是单词“last”的缩写
 * 　　       但是它在两个子表达式里的含义是不同的。
 * 　　       在天（月）子表达式中，“L”表示一个月的最后一天
 * 　　       在天（星期）自表达式中，“L”表示一个星期的最后一天，也就是SAT
 * 　　       如果在“L”前有具体的内容，它就具有其他的含义了
 * 　　       例如：“6L”表示这个月的倒数第６天，“FRIL”表示这个月的最一个星期五
 * 　　       注意：在使用“L”参数时，不要指定列表或范围，因为这会导致问题
 * </p>
 *
 * @author Rong.Jia
 * @link https://github.com/a852203465/CronUtils/blob/master/pom.xml
 */
@SuppressWarnings("ALL")
public class CronBuilder {

    private CronBuilder() {
    }

    private static final String ZERO = "0";
    private static final String SPACE = " ";
    private static final String EMPTY = "";
    private static final String SLASH = "/";
    private static final String ASTERISK = "*";
    private static final String QUESTION_MARK = "?";
    private static final String WELL_NO = "#";
    private static final String LAST = "L";
    private static final String DASHED = "-";
    private static final String COMMA = ",";
    private static final String LASTWEEK = "LW";
    private static final String WEEK = "W";

    private String seconds;
    private String minutes;
    private String hours;
    private String dayofMonth;
    private String month;
    private String dayofWeek;
    private String year;

    /**
     * 构建器
     *
     * @return {@link CronBuilder}  构建器对象
     */
    public static CronBuilder builder() {
        return new CronBuilder();
    }

    /**
     * 记录Cron 表达式
     */
    private StringJoiner joiner = new StringJoiner(SPACE);

    /**
     * 返回Cron 表达式
     *
     * @return Cron 表达式
     */
    public String build() {
        if (isBlank(this.seconds)) this.seconds = ASTERISK;
        if (isBlank(this.minutes)) this.minutes = ASTERISK;
        if (isBlank(this.hours)) this.hours = ASTERISK;
        if (isBlank(this.minutes)) this.minutes = ASTERISK;
        if (isBlank(this.dayofMonth)) this.dayofMonth = ASTERISK;
        if (isBlank(this.month)) this.month = ASTERISK;
        if (isBlank(this.dayofWeek)) this.dayofWeek = ASTERISK;
        if (isBlank(this.year)) this.year = ASTERISK;

        if (isNotBlank(this.dayofMonth) && isBlank(this.dayofWeek)) {
            this.dayofWeek = QUESTION_MARK;
        } else if (isNotBlank(this.dayofWeek) && isBlank(this.dayofMonth)) {
            this.dayofMonth = QUESTION_MARK;
        } else if (isNotBlank(this.dayofWeek) && isNotBlank(this.dayofMonth)) {
            if (equals(QUESTION_MARK, this.dayofMonth) && equals(QUESTION_MARK, this.dayofWeek)) {
                this.dayofWeek = QUESTION_MARK;
            }
            if (equals(ASTERISK, this.dayofMonth) && equals(ASTERISK, this.dayofWeek)) {
                this.dayofWeek = QUESTION_MARK;
            }

            if (notEquals(QUESTION_MARK, this.dayofMonth) && notEquals(QUESTION_MARK, this.dayofWeek)) {
                if (equals(ASTERISK, this.dayofMonth)
                        && notEquals(QUESTION_MARK, this.dayofWeek) && notEquals(ASTERISK, this.dayofWeek)) {
                    this.dayofMonth = QUESTION_MARK;
                } else if (equals(ASTERISK, this.dayofWeek)
                        && notEquals(QUESTION_MARK, this.dayofMonth) && notEquals(ASTERISK, this.dayofMonth)) {
                    this.dayofWeek = QUESTION_MARK;
                } else if (notEquals(QUESTION_MARK, this.dayofWeek)
                        && notEquals(ASTERISK, this.dayofWeek) && isNumeric(this.dayofMonth)) {
                    this.dayofMonth = QUESTION_MARK;
                } else if (notEquals(QUESTION_MARK, this.dayofMonth)
                        && notEquals(ASTERISK, this.dayofMonth) && isNumeric(this.dayofWeek)) {
                    this.dayofWeek = QUESTION_MARK;
                } else {
                    this.dayofWeek = QUESTION_MARK;
                }
            }

        } else if (isBlank(this.dayofWeek) && isBlank(this.dayofMonth)) {
            this.dayofWeek = QUESTION_MARK;
        }
        joiner.add(seconds).add(minutes).add(hours).add(dayofMonth).add(month).add(dayofWeek).add(year);
        String cron = joiner.toString();
        assert CronExpression.isValidExpression(cron);
        joiner = null;
        if (cron.contains(QUESTION_MARK)) {
            cron = StringUtils.removeEnd(cron, ASTERISK);
        }
        return StringUtils.removeEnd(cron, SPACE);
    }

    public CronBuilder time(LocalTime time) {
        return this.seconds(time.getSecond())
                .minutes(time.getMinute())
                .hours(time.getHour());
    }

    public CronBuilder date(LocalDate date) {
        return this.dayOfMonth(date.getDayOfMonth())
                .months(date.getMonthValue())
                .year(date.getYear());
    }

    public CronBuilder dateTime(LocalDateTime dateTime) {
        return this.date(dateTime.toLocalDate())
                .time(dateTime.toLocalTime());
    }

    /**
     * 每秒
     *
     * @return 返回当前对象
     */
    public CronBuilder seconds() {
        this.seconds = ASTERISK;
        return this;
    }

    /**
     * 指定秒
     *
     * @param seconds 秒，0~59的整数
     * @return 返回当前对象
     */
    public CronBuilder seconds(Integer... seconds) {
        this.setTime(0, seconds);
        StringJoiner stringJoiner = new StringJoiner(COMMA);
        for (Integer second : seconds) {
            if (second > 59) second = 59;
            if (second < 0) second = 0;
            stringJoiner.add(second + EMPTY);
        }

        this.seconds = stringJoiner.toString();
        return this;
    }

    /**
     * 指定秒 周期
     *
     * @param second  秒
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder seconds(int second, boolean isCycle) {
        return this.seconds(-1, second, isCycle);
    }

    /**
     * 指定秒 周期
     *
     * @param seconds 秒数组
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder seconds(Integer[] seconds, boolean isCycle) {
        if (isCycle) {
            return this.seconds(seconds);
        } else if (seconds.length == 1) {
            return this.seconds(seconds[0], false);
        } else {
            throw ValidationException.of("表达式数据错误");
        }
    }

    /**
     * 指定秒 周期
     *
     * @param start         开始，0~59的整数
     * @param endOrInterval 结束/间隔，0~59的整数
     * @param isCycle       true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder seconds(int start, int endOrInterval, boolean isCycle) {
        String symbol;
        if (isCycle) {
            if (start > 58) start = 58;
            if (start < 1) start = 1;
            if (endOrInterval > 59) endOrInterval = 59;
            if (endOrInterval < 2) endOrInterval = 2;
            symbol = DASHED;
        } else {
            if (start > 59) start = 59;
            if (start < 0) start = -1;
            if (endOrInterval > 59) endOrInterval = 59;
            if (endOrInterval < 1) endOrInterval = 1;
            symbol = SLASH;
        }

        this.seconds = gen(start, endOrInterval, symbol);
        return this;
    }

    /**
     * 每分
     *
     * @return 返回当前对象
     */
    public CronBuilder minutes() {
        if (isBlank(this.seconds)) seconds(0);
        this.minutes = ASTERISK;
        return this;
    }

    /**
     * 指定分
     *
     * @param minutesArr 分，0~59的整数
     * @return 返回当前对象
     */
    public CronBuilder minutes(Integer... minutesArr) {
        this.setTime(0, minutesArr);
        StringJoiner stringJoiner = new StringJoiner(COMMA);
        for (Integer minutes : minutesArr) {
            if (minutes > 59) minutes = 59;
            if (minutes < 0) minutes = 0;
            stringJoiner.add(minutes + EMPTY);
        }

        if (isBlank(this.seconds)) seconds(0);
        this.minutes = stringJoiner.toString();
        return this;
    }

    /**
     * 指定分 周期
     *
     * @param minute  小时
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder minutes(int minute, boolean isCycle) {
        return this.minutes(-1, minute, isCycle);
    }

    /**
     * 指定分 周期
     *
     * @param minutes 分数组
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder minutes(Integer[] minutes, boolean isCycle) {
        if (isCycle) {
            return this.minutes(minutes);
        } else if (minutes.length == 1) {
            return this.minutes(minutes[0], false);
        } else {
            throw ValidationException.of("表达式数据错误");
        }
    }

    /**
     * 指定分 周期
     *
     * @param start         开始，0~59的整数
     * @param endOrInterval 结束/间隔，0~59的整数
     * @param isCycle       true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder minutes(int start, int endOrInterval, boolean isCycle) {
        String symbol;
        if (isCycle) {
            if (start > 58) start = 58;
            if (start < 1) start = 1;
            if (endOrInterval > 59) endOrInterval = 59;
            if (endOrInterval < 2) endOrInterval = 2;
            symbol = DASHED;
        } else {
            if (start > 59) start = 59;
            if (start < 0) start = -1;
            if (endOrInterval > 59) endOrInterval = 59;
            if (endOrInterval < 1) endOrInterval = 1;
            symbol = SLASH;
        }
        if (isBlank(this.seconds)) seconds(0);
        this.minutes = gen(start, endOrInterval, symbol);
        return this;
    }

    /**
     * 每小时
     *
     * @return 返回当前对象
     */
    public CronBuilder hours() {
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        this.hours = ASTERISK;
        return this;
    }

    /**
     * 指定时
     *
     * @param hoursArr 分，0~23的整数
     * @return 返回当前对象
     */
    public CronBuilder hours(Integer... hoursArr) {
        this.setTime(0, hoursArr);
        StringJoiner stringJoiner = new StringJoiner(COMMA);
        for (Integer hours : hoursArr) {
            if (hours > 23) hours = 23;
            if (hours < 0) hours = 0;
            stringJoiner.add(hours + EMPTY);
        }

        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        this.hours = stringJoiner.toString();
        return this;
    }

    /**
     * 指定小时 周期
     *
     * @param hour    小时
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder hours(int hour, boolean isCycle) {
        return this.hours(-1, hour, isCycle);
    }

    /**
     * 指定小时 周期
     *
     * @param hours   小时数组
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder hours(Integer[] hours, boolean isCycle) {
        if (isCycle) {
            return this.hours(hours);
        } else if (hours.length == 1) {
            return this.hours(hours[0], false);
        } else {
            throw ValidationException.of("表达式数据错误");
        }
    }

    /**
     * 指定小时 周期
     *
     * @param start         开始，0~23的整数
     * @param endOrInterval 结束/间隔，0~23的整数
     * @param isCycle       true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder hours(int start, int endOrInterval, boolean isCycle) {
        String symbol;
        if (isCycle) {
            if (start > 23) start = 23;
            if (start < 0) start = 0;
            if (endOrInterval > 23) endOrInterval = 23;
            if (endOrInterval < 2) endOrInterval = 2;
            symbol = DASHED;
        } else {
            if (start > 23) start = 23;
            if (start < 0) start = -1;
            if (endOrInterval > 23) endOrInterval = 23;
            if (endOrInterval < 1) endOrInterval = 1;
            symbol = SLASH;
        }

        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        this.hours = gen(start, endOrInterval, symbol);
        return this;
    }

    /**
     * 每日
     *
     * @return 返回当前对象
     */
    public CronBuilder dayOfMonth() {
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);

        this.dayofMonth = ASTERISK;
        return this;
    }

    /**
     * 指定日
     *
     * @param days 日，0~31的整数
     * @return 返回当前对象
     */
    public CronBuilder dayOfMonth(Integer... days) {
        this.setTime(1, days);
        StringJoiner stringJoiner = new StringJoiner(COMMA);
        for (Integer day : days) {
            if (day > 31) day = 31;
            if (day < 1) day = 1;
            stringJoiner.add(day + EMPTY);
        }

        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        this.dayofMonth = stringJoiner.toString();
        return this;
    }

    /**
     * 指定天 周期
     *
     * @param day     天
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder dayOfMonth(int day, boolean isCycle) {
        return this.dayOfMonth(-1, day, isCycle);
    }

    /**
     * 指定天 周期
     *
     * @param days    天数组
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder dayOfMonth(Integer[] days, boolean isCycle) {
        if (isCycle) {
            return this.dayOfMonth(days);
        } else if (days.length == 1) {
            return this.dayOfMonth(days[0], false);
        } else {
            throw ValidationException.of("表达式数据错误");
        }
    }

    /**
     * 指定天 周期
     *
     * @param start         开始，0~31的整数
     * @param endOrInterval 结束/间隔，0~31的整数
     * @param isCycle       true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder dayOfMonth(int start, int endOrInterval, boolean isCycle) {
        String symbol;
        if (isCycle) {
            if (start > 31) start = 31;
            if (start < 1) start = 1;
            if (endOrInterval > 31) endOrInterval = 31;
            if (endOrInterval < 2) endOrInterval = 2;
            symbol = DASHED;
        } else {
            if (start > 31) start = 31;
            if (start < 1) start = -1;
            if (endOrInterval > 31) endOrInterval = 31;
            if (endOrInterval < 1) endOrInterval = 1;
            symbol = SLASH;
        }
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        this.dayofMonth = gen(start, endOrInterval, symbol);
        return this;
    }

    /**
     * 月最后一日
     *
     * @return {@link CronBuilder} 返回当前对象
     */
    public CronBuilder dayOfMonthLast() {
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        this.dayofMonth = LAST;
        return this;
    }

    /**
     * 月最后一个工作日
     *
     * @return {@link CronBuilder} 返回当前对象
     */
    public CronBuilder dayOfMonthLastWeek() {
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        this.dayofMonth = LASTWEEK;
        return this;
    }

    /**
     * 每月指定时间最近的那个工作日
     *
     * @return {@link CronBuilder} 返回当前对象
     */
    public CronBuilder dayOfMonthWeek(int day) {
        if (day > 31) day = 31;
        if (day < 1) day = 1;

        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        this.dayofMonth = day + WEEK;
        return this;
    }

    /**
     * 每月
     *
     * @return 返回当前对象
     */
    public CronBuilder months() {
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        if (isBlank(this.dayofMonth)) dayOfMonth(1);
        this.month = ASTERISK;
        return this;
    }

    /**
     * 指定月
     *
     * @param months 月，1~12的整数
     * @return 返回当前对象
     */
    public CronBuilder months(Integer... months) {
        this.setTime(1, months);
        StringJoiner stringJoiner = new StringJoiner(COMMA);
        for (Integer month : months) {
            if (month > 12) month = 12;
            if (month < 1) month = 1;
            stringJoiner.add(month + EMPTY);
        }

        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        if (isBlank(this.dayofMonth)) dayOfMonth(1);
        this.month = stringJoiner.toString();
        return this;
    }

    /**
     * 指定月 周期
     *
     * @param month   月
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder months(int month, boolean isCycle) {
        return this.months(-1, month, isCycle);
    }

    /**
     * 指定月 周期
     *
     * @param months  月数组
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder months(Integer[] months, boolean isCycle) {
        if (isCycle) {
            return this.months(months);
        } else if (months.length == 1) {
            return this.months(months[0], false);
        } else {
            throw ValidationException.of("表达式数据错误");
        }
    }

    /**
     * 指定月周期
     *
     * @param start         开始，0~12的整数
     * @param endOrInterval 结束/间隔，0~12的整数
     * @param isCycle       true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder months(int start, int endOrInterval, boolean isCycle) {
        String symbol;
        if (isCycle) {
            if (start > 12) start = 12;
            if (start < 1) start = 1;
            if (endOrInterval > 12) endOrInterval = 12;
            if (endOrInterval < 2) endOrInterval = 2;
            symbol = DASHED;
        } else {
            if (start > 12) start = 12;
            if (start < 1) start = -1;
            if (endOrInterval > 12) endOrInterval = 12;
            if (endOrInterval < 1) endOrInterval = 1;
            symbol = SLASH;
        }
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        if (isBlank(this.dayofMonth)) dayOfMonth(1);
        this.month = gen(start, endOrInterval, symbol);
        return this;
    }

    /**
     * 每周
     *
     * @return 返回当前对象
     */
    public CronBuilder dayOfWeek() {
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        if (isBlank(this.month)) months(1);
        this.dayofWeek = ASTERISK;
        return this;
    }

    /**
     * 指定周
     *
     * @param dayOfWeeks 周，1~7的整数
     * @return 返回当前对象
     */
    public CronBuilder dayOfWeek(Integer... dayOfWeeks) {
        this.setTime(1, dayOfWeeks);
        StringJoiner stringJoiner = new StringJoiner(COMMA);
        for (Integer dayOfWeek : dayOfWeeks) {
            if (dayOfWeek > 7) dayOfWeek = 7;
            if (dayOfWeek < 1) dayOfWeek = 1;
            stringJoiner.add(dayOfWeek + EMPTY);
        }

        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        this.dayofWeek = stringJoiner.toString();
        return this;
    }

    /**
     * 指定周 周期
     *
     * @param week    星期
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder dayOfWeek(int week, boolean isCycle) {
        return this.dayOfWeek(-1, week, isCycle);
    }

    /**
     * 指定周 周期
     *
     * @param weeks   周数组
     * @param isCycle true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder dayOfWeek(Integer[] weeks, boolean isCycle) {
        if (isCycle) {
            return this.dayOfWeek(weeks);
        } else if (weeks.length == 1) {
            return this.dayOfWeek(weeks[0], false);
        } else {
            throw ValidationException.of("表达式数据错误");
        }
    }

    /**
     * 指定周 周期
     *
     * @param start         开始，1-7, 1-4
     * @param endOrInterval 结束/间隔，1-5
     * @param isCycle       true: 是周期，false: 是间隔
     * @return 返回当前对象
     */
    public CronBuilder dayOfWeek(int start, int endOrInterval, boolean isCycle) {
        String symbol;
        if (isCycle) {
            if (start > 7) start = 7;
            if (start < 1) start = 1;
            if (endOrInterval > 7) endOrInterval = 7;
            if (endOrInterval < 2) endOrInterval = 2;
            symbol = DASHED;
        } else {
            if (start > 4) start = 4;
            if (start < 1) start = -1;
            if (endOrInterval > 5) endOrInterval = 5;
            if (endOrInterval < 1) endOrInterval = 1;
            symbol = WELL_NO;
        }
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        this.dayofWeek = gen(start, endOrInterval, symbol);
        return this;
    }

    private static String gen(int start, int end, String symbol) {
        return (start < 0 ? ASTERISK : start) + symbol + end;
    }

    /**
     * 本月的最后一个星期几
     *
     * @return {@link CronBuilder}  返回当前对象
     */
    public CronBuilder dayOfWeekLast(int week) {
        if (week > 7) week = 7;
        if (week < 1) week = 1;

        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);

        this.dayofWeek = week + LAST;
        return this;
    }

    /**
     * 每年
     *
     * @return 返回当前对象
     */
    public CronBuilder year() {
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        if (isBlank(this.month)) months(1);
        if (isBlank(this.dayofMonth)) dayOfMonth(1);

        this.year = ASTERISK;
        return this;
    }

    /**
     * 指定年
     *
     * @param year 年
     * @return 返回当前对象
     */
    public CronBuilder year(int year) {
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        if (isBlank(this.month)) months(1);
        if (isBlank(this.dayofMonth)) dayOfMonth(1);

        this.year = year < getCurrentYear() ? ASTERISK : year + EMPTY;
        return this;
    }

    /**
     * 指定年 周期
     *
     * @param start 开始  1970~2099
     * @param end   结束  1970~2099
     * @return 返回当前对象
     */
    public CronBuilder year(int start, int end) {
        if (start > end) start = end;
        if (start < getCurrentYear()) start = getCurrentYear();
        if (end < getCurrentYear()) end = getCurrentYear();
        if (isBlank(this.seconds)) seconds(0);
        if (isBlank(this.minutes)) minutes(0);
        if (isBlank(this.hours)) hours(0);
        if (isBlank(this.month)) months(1);
        if (isBlank(this.dayofMonth)) dayOfMonth(1);

        this.year = start + DASHED + end;
        return this;
    }

    /**
     * 字符串不是空
     *
     * @param str 字符串
     * @return boolean true/false
     */
    private static boolean isNotBlank(String str) {
        return null != str && !SPACE.equals(str) && !EMPTY.equals(str);
    }

    /**
     * 字符串是空
     *
     * @param str 字符串
     * @return boolean true/false
     */
    private static boolean isBlank(String str) {
        return null == str || SPACE.equals(str) || EMPTY.equals(str);
    }

    /**
     * 是否相等
     *
     * @param obj1 对象
     * @param obj2 对象
     * @return boolean
     */
    private static boolean equals(Object obj1, Object obj2) {
        return obj1.equals(obj2);
    }

    /**
     * 是否不相等
     *
     * @param obj1 对象
     * @param obj2 对象
     * @return boolean
     */
    private static boolean notEquals(Object obj1, Object obj2) {
        return !obj1.equals(obj2);
    }

    /**
     * 设置时间
     *
     * @param index0Value 索引0位置的值
     * @param time        时间
     */
    private static void setTime(Integer index0Value, Integer... time) {
        if (time == null || time.length <= 0) {
            time = new Integer[1];
            time[0] = index0Value;
        }
    }

    /**
     * 获取当前年
     *
     * @return 当前年份
     */
    private static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * 是数字
     *
     * @param str str
     * @return boolean
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return !isNum.matches() ? Boolean.FALSE : Boolean.TRUE;
    }
}
