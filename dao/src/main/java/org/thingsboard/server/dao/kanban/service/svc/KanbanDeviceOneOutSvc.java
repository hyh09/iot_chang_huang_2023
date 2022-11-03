package org.thingsboard.server.dao.kanban.service.svc;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.kanban.vo.KanbanDeviceVo;

import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: KanbanDeviceOneOutSvc
 * @Date: 2022/11/1 13:51
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
public interface KanbanDeviceOneOutSvc {

    KanbanDeviceVo integratedDeviceInterface(TenantId tenantId, UUID deviceId) throws ThingsboardException;
}
