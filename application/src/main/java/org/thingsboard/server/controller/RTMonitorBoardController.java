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
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.service.FileService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


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

    @Autowired
    ClientService clientService;

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
        return this.deviceMonitorService.getDeviceOnlineStatusData(getTenantId(), query);
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
        return this.deviceMonitorService.getAlarmRecordStatisticsForBoard(getTenantId(), query, timeQuery);
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
        return this.deviceMonitorService.getAlarmRecordStatisticByDay(getTenantId(), query);
    }


    /**
     * 查询设备详情
     *
     * @param id 设备id
     */
    @ApiOperation("查询设备详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备Id", paramType = "path", required = true)
    })
    @GetMapping("/rtMonitor/device/{id}")
    public DeviceDetailResult getRtMonitorDeviceDetail(@PathVariable("id") String id) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("id", id);
        return this.deviceMonitorService.getRTMonitorDeviceDetail(getTenantId(), id);
    }

    /**
     * 查看设备部件实时数据
     */
    @ApiOperation("查看设备部件实时数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "componentId", value = "设备字典部件Id", paramType = "query", required = true),
    })
    @GetMapping("/rtMonitor/component")
    public List<DictDeviceComponentPropertyVO> getRtMonitorDeviceComponentDetail(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("componentId") String componentId) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("componentId", componentId);
        return this.deviceMonitorService.getRtMonitorDeviceComponentDetailForBoard(getTenantId(), toUUID(deviceId), toUUID(componentId));
    }

    /**
     * 全部设备的在线状态
     */
    @ApiOperation(value = "全部设备的在线状态")
    @GetMapping("/rtMonitor/device/onlineStatus/all")
    public Map<String, Boolean> getDeviceOnlineStatus() throws ThingsboardException {
        return this.clientService.getDeviceOnlineStatusMap(getTenantId());
    }

    /**
     * 车间-设备关键参数
     */
    @ApiOperation(value = "车间-设备关键参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
    })
    @GetMapping("/rtMonitor/device/keyParameters")
    public DeviceKeyParametersResult getDeviceKeyParameters(@RequestParam("deviceId") UUID deviceId) throws ThingsboardException {
        return this.deviceMonitorService.getDeviceKeyParameters(getTenantId(), deviceId);
    }
}
