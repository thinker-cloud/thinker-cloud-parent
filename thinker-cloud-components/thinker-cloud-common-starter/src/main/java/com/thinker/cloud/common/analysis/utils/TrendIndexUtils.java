package com.thinker.cloud.common.analysis.utils;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.thinker.cloud.common.analysis.interfaces.common.AnalysisBaseCondition;
import com.thinker.cloud.common.analysis.model.trend.TrendIndexVO;
import com.thinker.cloud.common.analysis.constants.AnalysisCycle;
import com.thinker.cloud.common.analysis.interfaces.common.AnalysisSqlCondition;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 趋势图表坐标值帮助类
 *
 * @author admin
 */
public class TrendIndexUtils {

    /**
     * 设置趋势图通用sql条件
     */
    public static <SqlQuery extends AnalysisSqlCondition> SqlQuery
    setCommonCondition(SqlQuery analysisSqlCondition, AnalysisBaseCondition condition) {
        analysisSqlCondition
                .setStartDateTime(condition.getStartDateTime())
                .setEndDateTime(condition.getEndDateTime())
                .setGroupBy(condition.getGroupBy())
                .setGroupInterval(condition.getGroupInterval());
        return analysisSqlCondition;
    }

    /**
     * 获取一个时间段的趋势模板
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @param groupBy   groupBy
     * @return List<TrendIndexVO>
     */
    public static List<TrendIndexVO> newTimePeriodTemplate(LocalDateTime startDate, LocalDateTime endDate
            , String groupBy, Integer groupByUnit, CompletionParam completionParam) {
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        if (startDate.isAfter(endDate)) {
            return Lists.newArrayList();
        }
        DateTimeFormatter dateFormat = getDateFormat(groupBy);
        List<TrendIndexVO> trendIndexList = Lists.newArrayList();
        startDate = formatDate(groupBy, startDate);
        endDate = formatDate(groupBy, endDate);

        // 结束条件
        do {
            TrendIndexVO apiTrendIndexBO = new TrendIndexVO(dateFormat.format(startDate), completionParam.getInitValueY());
            apiTrendIndexBO.setXValueDateTime(DateUtil.date(startDate));
            trendIndexList.add(apiTrendIndexBO);
            if (AnalysisCycle.year.equals(groupBy)) {
                startDate = startDate.plusYears(groupByUnit);
            } else if (AnalysisCycle.month.equals(groupBy)) {
                startDate = startDate.plusMonths(groupByUnit);
            } else if (AnalysisCycle.day.equals(groupBy)) {
                startDate = startDate.plusDays(groupByUnit);
            } else if (AnalysisCycle.hour.equals(groupBy)) {
                startDate = startDate.plusHours(groupByUnit);
            } else if (AnalysisCycle.minute.equals(groupBy)) {
                startDate = startDate.plusMinutes(groupByUnit);
            }
        } while (!endDate.isBefore(startDate));
        if (completionParam.isReverseTime()) {
            trendIndexList.sort(Comparator.comparing(TrendIndexVO::getXValueDateTime).reversed());
        }
        return trendIndexList;
    }

    public static List<TrendIndexVO> newTimePeriodTemplate(LocalDateTime startDate, LocalDateTime endDate,
                                                           String groupBy, Integer groupByUnit) {
        return newTimePeriodTemplate(startDate, endDate, groupBy, groupByUnit, new CompletionParam());
    }

    /**
     * 并行TrendIndex补全
     *
     * @param list        list
     * @param startTime   startTime
     * @param endTime     endTime
     * @param groupBy     groupBy
     * @param groupByUnit groupByUnit
     */
    public static void parallelCoordinateCompletion(List<TrendIndexVO> list, LocalDateTime startTime, LocalDateTime endTime
            , String groupBy, Integer groupByUnit, CompletionParam completionParam) {
        DateTimeFormatter dateFormat = getDateFormat(groupBy);
        list.parallelStream().forEach(index -> index.setXValueText(DateUtil.format(index.getXValueDateTime(), dateFormat)));

        List<TrendIndexVO> templateIndexList = TrendIndexUtils.newTimePeriodTemplate(startTime, endTime, groupBy, groupByUnit, completionParam);
        if (list.size() < templateIndexList.size()) {
            templateIndexList.stream()
                    .filter(templateIndex -> list.parallelStream()
                            .noneMatch(trendIndex -> templateIndex.getXValueText().equals(trendIndex.getXValueText())))
                    .forEach(list::add);
        }

        // 排序
        list.sort(Comparator.comparing(TrendIndexVO::getXValueDateTime));

        // 精度处理
        Optional.ofNullable(completionParam).map(CompletionParam::getNumberOfDecimals).ifPresent(numberOfDecimals ->
                list.parallelStream().forEach(trendIndex ->
                        Optional.ofNullable(trendIndex.getYValue())
                                .ifPresent(yValue -> trendIndex.setYValue(yValue.setScale(numberOfDecimals, RoundingMode.HALF_UP)))
                )
        );
    }

    public static void parallelCoordinateCompletion(List<TrendIndexVO> list, LocalDateTime startTime, LocalDateTime endTime
            , String groupBy, CompletionParam completionParam) {
        parallelCoordinateCompletion(list, startTime, endTime, groupBy, 1, completionParam);
    }

