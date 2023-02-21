package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.attribute.AttributeCullingSvc;
import org.thingsboard.server.dao.kanban.service.svc.KanbanDeviceOneOutSvc;
import org.thingsboard.server.dao.kanban.vo.KanbanDeviceVo;
import org.thingsboard.server.dao.kanban.vo.inside.ComponentDataDTO;
import org.thingsboard.server.dao.kanban.vo.inside.DataDTO;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: KanbanDeviceOneOutController
 * @Date: 2022/11/1 14:11
 * @author: wb04
 * 业务中文描述: 第三方接口看板
 * Copyright (c) 2022,All Rights Reserved.
 */
@Api(value = "第三方看板接口 获取设备的信息看板", tags = {"第三方看板接口 获取设备的信息看板"})
@RestController
@TbCoreComponent
@RequestMapping("/api/three/kanban/")
public class KanbanDeviceOneOutController extends BaseController {

    @Autowired
    private KanbanDeviceOneOutSvc kanbanDeviceOneOutSvc;
    @Autowired
    private AttributeCullingSvc attributeCullingSvc;

    @ApiOperation(value = "获取设备的信息看板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", required = true, paramType = "form", value = "设备id", example = "ac0297b1-5656-11ec-a240-955d7c1497e4"),
    })
    @GetMapping(value = "/integratedDeviceInterface")
    public KanbanDeviceVo integratedDeviceInterface(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
        KanbanDeviceVo kanbanDeviceVo = kanbanDeviceOneOutSvc.integratedDeviceInterface(getTenantId(), deviceId);
        if (kanbanDeviceVo != null && CollectionUtils.isNotEmpty(kanbanDeviceVo.getComponentData())) {
            List<ComponentDataDTO> componentDataDTOList = kanbanDeviceVo.getComponentData();
            List<ComponentDataDTO> dataDTOList = attributeCullingSvc.componentData(componentDataDTOList, getTenantId(), deviceId, isFactoryUser());
            exchangeValue(dataDTOList);
            kanbanDeviceVo.setComponentData(dataDTOList);

        }
        return kanbanDeviceVo;
    }

    /**
     * @param dataDTOList
     * @return
     */
    private List<ComponentDataDTO> exchangeValue(List<ComponentDataDTO> dataDTOList) {
        List<ComponentDataDTO> resultSet = new ArrayList<>();
        dataDTOList.forEach(m1 -> {
            List<DataDTO> dataDTOList1 = m1.getData();
            dataDTOList1.forEach(m2 -> {
                String tableName = m2.getTableName();
                if (StringUtils.isNotEmpty(tableName)) {
                    m2.setKey(tableName);
                }
            });
            m1.setData(dataDTOList1);
            resultSet.add(m1);
        });
        return resultSet;
    }


}
