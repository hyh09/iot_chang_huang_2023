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
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryProductionInformationSvc;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.*;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.vo.CurrentOrdersInProduction07Vo;
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

    @ApiOperation("订单生产情况 ")
    @GetMapping("/getOrderProduction")
    @ResponseBody
    public OrderProductionVo getOrderProduction() throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            return factoryProductionInformationSvc.getOrderProduction();
        } catch (Exception e) {
            log.error("[工厂看板-订单生产情况 ].getOrderProduction方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }

    @ApiOperation("订单的 完成率  和 成品率 ")
    @GetMapping("/getOrderCompletionRateAndYieldRate")
    @ResponseBody
    public OrderCompletionRateAndYieldRateVo getOrderCompletionRateAndYieldRate() throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            return factoryProductionInformationSvc.getOrderCompletionRateAndYieldRate();
        } catch (Exception e) {
            log.error("[工厂看板-订单的 完成率  和 成品率 情况 ].getOrderCompletionRateAndYieldRate方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


    @ApiOperation("订单完成情况 ")
    @GetMapping("/queryListOrderFulfillmentVo")
    @ResponseBody
    public  List<OrderFulfillmentVo>  queryListOrderFulfillmentVo() throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            return factoryProductionInformationSvc.queryListOrderFulfillmentVo();
        } catch (Exception e) {
            log.error("[订单完成情况 ].queryListOrderFulfillmentVo方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


    @ApiOperation("工序实时产量 ")
    @GetMapping("/queryListProcessRealTimeOutputVo")
    @ResponseBody
    public  List<ProcessRealTimeOutputVo>  queryListProcessRealTimeOutputVo() throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            return factoryProductionInformationSvc.queryListProcessRealTimeOutputVo();
        } catch (Exception e) {
            log.error("[工序实时产量 ].queryListProcessRealTimeOutputVo方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }

    @ApiOperation("当前在产订单 ")
    @GetMapping("/queryCurrentOrdersInProduction")
    @ResponseBody
    public  List<CurrentOrdersInProductionDto>  queryCurrentOrdersInProduction() throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            return factoryProductionInformationSvc.queryCurrentOrdersInProductionDto();
        } catch (Exception e) {
            log.error("[工序实时产量 ].queryListProcessRealTimeOutputVo方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }

    @ApiOperation("当前在产订单 ")
    @GetMapping("/queryCurrentOrdersInProduction07")
    @ResponseBody
    public  List<CurrentOrdersInProduction07Vo>  queryCurrentOrdersInProduction07Dto() throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            return factoryProductionInformationSvc.queryCurrentOrdersInProduction07Dto();
        } catch (Exception e) {
            log.error("[工序实时产量 ].queryListProcessRealTimeOutputVo方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }



}
