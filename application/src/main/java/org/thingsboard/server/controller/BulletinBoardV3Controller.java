package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.BoardV3DeviceDictionaryVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.common.data.vo.bodrd.DashboardV3Vo;
import org.thingsboard.server.common.data.vo.tskv.ConsumptionTodayVo;
import org.thingsboard.server.common.data.vo.tskv.TrendChart02Vo;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;
import org.thingsboard.server.dao.board.BulletinV3BoardVsSvc;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 长胜3期需求
 * @author: HU.YUNHUI
 * @create: 2022-03-07 11:16
 **/
@Api(value = "看板3期", tags = {"看板的关于能耗和产能的相关接口"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/board/v3/")
public class BulletinBoardV3Controller   extends BaseController {

    @Autowired private BulletinV3BoardVsSvc bulletinV3BoardVsSvc;
    @Autowired private BulletinBoardSvc bulletinBoardSvc;

    /**
     * 查询设备字典
     * @param factoryId 工厂id
     * @param workshopId 车间id
     * @param productionLineId 产线id
     * @param deviceId 设备id
     * @return
     */
    @RequestMapping("/queryDeviceDictionary")
    public List<BoardV3DeviceDitEntity>  queryDeviceDictionary(@RequestParam(required = false ,value = "factoryId")  String factoryId,
                                                               @RequestParam(required = false ,value = "workshopId")  String workshopId,
                                                               @RequestParam(required = false ,value = "productionLineId")  String productionLineId,
                                                               @RequestParam(required = false ,value = "deviceId")  String deviceId)
    {

        try {
            TsSqlDayVo tsSqlDayVo =    TsSqlDayVo.constructionTsSqlDayVo(factoryId,workshopId,productionLineId,deviceId);
            tsSqlDayVo.setTenantId(getTenantId().getId());
            return bulletinV3BoardVsSvc.queryDeviceDictionaryByEntityVo(tsSqlDayVo);

        } catch (ThingsboardException e) {
            log.error("【看板3期】查询设备字典接口异常：{}",e);
            e.printStackTrace();
            return  new ArrayList<>();
        }
    }


    /**
     *
     * @param deviceDictionaryVo
     * @return
     */
    @PostMapping("/queryDashboardValue")
    public  List<DashboardV3Vo>  queryDashboardValue(@RequestBody BoardV3DeviceDictionaryVo  deviceDictionaryVo)
    {

        try {
            return bulletinV3BoardVsSvc.queryDashboardValue(deviceDictionaryVo);

        } catch (Exception e) {
            log.error("【看板3期】queryDashboardValue接口异常：{}",e);
            e.printStackTrace();
            return  new ArrayList<>();
        }
    }




    @ApiOperation(value = "【看板设备今日耗能量】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId",value = "工厂标识{如果传表示是工厂下的看板}",dataType = "string",paramType = "query")
    })
    @RequestMapping(value = "/energyConsumptionToday", method = RequestMethod.GET)
    @ResponseBody
    public ConsumptionTodayVo energyConsumptionToday(@RequestParam(required = false ,value = "dictDeviceId")  String dictDeviceId) throws ThingsboardException {
        try {
            QueryTsKvVo vo =  new  QueryTsKvVo();
            vo.setStartTime(CommonUtils.getZero());
            vo.setEndTime(CommonUtils.getNowTime());
            if(StringUtils.isNotEmpty(dictDeviceId))
            {
                vo.setDictDeviceId(UUID.fromString(dictDeviceId));
            }
            vo.setTenantId(getTenantId().getId());
            return bulletinBoardSvc.todayUnitEnergy(vo,getTenantId());
        }catch (Exception  e)
        {
            log.error("打印看板设备今日耗能量:{}",e);
            return  new ConsumptionTodayVo();
        }
    }


    /**
     * 维度 1小时
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation(value = "【3.新增设备单位能耗/标准单位能耗趋势图】")
    @RequestMapping(value = "/trendChart ", method = RequestMethod.POST)
    @ResponseBody
    public TrendChart02Vo trendChart(@RequestBody TrendParameterVo vo) throws ThingsboardException {
        try {
            log.info("3.新增设备单位能耗/标准单位能耗趋势图:{}",vo);
           vo.setTenantId(getTenantId().getId());
            return bulletinV3BoardVsSvc.trendChart(vo,getTenantId());
        }catch (Exception  e)
        {
            log.error("新增设备单位能耗/标准单位能耗趋势图:{}",e);
            return  new TrendChart02Vo();
        }
    }














}
