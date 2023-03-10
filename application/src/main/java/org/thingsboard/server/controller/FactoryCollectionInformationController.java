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
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.collectionVolume.HourlyTrendGraphOfCollectionVolumeVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.onlie.DeviceStatusNumVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.collection.piechart.RatePieChartVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.ChartDataVo;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
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
    public DeviceStatusNumVo queryDeviceStatusNum(@RequestParam(required = false, name = "factoryId") String factoryId) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            FactoryDeviceQuery factoryDeviceQuery = new FactoryDeviceQuery();
            factoryDeviceQuery.setFactoryId(factoryId);
            return factoryCollectionInformationSvc.queryDeviceStatusNum(tenantId, factoryDeviceQuery);
        } catch (Exception e) {
            log.error("[工厂看板-采集信息-设备在线率].queryCurrentEnergy方法异常入参:{}", factoryId);
            log.error("[工厂看板-采集信息-设备在线率].queryCurrentEnergy方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


    @ApiOperation("采集量-每小时")
    @ApiImplicitParam(name = "factoryId", value = "工厂id", dataType = "string", paramType = "query")
    @GetMapping("/queryCollectionVolumeByHourly")
    @ResponseBody
    public HourlyTrendGraphOfCollectionVolumeVo queryCollectionVolumeByHourly(@RequestParam(required = false, name = "factoryId") String factoryId) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            FactoryDeviceQuery factoryDeviceQuery = new FactoryDeviceQuery();
            factoryDeviceQuery.setFactoryId(factoryId);
            return factoryCollectionInformationSvc.queryCollectionVolumeByHourly(tenantId, factoryDeviceQuery);
        } catch (Exception e) {
            log.error("[工厂看板-采集信息-【采集量-每小时】]queryCollectionVolumeByHourly方法异常入参:{}", factoryId);
            log.error("[工厂看板-采集信息-【采集量-每小时【].queryCollectionVolumeByHourly方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


    /**
     * 作者:wb04
     * 日期:2023-03-02
     * 接口描述:  运行率 select *  from  trep_day_sta_detail
     * #############################################
     * 问题：年的返回的是负数为甚？
     * 修复时间: 2023-03-03  Duration between(Temporal startInclusive, Temporal endExclusive); 自己传反了；
     *
     * @param factoryId
     * @param dimension
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("开机率运行率趋势图-月和年的维度")
    @ApiImplicitParam(name = "factoryId", value = "工厂id", dataType = "string", paramType = "query")
    @GetMapping("/queryTrendChartOfOperatingRate")
    @ResponseBody
    public List<ChartDataVo> queryTrendChartOfOperatingRate(@RequestParam(name = "factoryId") String factoryId,
                                                            @RequestParam(name = "dimension") String dimension) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            FactoryDeviceQuery factoryDeviceQuery = new FactoryDeviceQuery();
            factoryDeviceQuery.setFactoryId(factoryId);
            ChartDateEnums chartDateEnums = ChartDateEnums.valueOf(dimension);

            return factoryCollectionInformationSvc.queryTrendChartOfOperatingRate(tenantId, factoryDeviceQuery, chartDateEnums);
        } catch (Exception e) {
            log.error("[工厂看板-采集信息-【开机率趋势图-月和年的维度】]queryTrendChartOfOperatingRate方法异常入参:{}", factoryId);
            log.error("[工厂看板-采集信息-【开机率趋势图-月和年的维度].queryTrendChartOfOperatingRate方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }

    /**
     * 修改去掉返回后的 % 的;
     * 饼状图的数据： trep_day_sta_detail
     *
     * @param factoryId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("开机率-今日昨日的运行率百分比")
    @ApiImplicitParam(name = "factoryId", value = "工厂id", dataType = "string", paramType = "query")
    @GetMapping("/queryPieChart")
    @ResponseBody
    public RatePieChartVo queryPieChart(@RequestParam(name = "factoryId") String factoryId
    ) throws ThingsboardException {
        try {
            TenantId tenantId = getTenantId();
            FactoryDeviceQuery factoryDeviceQuery = new FactoryDeviceQuery();
            factoryDeviceQuery.setFactoryId(factoryId);
            return factoryCollectionInformationSvc.queryPieChart(tenantId, factoryDeviceQuery);
        } catch (Exception e) {
            log.error("[工厂看板-采集信息-【饼状图接口异常】]queryPieChart方法异常入参:{}", factoryId);
            log.error("[工厂看板-采集信息-【饼状图接口异常].queryPieChart方法异常:{}", e);
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }


}
