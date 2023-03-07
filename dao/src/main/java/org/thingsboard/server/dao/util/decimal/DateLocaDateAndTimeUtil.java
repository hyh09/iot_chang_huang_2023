package org.thingsboard.server.dao.util.decimal;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;
import org.thingsboard.server.dao.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @Project Name: long-win-iot
 * @File Name: DateUtil
 * @Date: 2023/1/30 15:23
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public class DateLocaDateAndTimeUtil {
    public final static DateLocaDateAndTimeUtil INSTANCE = new DateLocaDateAndTimeUtil();


    public static String formatDate(LocalDate localDate, ChartDateEnums dateEnums) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(dateEnums.getPattern());
        String dateStr = localDate.format(fmt);
        return dateStr;
    }

    /**
     * 格式化当前的时间
     *
     * @param localDate
     * @param pattern
     * @return
     */
    public String formatDate(LocalDate localDate, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(fmt);

    }

    /**
     * 返回时间轴
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 开始与结束之间的所以日期，包括起止
     */
    public List<LocalDate> getMiddleDate(ChartDateEnums dateEnums, LocalDate begin, LocalDate end) {
        if (dateEnums == ChartDateEnums.MONTHS) {
            return getBetweenDay(begin, end);
        }
        return getBetweenMonth(begin, end);
    }

    /**
     * 获取两个时间的 月维度的时间轴
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<LocalDate> getBetweenMonth(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> list = new ArrayList<>();
        long distance = ChronoUnit.MONTHS.between(startDate, endDate);
        if (distance < 1) {
            list.add(startDate);
        }
        Stream.iterate(startDate, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> {
            list.add(f);
        });
        return list;
    }

    /**
     * 获取两个日期之间的 时间 ;天的移动
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<LocalDate> getBetweenDay(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> list = new ArrayList<>();
        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 1) {
            list.add(startDate);
        }
        Stream.iterate(startDate, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            list.add(f);
        });
        return list;
    }

    /**
     * 获取两个日期之间的 时间 ;天的移动
     *
     * @param startDate 整点的起始时间
     * @param endDate   整点的结束时间
     * @return
     */
    public List<LocalDateTime> getBetweenHour(LocalDateTime startDate, LocalDateTime endDate) {
        List<LocalDateTime> list = new ArrayList<>();
        long distance = ChronoUnit.HOURS.between(startDate, endDate);
        if (distance < 1) {
            list.add(startDate);
        }
        Stream.iterate(startDate, d -> d.plusHours(1)).limit(distance + 1).forEach(f -> {
            list.add(f);
        });
        return list;
    }

    /**
     * 当天0点
     *
     * @return
     */
    public LocalDateTime getTodayZeroTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    /**
     * 当天23点
     *
     * @return
     */
    public LocalDateTime getTodayTwentyThreeTime() {
        LocalTime localTime = LocalTime.of(23, 0);
        return LocalDateTime.of(LocalDate.now(), localTime);
    }

    /**
     * 当天0点
     *
     * @return
     */
    public LocalDateTime getYesterdayZeroTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }


    /**
     * 时间轴补齐列表； 补齐的步长是天；
     *
     * @param originalData 当前需要补齐的数据
     * @param startDate    开始的时间
     * @param endDate      结束的时间
     * @param defaultValue 默认补充的值
     * @param target       目标对象
     * @param timeFiled    时间轴x的字段
     * @param valeFiled    y轴 的value字段
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public <T> List<T> completionTime(List<T> originalData, LocalDate startDate, LocalDate endDate, String defaultValue, Class target, String timeFiled, String valeFiled, DateTimeFormatter dateTimeFormatter
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<T> resultList = new ArrayList<>();
        List<LocalDate> definedTimeList = getBetweenDay(startDate, endDate);
        Map<LocalDate, String> dataMapping = new HashMap<>();
        if (CollectionUtils.isNotEmpty(originalData)) {
            for (Object o : originalData) {
                Object t1 = ReflectionUtils.invokeGetterMethod(o, timeFiled);
                LocalDate localDate = null;
                if (t1 != null) {
                    if (t1 instanceof LocalDate) {
                        localDate = (LocalDate) t1;
                    } else if (t1 instanceof String) {
                        if (StringUtils.isNotEmpty(t1.toString())) {
                            localDate = LocalDate.parse(t1.toString(), dateTimeFormatter);
                        }

                    }
                    Object v1 = ReflectionUtils.invokeGetterMethod(o, valeFiled);
                    if (v1 != null) {
                        if (v1 instanceof Number) {
                            if (StringUtils.isNotEmpty(v1.toString())) {
                                dataMapping.put(localDate, v1.toString());
                            }
                        }
                    }
                }
            }
        }
        for (LocalDate date : definedTimeList) {
            T resultObj = (T) target.getDeclaredConstructor().newInstance();
            String valueStr = dataMapping.get(date);
            Field timeFile = ReflectionUtils.getAccessibleField(resultObj, timeFiled);
            if (timeFile.getType().isAssignableFrom(LocalDate.class)) {
                timeFile.set(resultObj, date);
            } else {
                timeFile.set(resultObj, date.format(dateTimeFormatter));
            }
            Field currentValueField = ReflectionUtils.getAccessibleField(resultObj, valeFiled);
            if (currentValueField.getType().isAssignableFrom(String.class)) {
                currentValueField.set(resultObj, StringUtils.isNoneBlank(valueStr) ? valueStr : defaultValue);
            } else if (currentValueField.getType().isAssignableFrom(BigDecimal.class)) {
                currentValueField.set(resultObj, StringUtils.isNoneBlank(valueStr) ? new BigDecimal(valueStr) : new BigDecimal(defaultValue));
            } else if (currentValueField.getType().isAssignableFrom(Double.class)) {
                currentValueField.set(resultObj, StringUtils.isNoneBlank(valueStr) ? Double.valueOf(valueStr) : Double.valueOf(defaultValue));
            }
            resultList.add(resultObj);

        }

        return resultList;
    }
}
