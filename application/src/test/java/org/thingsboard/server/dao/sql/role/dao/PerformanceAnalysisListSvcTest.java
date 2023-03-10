package org.thingsboard.server.dao.sql.role.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.key.KeyNameEnums;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;
import org.thingsboard.server.dao.sql.role.service.Imp.TsKvDeviceRepository;
import org.thingsboard.server.dao.sql.role.service.Imp.vo.TskvDto;

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

    @Autowired
    private EfficiencyStatisticsSvc efficiencyStatisticsSvc;

    /**
     * startTime: 1667750400000
     * endTime: 1667836799999
     * factoryId: e7fd0750-589a-11ec-afcd-2bd77acada1c
     */

    @Test
    public void yieldList() {
        QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setTenantId(UUID.fromString("34b42c20-4e61-11ec-8ae5-dbf4f4ba7d17"));
        queryTsKvVo.setFactoryId(UUID.fromString("e7fd0750-589a-11ec-afcd-2bd77acada1c"));
        queryTsKvVo.setStartTime(1667750400000L);
        queryTsKvVo.setEndTime(1667836799999L);
        queryTsKvVo.setKey(KeyNameEnums.capacities.getCode());
        List<EnergyEffciencyNewEntity> energyEffciencyNewEntities = performanceAnalysisListSvc.yieldList(queryTsKvVo);
        System.out.println("打印当前的数据:{}" + JacksonUtil.toString(energyEffciencyNewEntities));
    }

    /**
     *查询产量的列表数据
     */
    @Test
    public void queryPCCapAppNewMethod() {
        QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setTenantId(UUID.fromString("34b42c20-4e61-11ec-8ae5-dbf4f4ba7d17"));
        queryTsKvVo.setFactoryId(UUID.fromString("24d0aa00-589c-11ec-afcd-2bd77acada1c"));
        queryTsKvVo.setStartTime(1669564800000L);
        queryTsKvVo.setEndTime(1669651199999L);
        queryTsKvVo.setKey(KeyNameEnums.capacities.getCode());
        TenantId  tenantId  = new TenantId(queryTsKvVo.getTenantId());
        PageLink pageLink= new PageLink(10,0);
        PageDataAndTotalValue<AppDeviceCapVo> energyEffciencyNewEntities = efficiencyStatisticsSvc.queryPCCapAppNewMethod(queryTsKvVo,tenantId,pageLink);
        System.out.println("PageDataAndTotalValue<AppDeviceCapVo> 打印当前的数据:{}" + JacksonUtil.toString(energyEffciencyNewEntities));
    }

    @Test
    public void queryPCCapAppNewMethod02() {
        QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setTenantId(UUID.fromString("34b42c20-4e61-11ec-8ae5-dbf4f4ba7d17"));
        queryTsKvVo.setFactoryId(UUID.fromString("24d0aa00-589c-11ec-afcd-2bd77acada1c"));
        queryTsKvVo.setStartTime(1667836800000L);
        queryTsKvVo.setEndTime(1667923199999L);
        queryTsKvVo.setKey(KeyNameEnums.capacities.getCode());

        TenantId  tenantId  = new TenantId(queryTsKvVo.getTenantId());
        PageLink pageLink= new PageLink(8,0);
        PageDataAndTotalValue<AppDeviceCapVo> energyEffciencyNewEntities = efficiencyStatisticsSvc.queryPCCapAppNewMethod(queryTsKvVo,tenantId,pageLink);
        System.out.println("PageDataAndTotalValue<AppDeviceCapVo> 打印当前的数据:{}" + JacksonUtil.toString(energyEffciencyNewEntities));
    }



    @Autowired
    private TsKvDeviceRepository tsKvDeviceRepository;

    @Test
    public  void queryTest(){
        UUID id=UUID.fromString("0048d390-6465-11ec-903a-0b302b886cc7");
        Long startTime =1669564800000L;
          Long endTime =1669651199999L;
          Integer keyId =1242;
        String  tskvDto =  tsKvDeviceRepository.queryData(id,startTime,endTime,keyId);
        log.info(tskvDto.toString());
    }




}
