package org.thingsboard.server.dao.board.factoryBoard.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.board.factoryBoard.impl.base.SqlServerBascFactoryImpl;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryCollectionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryProductionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.OrderCompletionRateAndYieldRateVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.OrderProductionVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.SqlOnFieldAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.WorkshopAndRunRateVo;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.util.GenericsUtils;
import org.thingsboard.server.dao.workshop.WorkshopService;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryProductionInformationImpl
 * @Date: 2023/1/6 13:46
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@Service
public class FactoryProductionInformationImpl extends SqlServerBascFactoryImpl implements FactoryProductionInformationSvc, InitializingBean {

    @Autowired
    private WorkshopService workshopService;
    @Autowired
    private FactoryCollectionInformationSvc factoryCollectionInformationSvc;

    public FactoryProductionInformationImpl(@Autowired @Qualifier("sqlServerTemplate") JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }


    @Override
    public List<WorkshopAndRunRateVo> queryWorkshopAndRunRate(TenantId tenantId, UUID factoryId) {
        List<WorkshopAndRunRateVo> finalResultList = new ArrayList<>();
        List<Workshop> workshopList = workshopService.findWorkshopListByTenant(tenantId.getId(), factoryId);
        if (CollectionUtils.isEmpty(workshopList)) {
            return finalResultList;
        }
        List<CompletableFuture<WorkshopAndRunRateVo>> futures = workshopList.stream()
                .map(t ->
                        CompletableFuture.supplyAsync(() ->
                                (getVo(t, tenantId))
                        )


                ).collect(Collectors.toList());
        List<WorkshopAndRunRateVo> result =
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList());
        return result;
    }

    @Override
    public OrderProductionVo getOrderProduction() throws Exception {
        OrderProductionVo vo = new OrderProductionVo();
        super.executeSqlByObject(vo);
        return vo;
    }

    @Override
    public OrderCompletionRateAndYieldRateVo getOrderCompletionRateAndYieldRate() throws Exception {
        OrderCompletionRateAndYieldRateVo vo = new OrderCompletionRateAndYieldRateVo();
        super.executeSqlByObject(vo);
        return vo;
    }

    private WorkshopAndRunRateVo getVo(Workshop t1, TenantId tenantId) {
        WorkshopAndRunRateVo vo = JacksonUtil.convertValueNoUNKNOWN(t1, WorkshopAndRunRateVo.class);
        FactoryDeviceQuery factoryDeviceQuery = new FactoryDeviceQuery();
        factoryDeviceQuery.setWorkshopId(t1.getId().toString());
        vo.setOnlineRate(factoryCollectionInformationSvc.queryDeviceStatusNum(tenantId, factoryDeviceQuery).getOnlineRate());
        return vo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Hashtable<String, SqlOnFieldAnnotation> hashtable = GenericsUtils.getRowNameHashSql(OrderProductionVo.class);
        sqlMappingMap.put(OrderProductionVo.class,hashtable);
        Hashtable<String, SqlOnFieldAnnotation> orderCompletionMap = GenericsUtils.getRowNameHashSql(OrderCompletionRateAndYieldRateVo.class);
        sqlMappingMap.put(OrderCompletionRateAndYieldRateVo.class,orderCompletionMap);
    }
}
