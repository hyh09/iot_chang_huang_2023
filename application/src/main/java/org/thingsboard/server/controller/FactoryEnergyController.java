package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.dao.board.factoryBoard.svc.FactoryEnergySvc;
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
@Api(value = "工厂看板-能耗信息", tags = {"工厂看板-能耗信息"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/factoryEnergy")
public class FactoryEnergyController extends BaseController {
    @Autowired
    private FactoryEnergySvc factoryEnergySvc;


    @GetMapping("/queryCurrentEnergy")
    @ResponseBody
    public CurrentUtilitiesVo queryCurrentEnergy(QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            if (queryTsKvVo.getStartTime() == null) {
                queryTsKvVo.setStartTime(CommonUtils.getZero());
                queryTsKvVo.setEndTime(CommonUtils.getNowTime());
            }
            CurrentUtilitiesVo currentUtilitiesVo = factoryEnergySvc.queryCurrentEnergy(queryTsKvVo, tenantId);
            return currentUtilitiesVo;
        } catch (Exception e) {
            log.error("[工厂看板-能耗信息].queryCurrentEnergy方法异常入参:{}", queryTsKvVo);
            log.error("[工厂看板-能耗信息].queryCurrentEnergy方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


    @GetMapping("/queryCurrentTop")
    @ResponseBody
    public List<FactoryEnergyTop> queryCurrentTop(QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            if (queryTsKvVo.getStartTime() == null) {
                queryTsKvVo.setStartTime(CommonUtils.getZero());
                queryTsKvVo.setEndTime(CommonUtils.getNowTime());
            }
            List<FactoryEnergyTop> factoryEnergyTopList = factoryEnergySvc.queryCurrentTop(queryTsKvVo, tenantId);
            return factoryEnergyTopList;
        } catch (Exception e) {
            log.error("[工厂看板-能耗信息].queryCurrentTop方法异常入参:{}", queryTsKvVo);
            log.error("[工厂看板-能耗信息].queryCurrentTop方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


    @GetMapping("/queryTrendChart")
    @ResponseBody
    public ChartResultVo queryTrendChart(@RequestParam(required = true, name = "dimension") String dimension,
                                         QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
//            if (queryTsKvVo.getStartTime() == null) {
//                queryTsKvVo.setStartTime(CommonUtils.getZero());
//                queryTsKvVo.setEndTime(CommonUtils.getNowTime());
//            }
            queryTsKvVo.setTenantId(tenantId.getId());
            ChartDateEnums chartDateEnums = ChartDateEnums.valueOf(dimension);
            ChartResultVo vo = factoryEnergySvc.queryTrendChart(queryTsKvVo, chartDateEnums);
            return vo;
        } catch (Exception e) {
            log.error("[工厂看板-能耗信息].queryCurrentTop方法异常入参:{}", queryTsKvVo);
            log.error("[工厂看板-能耗信息].queryCurrentTop方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


}
