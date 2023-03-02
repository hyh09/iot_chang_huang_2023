package org.thingsboard.server.util;

import org.junit.Test;
import org.thingsboard.server.dao.util.decimal.DateLocaDateAndTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * @Project Name: long-win-iot
 * @File Name: LocalDateTimeTest
 * @Date: 2023/1/30 13:56
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public class LocalDateTimeTest {


    @Test
    public void Test01() {
        LocalDate date = LocalDate.now();
        LocalDate firstday = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("获取本月第一天:" + firstday);
        System.out.println("获取本月最后一天:" + lastDay);

        LocalDate firstYearday1 = date.with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastYearDay2 = date.with(TemporalAdjusters.lastDayOfYear());
        System.out.println("获取本年第一天:" + firstYearday1);
        System.out.println("获取本年最后一天:" + lastYearDay2);

    }


    @Test
    public void LocalDateToString() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        String dateStr = date.format(fmt);
        System.out.println("LocalDate转String:" + dateStr);
    }


    @Test
    public void eques() {
        LocalDateTime startDate = LocalDateTime.now().withHour(1).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDate = startDate.plusHours(23);
        System.out.println(DateLocaDateAndTimeUtil.INSTANCE.getBetweenHour(startDate, endDate));

        LocalDateTime today_start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);//当天零点
        LocalDateTime today_end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        System.out.println("获取今天开始时间:" + today_start);//2023-02-08T00:00
        System.out.println("获取今天结束时间:" + today_end);//2023-02-08T23:59:59.999999999
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm");
        System.out.println("结束时间格式话：" + dtf2.format(today_end));
        ;


    }

    @Test
    public void Test001() {


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

}
