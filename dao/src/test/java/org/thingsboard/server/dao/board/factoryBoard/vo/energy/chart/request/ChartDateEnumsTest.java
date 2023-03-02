package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Project Name: long-win-iot
 * @File Name: ChartDateEnumsTest
 * @Date: 2023/1/5 13:10
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public class ChartDateEnumsTest {

    @Test
    public void Test01() {
        ChartDateEnums chartDateEnums = ChartDateEnums.valueOf("MONTHS1");
        System.out.println("===?" + chartDateEnums);
    }


    @Test
    public void Test02() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC);
        ChartDateEnums tsFormat = ChartDateEnums.MONTHS;
        String date = zonedDateTime.format(DateTimeFormatter.ofPattern(tsFormat.getPattern()));
        System.out.println(date);
    }
}
