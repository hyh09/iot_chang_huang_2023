package org.thingsboard.server.dao.attribute;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;
import org.thingsboard.server.common.data.vo.device.RunningStateVo;
import org.thingsboard.server.dao.kanban.vo.inside.ComponentDataDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: AttributeCullingSvc
 * @Date: 2022/12/21 11:12
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
public interface AttributeCullingSvc {

    List<RunningStateVo> queryKeyToSwitch(List<RunningStateVo> resultList, TenantId tenantId, UUID deviceId, boolean isFactoryUser) throws ThingsboardException;

    Map toMakeToMap(Map<String, List<DictDeviceDataVo>> map, TenantId tenantId, UUID deviceId, boolean isFactoryUser);

    /**
     * 处理返回的flg 开关的属性
     *
     * @param componentDataDTOList
     * @param tenantId
     * @param deviceId
     * @param isFactoryUser
     * @return
     */
    List<ComponentDataDTO> componentData(List<ComponentDataDTO> componentDataDTOList, TenantId tenantId, UUID deviceId, boolean isFactoryUser);
}
