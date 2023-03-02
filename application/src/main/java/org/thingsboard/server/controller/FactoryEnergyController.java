package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.ExpenseDashboardVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.UserEveryYearCostVo;
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


    /**
     * 工厂看板--能耗信息  当前耗电，耗水，耗气 #维度都是当天维度；
     *
     * @param queryTsKvVo
     * @return
     * @throws ThingsboardException
     */
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

    /**
     * 工厂看板的 能耗top
     *
     * @param queryTsKvVo 入参是工厂id
     * @return
     * @throws ThingsboardException
     */
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

    /**
     * 【工厂看板-二期需求】
     * 查询（当月 or 当年)能耗趋势图  和 费用
     *
     * @param dimension
     * @param queryTsKvVo
     * @return
     * @throws ThingsboardException
     */
    @GetMapping("/queryTrendChart")
    @ResponseBody
    public ChartResultVo queryTrendChart(@RequestParam(required = true, name = "dimension") String dimension,
                                         QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
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

    /**
     * 本年度能耗费用趋势图接口
     *
     * @param queryTsKvVo
     * @return
     * @throws ThingsboardException
     */
    @GetMapping("/queryUserEveryYearCost")
    @ResponseBody
    public List<UserEveryYearCostVo> queryUserEveryYearCost(
            QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            queryTsKvVo.setTenantId(tenantId.getId());
            List<UserEveryYearCostVo> vo = factoryEnergySvc.queryUserEveryYearCost(queryTsKvVo, tenantId);
            return vo;
        } catch (Exception e) {
            log.error("[工厂看板-能耗信息].queryUserEveryYearCost方法异常入参:{}", queryTsKvVo);
            log.error("[工厂看板-能耗信息].queryUserEveryYearCost方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


    @ApiOperation("能耗费用的仪表盘接口")
    @GetMapping("/queryExpenseDashboard")
    @ResponseBody
    public ExpenseDashboardVo queryExpenseDashboardVo(
            QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            queryTsKvVo.setTenantId(tenantId.getId());
            ExpenseDashboardVo vo = factoryEnergySvc.queryExpenseDashboard(queryTsKvVo);
            return vo;
        } catch (Exception e) {
            log.error("[工厂看板-能耗信息].queryExpenseDashboard方法异常入参:{}", queryTsKvVo);
            log.error("[工厂看板-能耗信息].queryExpenseDashboard方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


}
