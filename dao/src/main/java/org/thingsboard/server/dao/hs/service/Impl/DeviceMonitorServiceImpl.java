package org.thingsboard.server.dao.hs.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.Aggregation;
import org.thingsboard.server.common.data.kv.BaseReadTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.dao.*;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleStatus;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;
import org.thingsboard.server.dao.hs.entity.po.DictData;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.*;
import org.thingsboard.server.dao.hs.utils.CommonComponent;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.model.sql.AlarmEntity;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceProfileEntity;
import org.thingsboard.server.dao.sql.alarm.AlarmRepository;
import org.thingsboard.server.dao.sql.attributes.AttributeKvRepository;
import org.thingsboard.server.dao.sql.device.DeviceProfileRepository;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.timeseries.TimeseriesService;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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

    // 工具
    CommonComponent commonComponent;

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

    // 设备字典部件属性Repository
    DictDeviceComponentPropertyRepository componentPropertyRepository;

    // 遥测Repository
    TimeseriesService timeseriesService;

    // 设备配置Service
    DictDeviceService dictDeviceService;

    // 数据字典Service
    DictDataService dictDataService;

    // 设备配置Service
    DeviceProfileService deviceProfileService;

    // 二方库Service
    ClientService clientService;

    // 图表Repository
    DictDeviceGraphRepository graphRepository;

    // 图表属性Repository
    DictDeviceGraphItemRepository graphItemRepository;

    /**
     * 获得设备配置列表
     *
     * @param tenantId 租户Id
     * @param name     设备配置名称
     * @param pageLink 分页排序参数
     * @return 设备配置列表
     */
    @Override
    public PageData<DeviceProfile> listPageDeviceProfiles(TenantId tenantId, String name, PageLink pageLink) {
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

        DeviceProfileVO deviceProfileVO = new DeviceProfileVO();
        BeanUtils.copyProperties(deviceProfile, deviceProfileVO);
        return deviceProfileVO;
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
    public PageData<AlarmRecordResult> listPageAlarmRecords(TenantId tenantId, AlarmRecordQuery query, TimePageLink timePageLink) {
        var deviceList = this.clientService.listDevicesByQuery(tenantId, query);
        var deviceIdMap = deviceList.stream().collect(Collectors.toMap(e -> e.getId().toString(), Function.identity()));
        var deviceIdList = deviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());

        var result = this.alarmRepository.findAlarmsByDeviceIdList(tenantId.getId(), deviceIdList, EntityType.DEVICE.toString(),
                timePageLink.getStartTime(), timePageLink.getEndTime(), query.getAlarmSimpleStatus().toAlarmStatusSet(),
                query.getAlarmSimpleLevel().toAlarmSeveritySet(), DaoUtil.toPageable(timePageLink));

        var affiliationDTO = this.clientService.getDevicesAffiliationInfo(deviceList);

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
                    .info(Optional.ofNullable(e.getDetails()).map(v -> v.get("data")).map(JsonNode::asText).orElse(null))
                    .factoryStr(Optional.ofNullable(affiliationDTO.getFactoryMap().get(device.getFactoryId())).map(Factory::getName).orElse(null))
                    .workShopStr(Optional.ofNullable(affiliationDTO.getWorkshopMap().get(device.getWorkshopId())).map(Workshop::getName).orElse(null))
                    .productionLineStr(Optional.ofNullable(affiliationDTO.getProductionLineMap().get(device.getProductionLineId())).map(ProductionLine::getName).orElse(null))
                    .picture(device.getPicture())
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
    @SuppressWarnings("all")
    public RTMonitorResult getRTMonitorData(TenantId tenantId, FactoryDeviceQuery query, PageLink pageLink) {
        var result = new RTMonitorResult();
        var uuids = this.clientService.listSimpleDevicesByQuery(tenantId, query).stream().map(Device::getId).map(DeviceId::getId).collect(Collectors.toList());
        result.setAllDeviceCount(uuids.size());
        result.setDeviceIdList(uuids.stream().map(UUID::toString).collect(Collectors.toList()));
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> this.listAlarmTimesResult(tenantId, uuids)).thenAcceptAsync(result::setAlarmTimesList),
                CompletableFuture.supplyAsync(() -> this.clientService.listPageDevicesPageByQuery(tenantId, query, pageLink))
                        .thenAcceptAsync(devicePageData -> CompletableFuture.supplyAsync(() -> {
                                    var uuidList = devicePageData.getData().stream().filter(v -> v.getPicture() == null).map(Device::getDictDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
                                    if (uuidList.isEmpty())
                                        return new HashMap<String, DictDevice>();
                                    else
                                        return DaoUtil.convertDataList(this.dictDeviceRepository.findAllByTenantIdAndIdIn(tenantId.getId(), uuidList)).stream()
                                                .collect(Collectors.toMap(DictDevice::getId, Function.identity(), (a, b) -> a, HashMap::new));
                                }).thenCombineAsync(CompletableFuture.supplyAsync(() -> this.clientService.listDevicesOnlineStatus(uuids)),
                                (dictDeviceMap, activeStatusMap) -> {
                                    int onLineCount = this.calculateValueInMap(activeStatusMap);
                                    result.setOnLineDeviceCount(onLineCount);
                                    result.setOffLineDeviceCount(result.getAllDeviceCount() - onLineCount);
                                    return devicePageData.getData().stream().map(e -> {
                                        var idStr = e.getId().toString();
                                        return RTMonitorDeviceResult.builder()
                                                .id(idStr)
                                                .name(e.getName())
                                                .image(Optional.ofNullable(e.getPicture()).orElse(Optional.ofNullable(e.getDictDeviceId()).map(UUID::toString).map(dictDeviceMap::get).map(DictDevice::getPicture).orElse(null)))
                                                .isOnLine(calculateValueInMap(activeStatusMap, idStr))
                                                .build();
                                    }).collect(Collectors.toList());
                                }).thenAcceptAsync(resultList -> result.setDevicePageData(new PageData<>(resultList, devicePageData.getTotalPages(), devicePageData.getTotalElements(), devicePageData.hasNext()))).join()
                        )).join();
        return result;
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
        var deviceBaseDTO = this.clientService.getFactoryBaseInfoByQuery(tenantId, new FactoryDeviceQuery(UUIDToString(device.getFactoryId()), UUIDToString(device.getWorkshopId()), UUIDToString(device.getProductionLineId()), device.getId().toString()));

        var kvEntryMap = this.timeseriesService.findAllLatest(tenantId, DeviceId.fromString(id)).get()
                .stream().sorted(Comparator.comparing(TsKvEntry::getKey)).collect(Collectors.toMap(TsKvEntry::getKey, Function.identity(), (key1, key2) -> key1, LinkedHashMap::new));

        Map<String, DictData> dictDataMap = this.dictDataService.getIdToDictDataMap(tenantId);

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
                                .icon(Optional.ofNullable(v.getDictDataId()).map(dictDataMap::get).map(DictData::getIcon).orElse(null))
                                .name(v.getName())
                                .title(v.getTitle())
                                .content(kvData.isEmpty() ? v.getContent() : this.formatKvEntryValue(kvData.get()))
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
                        .name(v.getKey())
                        .title(v.getKey())
                        .content(this.formatKvEntryValue(v))
                        .createdTime(v.getTs())
                        .build());
            }
        });

        return DeviceDetailResult.builder()
                .id(device.getId().toString())
                .name(device.getName())
                .picture(Optional.ofNullable(device.getPicture()).orElse(dictDevice.getPicture()))
                .isOnLine(calculateValueInMap(this.clientService.listDevicesOnlineStatus(List.of(device.getId().getId())), device.getId().toString()))
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
    public HistoryVO getGroupPropertyHistory(TenantId tenantId, String deviceId, String groupPropertyName, Long startTime, Long endTime) throws ExecutionException, InterruptedException {
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

        var dictDataMap = this.dictDataService.getIdToDictDataMap(tenantId);
        Map<String, String> rMap = Maps.newHashMap();
        var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(deviceId))).map(DeviceEntity::toData).orElse(null);
        if (device != null && device.getDictDeviceId() != null) {
            rMap = this.dictDeviceService.getNameToDictDataIdMap(device.getDictDeviceId());
        }
        Map<String, String> finalRMap = rMap;

        var data = this.timeseriesService.findAll(tenantId, DeviceId.fromString(deviceId), queries).get()
                .stream().sorted(Comparator.comparing(TsKvEntry::getTs).reversed()).map(e -> DictDeviceGroupPropertyVO.builder()
                        .content(this.formatKvEntryValue(e))
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
    public PageData<Map<String, Object>> listPageDeviceTelemetryHistories(TenantId tenantId, String deviceId, TimePageLink timePageLink) throws ExecutionException, InterruptedException {
        return this.clientService.listPageTsHistories(tenantId, DeviceId.fromString(deviceId), timePageLink);
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
    public List<DictDeviceGroupPropertyVO> listDeviceTelemetryHistoryTitles(TenantId tenantId, String deviceId) {
        List<DictDeviceGroupPropertyVO> propertyVOList = new ArrayList<>() {{
            add(DictDeviceGroupPropertyVO.builder()
                    .name(HSConstants.CREATED_TIME).title(HSConstants.CREATED_TIME).build());
        }};

        var keyList = this.timeseriesService.findAllKeysByEntityIds(tenantId, List.of(DeviceId.fromString(deviceId)));
        if (keyList.isEmpty())
            return propertyVOList;

        var dictDataMap = this.dictDataService.getIdToDictDataMap(tenantId);
        Map<String, String> rMap = Maps.newHashMap();
        Map<String, String> nameTitleMap = Maps.newHashMap();
        var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(deviceId))).map(DeviceEntity::toData).orElse(null);
        if (device != null && device.getDictDeviceId() != null) {
            rMap = this.dictDeviceService.getNameToDictDataIdMap(device.getDictDeviceId());
            nameTitleMap = this.dictDeviceService.getDictDeviceNameToTitleMap(device.getDictDeviceId());
        }
        Map<String, String> finalRMap = rMap;
        Map<String, String> finalNameTitleMap = nameTitleMap;

        propertyVOList.addAll(keyList.stream().sorted().map(e -> DictDeviceGroupPropertyVO.builder()
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
    public RTMonitorResult getRTMonitorDataForApp(TenantId tenantId, FactoryDeviceQuery query, PageLink pageLink) {
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
    public PageData<AlarmRecordResult> listPageAlarmRecordsForApp(TenantId tenantId, AlarmRecordQuery query, TimePageLink pageLink) {
        return this.listPageAlarmRecords(tenantId, query, pageLink);
    }

    /**
     * 获得报警记录统计信息
     *
     * @param tenantId 租户Id
     * @param query    请求参数
     * @return 报警记录统计信息
     */
    @Override
    public List<AlarmTimesResult> listAlarmRecordStatisticsForApp(TenantId tenantId, FactoryDeviceQuery query) {
        var allDeviceList = this.clientService.listDevicesByQuery(tenantId, query);
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
    public DeviceOnlineStatusResult getDeviceOnlineStatusData(TenantId tenantId, FactoryDeviceQuery query) {
        var allDeviceList = this.clientService.listDevicesByQuery(tenantId, query);
        var allDeviceIdList = allDeviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());
        var result = this.clientService.listDevicesOnlineStatus(allDeviceIdList);
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
     * @param tenantId  租户Id
     * @param query     查询参数
     * @param timeQuery 时间查询参数
     * @return 报警记录统计信息
     */
    @Override
    @SuppressWarnings("Duplicates")
    public BoardAlarmResult getAlarmRecordStatisticsForBoard(TenantId tenantId, FactoryDeviceQuery query, TimeQuery timeQuery) {
        var allDeviceList = this.clientService.listDevicesByQuery(tenantId, query);
        var allDeviceIdList = allDeviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());

        if (query.getIsQueryAll()) {
            var DeviceFactoryIdMap = allDeviceList.stream().collect(Collectors.toMap(e -> e.getId().toString(), e -> UUIDToStringOrElseNullStr(e.getFactoryId())));
            var factoryList = this.clientService.listFactories(tenantId);
            Map<String, Integer> factoryMap = Maps.newHashMap();

            var alarmList = this.alarmRepository.findAllAlarmsByStartTimeAndEndTime(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString(), timeQuery.getStartTime(), timeQuery.getEndTime());
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
                    .build())
                    .sorted(Comparator.comparing(BoardAlarmTimesResult::getNum).reversed())
                    .collect(Collectors.toList());

            var proportionResult = BoardAlarmLevelProportionResult.builder()
                    .criticalCount(criticalCount).majorCount(majorCount).minorCount(minorCount).
                            warningCount(warningCount).indeterminateCount(indeterminateCount)
                    .count(criticalCount + majorCount + minorCount + warningCount + indeterminateCount).build();
            return BoardAlarmResult.builder()
                    .proportionResult(proportionResult)
                    .timesResultList(timesResultList)
                    .build();
        } else if (query.isQueryFactoryOnly()) {
            var deviceNameMap = allDeviceList.stream().collect(Collectors.toMap(e -> e.getId().toString(), Device::getName));
            Map<String, Integer> deviceMap = Maps.newHashMap();
            var alarmList = this.alarmRepository.findAllAlarmsByStartTimeAndEndTime(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString(), timeQuery.getStartTime(), timeQuery.getEndTime());
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
                Optional.ofNullable(entity.getOriginatorId()).map(UUID::toString).ifPresent(e -> {
                    var value = Optional.ofNullable(deviceMap.get(e)).map(v -> v + 1).orElse(0);
                    deviceMap.put(e, value);
                });
            }

            var timesResultList = deviceMap.entrySet().stream().map(e -> BoardAlarmTimesResult.builder()
                    .value(deviceNameMap.getOrDefault(e.getKey(), e.getKey()))
                    .num(e.getValue())
                    .build())
                    .sorted(Comparator.comparing(BoardAlarmTimesResult::getNum).reversed())
                    .collect(Collectors.toList());

            var proportionResult = BoardAlarmLevelProportionResult.builder()
                    .criticalCount(criticalCount).majorCount(majorCount).minorCount(minorCount).
                            warningCount(warningCount).indeterminateCount(indeterminateCount)
                    .count(criticalCount + majorCount + minorCount + warningCount + indeterminateCount).build();
            return BoardAlarmResult.builder()
                    .proportionResult(proportionResult)
                    .timesResultList(timesResultList)
                    .build();
        } else if (query.isQueryWorkshopOnly()) {
            var deviceNameMap = allDeviceList.stream().collect(Collectors.toMap(e -> e.getId().toString(), Device::getName));
            Map<String, Integer> deviceMap = Maps.newHashMap();
            var alarmList = this.alarmRepository.findAllAlarmsByStartTimeAndEndTime(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString(), timeQuery.getStartTime(), timeQuery.getEndTime());
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
                Optional.ofNullable(entity.getOriginatorId()).map(UUID::toString).ifPresent(e -> {
                    var value = Optional.ofNullable(deviceMap.get(e)).map(v -> v + 1).orElse(0);
                    deviceMap.put(e, value);
                });
            }

            var timesResultList = deviceMap.entrySet().stream().map(e -> BoardAlarmTimesResult.builder()
                    .value(deviceNameMap.getOrDefault(e.getKey(), e.getKey()))
                    .num(e.getValue())
                    .build())
                    .sorted(Comparator.comparing(BoardAlarmTimesResult::getNum).reversed())
                    .collect(Collectors.toList());

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
     * 【看板】查看设备部件实时数据
     *
     * @param tenantId    租户Id
     * @param deviceId    设备Id
     * @param componentId 部件Id
     */
    @Override
    public List<DictDeviceComponentPropertyVO> getRtMonitorDeviceComponentDetailForBoard(TenantId tenantId, UUID deviceId, UUID componentId) throws ExecutionException, InterruptedException {
        var kvEntryMap = this.timeseriesService.findAllLatest(tenantId, DeviceId.fromString(deviceId.toString())).get()
                .stream().sorted(Comparator.comparing(TsKvEntry::getKey)).collect(Collectors.toMap(TsKvEntry::getKey, Function.identity(), (key1, key2) -> key1, LinkedHashMap::new));

        var map = this.dictDataService.getIdToDictDataMap(tenantId);

        return DaoUtil.convertDataList(this.componentPropertyRepository.findAllByComponentIdOrderBySortAsc(componentId)).stream().map(e ->
                DictDeviceComponentPropertyVO.builder()
                        .name(e.getName())
                        .content(Optional.ofNullable(kvEntryMap.get(e.getName())).map(this::formatKvEntryValue).orElse(e.getContent()))
                        .unit(Optional.ofNullable(e.getDictDataId()).map(map::get).map(DictData::getUnit).orElse(null))
                        .icon(Optional.ofNullable(e.getDictDataId()).map(map::get).map(DictData::getIcon).orElse(null))
                        .title(e.getTitle())
                        .createdTime(Optional.ofNullable(kvEntryMap.get(e.getName())).map(TsKvEntry::getTs).orElse(e.getCreatedTime()))
                        .build()
        ).collect(Collectors.toList());
    }

    /**
     * 【App】获得app首页实时监控数据，租户下全部
     *
     * @param tenantId 租户Id
     */
    @Override
    public AppIndexResult getRTMonitorIndexDataForApp(TenantId tenantId) {
        var allDeviceList = this.clientService.listDevicesByQuery(tenantId, FactoryDeviceQuery.newQueryAllEntity());
        var allDeviceIdList = allDeviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());
        var activeStatusMap = this.clientService.listDevicesOnlineStatus(allDeviceIdList);
        var count = calculateValueInMap(activeStatusMap);

        var factoryList = this.clientService.listFactories(tenantId);
        var map = new HashMap<String, AppIndexFactoryResult>();
        for (Factory factory : factoryList) {
            map.put(factory.getId().toString(), AppIndexFactoryResult.builder()
                    .offLineDeviceCount(0)
                    .onLineDeviceCount(0)
                    .id(factory.getId().toString())
                    .picture(factory.getLogoImages())
                    .name(factory.getName())
                    .build());
        }
        for (Device device : allDeviceList) {
            Optional.ofNullable(device.getFactoryId()).map(UUID::toString).map(map::get).ifPresent(e -> {
                if (Boolean.TRUE.equals(activeStatusMap.getOrDefault(device.getId().toString(), Boolean.FALSE)))
                    e.setOnLineDeviceCount(e.getOnLineDeviceCount() + 1);
                else
                    e.setOffLineDeviceCount(e.getOffLineDeviceCount() + 1);
            });
        }

        return AppIndexResult.builder()
                .onLineDeviceCount(count)
                .offLineDeviceCount(allDeviceIdList.size() - count)
                .factoryResultList(factoryList.stream().map(e -> map.get(e.getId().toString())).collect(Collectors.toList()))
                .alarmResult(AlarmDayResult.builder()
                        .historyAlarmTimes(this.alarmRepository.countAllByTenantId(tenantId.getId()))
                        .yesterdayAlarmTimes(this.alarmRepository.countAllByTenantIdAndCreatedTimeBetween(tenantId.getId(), CommonUtil.getYesterdayStartTime(), CommonUtil.getTodayStartTime()))
                        .todayAlarmTimes(this.alarmRepository.countAllByTenantIdAndCreatedTimeGreaterThan(tenantId.getId(), CommonUtil.getTodayStartTime()))
                        .build())
                .build();
    }

    /**
     * 【App】获得报警记录统计信息，按今日、昨日、历史
     *
     * @param tenantId 租户Id
     * @param query    查询条件
     */
    @Override
    public AlarmDayResult getAlarmRecordStatisticByDay(TenantId tenantId, FactoryDeviceQuery query) {
        var allDeviceList = this.clientService.listDevicesByQuery(tenantId, query);
        var allDeviceIdList = allDeviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());
        return AlarmDayResult.builder()
                .todayAlarmTimes(this.alarmRepository.countAllAlarmsByStartTime(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString(), CommonUtil.getTodayStartTime()))
                .yesterdayAlarmTimes(this.alarmRepository.countAllAlarmsByStartTimeAndEndTime(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString(), CommonUtil.getYesterdayStartTime(), CommonUtil.getTodayStartTime()))
                .historyAlarmTimes(this.alarmRepository.countAllAlarmsHistory(tenantId.getId(), allDeviceIdList, EntityType.DEVICE.toString()))
                .build();
    }

    /**
     * 分页查询设备分组属性历史数据
     *
     * @param tenantId          租户Id
     * @param deviceId          设备Id
     * @param groupPropertyName 属性名称
     * @param timePageLink      时间分页参数
     * @return 设备分组属性历史数据
     */
    @Override
    public PageData<DictDeviceGroupPropertyVO> listPageGroupPropertyHistories(TenantId tenantId, String deviceId, String groupPropertyName, TimePageLink timePageLink) throws ExecutionException, InterruptedException, ThingsboardException {
        return this.clientService.listPageTsHistories(tenantId, DeviceId.fromString(deviceId), groupPropertyName, timePageLink);
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
                propertyVO.setUnit(Optional.ofNullable(propertyVO.getDictDataId()).map(dictDataMap::get).map(DictData::getUnit).orElse(null));
                kvData.ifPresent(k -> {
                    groupPropertyNameList.add(propertyVO.getName());
                    propertyVO.setContent(this.formatKvEntryValue(kvData.get()));
                    propertyVO.setIcon(Optional.ofNullable(propertyVO.getDictDataId()).map(dictDataMap::get).map(DictData::getIcon).orElse(null));
                    propertyVO.setCreatedTime(kvData.get().getTs());
                });
            }
            if (componentVO.getComponentList() == null || componentVO.getComponentList().isEmpty()) {
                continue;
            }
            this.recursionDealComponentData(componentVO.getComponentList(), kvEntryMap, dictDataMap, groupPropertyNameList);
        }
    }

    /**
     * 获得实时监控列表数据 - 精简版
     *
     * @param tenantId 租户Id
     * @param query    查询参数
     * @param pageLink 分页参数
     * @return 实时监控列表数据
     */
    @Override
    @SuppressWarnings("all")
    public PageData<RTMonitorDeviceResult> getRTMonitorSimplificationData(TenantId tenantId, FactoryDeviceQuery query, PageLink pageLink) {
        var uuids = this.clientService.listSimpleDevicesByQuery(tenantId, query).stream().map(Device::getId).map(DeviceId::getId).collect(Collectors.toList());
        var devicePageData = this.clientService.listPageDevicesPageByQuery(tenantId, query, pageLink);
        if (devicePageData.getData().isEmpty())
            return new PageData<>(Lists.newArrayList(), devicePageData.getTotalPages(), devicePageData.getTotalElements(), devicePageData.hasNext());

        return CompletableFuture.supplyAsync(() -> {
            var dictDeviceIds = devicePageData.getData().stream().filter(v -> v.getPicture() == null).map(Device::getDictDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
            if (dictDeviceIds.isEmpty())
                return new HashMap<String, DictDevice>();
            else
                return DaoUtil.convertDataList(this.dictDeviceRepository.findAllByTenantIdAndIdIn(tenantId.getId(), dictDeviceIds)).stream()
                        .collect(Collectors.toMap(DictDevice::getId, Function.identity(), (a, b) -> a, HashMap::new));
        }).thenCombineAsync(CompletableFuture.supplyAsync(() -> this.clientService.listDevicesOnlineStatus(devicePageData.getData().stream().map(Device::getId).map(DeviceId::getId).collect(Collectors.toList()))),
                (dictDeviceMap, activeStatusMap) -> devicePageData.getData().stream().map(e -> {
                    var idStr = e.getId().toString();
                    return RTMonitorDeviceResult.builder()
                            .id(idStr)
                            .name(e.getName())
                            .image(Optional.ofNullable(e.getPicture()).orElse(Optional.ofNullable(e.getDictDeviceId()).map(UUID::toString).map(dictDeviceMap::get).map(DictDevice::getPicture).orElse(null)))
                            .isOnLine(calculateValueInMap(activeStatusMap, idStr))
                            .build();
                }).collect(Collectors.toList())).thenApplyAsync(resultList -> new PageData<>(resultList, devicePageData.getTotalPages(), devicePageData.getTotalElements(), devicePageData.hasNext())).join();
    }

    /**
     * 获得实时监控数据列表-设备在线状态
     *
     * @param tenantId 租户Id
     * @param query    查询参数
     * @return 实时监控数据列表-设备在线状态
     */
    @Override
    public RTMonitorDeviceOnlineStatusResult getRTMonitorDeviceOnlineStatusData(TenantId tenantId, FactoryDeviceQuery query) {
        RTMonitorDeviceOnlineStatusResult result = new RTMonitorDeviceOnlineStatusResult();
        var uuids = this.clientService.listSimpleDevicesByQuery(tenantId, query).stream().map(Device::getId).map(DeviceId::getId).collect(Collectors.toList());
        result.setAllDeviceCount(uuids.size());
        result.setDeviceIdList(uuids);
        result.setOnLineDeviceCount(this.calculateValueInMap(this.clientService.listDevicesOnlineStatus(uuids)));
        result.setOffLineDeviceCount(result.getAllDeviceCount() - result.getOnLineDeviceCount());
        return result;
    }

    /**
     * 获得实时监控数据列表-设备报警统计
     *
     * @param tenantId 租户Id
     * @param query    查询参数
     * @return 实时监控数据列表-设备报警统计
     */
    @Override
    public List<AlarmTimesResult> getRTMonitorDeviceAlarmStatisticsResult(TenantId tenantId, FactoryDeviceQuery query) {
        var uuids = this.clientService.listSimpleDevicesByQuery(tenantId, query).stream().map(Device::getId).map(DeviceId::getId).collect(Collectors.toList());
        return this.listAlarmTimesResult(tenantId, uuids);
    }

    /**
     * 获得实时监控数据列表-设备全部keyIds
     *
     * @param tenantId 租户Id
     * @param deviceId 设备Id
     * @return keyIds
     */
    @Override
    public List<Integer> listDeviceKeyIds(TenantId tenantId, UUID deviceId) {
        return this.clientService.listDeviceKeyIds(tenantId, deviceId);
    }

    /**
     * 获得实时监控数据列表-设备全部keys
     *
     * @param tenantId 租户Id
     * @param deviceId 设备Id
     * @return 全部keys
     */
    @Override
    public List<String> listDeviceKeys(TenantId tenantId, UUID deviceId) {
        return this.clientService.listDeviceKeys(tenantId, deviceId);
    }

    /**
     * 查询设备历史数据-无分页
     *
     * @param tenantId 租户Id
     * @param deviceId 设备Id
     * @param pageLink 分页参数
     * @return 设备历史数据
     */
    @Override
    public List<Map<String, Object>> listDeviceTelemetryHistories(TenantId tenantId, DeviceId deviceId, TimePageLink pageLink) throws ExecutionException, InterruptedException {
        return this.clientService.listTsHistories(tenantId, deviceId, pageLink);
    }

    /**
     * 查询设备详情-遥测属性历史数据图表
     *
     * @param tenantId         租户Id
     * @param deviceId         设备Id
     * @param tsPropertyName   遥测属性名称
     * @param todayStartTime   开始时间
     * @param todayCurrentTime 结束时间
     * @return 遥测属性历史数据图表
     */
    @Override
    public HistoryGraphVO getTsPropertyHistoryGraph(TenantId tenantId, UUID deviceId, String tsPropertyName, Long todayStartTime, Long todayCurrentTime) throws ThingsboardException {
        var historyGraphVO = new HistoryGraphVO();
        historyGraphVO.setName(tsPropertyName);
        var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), deviceId)).map(DaoUtil::getData).orElseThrow(() -> new ThingsboardException("设备不存在", ThingsboardErrorCode.GENERAL));
        var dictDeviceId = device.getDictDeviceId();
        if (device.getDictDeviceId() != null) {
            DictDeviceTsPropertyVO tsProperty = this.dictDeviceService.getTsPropertyByPropertyName(dictDeviceId, tsPropertyName);
            if (tsProperty != null) {
                Optional.ofNullable(tsProperty.getTitle()).filter(StringUtils::isNotBlank).ifPresent(historyGraphVO::setName);
                var graphId = this.graphItemRepository.findByPropertyIdAndPropertyType(tsProperty.getId(), tsProperty.getPropertyType().getCode())
                        .map(DictDeviceGraphItemEntity::getGraphId).orElse(null);
                if (graphId != null) {
                    var graph = this.graphRepository.findById(graphId).map(DaoUtil::getData).orElseThrow(() -> new ThingsboardException("图表不存在", ThingsboardErrorCode.GENERAL));
                    historyGraphVO.setName(graph.getName());
                    historyGraphVO.setEnable(graph.getEnable());
                    historyGraphVO.setProperties(this.graphItemRepository.findAllByGraphIdOrderBySortAsc(graphId)
                            .thenApplyAsync(v -> v.stream()
                                    .map(e -> CompletableFuture.supplyAsync(() -> this.dictDeviceService.getTsPropertyByIdAndType(e.getPropertyId(), DictDevicePropertyTypeEnum.valueOf(e.getPropertyType()))))
                                    .map(e -> e.thenApplyAsync(f -> {
                                        var data = this.listTsKvs(tenantId, DeviceId.fromString(deviceId.toString()), f.getName(), todayStartTime, todayCurrentTime);
                                        return HistoryGraphPropertyVO.builder()
                                                .tsKvs(data)
                                                .isShowChart(!data.isEmpty() && isNumberData(data.get(0).getValue()) ? Boolean.TRUE : Boolean.FALSE)
                                                .name(f.getName())
                                                .title(f.getTitle())
                                                .unit(f.getUnit())
                                                .build();
                                    }))
                                    .map(CompletableFuture::join)
                                    .collect(Collectors.toList())).join());
                    return historyGraphVO;
                }
            }
        }

        var data = this.listTsKvs(tenantId, DeviceId.fromString(deviceId.toString()), tsPropertyName, todayStartTime, todayCurrentTime);
        var property = HistoryGraphPropertyVO.builder()
                .name(tsPropertyName)
                .title(historyGraphVO.getName())
                .tsKvs(data)
                .isShowChart(!data.isEmpty() && isNumberData(data.get(0).getValue()) ? Boolean.TRUE : Boolean.FALSE)
                .build();
        historyGraphVO.setName(property.getTitle());
        historyGraphVO.setEnable(property.getIsShowChart());
        historyGraphVO.setProperties(Lists.newArrayList(property));
        return historyGraphVO;
    }

    /**
     * 获得遥测时序数据
     *
     * @param tenantId  租户Id
     * @param deviceId  设备Id
     * @param name      名称
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    @SuppressWarnings("all")
    public List<HistoryGraphPropertyTsKvVO> listTsKvs(TenantId tenantId, DeviceId deviceId, String name, Long startTime, Long endTime) {
        try {
            List<String> keyList = new ArrayList<>() {{
                add(name);
            }};
            List<ReadTsKvQuery> tempQueries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, startTime, endTime, endTime - startTime, 1, Aggregation.COUNT, "desc"))
                    .collect(Collectors.toList());

            var tempResult = this.timeseriesService.findAll(tenantId, deviceId, tempQueries).get()
                    .stream().collect(Collectors.toMap(TsKvEntry::getKey, Function.identity()));
            if (tempResult.isEmpty())
                return Lists.newArrayList();
            int count = Integer.parseInt(String.valueOf(tempResult.get(name).getValue()));
            List<ReadTsKvQuery> queries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, startTime, endTime, endTime - startTime, count, Aggregation.NONE, "desc"))
                    .collect(Collectors.toList());

            return this.timeseriesService.findAll(tenantId, deviceId, queries).get()
                    .stream().sorted(Comparator.comparing(TsKvEntry::getTs).reversed()).map(e -> HistoryGraphPropertyTsKvVO.builder()
                            .ts(e.getTs())
                            .value(this.formatKvEntryValue(e))
                            .build()).collect(Collectors.toList());
        } catch (Exception ignore) {
            return Lists.newArrayList();
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
    public void setDeviceProfileService(DeviceProfileService deviceProfileService) {
        this.deviceProfileService = deviceProfileService;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setComponentPropertyRepository(DictDeviceComponentPropertyRepository componentPropertyRepository) {
        this.componentPropertyRepository = componentPropertyRepository;
    }

    @Autowired
    public void setCommonComponent(CommonComponent commonComponent) {
        this.commonComponent = commonComponent;
    }

    @Autowired
    public void setGraphRepository(DictDeviceGraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    @Autowired
    public void setGraphItemRepository(DictDeviceGraphItemRepository graphItemRepository) {
        this.graphItemRepository = graphItemRepository;
    }
}
