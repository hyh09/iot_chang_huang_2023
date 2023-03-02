package org.thingsboard.server.dao.sqlserver.jdbc.server;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.ThingsboardServerApplication;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Project Name: long-win-iot
 * @File Name: HwnergyServiceTest
 * @Date: 2023/1/30 11:01
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThingsboardServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HwnergyServiceTest {

    @Autowired
    private HwEnergyService hwnergyService;

    @Test
    public void queryUnitPriceTest() {
        Map<String, BigDecimal> map = hwnergyService.queryUnitPrice();
        log.info("打印的结果:{}", JacksonUtil.toString(map));
    }
}
