package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryCollectionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryProductionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.onlie.DeviceStatusNumVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.WorkshopAndRunRateVo;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.UUID;

/**
 * @Project Name: long-win-iot
 * @File Name: FactoryEnergyController
 * @Date: 2023/1/4 11:24
 * @author: wb04
 * 业务中文描述: 工厂看板-生产信息
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@Api(value = "工厂看板-生产信息", tags = {"工厂看板-生产信息"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/factoryProduction")
public class FactoryProductionInformationController extends BaseController {
    @Autowired
    private FactoryProductionInformationSvc factoryProductionInformationSvc;


    @ApiOperation("查询车间的信息")
    @ApiImplicitParam(name = "factoryId", value = "工厂id", dataType = "string", paramType = "query")
    @GetMapping("/queryWorkshopAndRunRate")
    @ResponseBody
    public List<WorkshopAndRunRateVo> queryDeviceStatusNum(@RequestParam(required = false, name = "factoryId") String factoryId) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            return factoryProductionInformationSvc.queryWorkshopAndRunRate(tenantId, UUID.fromString(factoryId));
        } catch (Exception e) {
            log.error("[工厂看板-查询车间的信息].queryDeviceStatusNum方法异常入参:{}", factoryId);
            log.error("[工厂看板-查询车间的信息].queryDeviceStatusNum方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


}
