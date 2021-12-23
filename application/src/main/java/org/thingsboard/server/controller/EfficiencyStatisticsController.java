package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.AppQueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;
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




    /**
     *
     */
    @ApiOperation(value = "【app端查询产能接口】  老接口 只是为了比对返回结果用")
    @RequestMapping(value = "/queryCapacityTest", method = RequestMethod.POST)
    @ResponseBody
    public ResultCapAppVo queryCapacityTest(@RequestBody QueryTsKvVo queryTsKvVo) throws ThingsboardException {
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
            PageLink pageLink = createPageLink(queryTsKvVo.getPageSize(), queryTsKvVo.getPage(), "", "", "");
            return efficiencyStatisticsSvc.queryCapAppNewMethod(queryTsKvVo, getTenantId(),pageLink);
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
            if(queryTsKvVo.getStartTime() == null )
            {
                queryTsKvVo.setStartTime(CommonUtils.getHistoryPointTime());
            }
            PageLink pageLink = createPageLink(queryTsKvVo.getPageSize(), queryTsKvVo.getPage(), "", "", "");
            return efficiencyStatisticsSvc.queryAppEntityByKeysNewMethod(queryTsKvVo, getTenantId(),pageLink);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),e.getMessage());
        }
    }


    @ApiOperation(value = "【app端查询当前设备的运行状态】")
    @RequestMapping(value = "/queryTheRunningStatusByDevice", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, List<ResultRunStatusByDeviceVo>> queryTheRunningStatusByDevice(@RequestBody AppQueryRunningStatusVo queryTsKvVo) throws ThingsboardException {
        try {
            if (queryTsKvVo.getEndTime() == null) {
                queryTsKvVo.setStartTime(CommonUtils.getZero());
                queryTsKvVo.setEndTime(CommonUtils.getNowTime());
            }
            if(queryTsKvVo.getStartTime() == null )
            {
                queryTsKvVo.setStartTime(CommonUtils.getHistoryPointTime());
            }
            PageLink pageLink = createPageLink(queryTsKvVo.getPageSize(), queryTsKvVo.getPage(), queryTsKvVo.getTextSearch(), queryTsKvVo.getSortProperty(), queryTsKvVo.getSortOrder());

            return efficiencyStatisticsSvc.queryTheRunningStatusByDevice(queryTsKvVo, getTenantId(),pageLink);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),e.getMessage());
        }
    }


    /**
     * app端获取运行状态属性
     * @param deviceId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("设备属性分组属性接口")
    @RequestMapping(value = "/queryDictDevice", method = RequestMethod.GET)
    @ResponseBody
   public  Object queryDictDevice(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
        log.info("打印当前的入参:{}",deviceId);
     return  efficiencyStatisticsSvc.queryGroupDict(deviceId,getTenantId());

    }




}
