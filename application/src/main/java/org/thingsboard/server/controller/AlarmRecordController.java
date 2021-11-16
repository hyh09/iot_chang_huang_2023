package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.alarm.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleStatus;
import org.thingsboard.server.dao.hs.entity.vo.AlarmRecordQuery;
import org.thingsboard.server.dao.hs.entity.vo.AlarmRecordResource;
import org.thingsboard.server.dao.hs.entity.vo.AlarmRecordResult;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Operation;

import static org.thingsboard.server.dao.service.Validator.validatePageLink;

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
    @GetMapping(value = "/alarmRecord/resource")
    public AlarmRecordResource getAlarmRecordResource() {
        return new AlarmRecordResource().setAlarmStatusList(CommonUtil.toResourceList(EnumUtils.getEnumList(AlarmSimpleStatus.class)))
                .setAlarmLevelList(CommonUtil.toResourceList(EnumUtils.getEnumList(AlarmSimpleLevel.class)));
    }

    /**
     * 确认报警信息
     * <p>
     * 修改项：判断是否是未确认，如果是则置为已确认
     *
     * @param id 报警信息Id
     * @see AlarmController#ackAlarm(String) (String)
     */
    @ApiOperation("确认报警信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "报警信息Id", paramType = "path", required = true),})
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
            @ApiImplicitParam(name = "id", value = "报警信息Id", paramType = "path", required = true),})
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
    @ApiOperation(value = "获得报警记录列表", notes = "优先级为设备、产线、车间、工厂，如均为null则为未分配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页数", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "sortProperty", value = "排序属性", paramType = "query", defaultValue = "createdTime"),
            @ApiImplicitParam(name = "sortOrder", value = "排序顺序", paramType = "query", defaultValue = "desc"),
            @ApiImplicitParam(name = "status", value = "状态", paramType = "query", required = true),
            @ApiImplicitParam(name = "level", value = "级别", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
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
            @RequestParam AlarmSimpleStatus status,
            @RequestParam AlarmSimpleLevel level,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workshopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId
    ) throws ThingsboardException {
        TimePageLink pageLink = createTimePageLink(pageSize, page, null, sortProperty, sortOrder, startTime, endTime);
        validatePageLink(pageLink);
        var query = AlarmRecordQuery.builder()
                .alarmSimpleStatus(status).alarmSimpleLevel(level).build();
        query.setDeviceId(deviceId).setProductionLineId(productionLineId)
        .setFactoryId(factoryId).setWorkshopId(workshopId);
        return this.deviceMonitorService.listAlarmsRecord(getTenantId(), query, pageLink);
    }
}