package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleStatus;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.thingsboard.server.dao.service.Validator.validatePageLink;

/**
 * 实时监控app接口
 *
 * @author wwj
 * @since 2021.10.27
 */
@Api(value = "实时监控app接口", tags = {"实时监控app接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/deviceMonitor/app")
public class RTMonitorAppController extends BaseController {

    @Autowired
    DeviceMonitorService deviceMonitorService;

    /**
     * 报警记录查询界面资源
     */
    @ApiOperation("获得报警记录查询界面资源")
    @GetMapping(value = "/alarmRecord/resource")
    public AlarmRecordResource getAlarmRecordResource() {
        return new AlarmRecordResource().setAlarmStatusList(CommonUtil.toResourceList(EnumUtils.getEnumList(AlarmSimpleStatus.class)))
                .setAlarmLevelList(CommonUtil.toResourceList(EnumUtils.getEnumList(AlarmSimpleLevel.class)));
    }

    /**
     * 设备监控-获得实时监控数据列表
     */
    @ApiOperation(value = "获得实时监控数据列表", notes = "优先级为设备、产线、车间、工厂，如均为null则为未分配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query"),
            @ApiImplicitParam(name = "productionLineId", value = "产线Id", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query")
    })
    @GetMapping("/rtMonitor/device")
    public RTMonitorResult getRTMonitorData(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workshopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        var query = new FactoryDeviceQuery(factoryId, workshopId, productionLineId, deviceId);
        return this.deviceMonitorService.getRTMonitorAppData(getTenantId(), query, pageLink);
    }


    /**
     * 设备监控-实时监控-查询设备详情
     *
     * @param id 设备id
     */
    @ApiOperation("实时监控-查询设备详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备Id", paramType = "path", required = true)
    })
    @GetMapping("/rtMonitor/device/{id}")
    public DeviceDetailResult getRtMonitorDeviceDetail(@PathVariable("id") String id) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("id", id);
        return this.deviceMonitorService.getRTMonitorDeviceDetail(getTenantId(), id);
    }

    /**
     * 设备监控-实时监控-查询设备详情-分组属性历史数据
     */
    @ApiOperation(value = "实时监控-查询设备详情-分组属性历史数据", notes = "默认一天")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "groupPropertyName", value = "分组属性名称", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", required = true)
    })
    @GetMapping("/rtMonitor/device/groupProperty/history")
    public AppHistoryVO listRTMonitorGroupPropertyHistory(
            @RequestParam String deviceId,
            @RequestParam String groupPropertyName,
            @RequestParam Long startTime,
            @RequestParam Long endTime
    ) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("groupPropertyName", groupPropertyName);
        checkParameter("startTime", startTime);
        checkParameter("endTime", endTime);
        return this.deviceMonitorService.listAppGroupPropertyHistory(getTenantId(), deviceId, groupPropertyName, startTime, endTime);
    }

    /**
     * 设备监控-获得报警记录列表
     *
     * @see AlarmController#getAlarms
     */
    @ApiOperation(value = "获得报警记录列表", notes = "优先级为设备、产线、车间、工厂，如均为null则为未分配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", required = true),
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query"),
            @ApiImplicitParam(name = "productionLineId", value = "产线Id", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query")
    })
    @GetMapping(value = "/alarmRecord")
    public PageData<AlarmRecordResult> getAlarms(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam Long startTime,
            @RequestParam Long endTime,
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workshopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId
    ) throws ThingsboardException {
        TimePageLink pageLink = createTimePageLink(pageSize, page, null, sortProperty, sortOrder, startTime, endTime);
        validatePageLink(pageLink);
        var query = AlarmRecordQuery.builder()
                .alarmSimpleStatus(AlarmSimpleStatus.ANY).alarmSimpleLevel(AlarmSimpleLevel.ANY).build();
        query.setDeviceId(deviceId).setProductionLineId(productionLineId)
                .setFactoryId(factoryId).setWorkshopId(workshopId);
        return this.deviceMonitorService.listAppAlarmsRecord(getTenantId(), query, pageLink);
    }


    /**
     * 首页-获得报警记录统计信息
     */
    @ApiOperation(value = "获得报警记录统计信息", notes = "不传工厂id默认为未分配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query")
    })
    @GetMapping(value = "/alarmRecord/statistics")
    public List<AlarmTimesResult> getAlarms(
            @RequestParam(required = false) String factoryId
    ) throws ThingsboardException {
        FactoryDeviceQuery query = new FactoryDeviceQuery().setFactoryId(factoryId);
        return this.deviceMonitorService.listAppAlarmsRecordStatistics(getTenantId(), query);
    }

    /**
     * 首页-获得在线设备情况
     */
    @ApiOperation(value = "获得在线设备情况", notes = "不传工厂id默认为未分配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query")
    })
    @GetMapping(value = "/device/onlineStatus/statistics")
    public DeviceOnlineStatusResult getDeviceOnlineStatusStatistics(
            @RequestParam(required = false) String factoryId
    ) throws ThingsboardException {
        FactoryDeviceQuery query = new FactoryDeviceQuery().setFactoryId(factoryId);
        return this.deviceMonitorService.getRTMonitorOnlineStatusAppData(getTenantId(), query);
    }
}
