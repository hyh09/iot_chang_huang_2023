package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.key.KeyNameEnums;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;

import java.util.List;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: PerformanceAnalysisListSvcTest
 * @Date: 2022/11/7 14:01
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThingsboardServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PerformanceAnalysisListSvcTest {

    @Autowired
    private PerformanceAnalysisListSvc performanceAnalysisListSvc;

    /**
     * startTime: 1667750400000
     * endTime: 1667836799999
     * factoryId: e7fd0750-589a-11ec-afcd-2bd77acada1c
     */

    @Test
    public void yieldList() {
        QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setFactoryId(UUID.fromString("e7fd0750-589a-11ec-afcd-2bd77acada1c"));
        queryTsKvVo.setStartTime(1667750400000L);
        queryTsKvVo.setEndTime(1667836799999L);
        queryTsKvVo.setKey(KeyNameEnums.capacities.getCode());
        List<EnergyEffciencyNewEntity> energyEffciencyNewEntities = performanceAnalysisListSvc.yieldList(queryTsKvVo);
        log.info("打印当前的数据:{}", JacksonUtil.toString(energyEffciencyNewEntities));
    }
}
