package org.thingsboard.server.dao.kanban.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.entity.vo.AlarmDayResult;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.kanban.service.svc.KanbanDeviceOneOutSvc;
import org.thingsboard.server.dao.kanban.service.svc.KanbanDeviceOutSvc;
import org.thingsboard.server.dao.kanban.vo.KanbanDeviceVo;
import org.thingsboard.server.dao.kanban.vo.transformation.KanbanEnergyVo;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Project Name: thingsboard
 * @File Name: KanbanDeviceOneOutImpl
 * @Date: 2022/11/1 13:53
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Service
public class KanbanDeviceOneOutImpl implements KanbanDeviceOneOutSvc {
    @Autowired private KanbanDeviceOutSvc kanbanDeviceOutSvc;


    @Override
    public KanbanDeviceVo integratedDeviceInterface(TenantId tenantId, UUID deviceId) {
        KanbanDeviceVo  vo = new  KanbanDeviceVo();
        CompletableFuture.allOf(
                CompletableFuture.runAsync(()->{
                    KanbanEnergyVo  kanbanEnergyVo= kanbanDeviceOutSvc.queryEnergyByDeviceId(deviceId, CommonUtil.getTodayStartTime());
                    vo.setConsumptiontodayWater(kanbanEnergyVo.getConsumptiontodayWater());
                    vo.setConsumptiontodayElectricity(kanbanEnergyVo.getConsumptiontodayElectricity());
                    vo.setConsumptiontodayGas(kanbanEnergyVo.getConsumptiontodayGas());
                    vo.setProductionToday(kanbanEnergyVo.getProductionToday());
                    vo.setProductionTotal(kanbanEnergyVo.getProductionTotal());
                }),
                CompletableFuture.runAsync(()->{
                    AlarmDayResult alarmDayResult =  kanbanDeviceOutSvc.getAlarmRecordStatisticByDay(tenantId,deviceId);
                    vo.setAlertToday(String.valueOf(alarmDayResult.getTodayAlarmTimes()));
                    vo.setAlertYesterday(String.valueOf(alarmDayResult.getYesterdayAlarmTimes()));
                    vo.setAlertHistory(String.valueOf(alarmDayResult.getHistoryAlarmTimes()));
                }),
                CompletableFuture.runAsync(()->{
                    KanbanDeviceVo v1= kanbanDeviceOutSvc.getRTMonitorDeviceDetail(tenantId,deviceId.toString());
                    vo.setName(v1.getName());
                    vo.setOnlineState(v1.getOnlineState());
                    vo.setComponentData(v1.getComponentData());
                })

        ).join();
        return vo;
    }
}
