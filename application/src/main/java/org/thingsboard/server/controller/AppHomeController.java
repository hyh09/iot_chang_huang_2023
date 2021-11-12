package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.home.ResultHomeCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

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
}
