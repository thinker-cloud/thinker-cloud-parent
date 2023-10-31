package com.thinker.cloud.task.cron;

public class CronBuilderTest {

    public static void main(String[] args) {
        String perSeconds = CronBuilder.builder().seconds(0, 5, false).build();
        System.out.println("每隔5秒触发 : " + perSeconds);
        CronExpParserUtil.genNextTime(perSeconds);
        System.out.println(CronExpParserUtil.parser(perSeconds));

        System.out.println("-------------------------------------------------------");

        String perMinutes = CronBuilder.builder().minutes(0, 15, false).build();
        System.out.println("每隔15分钟触发 : " + perMinutes);
        CronExpParserUtil.genNextTime(perMinutes);

        System.out.println("-------------------------------------------------------");

        String perHours = CronBuilder.builder().hours(13, 15, true).build();
        System.out.println("每天13-15点整点触发 : " + perHours);
        CronExpParserUtil.genNextTime(perHours);

        System.out.println("-------------------------------------------------------");

        String perDayOfMonth = CronBuilder.builder().dayOfMonthLast().hours(23)
                .minutes(59).seconds(59).build();
        System.out.println("每月最后一天23点59分59秒触发 : " + perDayOfMonth);
        CronExpParserUtil.genNextTime(perDayOfMonth);
        System.out.println(CronExpParserUtil.parser(perDayOfMonth));

        System.out.println("-------------------------------------------------------");

        String perMonth = CronBuilder.builder().months().dayOfWeekLast(5)
                .hours(10).minutes(15).build();
        System.out.println("每月的最后一个星期五上午10:15触发  : " + perMonth);

        CronExpParserUtil.genNextTime(perMonth);
        System.out.println(CronExpParserUtil.parser(perMonth));

        System.out.println("-------------------------------------------------------");

        String perYear = CronBuilder.builder().year().build();
        System.out.println("每年的1月1号0点0分触发 : " + perYear);
        CronExpParserUtil.genNextTime(perYear);
        System.out.println(CronExpParserUtil.parser(perYear));

        System.out.println("-------------------------------------------------------");

        String perDayOfWeek = CronBuilder.builder().hours(6, 17, true)
                .dayOfWeek(1, 5, true).build();
        System.out.println("每周一到周五，每天6点到17点整点触发：" + perDayOfWeek);
        System.out.println(perDayOfWeek);
        CronExpParserUtil.genNextTime(perDayOfWeek);
        System.out.println(CronExpParserUtil.parser(perDayOfWeek));

        System.out.println("-------------------------------------------------------");

        String a1 = CronBuilder.builder().hours(2)
                .dayOfMonth(0, 1, false).build();
        System.out.println("a1 : " + a1);
        CronExpParserUtil.genNextTime(a1);
        System.out.println(CronExpParserUtil.parser(a1));

        System.out.println("-------------------------------------------------------");

        String a2 = CronBuilder.builder().minutes(12).dayOfWeek(4).build();
        System.out.println("a2 : " + a2);
        CronExpParserUtil.genNextTime(a2);
        System.out.println(CronExpParserUtil.parser(a2));

        System.out.println("-------------------------------------------------------");

        String a3 = CronBuilder.builder().hours(12, 14, 23).seconds(4, 12, 64).months().build();
        System.out.println("a3 : " + a3);
        CronExpParserUtil.genNextTime(a3);
        System.out.println(CronExpParserUtil.parser(a3));

        System.out.println("-------------------------------------------------------");

        String a4 = CronBuilder.builder().dayOfMonth(5, 20, true)
                .months(4, 12, false).seconds(40, 43, true).build();
        System.out.println("a4 : " + a4);
        CronExpParserUtil.genNextTime(a4);
        System.out.println(CronExpParserUtil.parser(a4));

        System.out.println("-------------------------------------------------------");

        String a5 = CronBuilder.builder().dayOfMonth(20, 5, false)
                .months(4, 12, true).seconds(40, 43, false)
                .hours(34).minutes(30, 54, false).build();
        System.out.println("a5 : " + a5);
        CronExpParserUtil.genNextTime(a5);
        System.out.println(CronExpParserUtil.parser(a5));

        System.out.println("-------------------------------------------------------");

        String a6 = CronBuilder.builder().months(4, 12, true).hours()
                .hours(34).minutes(30, 54, false).build();
        System.out.println("a6 : " + a6);
        CronExpParserUtil.genNextTime(a6);
        System.out.println(CronExpParserUtil.parser(a6));

        System.out.println("-------------------------------------------------------");

        String a7 = CronBuilder.builder().months(3).dayOfMonth(30)
                .hours(23).minutes(59).seconds(33).build();
        System.out.println("a7 : " + a7);
        CronExpParserUtil.genNextTime(a7);
        System.out.println(CronExpParserUtil.parser(a7));

        System.out.println("-------------------------------------------------------");

        String a8 = CronBuilder.builder().months(5).dayOfMonth(30)
                .hours(23).minutes(59).seconds(33).build();
        System.out.println("a8 : " + a8);
        CronExpParserUtil.genNextTime(a8);
        System.out.println(CronExpParserUtil.parser(a8));

        System.out.println("-------------------------------------------------------");

        String a9 = CronBuilder.builder().seconds(-1).minutes(-1).hours(-1).dayOfMonth(-1)
                .months(-1).dayOfWeek(-1).year(-1).build();
        System.out.println("a9 : " + a9);
        CronExpParserUtil.genNextTime(a9);
        System.out.println(CronExpParserUtil.parser(a9));

        System.out.println("-------------------------------------------------------");

        String a10 = CronBuilder.builder().minutes(12).dayOfMonth().dayOfWeek(4).build();
        System.out.println("a10 : " + a10);
        CronExpParserUtil.genNextTime(a10);
        System.out.println(CronExpParserUtil.parser(a10));

        System.out.println("-------------------------------------------------------");

        String a11 = CronBuilder.builder().minutes(12).dayOfMonth(1, 2).dayOfWeek(1, 3).build();
        System.out.println("a11 : " + a11);
        CronExpParserUtil.genNextTime(a11);
        System.out.println(CronExpParserUtil.parser(a11));

        System.out.println("-------------------------------------------------------");

        String a12 = CronBuilder.builder().dayOfMonth(1, 2).build();
        System.out.println("a12 : " + a12);
        CronExpParserUtil.genNextTime(a12);
        System.out.println(CronExpParserUtil.parser(a12));

        System.out.println("-------------------------------------------------------");

        String a13 = CronBuilder.builder().dayOfMonth(2, 4, true).build();
        System.out.println("a13 : " + a13);
        CronExpParserUtil.genNextTime(a13);
        System.out.println(CronExpParserUtil.parser(a13));

        System.out.println("-------------------------------------------------------");
    }
}
