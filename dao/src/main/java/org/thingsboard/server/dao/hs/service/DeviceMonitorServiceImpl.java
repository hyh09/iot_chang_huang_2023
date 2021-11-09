package org.thingsboard.server.dao.hs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
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
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.kv.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.hs.entity.po.DictData;
import org.thingsboard.server.dao.model.sql.AlarmEntity;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
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

import javax.persistence.criteria.Predicate;
import java.time.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 设备监控接口实现类
 *
 * @author wwj
 * @since 2021.10.26
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class DeviceMonitorServiceImpl extends AbstractEntityService implements DeviceMonitorService, CommonService {
    // 设备配置Repository
    DeviceProfileRepository deviceProfileRepository;

    // 设备Repository
    DeviceRepository deviceRepository;

    // 报警Repository
    AlarmRepository alarmRepository;

    // 属性Repository
    AttributeKvRepository attributeKvRepository;

    // 设备字典Repository
    DictDeviceRepository dictDeviceRepository;

    // 遥测Repository
    TimeseriesService timeseriesService;

    // 设备配置设备字典关系Repository
    DeviceProfileDictDeviceRepository deviceProfileDictDeviceRepository;

    // 设备配置Service
    DictDeviceService dictDeviceService;

    // 数据字典Service
    DictDataService dictDataService;

    // 设备配置Service
    DeviceProfileService deviceProfileService;

    // 二方库Service
    ClientService clientService;

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
        Specification<DeviceProfileEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.<UUID>get("tenantId"), tenantId.getId()));
            if (!StringUtils.isBlank(name))
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

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
        var deviceProfile = deviceProfileService.findDeviceProfileById(tenantId, deviceProfileId);

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
        Alarm alarm = this.alarmRepository.findByTenantIdAndId(tenantId.getId(), alarmId.getId())
                .map(AlarmEntity::toData).orElseThrow(() -> new ThingsboardException("TenantId error！", ThingsboardErrorCode.GENERAL));
        switch (alarmStatus) {
            case ACTIVE_ACK:
                if (AlarmSimpleStatus.valueOf(alarm.getStatus().toString()).isCanBeConfirm()) {
                    throw new ThingsboardException("alarm status is not ACTIVE_UNACK！", ThingsboardErrorCode.GENERAL);
                }
                alarm.setAckTs(ts);
                break;
            case CLEARED_ACK:
                if (AlarmSimpleStatus.valueOf(alarm.getStatus().toString()).isCanBeClear()) {
                    throw new ThingsboardException("alarm status is not ACTIVE_UNACK or ACTIVE_ACK！", ThingsboardErrorCode.GENERAL);
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
        var deviceList = this.clientService.listDeviceByQuery(tenantId, query);
        var deviceIdMap = deviceList.stream().collect(Collectors.toMap(e -> e.getId().toString(), Function.identity()));
        var deviceIdList = deviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());

        var result = this.alarmRepository.findAlarmsByDeviceIdList(tenantId.getId(), deviceIdList, EntityType.DEVICE.toString(),
                timePageLink.getStartTime(), timePageLink.getEndTime(), query.getAlarmSimpleStatus().toAlarmStatusSet(),
                query.getAlarmSimpleLevel().toAlarmSeveritySet(), DaoUtil.toPageable(timePageLink));

        var affiliationDTO = this.clientService.getDeviceListAffiliation(deviceList);

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
                    .info(Optional.ofNullable(e.getDetails()).map(v -> v.get("data")).map(JsonNode::toString).orElse(null))
                    .factoryStr(Optional.ofNullable(affiliationDTO.getFactoryMap().get(device.getFactoryId())).map(Factory::getName).orElse(null))
                    .workShopStr(Optional.ofNullable(affiliationDTO.getWorkshopMap().get(device.getWorkshopId())).map(Workshop::getName).orElse(null))
                    .productionLineStr(Optional.ofNullable(affiliationDTO.getProductionLineMap().get(device.getProductionLineId())).map(ProductionLine::getName).orElse(null))
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
    public DeviceDetailResult getRTMonitorDeviceDetail(TenantId tenantId, String id) throws ExecutionException, InterruptedException, ThingsboardException {
        return null;
    }

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
    @Override
    public List<DictDeviceGroupPropertyVO> listGroupPropertyHistory(TenantId tenantId, String deviceId, String groupPropertyName, Long startTime, Long endTime) throws ExecutionException, InterruptedException {
        return null;
    }

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
    @Override
    public AppHistoryVO listAppGroupPropertyHistory(TenantId tenantId, String deviceId, String groupPropertyName, Long startTime, Long endTime) throws ExecutionException, InterruptedException {
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
     * 【APP】获得实时监控列表数据
     *
     * @param tenantId 租户Id
     * @param query    查询参数
     * @param pageLink 分页参数
     * @return 实时监控列表数据
     */
    @Override
    public RTMonitorResult getRTMonitorAppData(TenantId tenantId, FactoryDeviceQuery query, PageLink pageLink) {
        return this.getRTMonitorData(tenantId, query, pageLink);
    }

    /**
     * 【APP】获得报警记录列表
     *
     * @param tenantId 租户Id
     * @param query    查询条件
     * @param pageLink 分页排序参数
     * @return 报警记录列表
     */
    @Override
    public PageData<AlarmRecordResult> listAppAlarmsRecord(TenantId tenantId, AlarmRecordQuery query, TimePageLink pageLink) {
        return this.listAlarmsRecord(tenantId, query, pageLink);
    }

    /**
     * 获得报警记录统计信息
     *
     * @param tenantId 租户Id
     * @param query    请求参数
     * @return 报警记录统计信息
     */
    @Override
    public List<AlarmTimesResult> listAppAlarmsRecordStatistics(TenantId tenantId, FactoryDeviceQuery query) {
        var allDeviceList = this.clientService.listDeviceByQuery(tenantId, query);
        var allDeviceIdList = allDeviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());
        return this.listAlarmTimesResult(tenantId, allDeviceIdList);
    }

    /**
     * 获得在线设备情况
     *
     * @param tenantId 租户Id
     * @param query    查询参数
     * @return 在线设备情况
     */
    @Override
    public DeviceOnlineStatusResult getRTMonitorOnlineStatusAppData(TenantId tenantId, FactoryDeviceQuery query) {
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
            entity.setDictDeviceId(toUUID(e.getId()));
            entity.setDeviceProfileId(deviceProfileId.getId());
            return entity;
        }).collect(Collectors.toList()));
    }

    /**
     * 获得近几个月报警次数
     *
     * @param tenantId        租户id
     * @param allDeviceIdList 设备id列表
     */
    public List<AlarmTimesResult> listAlarmTimesResult(TenantId tenantId, List<UUID> allDeviceIdList) {
        int monthNum = 6;
        var startTimeList = this.listLatestMonthsStartTime(monthNum);
        var alarmList = this.alarmRepository.findAllAlarmsByStartTime(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString(), startTimeList.get(monthNum - 1));
        var alarmMap = alarmList.stream().collect(Collectors.groupingBy(e -> {
            var r = IntStream.iterate(0, k -> k + 1).limit(monthNum).filter(v -> e.getCreatedTime() > startTimeList.get(v))
                    .findFirst().orElse(0);
            return startTimeList.get(r);
        }));

        return startTimeList.stream().map(e -> {
            var t = LocalDateTime.ofInstant(Instant.ofEpochMilli(e), ZoneId.systemDefault());
            var month = YearMonth.of(t.getYear(), t.getMonth()).toString();
            return AlarmTimesResult.builder()
                    .time(month)
                    .num(Optional.ofNullable(alarmMap.get(e)).map(List::size).orElse(0)).build();
        }).collect(Collectors.toList());
    }

    @Autowired
    public void setDeviceProfileRepository(DeviceProfileRepository deviceProfileRepository) {
        this.deviceProfileRepository = deviceProfileRepository;
    }

    @Autowired
    public void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Autowired
    public void setAlarmRepository(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    @Autowired
    public void setAttributeKvRepository(AttributeKvRepository attributeKvRepository) {
        this.attributeKvRepository = attributeKvRepository;
    }

    @Autowired
    public void setDictDeviceRepository(DictDeviceRepository dictDeviceRepository) {
        this.dictDeviceRepository = dictDeviceRepository;
    }

    @Autowired
    public void setTimeseriesService(TimeseriesService timeseriesService) {
        this.timeseriesService = timeseriesService;
    }

    @Autowired
    public void setDictDeviceService(DictDeviceService dictDeviceService) {
        this.dictDeviceService = dictDeviceService;
    }

    @Autowired
    public void setDictDataService(DictDataService dictDataService) {
        this.dictDataService = dictDataService;
    }

    @Autowired
    public void setDeviceProfileDictDeviceRepository(DeviceProfileDictDeviceRepository deviceProfileDictDeviceRepository) {
        this.deviceProfileDictDeviceRepository = deviceProfileDictDeviceRepository;
    }

    @Autowired
    public void setDeviceProfileService(DeviceProfileService deviceProfileService) {
        this.deviceProfileService = deviceProfileService;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}
