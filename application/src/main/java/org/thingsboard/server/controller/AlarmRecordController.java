package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.alarm.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.hs.entity.enums.AlarmSimpleStatus;
import org.thingsboard.server.hs.entity.vo.AlarmRecordQuery;
import org.thingsboard.server.hs.entity.vo.AlarmRecordResource;
import org.thingsboard.server.hs.entity.vo.AlarmRecordResult;
import org.thingsboard.server.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.hs.service.DeviceMonitorService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Operation;

import java.util.*;

/**
 * 报警记录接口
 *
 * @author wwj
 * @since 2021.10.27
 */
@Api(value = "报警记录接口", tags = {"报警记录接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/deviceMonitor")
public class AlarmRecordController extends BaseController {

    @Autowired
    DeviceMonitorService deviceMonitorService;

    /**
     * 报警记录查询界面资源
     */
    @ApiOperation("获得报警记录查询界面资源")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/alarmRecord/resource")
    public AlarmRecordResource getAlarmRecordResource() {
        return new AlarmRecordResource().setAlarmStatusList(AlarmSimpleStatus.toResourceList())
                .setAlarmLevelList(AlarmSimpleLevel.toResourceList());
    }

    /**
     * 确认报警信息
     * <p>
     * 修改项：判断是否是未确认，如果是则置为已确认
     *
     * @param id 报警信息Id
     * @see AlarmController#ackAlarm(String) (String)
     */
    @ApiOperation("获得报警记录查询界面资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "报警信息Id", paramType = "path"),})
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @PostMapping(value = "/alarmRecord/{id}/ack")
    public void ackAlarm(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("alarmId", id);
        try {
            AlarmId alarmId = new AlarmId(toUUID(id));
            Alarm alarm = checkAlarmId(alarmId, Operation.WRITE);
            long ackTs = System.currentTimeMillis();

            // 更新报警信息状态
            this.deviceMonitorService.updateAlarmStatus(getTenantId(), alarmId, ackTs, AlarmStatus.ACTIVE_ACK);

            alarm.setAckTs(ackTs);
            alarm.setStatus(AlarmStatus.ACTIVE_ACK);
            logEntityAction(alarm.getOriginator(), alarm, getCurrentUser().getCustomerId(), ActionType.ALARM_ACK, null);

            sendEntityNotificationMsg(getTenantId(), alarmId, EdgeEventActionType.ALARM_ACK);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 清除报警信息
     * <p>
     * 修改项：判断是否是已确认，如果是则置为已清除
     *
     * @param id 报警信息id
     * @see AlarmController#clearAlarm(String)
     */
    @ApiOperation("清除报警信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "报警信息Id", paramType = "path"),})
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @PostMapping(value = "/alarmRecord/{id}/clear")
    public void clearAlarm(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        try {
            AlarmId alarmId = new AlarmId(toUUID(id));
            Alarm alarm = checkAlarmId(alarmId, Operation.WRITE);
            long clearTs = System.currentTimeMillis();
            this.deviceMonitorService.updateAlarmStatus(getTenantId(), alarmId, clearTs, AlarmStatus.CLEARED_ACK);
            alarm.setClearTs(clearTs);
            alarm.setStatus(AlarmStatus.CLEARED_ACK);
            logEntityAction(alarm.getOriginator(), alarm, getCurrentUser().getCustomerId(), ActionType.ALARM_CLEAR, null);

            sendEntityNotificationMsg(getTenantId(), alarmId, EdgeEventActionType.ALARM_CLEAR);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 获得报警记录列表
     *
     * @see AlarmController#getAlarms
     */
    @ApiOperation("获得报警记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态", paramType = "query"),
            @ApiImplicitParam(name = "level", value = "级别", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query"),
            @ApiImplicitParam(name = "workShopId", value = "车间Id", paramType = "query"),
            @ApiImplicitParam(name = "productionLineId", value = "产线Id", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备Id", paramType = "query"),
            @ApiImplicitParam(name = "isUnAllocation", value = "是否选择未分配", paramType = "query")
    })
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/alarmRecord")
    public PageData<AlarmRecordResult> getAlarms(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) AlarmSimpleStatus status,
            @RequestParam(required = false) AlarmSimpleLevel level,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workShopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) Boolean isUnAllocation
    ) throws ThingsboardException {
        if (productionLineId == null && deviceId == null && !isUnAllocation) {
            throw new ThingsboardException("请选择设备！", ThingsboardErrorCode.GENERAL);
        }
        TimePageLink pageLink = createTimePageLink(pageSize, page, null, sortProperty, sortOrder, startTime, endTime);

        var query = AlarmRecordQuery.builder()
                .alarmSimpleStatus(status).alarmSimpleLevel(level).build();
        query.setDeviceId(deviceId).setIsUnAllocation(isUnAllocation).setProductionLineId(productionLineId)
        .setFactoryId(factoryId).setWorkShopId(workShopId);
        return this.deviceMonitorService.listAlarmsRecord(getTenantId(), query, pageLink);
    }
}
