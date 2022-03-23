package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.statisticoee.StatisticOee;
import org.thingsboard.server.entity.statisticoee.dto.StatisticOeeQry;
import org.thingsboard.server.entity.statisticoee.vo.StatisticOeeVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(value = "计算OEEController", tags = {"OEE计算"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/statisticoee")
public class DeviceOeeEveryHourController extends BaseController {

    @ApiOperation("查询OEE计算历史，返回每小时的值")
    //@ApiImplicitParam(name = "dto", value = "入参", dataType = "StatisticOeeQry", paramType = "query")
    @RequestMapping(value = "/dp/getStatisticOeeList", method = RequestMethod.GET)
    @ResponseBody
    public List<StatisticOeeVo> getStatisticOeeList(StatisticOeeQry dto) throws ThingsboardException {
        List<StatisticOeeVo> result = new ArrayList<>();
        checkParameterChinees("startTime",dto.getStartTime());
        checkParameterChinees("endTime",dto.getEndTime());
        List<StatisticOee> statisticOees = deviceOeeEveryHourService.getStatisticOeeEveryHourList(dto.toStatisticOee(getCurrentUser().getTenantId().getId()));
        if (!org.springframework.util.CollectionUtils.isEmpty(statisticOees)) {
            for (StatisticOee oee : statisticOees) {
                result.add(new StatisticOeeVo(oee));
            }
        }
        return result;
    }


    @ApiOperation("实时查询OEE计算，返回每小时的值")
    //@ApiImplicitParam(name = "dto", value = "入参", dataType = "StatisticOeeQry", paramType = "query")
    @RequestMapping(value = "/dp/getStatisticOeeListByRealTime", method = RequestMethod.GET)
    @ResponseBody
    public List<StatisticOeeVo> getStatisticOeeListByRealTime(StatisticOeeQry dto) throws ThingsboardException {
        List<StatisticOeeVo> result = new ArrayList<>();
        checkParameterChinees("startTime",dto.getStartTime());
        checkParameterChinees("endTime",dto.getEndTime());
        List<StatisticOee> statisticOees = deviceOeeEveryHourService.getStatisticOeeListByRealTime(dto.toStatisticOee(getCurrentUser().getTenantId().getId()));
        if (!org.springframework.util.CollectionUtils.isEmpty(statisticOees)) {
            for (StatisticOee oee : statisticOees) {
                result.add(new StatisticOeeVo(oee));
            }
        }
        return result;
    }


    /**
     * 查询设备当天OEE
     * 设备当天OEE需班次时间结束后运算，当天班次未结束取前一天的值
     *
     * @param deviceId
     * @return
     */
    @ApiOperation("查询设备当天OEE")
    @ApiImplicitParam(name = "deviceId",value = "deviceId工厂标识",dataType = "string",paramType="query",required = true)
    @RequestMapping(value = "/dp/getStatisticOeeDeviceByCurrentDay", method = RequestMethod.GET)
    @ResponseBody
    public BigDecimal getStatisticOeeDeviceByCurrentDay(String deviceId) {
        BigDecimal result = new BigDecimal(0);
        try {
            result =  deviceOeeEveryHourService.getStatisticOeeDeviceByCurrentDay(toUUID(deviceId));
        } catch (Exception e) {
            log.error("手动执行当天所有设备每小时OEE同步失败",e);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 手动执行当天所有设备每小时OEE统计
     */
    @ApiOperation("手动执行当天所有设备每小时OEE统计(定时任务手动执行)")
    @RequestMapping(value = "/dp/statisticOeeByTimedTask", method = RequestMethod.GET)
    @ResponseBody
    public void statisticOeeByTimedTask(){
        try {
            deviceOeeEveryHourService.statisticOeeByTimedTask();
        } catch (Exception e) {
            log.error("手动执行当天所有设备每小时OEE同步失败",e);
            e.printStackTrace();
        }
    }

    /**
     * 执行（指定时间区间）所有设备每小时OEE统计
     */
    @ApiOperation("执行（指定时间区间）所有设备每小时OEE统计")
    @RequestMapping(value = "/dp/statisticOeeByAnyTime", method = RequestMethod.GET)
    @ResponseBody
    public void statisticOeeByAnyTime(StatisticOeeQry dto){
        try {
            checkParameterChinees("startTime",dto.getStartTime());
            checkParameterChinees("endTime",dto.getEndTime());
            deviceOeeEveryHourService.statisticOeeByAnyTime(dto.toStatisticOee(getCurrentUser().getTenantId().getId()));
        } catch (Exception e) {
            log.error("执行（指定时间区间）所有设备每小时OEE同步",e);
            e.printStackTrace();
        }
    }

}
