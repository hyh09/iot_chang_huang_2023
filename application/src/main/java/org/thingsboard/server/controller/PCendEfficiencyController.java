package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: Pc端效能分析接口
 * @author: HU.YUNHUI
 * @create: 2021-11-16 09:49
 **/
@Api(value = "Pc端效能分析接口", tags = {"Pc端效能分析接口"})
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/pc/efficiency")
public class PCendEfficiencyController extends BaseController {



    @ApiOperation(value = "【PC端查询产能接口】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
            @ApiImplicitParam(name = "deviceId", value = "设备id"),
            @ApiImplicitParam(name = "productionLineId", value = "产线id  UUID类型"),
            @ApiImplicitParam(name = "workshopId", value = "车间id UUID类型"),
            @ApiImplicitParam(name = "factoryId", value = "工厂id  UUID类型"),
    })
    @RequestMapping(value = "/queryCapacity", params = {"pageSize", "page"}, method = RequestMethod.GET)

    @ResponseBody
    public PageDataAndTotalValue<AppDeviceCapVo> queryCapacity(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId,
            @RequestParam(required = false) UUID productionLineId,
            @RequestParam(required = false) UUID workshopId,
            @RequestParam(required = false) UUID factoryId
            ) throws ThingsboardException {
        try {
            QueryTsKvVo queryTsKvVo = new QueryTsKvVo(startTime, endTime, deviceId, productionLineId, workshopId, factoryId);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            return efficiencyStatisticsSvc.queryPCCapApp(queryTsKvVo, getTenantId(), pageLink);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),e.getMessage());
        }
    }


    @ApiOperation("设备属性分组-分组后的属性属性接口---pc端用不到")
    @RequestMapping(value = "/queryDictDevice", method = RequestMethod.GET)
    @ResponseBody
    public  Object queryGroupDict(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
        log.info("打印当前的入参:{}",deviceId);
        return  efficiencyStatisticsSvc.queryGroupDict(deviceId,getTenantId());

    }


    @ApiOperation("设备属性分组后的属性name属性接口--pcd端下拉框")
    @RequestMapping(value = "/queryDictName", method = RequestMethod.GET)
    @ResponseBody
    public  Object queryDictName(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
        log.info("打印当前的入参:{}",deviceId);
        return  efficiencyStatisticsSvc.queryDictDevice(deviceId,getTenantId());
    }



    @ApiOperation(value = "【PC端查询当前设备的运行状态】")
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
            log.error("【PC端查询当前设备的运行状态】异常信息:{}",e);
            throw  new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }








}
