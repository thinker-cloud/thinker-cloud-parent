package com.thinker.cloud.task.cron;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * cron表达式解析工具
 *
 * @author admin
 **/
@Slf4j
public class CronExpParserUtil {

    /**
     * 解析corn表达式为中文
     *
     * @param cron corn表达式
     * @return String
     */
    public static String parser(String cron) {
        Assert.hasText(cron, "cron表达式为空!");
        Assert.isTrue(CronExpression.isValidExpression(cron), "cron表达式错误，请重新生成！");
        List<String> tmpCorns = Lists.newArrayList(cron.split(" "));
        StringBuilder sBuffer = new StringBuilder("每年");
        if (tmpCorns.size() == 6) {
            // 解析月
            sBuffer.append(strEquals(tmpCorns.get(4), "月"));
            // 解析周
            String five = tmpCorns.get(5);
            if (!"*".equals(five) && !"?".equals(five)) {
                char[] tmpArray = five.toCharArray();
                for (char c : tmpArray) {
                    if (NumberUtil.isNumber(String.valueOf(c))) {
                        int weekIntValue = Integer.parseInt(String.valueOf(c)) + 1;
                        if (weekIntValue > 7) {
                            weekIntValue = 1;
                        }
                        Week week = Week.of(weekIntValue);
                        sBuffer.append(week.toChinese("周"));
                    } else {
                        sBuffer.append(c);
                    }
                }
            }
            // 解析日
            String three = tmpCorns.get(3);
            sBuffer.append(!"?".equals(three) ? strEquals(three, "号") : "");
            // 解析时
            sBuffer.append(strEquals(tmpCorns.get(2), "时"));
            // 解析分
            sBuffer.append(strEquals(tmpCorns.get(1), "分"));
            // 解析秒
            sBuffer.append(strEquals(tmpCorns.get(0), "秒"));
        }
        return sBuffer.append("执行").toString();

    }

    private static String strEquals(String tmpCorn, String str) {
        String division = "*";
        return division.equals(tmpCorn) ? "每" + str : tmpCorn + str;
    }

    /**
     * 根据cron反解析为时间列表
     *
     * @param cron 表达式
     * @return CronEntity
     */
    public static List<String> genNextTime(String cron) {
        List<String> cronList = Lists.newArrayList();
        if (StrUtil.isEmpty(cron)) {
            log.error("cron表达式不能为空！");
            return cronList;
        }

        // Spring 表达式不支持年份 Quartz 支持
        if (CronExpression.isValidExpression(cron)) {
            // Quartz CronExpression 底层周是 0-6
            // Spring CronSequenceGenerator 周支持 0-6 or 1-7
            CronExpression generator = CronExpression.parse(cron);
            LocalDateTime nextDate = LocalDateTime.now();
            for (int i = 1; i < 4; i++) {
                nextDate = Optional.ofNullable(generator.next(nextDate)).orElse(nextDate);
                String date = DateUtil.format(nextDate, DatePattern.NORM_DATETIME_PATTERN);
                System.out.println(date);
                cronList.add(date);
                nextDate = nextDate.plusNanos(1000);
            }
        } else {
            log.error("Spring cron表达式错误，请检查！");
            return cronList;
        }
        return cronList;
    }
}
