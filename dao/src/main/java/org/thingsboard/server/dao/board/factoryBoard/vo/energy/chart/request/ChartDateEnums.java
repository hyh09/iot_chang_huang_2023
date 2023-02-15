package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * @Project Name: thingsboard
 * @File Name: ChartDateEnums
 * @Date: 2023/1/5 11:28
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public enum ChartDateEnums {
    MONTHS("MONTHS", "yyyy-MM-dd", "day"),
    YEARS("YEARS", "yyyy-MM", "month"),
    ;

    private String code;
    private String pattern;
    private String precision;

    ChartDateEnums(String code, String pattern, String precision) {
        this.code = code;
        this.pattern = pattern;
        this.precision = precision;
    }

    public String forMartTime(LocalDate dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(this.getPattern());
        String dateStr = dateTime.format(fmt);
        return dateStr;

    }

    public ChartDateEnumsToLocalDateVo currentConvert() {
        LocalDate date = LocalDate.now();
        LocalDate beginDate = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());
        if (this == ChartDateEnums.YEARS) {
            beginDate = date.with(TemporalAdjusters.firstDayOfYear());
            endDate = date.with(TemporalAdjusters.lastDayOfYear());
        }
        return new ChartDateEnumsToLocalDateVo(beginDate,endDate);
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }
}
