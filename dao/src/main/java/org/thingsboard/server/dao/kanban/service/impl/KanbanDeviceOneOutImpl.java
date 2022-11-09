package org.thingsboard.server.dao.kanban.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.device.DeviceService;
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
    @Autowired private DeviceService deviceService;


    @Override
    public KanbanDeviceVo integratedDeviceInterface(TenantId tenantId, UUID deviceId) throws ThingsboardException {
        try {
            KanbanDeviceVo vo = new KanbanDeviceVo();
            vo.setDeviceId(deviceId.toString());
            Device device = deviceService.findDeviceById(tenantId, new DeviceId(deviceId));
            if (device == null) {
                throw new ThingsboardException("查询不到该设备！", ThingsboardErrorCode.FAIL_VIOLATION);
            }
            vo.setName(device.getRename());
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        KanbanEnergyVo kanbanEnergyVo = kanbanDeviceOutSvc.queryEnergyByDeviceId(deviceId, CommonUtil.getTodayStartTime());
                        vo.setConsumptiontodayWater(kanbanEnergyVo.getConsumptiontodayWater());
                        vo.setConsumptiontodayElectricity(kanbanEnergyVo.getConsumptiontodayElectricity());
                        vo.setConsumptiontodayGas(kanbanEnergyVo.getConsumptiontodayGas());
                        vo.setProductionToday(kanbanEnergyVo.getProductionToday());
                        vo.setProductionTotal(kanbanEnergyVo.getProductionTotal());
                    }),
                    CompletableFuture.runAsync(() -> {
                        AlarmDayResult alarmDayResult = kanbanDeviceOutSvc.getAlarmRecordStatisticByDay(tenantId, deviceId);
                        vo.setAlertToday(String.valueOf(alarmDayResult.getTodayAlarmTimes()));
                        vo.setAlertYesterday(String.valueOf(alarmDayResult.getYesterdayAlarmTimes()));
                        vo.setAlertHistory(String.valueOf(alarmDayResult.getHistoryAlarmTimes()));
                    }),
                    CompletableFuture.runAsync(() -> {
                        KanbanDeviceVo v1 = kanbanDeviceOutSvc.getRTMonitorDeviceDetail(tenantId, deviceId.toString());
                        vo.setOnlineState(v1.getOnlineState());
                        vo.setComponentData(v1.getComponentData());
                    })

            ).join();
            return vo;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
