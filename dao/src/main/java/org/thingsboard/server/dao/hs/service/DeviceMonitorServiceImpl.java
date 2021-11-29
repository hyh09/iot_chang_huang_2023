package org.thingsboard.server.dao.hs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.springframework.beans.BeanUtils;
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
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.po.DictData;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
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
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
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

        var dictDeviceList = DaoUtil.convertDataList(this.deviceProfileDictDeviceRepository.findAllDictDeviceEntityByDeviceProfileId(deviceProfile.getId().getId()));

        DeviceProfileVO deviceProfileVO = new DeviceProfileVO();
        BeanUtils.copyProperties(deviceProfile, deviceProfileVO);
        deviceProfileVO.setDictDeviceIdList(dictDeviceList.stream().map(DictDevice::getId).collect(Collectors.toList()));
        return deviceProfileVO;
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
                .map(AlarmEntity::toData).orElseThrow(() -> new ThingsboardException("该报警信息不存在！", ThingsboardErrorCode.GENERAL));
        switch (alarmStatus) {
            case ACTIVE_ACK:
                if (!AlarmSimpleStatus.valueOf(alarm.getStatus().toString()).canBeConfirm()) {
                    throw new ThingsboardException("该报警状态非未确认！", ThingsboardErrorCode.GENERAL);
                }
                alarm.setAckTs(ts);
                break;
            case CLEARED_ACK:
                if (!AlarmSimpleStatus.valueOf(alarm.getStatus().toString()).canBeClear()) {
                    throw new ThingsboardException("该报警状态非未确认或已确认！", ThingsboardErrorCode.GENERAL);
                }
                if (alarm.getAckTs() == 0L)
                    alarm.setAckTs(ts);
                alarm.setClearTs(ts);
                break;
            default:
                throw new ThingsboardException("该报警状态不支持！", ThingsboardErrorCode.GENERAL);
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
                    .createdTime(e.getCreatedTime())
                    .title(e.getType())
                    .status(status)
                    .level(level)
                    .statusStr(status.getName())
                    .levelStr(level.getName())
                    .isCanBeConfirm(status.canBeConfirm())
                    .isCanBeClear(status.canBeClear())
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
        var allDeviceList = this.clientService.listDeviceByQuery(tenantId, query);
        var allDeviceIdList = allDeviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());
        var devicePageData = this.clientService.listDevicePageByQuery(tenantId, query, pageLink);

        var activeStatusMap = this.clientService.listAllDeviceOnlineStatus(allDeviceIdList);

        var deviceWithoutImageList = devicePageData.getData().stream().filter(e -> StringUtils.isBlank(e.getPicture())).collect(Collectors.toList());
        var map = Lists.newArrayList(this.dictDeviceRepository.findAllById(deviceWithoutImageList.stream().map(Device::getId).map(DeviceId::getId).collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(e -> e.getId().toString(), DictDeviceEntity::getPicture, (a, b) -> a));

        var resultList = devicePageData.getData().stream().map(e -> {
            var idStr = e.getId().toString();
            return RTMonitorDeviceResult.builder()
                    .id(idStr)
                    .name(e.getName())
                    .image(Optional.ofNullable(map.get(e.getId().toString())).orElse(e.getPicture()))
                    .isOnLine(calculateValueInMap(activeStatusMap, idStr))
                    .build();
        }).collect(Collectors.toList());

        int onLineCount = calculateValueInMap(activeStatusMap);

        return RTMonitorResult.builder()
                .devicePageData(new PageData<>(resultList, devicePageData.getTotalPages(), devicePageData.getTotalElements(), devicePageData.hasNext()))
                .allDeviceCount(allDeviceList.size())
                .offLineDeviceCount(allDeviceList.size() - onLineCount)
                .onLineDeviceCount(onLineCount)
                .alarmTimesList(this.listAlarmTimesResult(tenantId, allDeviceIdList))
                .deviceIdList(allDeviceIdList.stream().map(UUID::toString).collect(Collectors.toList()))
                .build();
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
        var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(id))).map(DeviceEntity::toData).orElseThrow(() -> new ThingsboardException("设备不存在", ThingsboardErrorCode.GENERAL));
        var deviceBaseDTO = this.clientService.getDeviceBase(tenantId, new FactoryDeviceQuery(UUIDToString(device.getFactoryId()), UUIDToString(device.getWorkshopId()), UUIDToString(device.getProductionLineId()), device.getId().toString()));

        var kvEntryMap = this.timeseriesService.findAllLatest(tenantId, DeviceId.fromString(id)).get()
                .stream().sorted(Comparator.comparing(TsKvEntry::getKey)).collect(Collectors.toMap(TsKvEntry::getKey, Function.identity(), (key1, key2) -> key1, LinkedHashMap::new));

        Map<String, DictData> dictDataMap = this.dictDataService.mapAllDictData(tenantId);

        List<DictDeviceGroupVO> groupResultList = new ArrayList<>();
        List<DictDeviceComponentVO> componentList = new ArrayList<>();
        List<String> groupPropertyNameList = new ArrayList<>();

        DictDevice dictDevice = new DictDevice();
        if (device.getDictDeviceId() != null) {
            dictDevice = this.dictDeviceRepository.findByTenantIdAndId(tenantId.getId(), device.getDictDeviceId()).map(DictDeviceEntity::toData).orElseGet(DictDevice::new);

            var dictDeviceDetail = this.dictDeviceService.getDictDeviceDetail(device.getDictDeviceId().toString(), tenantId);

            var groupVOList = dictDeviceDetail.getGroupList();
            groupResultList = groupVOList.stream().map(e -> DictDeviceGroupVO.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .groupPropertyList(e.getGroupPropertyList().stream().map(v -> {
                        var kvData = Optional.ofNullable(kvEntryMap.get(v.getName()));
                        kvData.ifPresent(k -> groupPropertyNameList.add(v.getName()));
                        return DictDeviceGroupPropertyVO.builder()
                                .id(v.getId())
                                .unit(Optional.ofNullable(v.getDictDataId()).map(dictDataMap::get).map(DictData::getUnit).orElse(null))
                                .name(v.getName())
                                .title(v.getTitle())
                                .content(kvData.isEmpty() ? v.getContent() : kvData.get().getValue().toString())
                                .createdTime(kvData.isEmpty() ? v.getCreatedTime() : kvData.get().getTs())
                                .build();
                    }).collect(Collectors.toList()))
                    .build()).collect(Collectors.toList());

            componentList = dictDeviceDetail.getComponentList();
            this.recursionDealComponentData(componentList, kvEntryMap, dictDataMap, groupPropertyNameList);
        }

        var ungrouped = DictDeviceGroupVO.builder().name(HSConstants.UNGROUPED).groupPropertyList(new ArrayList<>()).build();
        kvEntryMap.forEach((k, v) -> {
            if (!groupPropertyNameList.contains(k)) {
                ungrouped.getGroupPropertyList().add(DictDeviceGroupPropertyVO.builder()
                        .unit(Optional.ofNullable(dictDataMap.get(v.getKey())).map(DictData::getUnit).orElse(null))
                        .name(v.getKey())
                        .title(v.getKey())
                        .content(v.getValueAsString())
                        .createdTime(v.getTs())
                        .build());
            }
        });

        return DeviceDetailResult.builder()
                .id(device.getId().toString())
                .name(device.getName())
                .picture(Optional.ofNullable(device.getPicture()).orElse(dictDevice.getPicture()))
                .isOnLine(calculateValueInMap(this.clientService.listAllDeviceOnlineStatus(List.of(device.getId().getId())), device.getId().toString()))
                .factoryName(Optional.ofNullable(deviceBaseDTO.getFactory()).map(Factory::getName).orElse(null))
                .workShopName(Optional.ofNullable(deviceBaseDTO.getWorkshop()).map(Workshop::getName).orElse(null))
                .productionLineName(Optional.ofNullable(deviceBaseDTO.getProductionLine()).map(ProductionLine::getName).orElse(null))
                .isUnAllocation(this.isDeviceUnAllocation(device))
                .resultList(groupResultList)
                .componentList(componentList)
                .resultUngrouped(ungrouped)
                .alarmTimesList(this.listAlarmTimesResult(tenantId, List.of(toUUID(id))))
                .build();
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
    @SuppressWarnings("Duplicates")
    public HistoryVO listGroupPropertyHistory(TenantId tenantId, String deviceId, String groupPropertyName, Long startTime, Long endTime) throws ExecutionException, InterruptedException {
        List<String> keyList = new ArrayList<>() {{
            add(groupPropertyName);
        }};
        List<ReadTsKvQuery> tempQueries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, startTime, endTime, endTime - startTime, 1, Aggregation.COUNT, "desc"))
                .collect(Collectors.toList());

        var tempResult = this.timeseriesService.findAll(tenantId, DeviceId.fromString(deviceId), tempQueries).get()
                .stream().collect(Collectors.toMap(TsKvEntry::getKey, Function.identity()));
        if (tempResult.isEmpty())
            return HistoryVO.builder().isShowChart(false).propertyVOList(Lists.newArrayList()).build();
        int count = Integer.parseInt(String.valueOf(tempResult.get(groupPropertyName).getValue()));
        List<ReadTsKvQuery> queries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, startTime, endTime, endTime - startTime, count, Aggregation.NONE, "desc"))
                .collect(Collectors.toList());

        var dictDataMap = this.dictDataService.mapAllDictData(tenantId);
        Map<String, String> rMap = Maps.newHashMap();
        var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(deviceId))).map(DeviceEntity::toData).orElse(null);
        if (device != null && device.getDictDeviceId() != null) {
            rMap = this.dictDeviceService.mapAllPropertyDictDataId(device.getDictDeviceId());
        }
        Map<String, String> finalRMap = rMap;

        var data = this.timeseriesService.findAll(tenantId, DeviceId.fromString(deviceId), queries).get()
                .stream().map(e -> DictDeviceGroupPropertyVO.builder()
                        .content(e.getValue().toString())
                        .unit(Optional.ofNullable(finalRMap.getOrDefault(groupPropertyName, null))
                                .map(dictDataMap::get).map(DictData::getUnit).orElse(null))
                        .createdTime(e.getTs())
                        .build())
                .collect(Collectors.toList());

        return HistoryVO.builder()
                .propertyVOList(data)
                .isShowChart(!data.isEmpty() && isNumberData(data.get(0).getContent()) ? Boolean.TRUE : Boolean.FALSE)
                .build();
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
        var keyList = this.timeseriesService.findAllKeysByEntityIds(tenantId, List.of(DeviceId.fromString(deviceId)));

        List<ReadTsKvQuery> tempQueries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, timePageLink.getStartTime(), timePageLink.getEndTime(), timePageLink.getEndTime() - timePageLink.getStartTime(), 1, Aggregation.COUNT, timePageLink.getSortOrder().getDirection().toString()))
                .collect(Collectors.toList());

        var temp = this.timeseriesService.findAll(tenantId, DeviceId.fromString(deviceId), tempQueries).get()
                .stream().map(KvEntry::getValue).map(e -> Integer.valueOf(String.valueOf(e))).collect(Collectors.toList());
        if (temp.isEmpty())
            return new PageData<>(Lists.newArrayList(), 0, 0L, false);
        var count = temp.stream().mapToInt(Integer::intValue).sum();
        var max = temp.stream().max(Integer::compareTo).orElse(0);

        List<ReadTsKvQuery> queries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, timePageLink.getStartTime(), timePageLink.getEndTime(), timePageLink.getEndTime() - timePageLink.getStartTime(), count, Aggregation.NONE, timePageLink.getSortOrder().getDirection().toString()))
                .collect(Collectors.toList());
        var KvResult = this.timeseriesService.findAll(tenantId, DeviceId.fromString(deviceId), queries).get();

        List<Map<String, Object>> result = new ArrayList<>();
        Map<Long, Map<String, Object>> resultMap = Maps.newLinkedHashMap();
        KvResult.forEach(v -> resultMap.computeIfAbsent(v.getTs(), k -> new HashMap<>()).put(v.getKey(), v.getValueAsString()));
        resultMap.forEach((k, v) -> {
            v.put(HSConstants.CREATED_TIME, k);
            result.add(v);
        });

        var totalPage = Double.valueOf(Math.ceil(max.doubleValue() / timePageLink.getPageSize())).intValue();
        var subList = result.subList(Math.min(timePageLink.getPageSize() * timePageLink.getPage(), max), Math.min(timePageLink.getPageSize() * (timePageLink.getPage() + 1), max));
        return new PageData<>(subList, totalPage, Long.parseLong(String.valueOf(max)), timePageLink.getPage() + 1 < totalPage);
    }

    /**
     * 查询设备历史-表头，包含时间
     *
     * @param tenantId 租户Id
     * @param deviceId 设备Id
     * @return 查询设备历史-表头，包含时间
     */
    @Override
    @SuppressWarnings("Duplicates")
    public List<DictDeviceGroupPropertyVO> listDictDeviceGroupPropertyTitle(TenantId tenantId, String deviceId) {
        List<DictDeviceGroupPropertyVO> propertyVOList = new ArrayList<>() {{
            add(DictDeviceGroupPropertyVO.builder()
                    .name(HSConstants.CREATED_TIME).title(HSConstants.CREATED_TIME).build());
        }};

        var keyList = this.timeseriesService.findAllKeysByEntityIds(tenantId, List.of(DeviceId.fromString(deviceId)));
        if (keyList.isEmpty())
            return propertyVOList;

        var dictDataMap = this.dictDataService.mapAllDictData(tenantId);
        Map<String, String> rMap = Maps.newHashMap();
        Map<String, String> nameTitleMap = Maps.newHashMap();
        var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(deviceId))).map(DeviceEntity::toData).orElse(null);
        if (device != null && device.getDictDeviceId() != null) {
            rMap = this.dictDeviceService.mapAllPropertyDictDataId(device.getDictDeviceId());
            nameTitleMap = this.dictDeviceService.mapAllPropertyTitle(device.getDictDeviceId());
        }
        Map<String, String> finalRMap = rMap;
        Map<String, String> finalNameTitleMap = nameTitleMap;

        propertyVOList.addAll(keyList.stream().map(e -> DictDeviceGroupPropertyVO.builder()
                .name(e)
                .title(finalNameTitleMap.getOrDefault(e, e))
                .unit(Optional.ofNullable(finalRMap.getOrDefault(e, null))
                        .map(dictDataMap::get).map(DictData::getUnit).orElse(null))
                .build()).collect(Collectors.toList()));
        return propertyVOList;
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
        var allDeviceList = this.clientService.listDeviceByQuery(tenantId, query);
        var allDeviceIdList = allDeviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());
        var result = this.clientService.listAllDeviceOnlineStatus(allDeviceIdList);
        var count = calculateValueInMap(result);
        return DeviceOnlineStatusResult.builder()
                .allDeviceCount(allDeviceIdList.size())
                .onLineDeviceCount(count)
                .offLineDeviceCount(allDeviceIdList.size() - count)
                .build();
    }

    /**
     * 【看板】获得报警记录统计信息
     *
     * @param tenantId 租户Id
     * @param query    查询参数
     * @return 报警记录统计信息
     */
    @Override
    @SuppressWarnings("Duplicates")
    public BoardAlarmResult getBoardAlarmsRecordStatistics(TenantId tenantId, FactoryDeviceQuery query) {
        var allDeviceList = this.clientService.listDeviceByQuery(tenantId, query);
        var allDeviceIdList = allDeviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());

        if (query.getIsQueryAll()) {
            var DeviceFactoryIdMap = allDeviceList.stream().collect(Collectors.toMap(e -> e.getId().toString(), e -> UUIDToString(e.getFactoryId())));
            var factoryList = this.clientService.listAllFactoryByTenantId(tenantId);
            Map<String, Integer> factoryMap = Maps.newHashMap();

            var alarmList = this.alarmRepository.findAllAlarmsByStartTime(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString(), CommonUtil.getThisYearStartTime());
            int criticalCount = 0;
            int majorCount = 0;
            int minorCount = 0;
            int warningCount = 0;
            int indeterminateCount = 0;
            for (AlarmEntity entity : alarmList) {
                switch (entity.getSeverity()) {
                    case INDETERMINATE:
                        indeterminateCount += 1;
                        break;
                    case CRITICAL:
                        criticalCount += 1;
                        break;
                    case WARNING:
                        warningCount += 1;
                        break;
                    case MINOR:
                        minorCount += 1;
                        break;
                    case MAJOR:
                        majorCount += 1;
                        break;
                }
                Optional.ofNullable(entity.getOriginatorId()).map(UUID::toString).map(DeviceFactoryIdMap::get).ifPresent(e -> {
                    var value = Optional.ofNullable(factoryMap.get(e)).map(v -> v + 1).orElse(0);
                    factoryMap.put(e, value);
                });
            }

            var timesResultList = factoryList.stream().map(e -> BoardAlarmTimesResult.builder()
                    .value(e.getName())
                    .num(factoryMap.getOrDefault(e.getId().toString(), 0))
                    .build()).collect(Collectors.toList());

            var proportionResult = BoardAlarmLevelProportionResult.builder()
                    .criticalCount(criticalCount).majorCount(majorCount).minorCount(minorCount).
                            warningCount(warningCount).indeterminateCount(indeterminateCount)
                    .count(criticalCount + majorCount + minorCount + warningCount + indeterminateCount).build();
            return BoardAlarmResult.builder()
                    .proportionResult(proportionResult)
                    .timesResultList(timesResultList)
                    .build();
        } else if (query.isQueryFactoryOnly()) {
            var DeviceWorkshopIdMap = allDeviceList.stream().collect(Collectors.toMap(e -> e.getId().toString(), e -> UUIDToString(e.getWorkshopId())));
            var workshopList = this.clientService.listAllWorkshopByTenantIdAndFactoryId(tenantId, toUUID(query.getFactoryId()));
            Map<String, Integer> workshopMap = Maps.newHashMap();
            var alarmList = this.alarmRepository.findAllAlarmsByStartTime(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString(), CommonUtil.getThisYearStartTime());
            int criticalCount = 0;
            int majorCount = 0;
            int minorCount = 0;
            int warningCount = 0;
            int indeterminateCount = 0;
            for (AlarmEntity entity : alarmList) {
                switch (entity.getSeverity()) {
                    case INDETERMINATE:
                        indeterminateCount += 1;
                        break;
                    case CRITICAL:
                        criticalCount += 1;
                        break;
                    case WARNING:
                        warningCount += 1;
                        break;
                    case MINOR:
                        minorCount += 1;
                        break;
                    case MAJOR:
                        majorCount += 1;
                        break;
                }
                Optional.ofNullable(entity.getOriginatorId()).map(UUID::toString).map(DeviceWorkshopIdMap::get).ifPresent(e -> {
                    var value = Optional.ofNullable(workshopMap.get(e)).map(v -> v + 1).orElse(0);
                    workshopMap.put(e, value);
                });
            }

            var timesResultList = workshopList.stream().map(e -> BoardAlarmTimesResult.builder()
                    .value(e.getName())
                    .num(workshopMap.getOrDefault(e.getId().toString(), 0))
                    .build()).collect(Collectors.toList());

            var proportionResult = BoardAlarmLevelProportionResult.builder()
                    .criticalCount(criticalCount).majorCount(majorCount).minorCount(minorCount).
                            warningCount(warningCount).indeterminateCount(indeterminateCount)
                    .count(criticalCount + majorCount + minorCount + warningCount + indeterminateCount).build();
            return BoardAlarmResult.builder()
                    .proportionResult(proportionResult)
                    .timesResultList(timesResultList)
                    .build();
        } else {
            return BoardAlarmResult.builder()
                    .timesResultList(this.listAlarmTimesResult(tenantId, allDeviceIdList).stream().map(e ->
                            BoardAlarmTimesResult.builder()
                                    .num(e.getNum()).value(e.getTime())
                                    .build()
                    ).collect(Collectors.toList()))
                    .proportionResult(null)
                    .build();
        }
    }

    /**
     * 绑定设备字典到设备配置
     *
     * @param tenantId         租户Id
     * @param dictDeviceIdList 设备字典Id列表
     * @param deviceProfileId  设备配置Id
     */
    @Override
    @Transactional
    public void bindDictDeviceToDeviceProfile(TenantId tenantId, List<String> dictDeviceIdList, DeviceProfileId deviceProfileId) {
        // 在其它程序代码无错误的前提下，设备正确绑定了设备配置的情况下执行下列代码
        var deleteList = Sets.newHashSet(deviceProfileDictDeviceRepository.findAllByDeviceProfileId(deviceProfileId.getId()).stream()
                .map(DeviceProfileDictDeviceEntity::getDictDeviceId).collect(Collectors.toList()));
        var addList = Sets.newHashSet(dictDeviceIdList.stream().map(this::toUUID).collect(Collectors.toList()));
        if (addList.equals(deleteList))
            return;
        var defaultDeviceProfileId = Optional.ofNullable(this.deviceProfileRepository.findByDefaultTrueAndTenantId(tenantId.getId())).map(DeviceProfileEntity::getId).orElse(null);

        var deleteDeviceList = this.deviceRepository.findAllByTenantIdAndDictDeviceIdIn(tenantId.getId(),
                deleteList.stream().filter(e -> !addList.contains(e)).collect(Collectors.toSet()));
        deleteDeviceList.forEach(e -> e.setDeviceProfileId(defaultDeviceProfileId));
        this.deviceRepository.saveAll(deleteDeviceList);

        var addDeviceList = this.deviceRepository.findAllByTenantIdAndDictDeviceIdIn(tenantId.getId(),
                addList.stream().filter(e -> !deleteList.contains(e)).collect(Collectors.toSet()));
        addDeviceList.forEach(e -> e.setDeviceProfileId(deviceProfileId.getId()));
        this.deviceRepository.saveAll(addDeviceList);

        this.deleteBindDictDevice(deviceProfileId); // default
        deviceProfileDictDeviceRepository.saveAll(dictDeviceIdList.stream().map(e -> {  // default
            DeviceProfileDictDeviceEntity entity = new DeviceProfileDictDeviceEntity();
            entity.setDictDeviceId(toUUID(e));
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

        var data = startTimeList.stream().map(e -> {
            var t = LocalDateTime.ofInstant(Instant.ofEpochMilli(e), ZoneId.systemDefault());
            var month = YearMonth.of(t.getYear(), t.getMonth()).toString();
            return AlarmTimesResult.builder()
                    .time(month)
                    .num(Optional.ofNullable(alarmMap.get(e)).map(List::size).orElse(0)).build();
        }).collect(Collectors.toList());

        Collections.reverse(data);
        return data;
    }

    /**
     * 递归处理部件属性数据
     *
     * @param componentList         部件列表
     * @param kvEntryMap            遥测数据map
     * @param dictDataMap           数据字典map
     * @param groupPropertyNameList 属性List
     */
    public void recursionDealComponentData(List<DictDeviceComponentVO> componentList, Map<String, TsKvEntry> kvEntryMap, Map<String, DictData> dictDataMap, List<String> groupPropertyNameList) {
        for (DictDeviceComponentVO componentVO : componentList) {
            for (DictDeviceComponentPropertyVO propertyVO : componentVO.getPropertyList()) {
                var kvData = Optional.ofNullable(kvEntryMap.get(propertyVO.getName()));
                kvData.ifPresent(k -> {
                    groupPropertyNameList.add(propertyVO.getName());
                    propertyVO.setContent(kvData.get().getValue().toString());
                    propertyVO.setUnit(Optional.ofNullable(propertyVO.getDictDataId()).map(dictDataMap::get).map(DictData::getUnit).orElse(null));
                    propertyVO.setCreatedTime(kvData.get().getTs());
                });
            }
            if (componentVO.getComponentList() == null || componentVO.getComponentList().isEmpty()) {
                continue;
            }
            this.recursionDealComponentData(componentVO.getComponentList(), kvEntryMap, dictDataMap, groupPropertyNameList);
        }
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
