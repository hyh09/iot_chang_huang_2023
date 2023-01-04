package org.thingsboard.server.dao.board.factoryBoard.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.thingsboard.server.dao.board.factoryBoard.vo.current.CurrentUtilitiesVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.current.EnergyUnitVo;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;

import java.util.Map;

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
}
