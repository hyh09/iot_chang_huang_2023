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
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.common.data.vo.home.ResultHomeCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.tskv.ConsumptionTodayVo;
import org.thingsboard.server.common.data.vo.tskv.TrendVo;
import org.thingsboard.server.common.data.vo.tskv.consumption.ConsumptionVo;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 看板的控制层
 * @author: HU.YUNHUI
 * @create: 2021-12-07 17:19
 **/
@Api(value = "看板的关于能耗和产能的相关接口", tags = {"看板的关于能耗和产能的相关接口"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/board/")
public class BulletinBoardController extends BaseController{

    @Autowired private BulletinBoardSvc bulletinBoardSvc;


    @ApiOperation(value = "【三个时期的总产量】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId",value = "工厂标识{如果传表示是工厂下的看板}",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "workshopId",value = "工厂的",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "productionLineId",value = "工厂的",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "deviceId",value = "工厂的",dataType = "string",paramType = "query")

    })
    @RequestMapping(value = "/threePeriodsValue", method = RequestMethod.GET)
    @ResponseBody
    public  ResultHomeCapAppVo  threePeriodsValue(@RequestParam(required = false ,value = "factoryId")  String factoryId,
                                                  @RequestParam(required = false ,value = "workshopId")  String workshopId,
                                                  @RequestParam(required = false ,value = "productionLineId")  String productionLineId,
                                                  @RequestParam(required = false ,value = "deviceId")  String deviceId
                                                  )
    {
        ResultHomeCapAppVo result = new ResultHomeCapAppVo();

        try {
            TsSqlDayVo tsSqlDayVo =    TsSqlDayVo.constructionTsSqlDayVo(factoryId,workshopId,productionLineId,deviceId);
            tsSqlDayVo.setTenantId(getTenantId().getId());
            return efficiencyStatisticsSvc.queryThreePeriodsCapacity(tsSqlDayVo);
//            result.setTodayValue(getValueByTime(factoryId,workshopId,productionLineId,deviceId, CommonUtils.getZero(), CommonUtils.getNowTime()));
//            result.setYesterdayValue(getValueByTime(factoryId,workshopId, productionLineId,deviceId,CommonUtils.getYesterdayZero(), CommonUtils.getYesterdayLastTime()));
//            result.setHistory(bulletinBoardSvc.getHistoryCapValue(factoryId,getTenantId().getId()));
//            return result;
        }catch (Exception e)
        {
            e.printStackTrace();

        }
        return  result;
    }




    @ApiOperation(value = "【看板设备今日耗能量】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId",value = "工厂标识{如果传表示是工厂下的看板}",dataType = "string",paramType = "query")
    })
    @RequestMapping(value = "/energyConsumptionToday", method = RequestMethod.GET)
    @ResponseBody
    public ConsumptionTodayVo energyConsumptionToday(@RequestParam(required = false ,value = "factoryId")  String factoryId) throws ThingsboardException {
        try {


        QueryTsKvVo  vo =  new  QueryTsKvVo();
        vo.setStartTime(CommonUtils.getZero());
        vo.setEndTime(CommonUtils.getNowTime());
        if(StringUtils.isNotEmpty(factoryId))
        {
            vo.setFactoryId(UUID.fromString(factoryId));
        }
        vo.setTenantId(getTenantId().getId());
       return bulletinBoardSvc.energyConsumptionToday(vo,getTenantId().getId());
        }catch (Exception  e)
        {
            log.error("打印看板设备今日耗能量:{}",e);
            return  new ConsumptionTodayVo();
        }
    }


    @ApiOperation(value = "【水电气的能耗总量】 分页信息不需要传接口内部复用了; 如果endTime不传就是默认今天时间")
    @RequestMapping(value = "/totalEnergyConsumption", method = RequestMethod.POST)
    @ResponseBody
    public List<ConsumptionVo> totalEnergyConsumption(
          @RequestBody  QueryTsKvVo  vo
    ) throws ThingsboardException {
        try {
            if(vo.getEndTime() == null )
            {
                vo.setStartTime(CommonUtils.getZero());
                vo.setEndTime(CommonUtils.getNowTime());
            }
            if(vo.getStartTime() == null )
            {
                vo.setStartTime(CommonUtils.getHistoryPointTime());
            }
            return bulletinBoardSvc.totalEnergyConsumption(vo, getTenantId());
        }catch (Exception e)
        {
            log.error("看板接口：【水电气的能耗总量】异常信息{}",e);
            throw  new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }

    @ApiOperation(value = "能耗趋势图(当前所选时间范围内最大30天)")
    @RequestMapping(value = "/energyConsumptionTrend")
    public TrendVo energyConsumptionTrend(@RequestBody TrendParameterVo vo) throws ThingsboardException {
        vo.setTenantId(getTenantId().getId());
        return bulletinBoardSvc.energyConsumptionTrend(vo);

    }





    private  String getValueByTime(String factoryId,String workshopId,String productionLineId, String deviceId,long startTime, long EndTime) throws ThingsboardException {
        QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setTenantId(getTenantId().getId());
        if(StringUtils.isNotEmpty(factoryId)) {
            queryTsKvVo.setFactoryId(UUID.fromString(factoryId));
        }
        if(StringUtils.isNotEmpty(workshopId)) {
            queryTsKvVo.setWorkshopId(UUID.fromString(workshopId));
        }
        if(StringUtils.isNotEmpty(productionLineId)) {
            queryTsKvVo.setProductionLineId(UUID.fromString(productionLineId));
        }
        if(StringUtils.isNotEmpty(deviceId)) {
            queryTsKvVo.setDeviceId(UUID.fromString(deviceId));
        }
        queryTsKvVo.setStartTime(startTime);
        queryTsKvVo.setEndTime(EndTime);
        queryTsKvVo.setFilterFirstFactory(false);
        ResultCapAppVo resultCapAppVo =   efficiencyStatisticsSvc.queryCapApp(queryTsKvVo,getTenantId());
        if(resultCapAppVo != null)
        {
            return  resultCapAppVo.getTotalValue();
        }
        return  "0";
    }



}