    public static void parallelCoordinateCompletion(List<TrendIndexVO> trendIndexList, AnalysisBaseCondition analysisVo) {
        parallelCoordinateCompletion(trendIndexList, analysisVo.getStartDateTime(), analysisVo.getEndDateTime()
                , analysisVo.getGroupBy(), analysisVo.getGroupInterval(), new CompletionParam().setInitValueY(BigDecimal.ZERO));
    }

    public static void parallelCoordinateCompletion(List<TrendIndexVO> trendIndexList, AnalysisBaseCondition analysisVo
            , CompletionParam completionParam) {
        parallelCoordinateCompletion(trendIndexList, analysisVo.getStartDateTime(), analysisVo.getEndDateTime()
                , analysisVo.getGroupBy(), analysisVo.getGroupInterval(), completionParam);
    }

    /**
     * 根据分组获取 时间格式
     *
     * @param groupBy groupBy
     * @return DateTimeFormatter
     */
    public static DateTimeFormatter getDateFormat(String groupBy) {
        if (AnalysisCycle.year.equals(groupBy)) {
            return DateTimeFormatter.ofPattern("yyyy");
        } else if (AnalysisCycle.month.equals(groupBy)) {
            return DateTimeFormatter.ofPattern("yyyy-MM");
        } else if (AnalysisCycle.day.equals(groupBy)) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else if (AnalysisCycle.hour.equals(groupBy)) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        } else if (AnalysisCycle.minute.equals(groupBy)) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        } else {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }
    }

    public static LocalDateTime formatDate(String groupBy, LocalDateTime dataTime) {
        if (AnalysisCycle.year.equals(groupBy)) {
            return dataTime.withMonth(1).withDayOfMonth(1).with(LocalTime.MIN);
        } else if (AnalysisCycle.month.equals(groupBy)) {
            return dataTime.withDayOfMonth(1).with(LocalTime.MIN);
        } else if (AnalysisCycle.day.equals(groupBy)) {
            return dataTime.with(LocalTime.MIN);
        } else if (AnalysisCycle.hour.equals(groupBy)) {
            return dataTime.withMinute(0).withSecond(0).withNano(0);
        } else if (AnalysisCycle.minute.equals(groupBy)) {
            return dataTime.withSecond(0).withNano(0);
        } else {
            return dataTime.with(LocalTime.MIN);
        }
    }

    /**
     * 连续性修复
     *
     * @param trendIndexList         趋势点列表
     * @param continuityRepairConfig 连续性补全配置
     * @return 修复的列表
     */
    public static List<TrendIndexVO> continuityRepair(List<TrendIndexVO> trendIndexList, ContinuityRepairConfig continuityRepairConfig) {
        if (trendIndexList.isEmpty()) {
            return trendIndexList;
        }
        TrendIndexVO firstIndex = trendIndexList.stream().findFirst().get();
        // 当前第一个有值得点
        TrendIndexVO currentFistExistsValueIndex = null;
        // 第一个点已经为null,需要修复最前面这一段
        if (firstIndex.getYValue() == null) {
            TrendIndexVO firstNotNullValueIndex = null;
            for (TrendIndexVO trendIndex : trendIndexList) {
                if (trendIndex.getYValue() != null) {
                    firstNotNullValueIndex = trendIndex;
                    break;
                }
            }
            if (firstNotNullValueIndex == null) {
                // 所有点没值，没法修复
                return trendIndexList;
            }
            currentFistExistsValueIndex = firstNotNullValueIndex;
        }
        // 最后一个不为null的点
        TrendIndexVO lastNotNullValueIndex = null;
        for (TrendIndexVO trendIndex : Lists.reverse(trendIndexList)) {
            if (trendIndex.getYValue() != null) {
                lastNotNullValueIndex = trendIndex;
                break;
            }
        }
        // 修复最后一段
        assert currentFistExistsValueIndex != null;
        Date correctMaxTime = Optional.ofNullable(continuityRepairConfig.getCorrectMaxTime()).orElse(DateUtil.date());
        for (TrendIndexVO trendIndex : trendIndexList) {
            // 小于开始修复的时间则跳过
            if (continuityRepairConfig.getCorrectMinTime() != null
                    && trendIndex.getXValueDateTime().compareTo(continuityRepairConfig.getCorrectMinTime()) < 0) {
                continue;
            }
            if (trendIndex.getXValueDateTime().compareTo(correctMaxTime) > 0) {
                break;
            }
            if (trendIndex.equals(lastNotNullValueIndex)) {
                // 循环到最后一个不为null的点结束
                break;
            }
            if (trendIndex.getYValue() == null) {
                trendIndex.setYValue(currentFistExistsValueIndex.getYValue());
            }
            currentFistExistsValueIndex = trendIndex;
        }
        return trendIndexList;
    }

    /**
     * 连续性补全配置
     */
    @Data
    @Builder
    public static class ContinuityRepairConfig {
        /**
         * 最大修复到哪个时间点
         * 默认到当前时间
         */
        private Date correctMaxTime;
        /**
         * 最小修复到哪个时间点
         * 默认从第一个数据修复
         */
        private Date correctMinTime;
    }
}
