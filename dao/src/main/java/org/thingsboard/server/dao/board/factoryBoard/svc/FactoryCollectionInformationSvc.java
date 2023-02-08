package org.thingsboard.server.dao.board.factoryBoard.svc;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.collectionVolume.HourlyTrendGraphOfCollectionVolumeVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.onlie.DeviceStatusNumVo;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;

/**
 * @Project Name: thingsboard
 * @File Name: FactoryCollectionInformationSvc
 * @Date: 2023/1/4 9:30
 * @author: wb04
 * 业务中文描述: 工厂看板——设备采集信息相关接口
 * Copyright (c) 2023,All Rights Reserved.
 */
public interface FactoryCollectionInformationSvc {


    /**
     * 查询 设备数（设备总数，在线，离线）在线率%
     */
    DeviceStatusNumVo queryDeviceStatusNum(TenantId tenantId,   FactoryDeviceQuery factoryDeviceQuery);

    HourlyTrendGraphOfCollectionVolumeVo  queryCollectionVolumeByHourly(TenantId tenantId,   FactoryDeviceQuery factoryDeviceQuery);

}
