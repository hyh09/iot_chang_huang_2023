package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.service.FileService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;


/**
 * 实时监控看板接口
 *
 * @author wwj
 * @since 2021.11.22
 */
@Api(value = "实时监控看板接口", tags = {"实时监控看板接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/deviceMonitor/board")
public class RTMonitorBoardController extends BaseController {

    @Autowired
    DeviceMonitorService deviceMonitorService;

    @Autowired
    FileService fileService;

    /**
     * 报表界面资源
     */
    @ApiOperation("报表界面资源")
    @GetMapping(value = "/rtMonitor/resource")
    public BoardResource getResource() {
        return new BoardResource().setAlarmLevelList(CommonUtil.toResourceList(EnumUtils.getEnumList(AlarmSimpleLevel.class)));
    }

    /**
     * 获得在线设备情况
     */
    @ApiOperation(value = "获得在线设备情况", notes = "优先级是设备、产线、车间、工厂、均不传默认查询租户下")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query"),
            @ApiImplicitParam(name = "productionLineId", value = "产线Id", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query"),
    })
    @GetMapping(value = "/device/onlineStatus/statistics")
    public DeviceOnlineStatusResult getDeviceOnlineStatusStatistics(
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workshopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId
    ) throws ThingsboardException {
        boolean isQueryAll = false;
        if (factoryId == null && workshopId == null && productionLineId == null && deviceId == null)
            isQueryAll = true;
        FactoryDeviceQuery query = new FactoryDeviceQuery(factoryId, workshopId, productionLineId, deviceId, isQueryAll);
        return this.deviceMonitorService.getRTMonitorOnlineStatusAppData(getTenantId(), query);
    }

    /**
     * 获得报警记录统计信息
     */
    @ApiOperation(value = "获得报警记录统计信息", notes = "优先级是设备、产线、车间、工厂、均不传默认查询全部，默认查询当天")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query"),
            @ApiImplicitParam(name = "productionLineId", value = "产线Id", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
    })
    @GetMapping(value = "/alarmRecord/statistics")
    public BoardAlarmResult getAlarms(
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workshopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime
    ) throws ThingsboardException {
        boolean isQueryAll = false;
        if (factoryId == null && workshopId == null && productionLineId == null && deviceId == null)
            isQueryAll = true;
        if (startTime == null || startTime <= 0)
            startTime = CommonUtil.getTodayStartTime();
        if (endTime == null || endTime <= 0)
            endTime = CommonUtil.getTodayCurrentTime();
        TimeQuery timeQuery = TimeQuery.builder().startTime(startTime).endTime(endTime).build();
        FactoryDeviceQuery query = new FactoryDeviceQuery(factoryId, workshopId, productionLineId, deviceId, isQueryAll);
        return this.deviceMonitorService.getBoardAlarmsRecordStatistics(getTenantId(), query, timeQuery);
    }

    /**
     * 首页-获得报警记录统计信息，按今日、昨日、历史
     */
    @ApiOperation(value = "首页-获得报警记录统计信息，按今日、昨日、历史")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query")
    })
    @GetMapping(value = "/alarmRecord/day/statistics")
    public AlarmDayResult getAlarmsDay(
            @RequestParam(required = false) String deviceId
    ) throws ThingsboardException {
        FactoryDeviceQuery query = new FactoryDeviceQuery().setDeviceId(deviceId);
        return this.deviceMonitorService.getAlarmsRecordDayStatistics(getTenantId(), query);
    }
}
