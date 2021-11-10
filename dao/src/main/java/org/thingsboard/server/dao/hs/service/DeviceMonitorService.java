package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.vo.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 设备监控接口
 *
 * @author wwj
 * @since 2021.10.26
 */
public interface DeviceMonitorService {

    /**
     * 获得设备配置列表
     *
     * @param tenantId 租户Id
     * @param name     设备配置名称
     * @param pageLink 分页排序参数
     * @return 设备配置列表
     */
    PageData<DeviceProfile> listDeviceProfile(TenantId tenantId, String name, PageLink pageLink);

    /**
     * 获得设备配置详情
     *
     * @param tenantId        租户Id
     * @param deviceProfileId 设备配置Id
     * @return 设备详情
     */
    DeviceProfileVO getDeviceProfileDetail(TenantId tenantId, DeviceProfileId deviceProfileId);

    /**
     * 绑定设备字典到设备配置
     *
     * @param dictDeviceList  设备字典列表
     * @param deviceProfileId 设备配置Id
     */
    void bindDictDeviceToDeviceProfile(List<DictDevice> dictDeviceList, DeviceProfileId deviceProfileId);

    /**
     * 删除绑定的设备字典
     *
     * @param deviceProfileId 设备配置id
     */
    void deleteBindDictDevice(DeviceProfileId deviceProfileId);

    /**
     * 更新报警信息状态
     *
     * @param tenantId    租户Id
     * @param alarmId     报警信息Id
     * @param ts          时间
     * @param alarmStatus 报警信息状态
     */
    void updateAlarmStatus(TenantId tenantId, AlarmId alarmId, long ts, AlarmStatus alarmStatus) throws ThingsboardException;

    /**
     * 获得报警记录列表
     *
     * @param tenantId 租户Id
     * @param query    查询条件
     * @param pageLink 分页排序参数
     * @return 报警记录列表
     */
    PageData<AlarmRecordResult> listAlarmsRecord(TenantId tenantId, AlarmRecordQuery query, TimePageLink pageLink);

    /**
     * 获得实时监控数据列表
     *
     * @param tenantId 租户Id
     * @param query    查询参数
     * @param pageLink 分页参数
     * @return 实时监控数据列表
     */
    RTMonitorResult getRTMonitorData(TenantId tenantId, FactoryDeviceQuery query, PageLink pageLink);

    /**
     * 查询设备详情
     *
     * @param tenantId 租户Id
     * @param id       设备id
     * @return 设备详情
     */
    DeviceDetailResult getRTMonitorDeviceDetail(TenantId tenantId, String id) throws ExecutionException, InterruptedException, ThingsboardException;

    /**
     * 查询设备分组属性历史数据
     *
     * @param tenantId          租户Id
     * @param deviceId          设备Id
     * @param groupPropertyName 属性名称
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @return 设备分组属性历史数据
     */
    List<DictDeviceGroupPropertyVO> listGroupPropertyHistory(TenantId tenantId, String deviceId, String groupPropertyName, Long startTime, Long endTime) throws ExecutionException, InterruptedException;

    /**
     * 【APP】查询设备分组属性历史数据
     *
     * @param tenantId          租户Id
     * @param deviceId          设备Id
     * @param groupPropertyName 属性名称
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @return 设备分组属性历史数据
     */
    AppHistoryVO listAppGroupPropertyHistory(TenantId tenantId, String deviceId, String groupPropertyName, Long startTime, Long endTime) throws ExecutionException, InterruptedException;

    /**
     * 查询设备遥测数据历史数据
     *
     * @param tenantId     租户Id
     * @param deviceId     设备Id
     * @param timePageLink 时间分页参数
     * @return 设备遥测数据历史数据
     */
    PageData<Map<String, Object>> listDeviceTelemetryHistory(TenantId tenantId, String deviceId, TimePageLink timePageLink) throws ExecutionException, InterruptedException;

    /**
     * 查询设备历史-表头，包含时间
     *
     * @param tenantId 租户Id
     * @param deviceId 设备Id
     * @return 查询设备历史-表头，包含时间
     */
    List<DictDeviceGroupPropertyVO> listDictDeviceGroupPropertyTitle(TenantId tenantId, String deviceId);

    /**
     * 【APP】获得实时监控列表数据
     *
     * @param tenantId 租户Id
     * @param query    查询参数
     * @param pageLink 分页参数
     * @return 实时监控列表数据
     */
    RTMonitorResult getRTMonitorAppData(TenantId tenantId, FactoryDeviceQuery query, PageLink pageLink);

    /**
     * 【APP】获得报警记录列表
     *
     * @param tenantId 租户Id
     * @param query    查询条件
     * @param pageLink 分页排序参数
     * @return 报警记录列表
     */
    PageData<AlarmRecordResult> listAppAlarmsRecord(TenantId tenantId, AlarmRecordQuery query, TimePageLink pageLink);

    /**
     * 获得报警记录统计信息
     *
     * @param tenantId 租户Id
     * @param query    请求参数
     * @return 报警记录统计信息
     */
    List<AlarmTimesResult> listAppAlarmsRecordStatistics(TenantId tenantId, FactoryDeviceQuery query);

    /**
     * 获得在线设备情况
     *
     * @param tenantId 租户Id
     * @param query 查询参数
     * @return 在线设备情况
     */
    DeviceOnlineStatusResult getRTMonitorOnlineStatusAppData(TenantId tenantId, FactoryDeviceQuery query);}
