package org.thingsboard.server.dao.board.factoryBoard.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryCollectionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.onlie.DeviceStatusNumVo;
import org.thingsboard.server.dao.hs.entity.vo.DeviceOnlineStatusResult;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.util.decimal.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryCollectionInformationImpl
 * @Date: 2023/1/6 9:54
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@Service
public class FactoryCollectionInformationImpl implements FactoryCollectionInformationSvc {

    private DeviceMonitorService deviceMonitorService;

    public FactoryCollectionInformationImpl(DeviceMonitorService deviceMonitorService) {
        this.deviceMonitorService = deviceMonitorService;
    }

    @Override
    public DeviceStatusNumVo queryDeviceStatusNum(TenantId tenantId, String factoryId) {
        FactoryDeviceQuery factoryDeviceQuery = new FactoryDeviceQuery();
        factoryDeviceQuery.setFactoryId(factoryId);
        DeviceOnlineStatusResult deviceOnlineStatusResult = deviceMonitorService.getDeviceOnlineStatusData(tenantId, factoryDeviceQuery);
        DeviceStatusNumVo deviceStatusNumVo = JacksonUtil.convertValue(deviceOnlineStatusResult, DeviceStatusNumVo.class);
        BigDecimalUtil bigDecimalUtil = new BigDecimalUtil(4, RoundingMode.HALF_UP);//保留4位小数
        BigDecimal deviceAfterResult = bigDecimalUtil.divide(deviceOnlineStatusResult.getOnLineDeviceCount(), deviceOnlineStatusResult.getAllDeviceCount());
        String rate = BigDecimalUtil.INSTANCE.multiply(deviceAfterResult, "100").toPlainString() + "%";
        deviceStatusNumVo.setOnlineRate(rate);
        return deviceStatusNumVo;
    }
}
