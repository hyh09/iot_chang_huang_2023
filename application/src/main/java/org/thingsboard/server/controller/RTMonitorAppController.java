package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.vo.user.enums.CreatorTypeEnum;
import org.thingsboard.server.common.data.vo.user.enums.UserLeveEnums;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleStatus;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.hsms.entity.enums.DictDevicePropertySwitchEnum;
import org.thingsboard.server.dao.hsms.entity.vo.DictDevicePropertySwitchVO;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.service.Validator.validatePageLink;

/**
 * 实时监控app接口
 *
 * @author wwj
 * @since 2021.10.27
 */
@Api(value = "实时监控app接口", tags = {"实时监控app接口"})
@Slf4j
@RestController
@TbCoreComponent
@RequestMapping("/api/deviceMonitor/app")
public class RTMonitorAppController extends BaseController {

    @Autowired
    DeviceMonitorService deviceMonitorService;

    @Autowired
    ClientService clientService;

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
        return this.deviceMonitorService.getRTMonitorDataForApp(getTenantId(), query, pageLink);
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
        return this.deviceMonitorService.filterDeviceDetailResult(getTenantId(), this.deviceMonitorService.getRTMonitorDeviceDetail(getTenantId(), id), isFactoryUser());
    }

    /**
     * 查询设备详情-遥测属性历史数据图表
     */
    @ApiOperation(value = "查询设备详情-遥测属性历史数据图表", notes = "默认当天")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "tsPropertyName", value = "遥测属性名称", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query")
    })
    @GetMapping("/ts/property/history")
    public HistoryGraphVO listRTMonitorGroupPropertyHistory(
            @RequestParam UUID deviceId,
            @RequestParam String tsPropertyName,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime
    ) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("tsPropertyName", tsPropertyName);
        if (StringUtils.isBlank(tsPropertyName))
            throw new ThingsboardException("属性不能为空", ThingsboardErrorCode.GENERAL);
        if (startTime == null || startTime == 0)
            startTime = CommonUtil.getTodayStartTime();
        if (endTime == null || endTime == 0)
            endTime = CommonUtil.getTodayCurrentTime();
        return this.deviceMonitorService.getTsPropertyHistoryGraph(getTenantId(), deviceId, tsPropertyName, startTime, endTime);
    }

    /**
     * 设备监控-实时监控-查询设备详情-分组属性历史数据
     */
    @ApiOperation(value = "实时监控-查询设备详情-分组属性历史数据", notes = "默认当天")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "groupPropertyName", value = "分组属性名称", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query")
    })
    @GetMapping("/rtMonitor/device/groupProperty/history")
    public HistoryVO listRTMonitorGroupPropertyHistory(
            @RequestParam String deviceId,
            @RequestParam String groupPropertyName,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime
    ) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("groupPropertyName", groupPropertyName);
        if (startTime == null || startTime == 0)
            startTime = CommonUtil.getTodayStartTime();
        if (endTime == null || endTime == 0)
            endTime = CommonUtil.getTodayCurrentTime();
        return this.deviceMonitorService.getGroupPropertyHistory(getTenantId(), deviceId, groupPropertyName, startTime, endTime);
    }

    /**
     * 设备监控-实时监控-查询设备详情-分组属性历史数据-【分页】
     */
    @ApiOperation(value = "设备监控-实时监控-查询设备详情-分组属性历史数据-【分页】", notes = "默认当天")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "ts"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "groupPropertyName", value = "分组属性名称", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query")
    })
    @GetMapping("/rtMonitor/device/groupProperty/history/page")
    public PageData<DictDeviceGroupPropertyVO> listPageRTMonitorGroupPropertyHistory(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false, defaultValue = "ts") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam String deviceId,
            @RequestParam String groupPropertyName,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime
    ) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("groupPropertyName", groupPropertyName);
        if (startTime == null || startTime == 0)
            startTime = CommonUtil.getTodayStartTime();
        if (endTime == null || endTime == 0)
            endTime = CommonUtil.getTodayCurrentTime();
        if (!sortProperty.toLowerCase().contains(HSConstants.TS))
            sortProperty = HSConstants.TS;
        TimePageLink timePageLink = createTimePageLink(pageSize, page, null, sortProperty, sortOrder, startTime, endTime);
        validatePageLink(timePageLink);
        return this.deviceMonitorService.listPageGroupPropertyHistories(getTenantId(), deviceId, groupPropertyName, timePageLink);
    }

    /**
     * 设备监控-实时监控-查询设备详情-关联图表历史数据-【分页】
     */
    @ApiOperation(value = "设备监控-实时监控-查询设备详情-关联图表历史数据-【分页】", notes = "默认当天")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "ts"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "graphId", value = "关联图表Id", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query")
    })
    @GetMapping("/rtMonitor/device/graph/history/page")
    public HistoryGraphAppVO listPageRTMonitorGroupPropertyHistory(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false, defaultValue = "ts") String sortProperty,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam UUID deviceId,
            @RequestParam UUID graphId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime
    ) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter("deviceId", deviceId);
        checkParameter("graphId", graphId);
        if (startTime == null || startTime == 0)
            startTime = CommonUtil.getTodayStartTime();
        if (endTime == null || endTime == 0)
            endTime = CommonUtil.getTodayCurrentTime();
        if (!sortProperty.toLowerCase().contains(HSConstants.TS))
            sortProperty = HSConstants.TS;

        TimePageLink timePageLink = createTimePageLink(pageSize, page, null, sortProperty, sortOrder, startTime, endTime);
        validatePageLink(timePageLink);
        return this.deviceMonitorService.getGraphHistoryForApp(getTenantId(), deviceId, graphId, timePageLink);
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
        return this.deviceMonitorService.listPageAlarmRecordsForApp(getTenantId(), query, pageLink);
    }

    /**
     * 报警记录-获得指定工厂报警记录统计信息，按月份
     */
    @ApiOperation(value = "报警记录-获得指定工厂报警记录统计信息，按月份")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query", required = true)
    })
    @GetMapping(value = "/alarmRecord/statistics")
    public List<AlarmTimesResult> getAlarms(
            @RequestParam String factoryId
    ) throws ThingsboardException {
        checkParameter("factoryId", factoryId);
        FactoryDeviceQuery query = new FactoryDeviceQuery().setFactoryId(factoryId);
        return this.deviceMonitorService.listAlarmRecordStatisticsForApp(getTenantId(), query);
    }

    /**
     * 首页-实时监控全部信息，包括工厂、设备在线情况、预警
     */
    @ApiOperation(value = "首页-实时监控全部信息，包括工厂、设备在线情况、预警")
    @ApiImplicitParams({
    })
    @GetMapping(value = "/index")
    public AppIndexResult getIndexData() throws ThingsboardException {
        if (getCurrentUser().getType().equals(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode())) {
            var factoryList = this.clientService.listFactoriesByUserId(getTenantId(), getCurrentUser().getId());
            if (factoryList != null && !factoryList.isEmpty()) {
                FactoryDeviceQuery query = new FactoryDeviceQuery().setFactoryId(factoryList.get(0).getId().toString());
                var onlineStatusResult = this.deviceMonitorService.getDeviceOnlineStatusData(getTenantId(), query);
                var alarmDayResult = this.deviceMonitorService.getAlarmRecordStatisticByDay(getTenantId(), query);
                return AppIndexResult.builder()
                        .onLineDeviceCount(onlineStatusResult.getOnLineDeviceCount())
                        .offLineDeviceCount(onlineStatusResult.getOffLineDeviceCount())
                        .alarmResult(alarmDayResult)
                        .factoryResultList(null)
                        .build();
            } else {
                log.info("获得app首页实时监控数据-工厂列表为空:【{}】【{}】", getTenantId(), getCurrentUser().getId());
                return new AppIndexResult();
            }
        } else {
            return this.deviceMonitorService.getRTMonitorIndexDataForApp(getTenantId());
        }
    }

    /**
     * 首页-获得指定工厂在线设备情况
     */
    @ApiOperation(value = "首页-获得指定工厂在线设备情况")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query", required = true)
    })
    @GetMapping(value = "/device/onlineStatus/statistics")
    public DeviceOnlineStatusResult getDeviceOnlineStatusStatistics(
            @RequestParam String factoryId
    ) throws ThingsboardException {
        checkParameter("factoryId", factoryId);
        FactoryDeviceQuery query = new FactoryDeviceQuery().setFactoryId(factoryId);
        return this.deviceMonitorService.getDeviceOnlineStatusData(getTenantId(), query);
    }

    /**
     * 首页-获得指定工厂报警记录统计信息，按今日、昨日、历史
     */
    @ApiOperation(value = "首页-获得指定工厂报警记录统计信息，按今日、昨日、历史")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query", required = true)
    })
    @GetMapping(value = "/alarmRecord/day/statistics")
    public AlarmDayResult getAlarmsDay(
            @RequestParam String factoryId
    ) throws ThingsboardException {
        checkParameter("factoryId", factoryId);
        FactoryDeviceQuery query = new FactoryDeviceQuery().setFactoryId(factoryId);
        return this.deviceMonitorService.getAlarmRecordStatisticByDay(getTenantId(), query);
    }
}
