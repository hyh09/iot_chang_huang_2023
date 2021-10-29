package org.thingsboard.server.hs.service;

import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.hs.entity.po.DictDevice;
import org.thingsboard.server.hs.entity.vo.AlarmRecordQuery;
import org.thingsboard.server.hs.entity.vo.AlarmRecordResult;
import org.thingsboard.server.hs.entity.vo.DeviceProfileVO;
import org.thingsboard.server.hs.entity.vo.FactoryDeviceQuery;

import java.util.List;

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
}
