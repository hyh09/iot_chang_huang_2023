package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.kanban.service.svc.KanbanDeviceOneOutSvc;
import org.thingsboard.server.dao.kanban.vo.KanbanDeviceVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

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

    @Autowired  private KanbanDeviceOneOutSvc kanbanDeviceOneOutSvc;

    @ApiOperation(value = "获取设备的信息看板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id"),
    })
    @GetMapping(value = "/integratedDeviceInterface")
    public KanbanDeviceVo integratedDeviceInterface(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
        return kanbanDeviceOneOutSvc.integratedDeviceInterface(getTenantId(),deviceId);
    }
}
