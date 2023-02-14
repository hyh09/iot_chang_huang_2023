package org.thingsboard.server.dao.util.decimal;

import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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


    public static String formatDate(LocalDate localDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(ChartDateEnums.YEARS.getPattern());
        String dateStr = localDate.format(fmt);
        return dateStr;
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


}
