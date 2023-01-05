package org.thingsboard.server.dao.board.factoryBoard.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.effciency.EfficiencyEntityInfo;
import org.thingsboard.server.common.data.effciency.total.EfficiencyTotalValue;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.key.KeyNameEnums;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryEnergySvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.ChartResultVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.current.CurrentUtilitiesVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.current.EnergyUnitVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.top.FactoryEnergyTop;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.sql.role.dao.EffciencyAnalysisRepository;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryEnergyImpl
 * @Date: 2023/1/4 9:32
 * @author: wb04
 * 业务中文描述: 工厂看板——能耗信息接口
 * Copyright (c) 2023,All Rights Reserved.
 */
@Service
public class FactoryEnergyImpl implements FactoryEnergySvc {

    @Autowired
    private EfficiencyStatisticsSvc efficiencyStatisticsSvc;
    @Autowired
    private DeviceDictPropertiesSvc deviceDictPropertiesSvc;
    @Autowired
    private EffciencyAnalysisRepository effciencyAnalysisRepository;

    /**
     * 查询
     *
     * @param queryTsKvVo
     * @return
     */
    @Override
    public CurrentUtilitiesVo queryCurrentEnergy(QueryTsKvVo queryTsKvVo, TenantId tenantId) throws JsonProcessingException {
        PageLink pageLink = new PageLink(2, 0);
        PageDataAndTotalValue<EfficiencyEntityInfo> pageDataAndTotalValue = efficiencyStatisticsSvc.queryEntityByKeysNew(queryTsKvVo, tenantId, pageLink);
        EfficiencyTotalValue efficiencyTotalValue = JacksonUtil.convertValue(pageDataAndTotalValue.getTotalValue(), EfficiencyTotalValue.class);
        Map<String, DictDeviceGroupPropertyVO> map = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        CurrentUtilitiesVo currentUtilitiesVo = new CurrentUtilitiesVo();
        currentUtilitiesVo.setWater(getEnergyUnitVo(efficiencyTotalValue.getTotalWaterConsumption(), map.get(KeyNameEnums.water.getName())));
        currentUtilitiesVo.setElectricity(getEnergyUnitVo(efficiencyTotalValue.getTotalElectricConsumption(), map.get(KeyNameEnums.electric.getName())));
        currentUtilitiesVo.setGas(getEnergyUnitVo(efficiencyTotalValue.getTotalGasConsumption(), map.get(KeyNameEnums.gas.getName())));
        return currentUtilitiesVo;
    }

    /**
     * 1 先按照水 --- 电 --- 气 排序
     * 2. 排序之后截取前 15条数据返回
     *
     * @param queryTsKvVo
     * @param tenantId
     * @return
     */
    @Override
    public List<FactoryEnergyTop> queryCurrentTop(QueryTsKvVo queryTsKvVo, TenantId tenantId) {
        List<FactoryEnergyTop> resultList = new ArrayList<>();
        List<EnergyEffciencyNewEntity> entityList = effciencyAnalysisRepository.queryEnergy(queryTsKvVo);
        if (CollectionUtils.isEmpty(entityList)) {
            return resultList;
        }

        List<EnergyEffciencyNewEntity> waterList = entityList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                .sorted((s1, s2) -> new BigDecimal(s2.getWaterAddedValue()).compareTo(new BigDecimal(s1.getWaterAddedValue())))
                .limit(5)
                .collect(Collectors.toList());
        resultList.addAll(setTopDataConversion(waterList));

        List<EnergyEffciencyNewEntity> energyEffciencyNewEntities = entityList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                .sorted((s1, s2) -> new BigDecimal(s2.getWaterAddedValue()).compareTo(new BigDecimal(s1.getWaterAddedValue())))
                .limit(5)
                .collect(Collectors.toList());
        resultList.addAll(setTopDataConversion(energyEffciencyNewEntities));

        List<EnergyEffciencyNewEntity> gasList = entityList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1.getWaterAddedValue()))
                .sorted((s1, s2) -> new BigDecimal(s2.getGasAddedValue()).compareTo(new BigDecimal(s1.getGasAddedValue())))
                .limit(5)
                .collect(Collectors.toList());
        resultList.addAll(setTopDataConversion(gasList));
        List<FactoryEnergyTop> deduplicationResut = resultList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FactoryEnergyTop::getDeviceId))), ArrayList::new)
        );
        return  deduplicationResut;
    }


    @Override
    public ChartResultVo queryTrendChart(QueryTsKvVo queryTsKvVo, ChartDateEnums dateEnums) {
        return null;
    }

    private EnergyUnitVo getEnergyUnitVo(String value, DictDeviceGroupPropertyVO deviceGroupPropertyVO) {
        EnergyUnitVo energyUnitVo = new EnergyUnitVo();
        energyUnitVo.setActualValue(value);
        if (deviceGroupPropertyVO != null) {
            energyUnitVo.setKey(deviceGroupPropertyVO.getName());
            energyUnitVo.setName(deviceGroupPropertyVO.getTitle());
            energyUnitVo.setUnit(deviceGroupPropertyVO.getUnit());
        }
        return energyUnitVo;
    }


    private List<FactoryEnergyTop> setTopDataConversion(List<EnergyEffciencyNewEntity> energyEffciencyNewEntities) {
        if (CollectionUtils.isNotEmpty(energyEffciencyNewEntities)) {
            return energyEffciencyNewEntities.stream().map(m1 -> {
                FactoryEnergyTop top = new FactoryEnergyTop();
                top.setDeviceId(m1.getEntityId());
                top.setDeviceName(m1.getDeviceName());
                top.setElectricity(m1.getElectricAddedValue());
                top.setWater(m1.getWaterAddedValue());
                top.setGas(m1.getGasAddedValue());
                return top;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
