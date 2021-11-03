package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
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
    @ApiOperation(value = "获得实时监控数据列表", notes = "默认显示第一个工厂")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query"),
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "workShopId", value = "车间Id", paramType = "query"),
            @ApiImplicitParam(name = "productionLineId", value = "产线Id", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query")
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping("/rtMonitor/device")
    public RTMonitorResult getRTMonitorData(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workShopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        var query = new FactoryDeviceQuery(factoryId, workShopId, productionLineId, deviceId);
        return this.deviceMonitorService.getRTMonitorData(getTenantId(), query, pageLink);
    }

    /**
     * 查询设备详情
     *
     * @param id 设备id
     */
    @ApiOperation("查询设备详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备Id", paramType = "path")
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping("/rtMonitor/device/{id}")
    public DeviceDetailResult getRtMonitorDeviceDetail(@PathVariable("id") String id) throws ThingsboardException, ExecutionException, InterruptedException {
        return this.deviceMonitorService.getRTMonitorDeviceDetail(getTenantId(), id);
    }

    /**
     * 查询设备详情-分组属性历史数据
     */
    @ApiOperation(value = "查询设备详情-分组属性历史数据", notes = "默认一天")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query"),
            @ApiImplicitParam(name = "groupPropertyName", value = "分组属性名称", paramType = "query")
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping("/rtMonitor/device/groupProperty/history")
    public List<DictDeviceGroupPropertyVO> listRTMonitorGroupPropertyHistory(
            @RequestParam String deviceId,
            @RequestParam String groupPropertyName) throws ThingsboardException, ExecutionException, InterruptedException {
        return this.deviceMonitorService.listGroupPropertyHistory(getTenantId(), deviceId, groupPropertyName, CommonUtil.getTodayStartTime(), CommonUtil.getTodayCurrentTime());
    }

    /**
     * 查询设备历史-表头，包含时间
     */
    @ApiOperation(value = "查询设备历史数据-表头", notes = "包含时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query")
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping("/rtMonitor/device/history/header")
    public List<DictDeviceGroupPropertyVO> listRTMonitorHistory(@RequestParam(required = false) String deviceId) throws ThingsboardException {
        return this.deviceMonitorService.listDictDeviceGroupPropertyTitle(getTenantId(), deviceId);
    }

    /**
     * 查询设备历史数据
     */
    @ApiOperation("查询设备详情-分组属性历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping("/rtMonitor/device/history")
    public PageData<Map<String, Object>> listRTMonitorHistory(
            @RequestParam(required = false) String deviceId,
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime
    ) throws ThingsboardException, ExecutionException, InterruptedException {
        TimePageLink pageLink = createTimePageLink(pageSize, page, null, sortProperty, sortOrder, startTime, endTime);
        return this.deviceMonitorService.listDeviceTelemetryHistory(getTenantId(), deviceId, pageLink);
    }
}
