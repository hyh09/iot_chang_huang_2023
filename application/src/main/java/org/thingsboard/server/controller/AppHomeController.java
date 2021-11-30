package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.home.EachMonthStartEndVo;
import org.thingsboard.server.common.data.vo.home.ResultHomeCapAppVo;
import org.thingsboard.server.common.data.vo.home.ResultHomeEnergyAppVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: app端首页接口-效能相关接口
 * @author: HU.YUNHUI
 * @create: 2021-11-12 13:14
 **/
@Api(value = "app端首页接口-效能相关接口", tags = {"app端首页接口-效能相关接口"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/app/home/")
public class AppHomeController extends BaseController{

    @Autowired
    private EfficiencyStatisticsSvc efficiencyStatisticsSvc;

    @ApiOperation(value = "【app首页的产量接口--三个时期的总产量】")
    @RequestMapping(value = "/threePeriodsValue", method = RequestMethod.GET)
    @ResponseBody
    public ResultHomeCapAppVo queryCapacity(@RequestParam("factoryId") UUID factoryId) throws ThingsboardException {
        ResultHomeCapAppVo result = new ResultHomeCapAppVo();

        try {
            result.setTodayValue(getValueByTime(factoryId, CommonUtils.getZero(), CommonUtils.getNowTime()));
            result.setYesterdayValue(getValueByTime(factoryId, CommonUtils.getYesterdayZero(), CommonUtils.getYesterdayLastTime()));
            result.setHistory(getValueByTime(factoryId, CommonUtils.getHistoryPointTime(), CommonUtils.getNowTime()));
            return result;
        }catch (Exception e)
        {
            e.printStackTrace();

        }
        return  result;
    }




    @ApiOperation(value = "【app首页的能耗接口--三个时期的总产量 昨天 今天 历史的 水 电 气能耗】")
    @RequestMapping(value = "/threeEnergyValue", method = RequestMethod.GET)
    @ResponseBody
    public ResultHomeEnergyAppVo threeEnergyValue(@RequestParam("factoryId") UUID factoryId) throws ThingsboardException {
        ResultHomeEnergyAppVo result = new ResultHomeEnergyAppVo();

        try {
            result.setTodayValue(getMapValueByTime(factoryId, CommonUtils.getZero(), CommonUtils.getNowTime()));
            result.setYesterdayValue(getMapValueByTime(factoryId, CommonUtils.getYesterdayZero(), CommonUtils.getYesterdayLastTime()));
            result.setHistory(getMapValueByTime(factoryId, CommonUtils.getHistoryPointTime(), CommonUtils.getNowTime()));
            return result;
        }catch (Exception e)
        {
            e.printStackTrace();

        }
        return  result;
    }


    @ApiOperation(value = "【app首页的能耗接口--6个月的能耗")
    @RequestMapping(value = "/sixMonthsEnergy", method = RequestMethod.GET)
    @ResponseBody
    public List<EachMonthStartEndVo>  sixMonthsEnergy(@RequestParam("factoryId") UUID factoryId) throws ThingsboardException {

        try {
            List<EachMonthStartEndVo>   sixMonths = CommonUtils.getSixMonths();
            sixMonths.forEach(vo ->{
                try {
                    vo.setValue(getMapValueByTime(factoryId,vo.getStartTime(),vo.getEndTime()));
                } catch (ThingsboardException e) {
                    e.printStackTrace();
                }
            });

            return sixMonths;
        }catch (Exception e)
        {
            e.printStackTrace();

        }
        return  null;
    }





    private  String getValueByTime(UUID factoryId,long startTime,long EndTime) throws ThingsboardException {
        QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setFactoryId(factoryId);
        queryTsKvVo.setStartTime(startTime);
        queryTsKvVo.setEndTime(EndTime);
        ResultCapAppVo resultCapAppVo =   efficiencyStatisticsSvc.queryCapApp(queryTsKvVo,getTenantId());
        if(resultCapAppVo != null)
        {
            return  resultCapAppVo.getTotalValue();
        }
        return  "0";
    }




    private Map<String,String> getMapValueByTime(UUID factoryId, long startTime, long EndTime) throws ThingsboardException {
        QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
        queryTsKvVo.setFactoryId(factoryId);
        queryTsKvVo.setStartTime(startTime);
        queryTsKvVo.setEndTime(EndTime);
        ResultEnergyAppVo resultEnergyAppVo =   efficiencyStatisticsSvc.queryEntityByKeys(queryTsKvVo,getTenantId());
        if(resultEnergyAppVo != null)
        {
            return  resultEnergyAppVo.getTotalValue();
        }
        return  new HashMap<>();
    }
}
