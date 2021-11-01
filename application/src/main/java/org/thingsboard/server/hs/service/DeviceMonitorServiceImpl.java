package org.thingsboard.server.hs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.model.sql.AlarmEntity;
import org.thingsboard.server.dao.model.sql.AlarmInfoEntity;
import org.thingsboard.server.dao.model.sql.DeviceProfileEntity;
import org.thingsboard.server.dao.model.sql.RelationEntity;
import org.thingsboard.server.dao.sql.alarm.AlarmRepository;
import org.thingsboard.server.dao.sql.device.DeviceProfileRepository;
import org.thingsboard.server.hs.dao.*;
import org.thingsboard.server.hs.entity.po.DictData;
import org.thingsboard.server.hs.entity.po.DictDevice;
import org.thingsboard.server.hs.entity.vo.*;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    AlarmRepository alarmRepository;

    @Autowired
    DictDeviceRepository dictDeviceRepository;

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
        // 动态条件查询 TODO 增加@Type
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
                if (!alarm.getStatus().equals(AlarmStatus.ACTIVE_UNACK)) {
                    throw new ThingsboardException("当前报警信息状态非未确认！", ThingsboardErrorCode.GENERAL);
                }
                alarm.setAckTs(ts);
                break;
            case CLEARED_ACK:
                if (!alarm.getStatus().equals(AlarmStatus.ACTIVE_ACK)) {
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
     * @param tenantId 租户Id
     * @param query    查询条件
     * @param timePageLink 分页排序参数
     * @return 报警记录列表
     * @see AlarmRepository#findAlarms
     */
    @Override
    public PageData<AlarmRecordResult> listAlarmsRecord(TenantId tenantId, AlarmRecordQuery query, TimePageLink timePageLink) {
        var deviceList = this.listDevice(query);
        var deviceIdList = deviceList.stream().map(Device::getId).collect(Collectors.toList());

        // TODO

        // 动态条件查询
        Specification<AlarmEntity> specification = (root, query1, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<AlarmEntity, RelationEntity> join = root.join("toId", JoinType.LEFT);
            predicates.add(cb.equal(root.<UUID>get("tenantId"), tenantId.getId()));
            predicates.add(cb.equal(join.get("relationTypeGroup"), EntityType.ALARM.toString()));
            predicates.add(cb.equal(join.get("toType"), EntityType.ALARM.toString()));
            predicates.add(cb.in(join.get("fromId")).value(deviceIdList));
            predicates.add(cb.equal(join.get("fromType"), EntityType.DEVICE.toString()));
            predicates.add(cb.equal(join.get("fromType"), EntityType.DEVICE.toString()));
            predicates.add(cb.or(cb.isNotNull(join.get("fromId")), cb.in(root.get("originatorId")).value(deviceIdList)));
            if (timePageLink.getStartTime() !=null && timePageLink.getStartTime() > 0) {
                predicates.add(cb.greaterThan(root.get("createdTime"), timePageLink.getStartTime()));
            }
            if (timePageLink.getEndTime() !=null && timePageLink.getEndTime() > 0) {
                predicates.add(cb.lessThan(root.get("createdTime"), timePageLink.getEndTime()));
            }
            predicates.add(cb.in(root.get("status")).value(query.getAlarmSimpleStatus().toAlarmStatusSet()));
            predicates.add(cb.in(root.get("severity")).value(query.getAlarmSimpleLevel().toAlarmSeveritySet()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 查询数据
        var r = DaoUtil.toPageData(this.alarmRepository.findAll(specification, DaoUtil.toPageable(timePageLink)));

        // TODO 做数据的抽离分析
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
     */
    public <T extends FactoryDeviceQuery> List<Device> listDevice(T t) {
        // TODO 查询设备列表
        return new ArrayList<>();
    }
}
