package org.thingsboard.server.dao.kanban.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.kanban.service.svc.KanbanDeviceOutSvc;
import org.thingsboard.server.dao.kanban.vo.KanbanDeviceVo;
import org.thingsboard.server.dao.kanban.vo.inside.ComponentDataDTO;
import org.thingsboard.server.dao.kanban.vo.inside.DataDTO;
import org.thingsboard.server.dao.kanban.vo.transformation.KanbanEnergyVo;
import org.thingsboard.server.dao.sql.census.entity.StatisticalDataEntity;
import org.thingsboard.server.dao.sql.census.service.StatisticalDataService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    /**
     * 预警  /api/deviceMonitor/board/alarmRecord/day/statistics
     *
     * @param tenantId
     * @param deviceId
     * @return
     */
    @Override
    public AlarmDayResult getAlarmRecordStatisticByDay(TenantId tenantId, UUID deviceId) {
        FactoryDeviceQuery query = new FactoryDeviceQuery().setDeviceId(deviceId.toString());
        AlarmDayResult alarmDayResult = deviceMonitorService.getAlarmRecordStatisticByDay(tenantId, query);
        return alarmDayResult;
    }


    @Override
    public KanbanDeviceVo getRTMonitorDeviceDetail(TenantId tenantId, String id)  {
        DeviceDetailResult detailResult = null;
        try {
            detailResult = deviceMonitorService.getRTMonitorDeviceDetail(tenantId, id);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ThingsboardException e) {
            e.printStackTrace();
        }
        return deviceDetailResultToKanbanDeviceVo(detailResult);
    }


    private KanbanDeviceVo deviceDetailResultToKanbanDeviceVo(DeviceDetailResult detailResult) {

        KanbanDeviceVo deviceVo = new KanbanDeviceVo();
        if (detailResult == null) {
            return deviceVo;
        }
        Boolean isOnline = detailResult.getIsOnLine();
        deviceVo.setDeviceId(detailResult.getId());
        deviceVo.setOnlineState(processingIsOnline(isOnline));
        List<DictDeviceComponentVO> deviceComponentVOList= detailResult.getComponentList();

        List<ComponentDataDTO> componentDataList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(deviceComponentVOList)){

            for(DictDeviceComponentVO data:deviceComponentVOList){
                ComponentDataDTO  componentDataDTO = new ComponentDataDTO();
                componentDataDTO.setName(data.getName());
                List<DictDeviceComponentPropertyVO> propertyVOList = data.getPropertyList();
                if(!CollectionUtils.isEmpty(propertyVOList)){
                    List<DataDTO>  list=   propertyVOList.stream().map(data2->{
                        DataDTO  dataDTO = new DataDTO();
                        dataDTO.setKey(data2.getName());
                        dataDTO.setValue(data2.getContent());
                        return  dataDTO;
                    }).collect(Collectors.toList());
                    componentDataDTO.setData(list);
                }
                componentDataList.add(componentDataDTO);
            }
        }
        deviceVo.setComponentData(componentDataList);
        return deviceVo;
    }


    private KanbanEnergyVo statisticalDataEntityToKanbanEnergyVo(StatisticalDataEntity entity) {
        return KanbanEnergyVo.builder()
                .consumptiontodayWater(processingIsEmpty(entity, "getWaterAddedValue"))
                .consumptiontodayElectricity(processingIsEmpty(entity, "getElectricAddedValue"))
                .consumptiontodayGas(processingIsEmpty(entity, "getGasAddedValue"))
                .productionToday(processingIsEmpty(entity, "getCapacityAddedValue"))
                .productionTotal(processingIsEmpty(entity, "getCapacityAddedValue"))
                .build();
    }


    private String processingIsEmpty(StatisticalDataEntity entity, String str) {
        if (entity == null) {
            return "0";
        }
        Class<?> clazz=  entity.getClass();
        try {
            Method getMethod = clazz.getMethod(str);
         String value= (String) getMethod.invoke(entity);
         if (StringUtils.isNotEmpty(value)) {
                return value;
          }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0";
    }

    /**
     * 在线状态（0-在线 1-离线）
     *
     * @param isOnline
     * @return
     */
    private String processingIsOnline(Boolean isOnline) {
        if (isOnline == null) {
            return "1";
        }
        if (isOnline) {
            return "0";
        }
        return "1";
    }
}
