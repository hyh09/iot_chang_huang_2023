package org.thingsboard.server.dao.board.factoryBoard.impl.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.chart.TrendChartRateDto;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Project Name: long-win-iot
 * @File Name: TrendChartOfOperatingRateJdbcImplTest
 * @Date: 2023/2/14 15:32
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThingsboardServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrendChartOfOperatingRateJdbcImplTest {


    @Autowired
    private JdbcTemplate jdbcTemplate;
    private TrendChartOfOperatingRateJdbcImpl trendChartOfOperatingRateJdbc;

    List<UUID> deviceIdList;

    private String str = "1ef46dc0-9549-11ed-b871-adf1f935f2f7,\n" +
            "86cadaa0-614d-11ec-8461-59de65c6b4c4,\n" +
            "9a778450-8c11-11ed-8204-ff162fa5a3f9,\n" +
            "34735fd1-6ad8-11ed-8822-dd9b4d35cb69,\n" +
            "5cabae20-f914-11ec-9a2c-c9071742e1f7,\n" +
            "14ca57f0-f914-11ec-9a2c-c9071742e1f7,\n" +
            "6d591f60-565c-11ec-a240-955d7c1497e4,\n" +
            "b6a25580-8c14-11ed-ab4c-bf9d92b6efb4,\n" +
            "1ae35660-6ad8-11ed-8822-dd9b4d35cb69,";

    @Before
    public void init() {
//        trendChartOfOperatingRateJdbc = new TrendChartOfOperatingRateJdbcImpl(jdbcTemplate);
        System.out.println("=======>");
        deviceIdList = Arrays.asList(str.split(",")).stream().map(s -> (UUID.fromString(s.trim()))).collect(Collectors.toList());
    }

    @Test
    public void monthTest() {
        System.out.println(trendChartOfOperatingRateJdbc);
        List<TrendChartRateDto> trendChartRateDtoList = trendChartOfOperatingRateJdbc.startTimeOfThisMonth(deviceIdList);
        System.out.println("打印当前的数据的输出：{}" + trendChartRateDtoList);
    }

    @Test
    public void startTimeOfThisYearTest() {
        try {
            System.out.println(trendChartOfOperatingRateJdbc);
            List<TrendChartRateDto> trendChartRateDtoList = trendChartOfOperatingRateJdbc.startTimeOfThisYear(deviceIdList);
            System.out.println("打印当前的数据的输出：{}" + trendChartRateDtoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
