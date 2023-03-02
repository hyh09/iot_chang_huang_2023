package org.thingsboard.server.dao.kanban.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.hs.entity.vo.AlarmDayResult;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGraphAndPropertyVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGraphPropertyVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGraphVO;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.kanban.service.svc.KanbanDeviceOneOutSvc;
import org.thingsboard.server.dao.kanban.service.svc.KanbanDeviceOutSvc;
import org.thingsboard.server.dao.kanban.vo.KanbanDeviceVo;
import org.thingsboard.server.dao.kanban.vo.inside.AttributesPropertiesGraphUnderVo;
import org.thingsboard.server.dao.kanban.vo.inside.ComponentDataDTO;
import org.thingsboard.server.dao.kanban.vo.inside.DataDTO;
import org.thingsboard.server.dao.kanban.vo.transformation.KanbanEnergyVo;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    @Autowired
    private KanbanDeviceOutSvc kanbanDeviceOutSvc;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DictDeviceService dictDeviceService;
    @Autowired
    private DeviceDao deviceDao;

    /**
     * 查询设备的部件的信息接口
     * 1. 查询设备的详情接口：  select  *  from  device  where  id=? and
     *
     * @param tenantId 租户id
     * @param deviceId 设备id
     * @return
     * @throws ThingsboardException
     */
    @Override
    public KanbanDeviceVo integratedDeviceInterface(TenantId tenantId, UUID deviceId) throws ThingsboardException {
        try {
            KanbanDeviceVo vo = new KanbanDeviceVo();
            vo.setDeviceId(deviceId.toString());
            Device device = deviceDao.findDeviceByTenantIdAndId(tenantId, deviceId);
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
            List<ComponentDataDTO> componentDataDTOList = parameterMerging(tenantId, device.getDictDeviceId(), vo.getComponentData());
            vo.setComponentData(componentDataDTOList);
            return vo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private List<ComponentDataDTO> parameterMerging(TenantId tenantId, UUID dictDeviceId, List<ComponentDataDTO> componentDataDTOList) {
        List<DictDeviceGraphVO> dictDeviceGraphVOList = dictDeviceService.listDictDeviceGraphs(tenantId, dictDeviceId);
        if (CollectionUtils.isEmpty(dictDeviceGraphVOList)) {
            return componentDataDTOList;
        }
        /** 将 图表 和图表下的 属性 ； 转换为一层； 数据扁平化*/
        List<DictDeviceGraphAndPropertyVO> propertyVOList = dataConversion(dictDeviceGraphVOList);
        if (CollectionUtils.isEmpty(componentDataDTOList)) {
            return componentDataDTOList;
        }
        /** 用于分类聚合 图表> 图表下的属性和值*/
        Map<String, List<AttributesPropertiesGraphUnderVo>> mapGraph = new HashMap<>();
        /** 遍历当前的分组下的 value */
        for (ComponentDataDTO v1 : componentDataDTOList) {
            List<DataDTO> dataDTOList = v1.getData();
            if (CollectionUtils.isNotEmpty(dataDTOList)) {
                for (DataDTO vo : dataDTOList) {
                    dataConversionMap(propertyVOList, mapGraph, vo);
                }
            }
        }
        if (mapGraph.isEmpty()) {
            return componentDataDTOList;
        }
        return conertMapToList(componentDataDTOList, mapGraph, propertyVOList);

    }


    private List<DictDeviceGraphAndPropertyVO> dataConversion(List<DictDeviceGraphVO> dictDeviceGraphVOList) {
        List<DictDeviceGraphAndPropertyVO> resultList = new ArrayList<>();
        for (DictDeviceGraphVO v1 : dictDeviceGraphVOList) {
            String tableName = v1.getName();
            List<DictDeviceGraphPropertyVO> propertyVOList = v1.getProperties();
            List<DictDeviceGraphAndPropertyVO> list03 = propertyVOList.stream().map(m1 -> {
                DictDeviceGraphAndPropertyVO conversionObject = new DictDeviceGraphAndPropertyVO();
                conversionObject.setChartName(tableName);
                conversionObject.setName(m1.getName());
                conversionObject.setId(m1.getId());
                conversionObject.setPropertyType(m1.getPropertyType());
                conversionObject.setSuffix(m1.getSuffix());
                conversionObject.setTitle(m1.getTitle());
                conversionObject.setUnit(m1.getUnit());
                return conversionObject;
            }).collect(Collectors.toList());
            resultList.addAll(list03);
        }
        return resultList;

    }

    /**
     * @param propertyVOList 扁片化的图表和key的list
     * @param mapGraph       用于分类聚合 图表> 图表下的属性和值
     * @param dataDTO        当前的分组下的 value
     */
    private void dataConversionMap(List<DictDeviceGraphAndPropertyVO> propertyVOList, Map<String, List<AttributesPropertiesGraphUnderVo>> mapGraph, DataDTO dataDTO) {
        String currentKey = dataDTO.getKey();
        String currentValue = dataDTO.getValue();
        DictDeviceGraphAndPropertyVO chartInformationVo = getChartNameAndKey(propertyVOList, currentKey);
        if (chartInformationVo != null) {
            String chartName = chartInformationVo.getChartName();
            AttributesPropertiesGraphUnderVo currentObject = new AttributesPropertiesGraphUnderVo(chartInformationVo, currentValue);
            List<AttributesPropertiesGraphUnderVo> graphUnderVos = mapGraph.get(chartName);
            if (CollectionUtils.isNotEmpty(graphUnderVos)) {
                graphUnderVos.add(currentObject);
                mapGraph.put(chartName, graphUnderVos);
            } else {
                List<AttributesPropertiesGraphUnderVo> currentList = new ArrayList<>();
                currentList.add(currentObject);
                mapGraph.put(chartName, currentList);
            }
        }

    }

    /**
     * 获取当前的图表下的属性
     *
     * @param propertyVOList 扁平化的 图表-属性 集合
     * @param currentKey     当前的key
     * @return
     */
    private DictDeviceGraphAndPropertyVO getChartNameAndKey(List<DictDeviceGraphAndPropertyVO> propertyVOList, String currentKey) {
        DictDeviceGraphAndPropertyVO chartInformationVo = propertyVOList.stream().filter(m1 -> m1.getName().equals(currentKey)).findFirst().orElse(null);
//        if (chartInformationVo == null) {
//            return new DictDeviceGraphAndPropertyVO();
//        }
        return chartInformationVo;
    }


    /**
     * 填充数据， 合并参数
     *
     * @param componentDataDTOList 当前的部件 和部件下的属性与值的接口
     * @param mapGraph             map 图表--属性值 ; 这个是配置的 还是从当前的componentDataDTOList过滤提取出来的；
     * @param propertyVOList       图表扁平化集合数据
     * @return
     */
    private List<ComponentDataDTO> conertMapToList(List<ComponentDataDTO> componentDataDTOList, Map<String, List<AttributesPropertiesGraphUnderVo>> mapGraph, List<DictDeviceGraphAndPropertyVO> propertyVOList) {
        List<ComponentDataDTO> resultList = new ArrayList<>();

        HashMap<String, String> localSimpleLockMap = new HashMap<>();

        for (ComponentDataDTO dto : componentDataDTOList) {
            List<DataDTO> dataDTOList = dto.getData();

            List<DataDTO> processedList = new ArrayList<>();

            for (DataDTO d2 : dataDTOList) {
                DictDeviceGraphAndPropertyVO propertyVO = getChartNameAndKey(propertyVOList, d2.getKey());
                if (propertyVO == null) {
                    processedList.add(d2);
                } else {
                    String chartName = propertyVO.getChartName();
                    mapGraph.get(chartName);
                    DataDTO dataDTO = mergeField(d2, mapGraph, propertyVOList, localSimpleLockMap);
                    if (dataDTO != null) {
                        processedList.add(dataDTO);
                    }
                }
            }
            dto.setData(processedList);
            resultList.add(dto);

        }
        return resultList;

    }

    /**
     * @param d2                 当前的设备的key
     * @param mapGraph           图表-- 属性 value
     * @param propertyVOList     图表属性扁平化list
     * @param localSimpleLockMap 用于多个key合并后，避免重复合并
     * @return 返回null 跳出返回，说明已经合并过
     */
    private DataDTO mergeField(DataDTO d2, Map<String, List<AttributesPropertiesGraphUnderVo>> mapGraph, List<DictDeviceGraphAndPropertyVO> propertyVOList, HashMap<String, String> localSimpleLockMap) {
        DictDeviceGraphAndPropertyVO v3 = getChartNameAndKey(propertyVOList, d2.getKey());
        if (v3 == null) {
            return null;
        }
        String lockKey = localSimpleLockMap.get(v3.getChartName());
        if (StringUtils.isNotEmpty(lockKey)) {
            return null;
        }
        List<AttributesPropertiesGraphUnderVo> graphUnderVoList = mapGraph.get(v3.getChartName());
        if (CollectionUtils.isEmpty(graphUnderVoList)) {
            return null;
        }
        localSimpleLockMap.put(v3.getChartName(), v3.getChartName());
        d2.setValue(constructStringReturn(graphUnderVoList));
        d2.setKey(d2.getKey());
        d2.setTableName(v3.getChartName());
        return d2;
    }

    private String constructStringReturn(List<AttributesPropertiesGraphUnderVo> graphUnderVoList) {
        StringBuffer valueStr = new StringBuffer();
        for (int i = 0; i < graphUnderVoList.size(); i++) {
            AttributesPropertiesGraphUnderVo currentObject = graphUnderVoList.get(i);
            String value = currentObject.getValue();
            String separate = currentObject.getSuffix();
            valueStr.append(value);
            if (i != graphUnderVoList.size() - 1) {
                valueStr.append(separate);
            }
        }
        return valueStr.toString();

    }


}
