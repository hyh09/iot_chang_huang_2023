package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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

@Api(value = "计算OEEController", tags = {"OEE计算"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/statisticoee")
public class StatisticOeeController extends BaseController {

    @ApiOperation("OEE计算，返回每小时的值")
    //@ApiImplicitParam(name = "dto", value = "入参", dataType = "StatisticOeeQry", paramType = "query")
    @RequestMapping(value = "/dp/getStatisticOeeList", method = RequestMethod.GET)
    @ResponseBody
    public List<StatisticOeeVo> getStatisticOeeList(StatisticOeeQry dto) throws ThingsboardException {
        try {
            List<StatisticOeeVo> result = new ArrayList<>();
            checkParameterChinees("startTime",dto.getStartTime());
            checkParameterChinees("endTime",dto.getEndTime());
            List<StatisticOee> statisticOees = statisticOeeService.getStatisticOeeEveryHourList(dto.toStatisticOee(getCurrentUser().getTenantId().getId()));
            if (!org.springframework.util.CollectionUtils.isEmpty(statisticOees)) {
                for (StatisticOee oee : statisticOees) {
                    result.add(new StatisticOeeVo(oee));
                }
            }
            return result;
        } catch (Exception e) {
            throw handleException(e);
        }
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
        return statisticOeeService.getStatisticOeeDeviceByCurrentDay(toUUID(deviceId));
    }

}
