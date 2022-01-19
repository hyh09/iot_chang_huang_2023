package org.thingsboard.server.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.QueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.QueryTsKvHisttoryVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.device.DeviceDictionaryPropertiesVo;
import org.thingsboard.server.common.data.vo.enums.ActivityException;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.CapacityHistoryVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.controller.example.AnswerExample;
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
public class PCendEfficiencyController extends BaseController implements AnswerExample {



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
            queryTsKvVo.setTenantId(getTenantId().getId());
            return efficiencyStatisticsSvc.queryPCCapAppNewMethod(queryTsKvVo, getTenantId(), pageLink);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),e.getMessage());
        }
    }


    @ApiOperation("效能分页首页得数据，获取表头接口")
    @ApiResponses({
            @ApiResponse(code = 200, message =queryEntityByKeysHeader),
    })
    @RequestMapping(value = "/queryEntityByKeysHeader", method = RequestMethod.GET)
    public  Object queryEntityByKeysHeader() throws ThingsboardException {
        try{
            return efficiencyStatisticsSvc.queryEntityByKeysHeader();
        }catch (Exception e)
        {
            log.error("【效能分析-能耗历史的表头数据返回-无参请求 】异常信息:{}",e);
            throw  new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }







    @ApiOperation("效能分析 首页的数据; 包含单位能耗数据 ###新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
            @ApiImplicitParam(name = "deviceId", value = "设备id")
    })
    @RequestMapping(value = "/queryEntityByKeys", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message =queryEnergyHistory_messg),
    })
    @ResponseBody
    public Object queryEntityByKeys(
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
            if ( startTime == null  || endTime == null) {
                startTime=(CommonUtils.getZero());
                endTime=(CommonUtils.getNowTime());
            }

            QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
            queryTsKvVo.setDeviceId(deviceId);
            queryTsKvVo.setProductionLineId(productionLineId);
            queryTsKvVo.setWorkshopId(workshopId);
            queryTsKvVo.setFactoryId(factoryId);
            queryTsKvVo.setStartTime(startTime);
            queryTsKvVo.setEndTime(endTime);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            Long lo1 = System.currentTimeMillis();
            queryTsKvVo.setTenantId(getTenantId().getId());
            PageDataAndTotalValue<Map> obj =  efficiencyStatisticsSvc.queryEntityByKeysNewMethod(queryTsKvVo,getTenantId(), pageLink);
            Long lo2 = System.currentTimeMillis();
            Long t3 = lo2-lo1;
            log.info("--queryEntityByKeys查询的耗时时间--：{}毫秒",t3);
            return  obj;

        }catch (Exception e)
        {
            log.error("【效能分析 首页的数据; 包含单位能耗数据】异常信息:{}",e);
            throw  new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }






    @ApiOperation("效能分析-能耗历史的表头数据返回-无参请求")
    @ApiResponses({
            @ApiResponse(code = 200, message =queryEnergyHistoryHeader),
    })
    @ResponseBody
    @RequestMapping(value = "/queryEnergyHistoryHeader", method = RequestMethod.GET)
    public List<String> queryEnergyHistoryHeader() throws ThingsboardException {
        try{
        return efficiencyStatisticsSvc.queryEnergyHistoryHeader();
        }catch (Exception e)
        {
            log.error("【效能分析-能耗历史的表头数据返回-无参请求 】异常信息:{}",e);
            throw  new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }

    }








    @ApiOperation("效能分析-能耗历史的分页查询接口 ---统计维度是时间，排序只能是时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
            @ApiImplicitParam(name = "deviceId", value = "设备id")
    })
    @RequestMapping(value = "/queryEnergyHistory", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message =queryEnergyHistory_messg),
    })
    @ResponseBody
    public Object queryEnergyHistory(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId
    ) throws ThingsboardException {
        try {
            if ( startTime == null  || endTime == null) {
                startTime=(CommonUtils.getZero());
                endTime=(CommonUtils.getNowTime());
            }

            QueryTsKvHisttoryVo queryTsKvVo = new QueryTsKvHisttoryVo();
            queryTsKvVo.setDeviceId(deviceId);
            queryTsKvVo.setStartTime(startTime);
            queryTsKvVo.setEndTime(endTime);
            queryTsKvVo.setSortOrder(sortOrder);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);

            return efficiencyStatisticsSvc.queryEnergyHistory(queryTsKvVo,getTenantId(), pageLink);
        }catch (Exception e)
        {
            log.error("【效能分析-能耗历史的分页查询接口 ---统计维度是时间，排序只能是时间 】异常信息:{}",e);
            throw  new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }




    @ApiOperation("效能分析-能耗历史的分页查询接口 ---统计维度是时间，排序只能是时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
            @ApiImplicitParam(name = "deviceId", value = "设备id")
    })
    @RequestMapping(value = "/queryCapacityHistory", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message =queryEnergyHistory_messg),
    })
    @ResponseBody
    public PageData<CapacityHistoryVo> queryCapacityHistory(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) UUID deviceId
    ) throws ThingsboardException {
        try {
            if ( startTime == null  || endTime == null) {
                startTime=(CommonUtils.getZero());
                endTime=(CommonUtils.getNowTime());
            }

            QueryTsKvHisttoryVo queryTsKvVo = new QueryTsKvHisttoryVo();
            queryTsKvVo.setDeviceId(deviceId);
            queryTsKvVo.setStartTime(startTime);
            queryTsKvVo.setEndTime(endTime);
            queryTsKvVo.setSortOrder(sortOrder);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);

            return efficiencyStatisticsSvc.queryCapacityHistory(queryTsKvVo,getTenantId(), pageLink);
        }catch (Exception e)
        {
            log.error("【效能分析-能耗历史的分页查询接口 ---统计维度是时间，排序只能是时间 】异常信息:{}",e);
            throw  new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }



    /**
     * PC端
     * @param deviceId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("设备属性分组后的属性name属性接口--pc端下拉框")
    @RequestMapping(value = "/queryDictName", method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 200, message =pc_queryDictName),
    })
    @ResponseBody
    public  List<DeviceDictionaryPropertiesVo> queryDictName(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
//        log.info("打印当前的入参:{}",deviceId);
        try {
            return efficiencyStatisticsSvc.queryDictDevice(deviceId, getTenantId());
        }catch (Exception e)
        {
            e.printStackTrace();
            log.info("====>:{}",e);
            throw  new  ThingsboardException(e.getMessage(),ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }



    @ApiOperation(value = "【PC端查询当前设备的运行状态】")
    @ApiResponses({
            @ApiResponse(code = 200, message =pc_queryTheRunningStatusByDevice),
    })
    @RequestMapping(value = "/queryTheRunningStatusByDevice", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, List<ResultRunStatusByDeviceVo>> queryTheRunningStatusByDevice(@RequestBody QueryRunningStatusVo queryTsKvVo) throws ThingsboardException {
        try {
            return efficiencyStatisticsSvc.queryPcTheRunningStatusByDevice(queryTsKvVo, getTenantId());
        }catch (Exception e)
        {
            log.error("【PC端查询当前设备的运行状态】异常信息:{}",e);
            throw  new ThingsboardException(e.getMessage(), ThingsboardErrorCode.FAIL_VIOLATION);
        }
    }








}
