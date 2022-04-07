package org.thingsboard.server.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.vo.user.enums.CreatorTypeEnum;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    @Autowired
    DeviceService deviceService;

    @Autowired
    ClientService clientService;

    /**
     * 根据当前登录人获得工厂层级-适配
     */
    @ApiOperation(value = "根据当前登录人获得工厂层级-适配")
    @GetMapping("/rtMonitor/factory/hierarchy")
    public FactoryRedundantHierarchyResult getFactoryHierarchy() throws ThingsboardException {
        var user = getCurrentUser();
        UUID factoryId = null;
        if (CreatorTypeEnum.FACTORY_MANAGEMENT.getCode().equalsIgnoreCase(user.getType())) {
            factoryId = user.getFactoryId();
            if (factoryId == null)
                return new FactoryRedundantHierarchyResult();
        }
        return this.clientService.getFactoryHierarchy(getTenantId(), factoryId).toFactoryRedundantHierarchyResult();
    }

    /**
     * 根据当前登录人获得工厂层级-通用
     */
    @Deprecated
    @ApiOperation(value = "根据当前登录人获得工厂层级-通用")
    @GetMapping("/rtMonitor/factory/hierarchy/common")
    public FactoryHierarchyResult getFactoryHierarchyCommon() throws ThingsboardException {
        var user = getCurrentUser();
        UUID factoryId = null;
        if (CreatorTypeEnum.FACTORY_MANAGEMENT.getCode().equalsIgnoreCase(user.getType())) {
            factoryId = user.getFactoryId();
            if (factoryId == null)
                return new FactoryHierarchyResult();
        }
        return this.clientService.getFactoryHierarchy(getTenantId(), factoryId).toFactoryHierarchyResult();
    }

    /**
     * 根据当前登录人获得全部设备的在线状态
     */
    @ApiOperation(value = "根据当前登录人获得全部设备的在线状态")
    @GetMapping("/rtMonitor/device/onlineStatus/all")
    public Map<String, Boolean> getDeviceOnlineStatus() throws ThingsboardException {
        var user = getCurrentUser();
        UUID factoryId = null;
        if (CreatorTypeEnum.FACTORY_MANAGEMENT.getCode().equalsIgnoreCase(user.getType())) {
            factoryId = user.getFactoryId();
            if (factoryId == null)
                return Maps.newHashMap();
        }
        return this.clientService.getDeviceOnlineStatusMap(getTenantId(), factoryId);
    }

    /**
     * 根据当前登录人查询所在工厂在线状态
     */
    @ApiOperation(value = "根据当前登录人查询所在工厂在线状态", notes = "默认查全部，传工厂Id则查单个工厂")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
    })
    @GetMapping("/rtMonitor/factory/onlineStatus")
    public Map<String, Boolean> getFactoryOnlineStatus(@RequestParam(value = "factoryId", required = false) UUID factoryId) throws ThingsboardException {
        if (factoryId == null) {
            var user = getCurrentUser();
            if (CreatorTypeEnum.FACTORY_MANAGEMENT.getCode().equalsIgnoreCase(user.getType())) {
                factoryId = user.getFactoryId();
                if (factoryId == null)
                    return Maps.newHashMap();
            }
        }
        return this.clientService.getFactoryOnlineStatusMap(getTenantId(), factoryId);
    }

    /**
     * 根据当前登录人查询所在工厂下的网关设备
     */
    @ApiOperation(value = "根据当前登录人查询所在工厂下的网关设备")
    @GetMapping("/rtMonitor/factory/gateway/devices")
    public List<FactoryGatewayDevicesResult> getFactoryGatewayDevicesResult() throws ThingsboardException {
        var user = getCurrentUser();
        UUID factoryId = null;
        if (CreatorTypeEnum.FACTORY_MANAGEMENT.getCode().equalsIgnoreCase(user.getType())) {
            factoryId = user.getFactoryId();
            if (factoryId == null)
                return Lists.newArrayList();
        }
        return this.clientService.listFactoryGatewayDevices(getTenantId(), factoryId);
    }

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
            @RequestParam(required = false) String deviceId
    ) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        FactoryDeviceQuery query;
        query = new FactoryDeviceQuery(factoryId, workshopId, productionLineId, deviceId);
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
     * 查询设备详情-遥测属性历史数据图表
     */
    @ApiOperation(value = "查询设备详情-遥测属性历史数据图表", notes = "默认一天")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "tsPropertyName", value = "遥测属性名称", paramType = "query", required = true)
    })
    @GetMapping("/rtMonitor/device/ts/property/history")
    public HistoryGraphVO listRTMonitorGroupPropertyHistory(
            @RequestParam UUID deviceId,
            @RequestParam String tsPropertyName) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("tsPropertyName", tsPropertyName);
        if (StringUtils.isBlank(tsPropertyName))
            throw new ThingsboardException("属性不能为空", ThingsboardErrorCode.GENERAL);
        return this.deviceMonitorService.getTsPropertyHistoryGraph(getTenantId(), deviceId, tsPropertyName, CommonUtil.getTodayStartTime(), CommonUtil.getTodayCurrentTime());
    }

    /**
     * 查询设备历史-表头，包含时间
     */
    @ApiOperation(value = "查询设备历史数据-表头", notes = "包含时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "isShowAttributes", value = "是否显示属性", paramType = "query", defaultValue = "false")
    })
    @GetMapping("/rtMonitor/device/history/header")
    public List<DictDeviceGroupPropertyVO> listRTMonitorHistory(@RequestParam String deviceId, @RequestParam(value = "isShowAttributes", defaultValue = "false") boolean isShowAttributes) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        return this.deviceMonitorService.listDeviceTelemetryHistoryTitles(getTenantId(), deviceId, isShowAttributes);
    }

    /**
     * 查询设备历史数据-表头-图表
     */
    @ApiOperation(value = "查询设备历史数据-表头-图表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true)
    })
    @GetMapping("/rtMonitor/device/history/header/graphs")
    public List<DictDeviceGraphVO> listRTMonitorHistoryGraphs(@RequestParam String deviceId) throws ThingsboardException {
        checkParameter("deviceId", deviceId);
        var device = deviceService.findDeviceById(getTenantId(), DeviceId.fromString(deviceId));
        if (device.getDictDeviceId() != null)
            return this.dictDeviceService.listDictDeviceGraphs(getTenantId(), device.getDictDeviceId());
        else
            return Lists.newArrayList();
    }

    /**
     * 查询设备历史数据
     */
    @ApiOperation(value = "查询设备历史数据", notes = "默认倒序，不允许排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "isShowAttributes", value = "是否显示属性", paramType = "query", defaultValue = "false")
    })
    @GetMapping("/rtMonitor/device/history")
    public PageData<Map<String, Object>> listPageDeviceTelemetryHistories(
            @RequestParam String deviceId,
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(value = "isShowAttributes", defaultValue = "false") boolean isShowAttributes
    ) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("startTime", startTime);
        if (endTime == null || endTime <= 0L)
            endTime = CommonUtil.getTodayCurrentTime();
        TimePageLink pageLink = createTimePageLink(pageSize, page, null, HSConstants.TS, "desc", startTime, endTime);
        validatePageLink(pageLink);
        return this.deviceMonitorService.listPageDeviceTelemetryHistories(getTenantId(), deviceId, isShowAttributes, pageLink);
    }

    /**
     * 查询设备历史数据-无分页
     */
    @Deprecated
    @ApiOperation(value = "查询设备历史数据-无分页", notes = "默认倒序，不允许排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
    })
    @GetMapping("/rtMonitor/device/history/sequence")
    public List<Map<String, Object>> listDeviceTelemetryHistories(
            @RequestParam String deviceId,
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam Long startTime,
            @RequestParam(required = false) Long endTime
    ) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("startTime", startTime);
        if (endTime == null || endTime <= 0L)
            endTime = CommonUtil.getTodayCurrentTime();
        TimePageLink pageLink = createTimePageLink(pageSize, page, null, HSConstants.TS, "desc", startTime, endTime);
        validatePageLink(pageLink);
        return this.deviceMonitorService.listDeviceTelemetryHistories(getTenantId(), DeviceId.fromString(deviceId), pageLink);
    }

    /**
     * 获得实时监控数据列表-精简版
     */
    @Deprecated
    @ApiOperation(value = "获得实时监控数据列表-精简版", notes = "优先级为设备、产线、车间、工厂，如均为null则为未分配")
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
    @GetMapping("/rtMonitor/device/simplification")
    public PageData<RTMonitorDeviceResult> getRTMonitorSimplificationData(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false, defaultValue = "createdTime") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workshopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId
    ) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        validatePageLink(pageLink);
        FactoryDeviceQuery query;
        query = new FactoryDeviceQuery(factoryId, workshopId, productionLineId, deviceId);
        return this.deviceMonitorService.getRTMonitorSimplificationData(getTenantId(), query, pageLink);
    }

    /**
     * 获得实时监控数据列表-设备在线状态
     */
    @ApiOperation(value = "获得实时监控数据列表-设备在线状态", notes = "优先级为设备、产线、车间、工厂，如均为null则为未分配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query"),
            @ApiImplicitParam(name = "productionLineId", value = "产线Id", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query")
    })
    @GetMapping("/rtMonitor/device/onlineStatus")
    public RTMonitorDeviceOnlineStatusResult getRTMonitorDeviceOnlineStatusData(
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workshopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId
    ) throws ThingsboardException {
        FactoryDeviceQuery query;
        query = new FactoryDeviceQuery(factoryId, workshopId, productionLineId, deviceId);
        return this.deviceMonitorService.getRTMonitorDeviceOnlineStatusData(getTenantId(), query);
    }

    /**
     * 获得实时监控数据列表-设备报警统计
     */
    @ApiOperation(value = "获得实时监控数据列表-设备报警统计", notes = "近六个月，从远及近; 优先级为设备、产线、车间、工厂，如均为null则为未分配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query"),
            @ApiImplicitParam(name = "productionLineId", value = "产线Id", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query")
    })
    @GetMapping("/rtMonitor/device/alarm/statistics")
    public List<AlarmTimesResult> RTMonitorDeviceAlarmStatisticsResult(
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workshopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId
    ) throws ThingsboardException {
        FactoryDeviceQuery query;
        query = new FactoryDeviceQuery(factoryId, workshopId, productionLineId, deviceId);
        return this.deviceMonitorService.getRTMonitorDeviceAlarmStatisticsResult(getTenantId(), query);
    }

    /**
     * 获得实时监控数据列表-设备全部keyIds
     */
    @Deprecated
    @ApiOperation(value = "获得实时监控数据列表-设备全部keyIds")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "path")
    })
    @GetMapping("/rtMonitor/device/{deviceId}/keyIds")
    public List<Integer> listDeviceKeyIds(
            @PathVariable("deviceId") UUID deviceId
    ) throws ThingsboardException {
        checkParameter("deviceId", deviceId);
        return this.deviceMonitorService.listDeviceKeyIds(getTenantId(), deviceId);
    }

    /**
     * 获得实时监控数据列表-设备全部keys
     */
    @Deprecated
    @ApiOperation(value = "获得实时监控数据列表-设备全部keys")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "path")
    })
    @GetMapping("/rtMonitor/device/{deviceId}/keys")
    public List<String> listDeviceKeys(
            @PathVariable("deviceId") UUID deviceId
    ) throws ThingsboardException {
        checkParameter("deviceId", deviceId);
        return this.deviceMonitorService.listDeviceKeys(getTenantId(), deviceId);
    }

}
