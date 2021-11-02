package org.thingsboard.server.dao.hs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.kv.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.model.sql.AlarmEntity;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;
import org.thingsboard.server.dao.model.sql.DeviceProfileEntity;
import org.thingsboard.server.dao.sql.alarm.AlarmRepository;
import org.thingsboard.server.dao.sql.attributes.AttributeKvRepository;
import org.thingsboard.server.dao.sql.device.DeviceProfileRepository;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.timeseries.TimeseriesService;
import org.thingsboard.server.dao.hs.dao.*;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleStatus;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.utils.CommonUtil;

import javax.persistence.criteria.Predicate;
import java.time.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 设备监控接口实现类
 *
 * @author wwj
 * @since 2021.10.26
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class DeviceMonitorServiceImpl extends AbstractEntityService implements DeviceMonitorService {

    @Autowired
    DeviceProfileRepository deviceProfileRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    AlarmRepository alarmRepository;

    @Autowired
    AttributeKvRepository attributeKvRepository;

    @Autowired
    DictDeviceRepository dictDeviceRepository;

    @Autowired
    TimeseriesService timeseriesService;

    @Autowired
    DictDeviceService dictDeviceService;

    @Autowired
    DeviceProfileDictDeviceRepository deviceProfileDictDeviceRepository;

    @Autowired
    DeviceProfileService deviceProfileService;

    /**
     * 获得设备配置列表
     *
     * @param tenantId 租户Id
     * @param name     设备配置名称
     * @param pageLink 分页排序参数
     * @return 设备配置列表
     */
    @Override
    public PageData<DeviceProfile> listDeviceProfile(TenantId tenantId, String name, PageLink pageLink) {
        // 动态条件查询
        Specification<DeviceProfileEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            var es = cb.equal(root.<UUID>get("tenantId"), tenantId.getId());

            if (!StringUtils.isBlank(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }

            if (predicates.isEmpty())
                return es;
            predicates.add(es);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 查询数据
        return DaoUtil.toPageData(this.deviceProfileRepository.findAll(specification, DaoUtil.toPageable(pageLink)));
    }

    /**
     * 获得设备配置详情
     *
     * @param tenantId        租户Id
     * @param deviceProfileId 设备配置Id
     * @return 设备详情
     */
    @Override
    public DeviceProfileVO getDeviceProfileDetail(TenantId tenantId, DeviceProfileId deviceProfileId) {
        // 获得设备配置
        var deviceProfile = deviceProfileService.findDeviceProfileById(tenantId, deviceProfileId);

        // 获得绑定的设备字典
        var dictDeviceList = DaoUtil.convertDataList(this.deviceProfileDictDeviceRepository.findAllBindDeviceProfile(deviceProfile.getId().getId()));
        return DeviceProfileVO.builder().deviceProfile(deviceProfile).dictDeviceList(dictDeviceList).build();
    }

    /**
     * 删除绑定的设备字典
     *
     * @param deviceProfileId 设备配置id
     */
    @Override
    @Transactional
    public void deleteBindDictDevice(DeviceProfileId deviceProfileId) {
        this.deviceProfileDictDeviceRepository.deleteByDeviceProfileId(deviceProfileId.getId());
    }

    /**
     * 更新报警信息状态
     *
     * @param tenantId    租户Id
     * @param alarmId     报警信息Id
     * @param ts          时间
     * @param alarmStatus 报警信息状态
     */
    @Override
    @Transactional
    public void updateAlarmStatus(TenantId tenantId, AlarmId alarmId, long ts, AlarmStatus alarmStatus) throws ThingsboardException {
        Alarm alarm = this.alarmRepository.findById(alarmId.getId()).get().toData();
        if (!alarm.getTenantId().equals(tenantId)) {
            throw new ThingsboardException("当前租户Id不相等！", ThingsboardErrorCode.GENERAL);
        }
        switch (alarmStatus) {
            case ACTIVE_ACK:
                if (AlarmSimpleStatus.valueOf(alarm.getStatus().toString()).isCanBeConfirm()) {
                    throw new ThingsboardException("当前报警信息状态非未确认！", ThingsboardErrorCode.GENERAL);
                }
                alarm.setAckTs(ts);
                break;
            case CLEARED_ACK:
                if (AlarmSimpleStatus.valueOf(alarm.getStatus().toString()).isCanBeClear()) {
                    throw new ThingsboardException("当前报警信息状态非已确认！", ThingsboardErrorCode.GENERAL);
                }
                alarm.setClearTs(ts);
                break;
        }
        alarm.setStatus(alarmStatus);
        this.alarmRepository.save(new AlarmEntity(alarm));
    }

    /**
     * 获得报警记录列表
     *
     * @param tenantId     租户Id
     * @param query        查询条件
     * @param timePageLink 分页排序参数
     * @return 报警记录列表
     * @see AlarmRepository#findAlarms 参考
     */
    @Override
    public PageData<AlarmRecordResult> listAlarmsRecord(TenantId tenantId, AlarmRecordQuery query, TimePageLink timePageLink) {
        var deviceList = this.listDevice(query);
        var deviceIdMap = deviceList.stream().collect(Collectors.toMap(e -> e.getId().getId().toString(), Function.identity()));
        var deviceIdList = deviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());

        var result = this.alarmRepository.findAlarmsByDeviceIdList(tenantId.getId(), deviceIdList, EntityType.DEVICE.toString(),
                timePageLink.getStartTime(), timePageLink.getEndTime(), query.getAlarmSimpleStatus().toAlarmStatusSet(),
                query.getAlarmSimpleLevel().toAlarmSeveritySet(), DaoUtil.toPageable(timePageLink));

        var recordResultList = result.getContent().stream().map(e -> {
            var device = deviceIdMap.get(e.getOriginatorId().toString());
            var status = AlarmSimpleStatus.valueOf(e.getStatus().toString());
            var level = AlarmSimpleLevel.valueOf(e.getSeverity().toString());
            return AlarmRecordResult.builder()
                    .name(device.getName())
                    .id(e.getId().toString())
                    .createTime(e.getCreatedTime())
                    .title(e.getType())
                    .status(status)
                    .level(level)
                    .statusStr(status.getName())
                    .levelStr(level.getName())
                    .isCanBeConfirm(status.isCanBeConfirm())
                    .isCanBeClear(status.isCanBeClear())
                    .info(e.getDetails() != null && e.getDetails().get("data") != null ? e.getDetails().get("data").toString() : null)
                    .build();
        }).collect(Collectors.toList());

        return new PageData<>(recordResultList, result.getTotalPages(), result.getTotalElements(), result.hasNext());
    }

    /**
     * 获得实时监控数据列表
     *
     * @param tenantId 租户Id
     * @param query    查询参数
     * @param pageLink 分页参数
     * @return 实时监控数据列表
     */
    @Override
    public RTMonitorResult getRTMonitorData(TenantId tenantId, FactoryDeviceQuery query, PageLink pageLink) {
        return null;
    }

    /**
     * 查询设备详情
     *
     * @param tenantId 租户Id
     * @param id       设备id
     * @return 设备详情
     */
    @Override
    public DeviceDetailResult getRTMonitorDeviceDetail(TenantId tenantId, String id) throws ExecutionException, InterruptedException {
        return null;
    }

    /**
     * 查询设备分组属性历史数据
     *
     * @param tenantId          租户Id
     * @param deviceId          设备Id
     * @param groupPropertyName 属性名称
     * @param startTime         开始时间
     * @return 设备分组属性历史数据
     */
    @Override
    public List<DeviceDetailGroupPropertyResult> listGroupPropertyHistory(TenantId tenantId, String deviceId, String groupPropertyName, Long startTime) throws ExecutionException, InterruptedException {
        return null;
    }

    /**
     * 查询设备遥测数据历史数据
     *
     * @param tenantId     租户Id
     * @param deviceId     设备Id
     * @param timePageLink 时间分页参数
     * @return 设备遥测数据历史数据
     */
    @Override
    public PageData<Map<String, Object>> listDeviceTelemetryHistory(TenantId tenantId, String deviceId, TimePageLink timePageLink) throws ExecutionException, InterruptedException {
        return null;
    }

    /**
     * 查询设备历史-表头，包含时间
     *
     * @param tenantId 租户Id
     * @param deviceId 设备Id
     * @return 查询设备历史-表头，包含时间
     */
    @Override
    public List<DictDeviceGroupPropertyVO> listDictDeviceGroupPropertyTitle(TenantId tenantId, String deviceId) {
        return null;
    }

    /**
     * 绑定设备字典到设备配置
     *
     * @param dictDeviceList  设备字典列表
     * @param deviceProfileId 设备配置Id
     */
    @Override
    @Transactional
    public void bindDictDeviceToDeviceProfile(List<DictDevice> dictDeviceList, DeviceProfileId deviceProfileId) {
        this.deleteBindDictDevice(deviceProfileId);
        deviceProfileDictDeviceRepository.saveAll(dictDeviceList.stream().map(e -> {
            DeviceProfileDictDeviceEntity entity = new DeviceProfileDictDeviceEntity();
            entity.setDictDeviceId(UUID.fromString(e.getId()));
            entity.setDeviceProfileId(deviceProfileId.getId());
            return entity;
        }).collect(Collectors.toList()));
    }

    /**
     * 查询设备Id列表
     *
     * @param t 查询设备参数实体
     */
    public <T extends FactoryDeviceQuery> List<Device> listDevice(T t) {
        // TODO 查询设备列表 delete
        // TODO delete
        Device device1 = new Device();
        device1.setName("测试设备");
        device1.setId(DeviceId.fromString("e3ca9a30-3559-11ec-9e0b-1178259bc7c9"));
        Device device2 = new Device();
        device2.setName("实际设备");
        device2.setId(DeviceId.fromString("945353e0-3854-11ec-b4d5-2d3cef2c7846"));
        return new ArrayList<>() {{
            add(device1);
            add(device2);
        }};
    }

    /**
     * 查询设备Id分页列表
     *
     * @param t 查询设备参数实体
     */
    public <T extends FactoryDeviceQuery> PageData<Device> listDevicePageData(T t, PageLink pageLink) {
        // TODO 查询设备列表 delete
        // TODO delete
        Device device1 = new Device();
        device1.setName("测试设备");
        device1.setId(DeviceId.fromString("e3ca9a30-3559-11ec-9e0b-1178259bc7c9"));
        Device device2 = new Device();
        device2.setName("实际设备");
        device2.setId(DeviceId.fromString("945353e0-3854-11ec-b4d5-2d3cef2c7846"));
        var list = new ArrayList<Device>() {{
            add(device1);
            add(device2);
        }};
        return new PageData<>(list, 1, 2, false);
    }

    /**
     * 获得近几个月报警次数
     *
     * @param tenantId        租户id
     * @param allDeviceIdList 设备id列表
     */
    public List<AlarmTimesResult> listAlarmTimesResult(TenantId tenantId, List<UUID> allDeviceIdList) {
        // 获得报警次数
        var startTimeList = CommonUtil.listLatestMonthsStartTime(5);
        var alarmList = this.alarmRepository.findAllAlarmsByStartTime(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString(), startTimeList.get(0));
        var alarmMap = alarmList.stream().collect(Collectors.groupingBy(e -> {
            for (int i = 0; i <= startTimeList.size() - 1; i++) {
                if (e.getCreatedTime() > startTimeList.get(i + 1)) {
                    return startTimeList.get(i);
                }
            }
            return startTimeList.get(0);
        }));

        return startTimeList.stream().map(e -> {
            var t = LocalDateTime.ofInstant(Instant.ofEpochMilli(e), ZoneId.systemDefault());
            var month = YearMonth.of(t.getYear(), t.getMonth()).toString();
            return AlarmTimesResult.builder()
                    .time(month)
                    .num(alarmMap.get(e) != null ? alarmMap.get(e).size() : 0).build();
        }).collect(Collectors.toList());
    }
}
