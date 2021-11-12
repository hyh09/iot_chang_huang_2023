package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;
import org.thingsboard.server.dao.sql.role.service.EfficiencyStatisticsSvc;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 效能分析(App接口)
 * @author: HU.YUNHUI
 * @create: 2021-11-09 16:16
 **/
@Api(value = "效能分析(App接口)", tags = {"效能分析(App接口)"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/app/efficiency")
public class EfficiencyStatisticsController extends BaseController {

    @Autowired private EfficiencyStatisticsSvc efficiencyStatisticsSvc;


    /**
     *
     */
    @ApiOperation(value = "【app端查询产能接口】")
    @RequestMapping(value = "/queryCapacity", method = RequestMethod.POST)
    @ResponseBody
    public ResultCapAppVo queryCapacity(@RequestBody QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        try {
            if(queryTsKvVo.getEndTime() == null )
            {
                queryTsKvVo.setStartTime(CommonUtils.getZero());
                queryTsKvVo.setEndTime(CommonUtils.getNowTime());
            }
            return efficiencyStatisticsSvc.queryCapApp(queryTsKvVo, getTenantId());
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),e.getMessage());
        }
    }


    /**
     *
     */
    @ApiOperation(value = "【app端查询能耗接口接口】")
    @RequestMapping(value = "/queryEnergy", method = RequestMethod.POST)
    @ResponseBody
    public ResultEnergyAppVo queryEnergy(@RequestBody QueryTsKvVo queryTsKvVo) throws ThingsboardException {
        try{
            if(queryTsKvVo.getEndTime() == null )
            {
                queryTsKvVo.setStartTime(CommonUtils.getZero());
                queryTsKvVo.setEndTime(CommonUtils.getNowTime());
            }
            return efficiencyStatisticsSvc.queryEntityByKeys(queryTsKvVo, getTenantId());
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),e.getMessage());
        }
    }


    @ApiOperation(value = "【app端查询当前设备的运行状态】")
    @RequestMapping(value = "/queryTheRunningStatusByDevice", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, List<ResultRunStatusByDeviceVo>> queryTheRunningStatusByDevice(@RequestBody QueryRunningStatusVo queryTsKvVo) throws ThingsboardException {
        try {
            if (queryTsKvVo.getEndTime() == null) {
                queryTsKvVo.setStartTime(CommonUtils.getZero());
                queryTsKvVo.setEndTime(CommonUtils.getNowTime());
            }
            return efficiencyStatisticsSvc.queryTheRunningStatusByDevice(queryTsKvVo, getTenantId());
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),e.getMessage());
        }
    }



    @ApiOperation("设备属性分组属性接口")
    @RequestMapping(value = "/queryDictDevice", method = RequestMethod.GET)
    @ResponseBody
   public  Object queryDictDevice(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
        log.info("打印当前的入参:{}",deviceId);
     return  efficiencyStatisticsSvc.queryGroupDict(deviceId,getTenantId());

    }




}
