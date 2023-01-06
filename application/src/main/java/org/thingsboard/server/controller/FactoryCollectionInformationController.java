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
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryCollectionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryEnergySvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.onlie.DeviceStatusNumVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.ChartResultVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.current.CurrentUtilitiesVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.top.FactoryEnergyTop;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;

/**
 * @Project Name: long-win-iot
 * @File Name: FactoryEnergyController
 * @Date: 2023/1/4 11:24
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Slf4j
@Api(value = "工厂看板-采集信息", tags = {"工厂看板-采集信息"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/factoryCollectionInformation")
public class FactoryCollectionInformationController extends BaseController {
    @Autowired
    private FactoryCollectionInformationSvc factoryCollectionInformationSvc;


    @ApiOperation("查询设备数量-在线率")
    @ApiImplicitParam(name = "factoryId", value = "工厂id", dataType = "string", paramType = "query")
    @GetMapping("/queryDeviceStatusNum")
    @ResponseBody
    public DeviceStatusNumVo queryDeviceStatusNum( @RequestParam(required = false,name = "factoryId") String factoryId) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            return factoryCollectionInformationSvc.queryDeviceStatusNum(tenantId, factoryId);
        } catch (Exception e) {
            log.error("[工厂看板-采集信息-设备在线率].queryCurrentEnergy方法异常入参:{}", factoryId);
            log.error("[工厂看板-采集信息-设备在线率].queryCurrentEnergy方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


}
