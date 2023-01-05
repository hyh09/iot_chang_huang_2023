package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * @Project Name: thingsboard
 * @File Name: ChartDateEnums
 * @Date: 2023/1/5 11:28
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public enum ChartDateEnums {
    MONTHS("MONTHS", "yyyy_MM_dd", ChronoUnit.MONTHS),
    YEARS("YEARS", "yyyy_MM", ChronoUnit.YEARS),
    ;

    private String code;
    private String pattern;
    private TemporalUnit truncateUnit;


    ChartDateEnums(String code, String pattern, TemporalUnit truncateUnit) {
        this.code = code;
        this.pattern = pattern;
        this.truncateUnit = truncateUnit;
    }


    public LocalDateTime trancateTo1(LocalDateTime time) {
        switch (this) {
            case MONTHS:
                return time.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
            case YEARS:
                return time.truncatedTo(ChronoUnit.DAYS).withDayOfYear(1);
            default:
                throw new RuntimeException("Failed to parse partitioning property!");
        }
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

    public TemporalUnit getTruncateUnit() {
        return truncateUnit;
    }

    public void setTruncateUnit(TemporalUnit truncateUnit) {
        this.truncateUnit = truncateUnit;
    }
}
