package org.thingsboard.server.dao.kanban.service.svc;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.entity.vo.AlarmDayResult;
import org.thingsboard.server.dao.hs.entity.vo.DeviceDetailResult;
import org.thingsboard.server.dao.kanban.vo.KanbanDeviceVo;
import org.thingsboard.server.dao.kanban.vo.transformation.KanbanEnergyVo;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @Project Name: thingsboard
 * @File Name: KanbanDeviceOutSvc
 * @Date: 2022/11/1 9:44
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
public interface KanbanDeviceOutSvc {

    /**
     * 查询设备今日的水电气 产量的数据
     *
     * @param deviceId
     * @return
     */
    KanbanEnergyVo queryEnergyByDeviceId(UUID deviceId, long timestamp);

    /**
     * 预警  /api/deviceMonitor/board/alarmRecord/day/statistics
     * @param tenantId
     * @param deviceId
     * @return
     */
    AlarmDayResult getAlarmRecordStatisticByDay(TenantId tenantId, UUID deviceId);

    /**
     * 部件的
     */
    KanbanDeviceVo getRTMonitorDeviceDetail(TenantId tenantId, String id) ;
}
