package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.thingsboard.server.dao.service.Validator.validatePageLink;

/**
 * 实时监控接口
 *
 * @author wwj
 * @since 2021.10.27
 */
@Api(value = "实时监控接口", tags = {"实时监控接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/deviceMonitor")
public class RTMonitorController extends BaseController {

    @Autowired
    DeviceMonitorService deviceMonitorService;

    /**
     * 获得实时监控数据列表
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
        return this.deviceMonitorService.getRTMonitorData(getTenantId(), query, pageLink);
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
     * 查询设备详情-分组属性历史数据
     */
    @ApiOperation(value = "查询设备详情-分组属性历史数据", notes = "默认一天")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "groupPropertyName", value = "分组属性名称", paramType = "query", required = true)
    })
    @GetMapping("/rtMonitor/device/groupProperty/history")
    public HistoryVO listRTMonitorGroupPropertyHistory(
            @RequestParam String deviceId,
            @RequestParam String groupPropertyName) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("groupPropertyName", groupPropertyName);
        return this.deviceMonitorService.getGroupPropertyHistory(getTenantId(), deviceId, groupPropertyName, CommonUtil.getTodayStartTime(), CommonUtil.getTodayCurrentTime());
    }

    /**
     * 查询设备历史-表头，包含时间
     */
    @ApiOperation(value = "查询设备历史数据-表头", notes = "包含时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true)
    })
    @GetMapping("/rtMonitor/device/history/header")
    public List<DictDeviceGroupPropertyVO> listRTMonitorHistory(@RequestParam String deviceId) throws ThingsboardException {
        checkParameter("deviceId", deviceId);
        return this.deviceMonitorService.listDeviceTelemetryHistoryTitles(getTenantId(), deviceId);
    }

    /**
     * 查询设备历史数据
     */
    @ApiOperation("查询设备历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
    })
    @GetMapping("/rtMonitor/device/history")
    public PageData<Map<String, Object>> listRTMonitorHistory(
            @RequestParam String deviceId,
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false, defaultValue = "ts") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam Long startTime,
            @RequestParam(required = false) Long endTime
    ) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("startTime", startTime);
        if (endTime == null || endTime <= 0L)
            endTime = CommonUtil.getTodayCurrentTime();
        if (HSConstants.CREATED_TIME.equalsIgnoreCase(sortProperty))
            sortProperty = HSConstants.TS;
        TimePageLink pageLink = createTimePageLink(pageSize, page, null, sortProperty, sortOrder, startTime, endTime);
        validatePageLink(pageLink);
        return this.deviceMonitorService.listPageDeviceTelemetryHistories(getTenantId(), deviceId, pageLink);
    }

}
