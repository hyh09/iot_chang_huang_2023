package org.thingsboard.server.dao.board.factoryBoard.svc;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.current.CurrentUtilitiesVo;
import org.thingsboard.server.dao.util.CommonUtils;

import java.util.UUID;

/**
 * @Project Name: long-win-iot
 * @File Name: FactoryEnergySvcTest
 * @Date: 2023/1/4 11:02
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThingsboardServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FactoryEnergySvcTest {

    @Autowired
    private FactoryEnergySvc factoryEnergySvc;


    /**
     * 工厂id : 24d0aa00-589c-11ec-afcd-2bd77acada1c  新乡飞鹭工厂
     * 租户id: 34b42c20-4e61-11ec-8ae5-dbf4f4ba7d17
     *
     * @throws JsonProcessingException
     */
    @Test
    public void queryCurrentEnergy() throws JsonProcessingException {
        try {
            QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
            queryTsKvVo.setStartTime(CommonUtils.getZero());
            queryTsKvVo.setEndTime(CommonUtils.getNowTime());
            queryTsKvVo.setFactoryId(UUID.fromString("24d0aa00-589c-11ec-afcd-2bd77acada1c"));
            TenantId tenantId = new TenantId(UUID.fromString("34b42c20-4e61-11ec-8ae5-dbf4f4ba7d17"));
            CurrentUtilitiesVo currentUtilitiesVo = factoryEnergySvc.queryCurrentEnergy(queryTsKvVo, tenantId);
            log.info("打印结果:{}", JacksonUtil.toString(currentUtilitiesVo));
        } catch (Exception e) {
            System.out.println("====>" + e);
        }
    }
}
