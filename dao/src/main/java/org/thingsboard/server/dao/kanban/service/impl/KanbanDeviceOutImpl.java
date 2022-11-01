package org.thingsboard.server.dao.kanban.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.entity.vo.DeviceDetailResult;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.kanban.service.svc.KanbanDeviceOutSvc;
import org.thingsboard.server.dao.kanban.vo.KanbanDeviceVo;
import org.thingsboard.server.dao.kanban.vo.transformation.KanbanEnergyVo;
import org.thingsboard.server.dao.sql.census.entity.StatisticalDataEntity;
import org.thingsboard.server.dao.sql.census.service.StatisticalDataService;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @Project Name: thingsboard
 * @File Name: KanbanDeviceOutImpl
 * @Date: 2022/11/1 9:44
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Service
public class KanbanDeviceOutImpl implements KanbanDeviceOutSvc {

    @Autowired
    private StatisticalDataService statisticalDataService;
    @Autowired
    private DeviceMonitorService deviceMonitorService;

    @Override
    public KanbanEnergyVo queryEnergyByDeviceId(UUID deviceId, long timestamp) {
        StatisticalDataEntity statisticalDataEntity = statisticalDataService.queryTodayByEntityId(deviceId, timestamp);
        return statisticalDataEntityToKanbanEnergyVo(statisticalDataEntity);

    }

    @Override
    public KanbanDeviceVo getRTMonitorDeviceDetail(TenantId tenantId, String id) throws InterruptedException, ExecutionException, ThingsboardException {
        DeviceDetailResult detailResult = deviceMonitorService.getRTMonitorDeviceDetail(tenantId, id);
        return  null;
    }


    private KanbanDeviceVo deviceDetailResultToKanbanDeviceVo(DeviceDetailResult detailResult) {
        KanbanDeviceVo deviceVo = new KanbanDeviceVo();
        Boolean isOnline = detailResult.getIsOnLine();
        deviceVo.setDeviceId(detailResult.getId());
        deviceVo.setOnlineState(processingIsOnline(isOnline));
        return  deviceVo;
    }


    private KanbanEnergyVo statisticalDataEntityToKanbanEnergyVo(StatisticalDataEntity entity) {
        return KanbanEnergyVo.builder()
                .consumptiontodayWater(processingIsEmpty(entity, entity.getWaterAddedValue()))
                .consumptiontodayElectricity(processingIsEmpty(entity, entity.getElectricAddedValue()))
                .consumptiontodayGas(processingIsEmpty(entity, entity.getGasAddedValue()))
                .productionToday(processingIsEmpty(entity, entity.getCapacityAddedValue()))
                .productionTotal(processingIsEmpty(entity, entity.getCapacityValue()))
                .build();
    }


    private String processingIsEmpty(StatisticalDataEntity entity, String str) {
        if (entity == null) {
            return "0";
        }
        if (StringUtils.isEmpty(str)) {
            return "0";
        }
        return str;
    }

    /**
     * 在线状态（0-在线 1-离线）
     * @param isOnline
     * @return
     */
    private String processingIsOnline(Boolean isOnline) {
        if (isOnline == null) {
            return "1";
        }
        if(isOnline){
            return "0";
        }
        return "1";
    }
}
