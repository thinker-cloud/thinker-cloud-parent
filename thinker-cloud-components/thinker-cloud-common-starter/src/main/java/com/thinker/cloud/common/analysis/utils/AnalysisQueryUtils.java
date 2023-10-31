package com.thinker.cloud.common.analysis.utils;

import com.google.common.collect.Lists;
import com.thinker.cloud.common.analysis.constants.AnalysisCycle;
import com.thinker.cloud.common.analysis.interfaces.common.AnalysisSameTimeLastCondition;
import com.thinker.cloud.common.analysis.utils.AnalysisDateTime;
import com.thinker.cloud.common.analysis.utils.DateTimeCondition;
import com.thinker.cloud.common.analysis.utils.IDateTimeCondition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 分析统计查询帮助类
 *
 * @author admin
 */
public class AnalysisQueryUtils {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(6, 12
            , 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 计算同比/环比等数据
     *
     * @param analysisSameLastCondition 同比分析条件
     */
    public static void sameRingTimeHandle(AnalysisSameTimeLastCondition analysisSameLastCondition
            , Consumer<IDateTimeCondition> dateTimeConditionConsumer) {
        sameRingTimeHandle(analysisSameLastCondition, dateTimeConditionConsumer, dateTimeConditionConsumer);
    }

    public static void sameRingTimeHandle(AnalysisSameTimeLastCondition sameTimeLastCondition
            , Consumer<IDateTimeCondition> sameDateTimeConditionConsumer
            , Consumer<IDateTimeCondition> chainRatioDateTimeConditionConsumer) {
        AnalysisDateTime analysisDateTime = analysisDateTime(sameTimeLastCondition);
        if (analysisDateTime == null) {
            return;
        }

        // 同比
        List<Callable<Boolean>> tasks = Lists.newArrayList();
        if (Optional.ofNullable(sameTimeLastCondition.getQuerySamePeriodLastYear()).orElse(false)) {
            tasks.add(getSameTask(sameDateTimeConditionConsumer, analysisDateTime));
        }

        // 环比
        if (Optional.ofNullable(sameTimeLastCondition.getQueryChainLastPeriod()).orElse(false)) {
            tasks.add(getChainRatioTask(chainRatioDateTimeConditionConsumer, analysisDateTime));
        }

        // 并发执行
        if (!tasks.isEmpty()) {
            invokeAllTask(tasks);
        }
    }

    private static void invokeAllTask(List<Callable<Boolean>> tasks) {
        try {
            List<Future<Boolean>> results = THREAD_POOL_EXECUTOR.invokeAll(tasks);
            for (Future<Boolean> result : results) {
                result.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 环比
     *
     * @param dateTimeConditionConsumer dateTimeConditionConsumer
     * @param analysisDateTime          analysisDateTime
     * @return Callable<Boolean>
     */
    private static Callable<Boolean> getChainRatioTask(Consumer<IDateTimeCondition> dateTimeConditionConsumer, AnalysisDateTime analysisDateTime) {
        return () -> {
            if (analysisDateTime.getIsCorrectChainLastPeriod() && Objects.nonNull(dateTimeConditionConsumer)) {
                DateTimeCondition dateTimeCondition = new DateTimeCondition()
                        .setStartDateTime(analysisDateTime.getStartChainLastPeriodTime())
                        .setEndDateTime(analysisDateTime.getEndChainLastPeriodTime())
                        .setLastPeriodTimeName(analysisDateTime.getChainLastPeriodTimeName());
                dateTimeConditionConsumer.accept(dateTimeCondition);
            }
            return true;
        };
    }


    /**
     * 同比
     *
     * @param dateTimeConditionConsumer dateTimeConditionConsumer
     * @param analysisDateTime          analysisDateTime
     * @return Callable<Boolean>
     */
    private static Callable<Boolean> getSameTask(Consumer<IDateTimeCondition> dateTimeConditionConsumer, AnalysisDateTime analysisDateTime) {
        return () -> {
            if (Objects.nonNull(dateTimeConditionConsumer)) {
                IDateTimeCondition dateTimeCondition = new DateTimeCondition()
                        .setStartDateTime(analysisDateTime.getStartSamePeriodLastYearTime())
                        .setEndDateTime(analysisDateTime.getEndSamePeriodLastYearTime())
                        .setLastPeriodTimeName("去年同期");
                dateTimeConditionConsumer.accept(dateTimeCondition);
            }
            return true;
        };
    }

    /**
     * 分析时间间隔，根据分组获得该时间段的环比与同比
     *
     * @param analysisSameLastCondition 同比分析条件
     */
    public static AnalysisDateTime analysisDateTime(AnalysisSameTimeLastCondition analysisSameLastCondition) {
        LocalDateTime startDateTime = analysisSameLastCondition.getStartDateTime();
        LocalDateTime endDateTime = analysisSameLastCondition.getEndDateTime();
        if (startDateTime == null || endDateTime == null) {
            return null;
        }
        AnalysisDateTime analysisDateTime = new AnalysisDateTime().setIsCorrectChainLastPeriod(true);

        // 同比时间
        analysisDateTime.setStartSamePeriodLastYearTime(startDateTime.minusYears(1));
        analysisDateTime.setEndSamePeriodLastYearTime(endDateTime.minusYears(1));

        // 环比时间
        if (Optional.ofNullable(analysisSameLastCondition.getQueryChainLastPeriod()).orElse(false)) {
            handCycle(analysisSameLastCondition, analysisDateTime);
        }
        return analysisDateTime;
    }

    private static void handCycle(AnalysisSameTimeLastCondition analysisSameLastCondition, AnalysisDateTime analysisDateTime) {
        LocalDateTime startDateTime = analysisSameLastCondition.getStartDateTime();
        LocalDateTime endDateTime = analysisSameLastCondition.getEndDateTime();
        String cycle = Optional.ofNullable(analysisSameLastCondition.getQueryChainLastPeriodCycle())
                .orElse(analysisSameLastCondition.getGroupBy());
        AnalysisCycle analysisCycle = AnalysisCycle.resolve(cycle);
        if (analysisCycle == null) {
            analysisDateTime.setIsCorrectChainLastPeriod(false);
            return;
        }
        switch (analysisCycle) {
            case hour: {
                analysisDateTime.setStartChainLastPeriodTime(startDateTime.minusHours(1));
                analysisDateTime.setEndChainLastPeriodTime(endDateTime.minusHours(1));
                analysisDateTime.setChainLastPeriodTimeName("上小时同期");
                break;
            }
            case day: {
                analysisDateTime.setStartChainLastPeriodTime(startDateTime.minusDays(1));
                analysisDateTime.setEndChainLastPeriodTime(endDateTime.minusDays(1));
                analysisDateTime.setChainLastPeriodTimeName("昨日同期");
                break;
            }
            case week: {
                analysisDateTime.setStartChainLastPeriodTime(startDateTime.minusWeeks(1));
                analysisDateTime.setEndChainLastPeriodTime(endDateTime.minusWeeks(1));
                analysisDateTime.setChainLastPeriodTimeName("上周同期");
                break;
            }
            case month: {
                analysisDateTime.setStartChainLastPeriodTime(startDateTime.minusMonths(1));
                analysisDateTime.setEndChainLastPeriodTime(endDateTime.minusMonths(1));
                analysisDateTime.setChainLastPeriodTimeName("上月同期");
                break;
            }
            case quarterly: {
                analysisDateTime.setStartChainLastPeriodTime(startDateTime.minusMonths(3));
                analysisDateTime.setEndChainLastPeriodTime(endDateTime.minusMonths(3));
                analysisDateTime.setChainLastPeriodTimeName("上季度同期");
                break;
            }
            case year: {
                analysisDateTime.setStartChainLastPeriodTime(startDateTime.minusYears(1));
                analysisDateTime.setEndChainLastPeriodTime(endDateTime.minusYears(1));
                analysisDateTime.setChainLastPeriodTimeName("去年同期");
                break;
            }
            default:
                analysisDateTime.setIsCorrectChainLastPeriod(false);
        }
    }
}
