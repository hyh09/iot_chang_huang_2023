package org.thingsboard.server.dao.hs.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
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
import org.thingsboard.server.dao.hs.dao.*;
import org.thingsboard.server.dao.hs.entity.bo.DeviceTimeBO;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleStatus;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;
import org.thingsboard.server.dao.hs.entity.po.DictData;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceComponent;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.manager.TrepDayStaDetailManager;
import org.thingsboard.server.dao.hs.service.*;
import org.thingsboard.server.dao.hs.utils.CommonComponent;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.hsms.entity.enums.DictDevicePropertySwitchEnum;
import org.thingsboard.server.dao.hsms.entity.vo.DictDevicePropertySwitchVO;
import org.thingsboard.server.dao.model.sql.AlarmEntity;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceProfileEntity;
import org.thingsboard.server.dao.model.sql.MesDeviceRelationEntity;
import org.thingsboard.server.dao.sql.alarm.AlarmRepository;
import org.thingsboard.server.dao.sql.attributes.AttributeKvRepository;
import org.thingsboard.server.dao.sql.device.DeviceProfileRepository;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.mesdevicerelation.MesDeviceRelationRepository;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesEquipmentProcedureVo;
import org.thingsboard.server.dao.sqlserver.mes.service.MesProductionService;
import org.thingsboard.server.dao.sqlts.Manager.TsKvLatestDaoManager;
import org.thingsboard.server.dao.timeseries.TimeseriesService;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * ???????????????????????????
 *
 * @author wwj
 * @since 2021.10.26
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class DeviceMonitorServiceImpl extends AbstractEntityService implements DeviceMonitorService, CommonService {

    // ??????
    CommonComponent commonComponent;

    // ????????????Repository
    DeviceProfileRepository deviceProfileRepository;

    // ??????Repository
    DeviceRepository deviceRepository;

    // ??????Repository
    AlarmRepository alarmRepository;

    // ??????Repository
    AttributeKvRepository attributeKvRepository;

    // ????????????Repository
    DictDeviceRepository dictDeviceRepository;

    // ????????????????????????Repository
    DictDeviceComponentPropertyRepository componentPropertyRepository;

    // ??????Repository
    TimeseriesService timeseriesService;

    // ????????????Service
    DictDeviceService dictDeviceService;

    // ????????????Service
    DictDataService dictDataService;

    // ????????????Service
    DeviceProfileService deviceProfileService;

    // ?????????Service
    ClientService clientService;

    // ??????Repository
    DictDeviceGraphRepository graphRepository;

    // ????????????Repository
    DictDeviceGraphItemRepository graphItemRepository;

    // ??????Service
    OrderRtService orderService;

    // ??????componentRepository
    DictDeviceComponentRepository componentRepository;

    @Resource
    private MesDeviceRelationRepository mesDeviceRelationRepository;

    @Resource
    private MesProductionService mesProductionService;

    @Resource
    private TrepDayStaDetailManager trepDayStaDetailManager;

    @Resource
    private TsKvLatestDaoManager tsKvLatestDaoManager;

    /**
     * ????????????????????????
     *
     * @param tenantId ??????Id
     * @param name     ??????????????????
     * @param pageLink ??????????????????
     * @return ??????????????????
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
     * ????????????????????????
     *
     * @param tenantId        ??????Id
     * @param deviceProfileId ????????????Id
     * @return ????????????
     */
    @Override
    public DeviceProfileVO getDeviceProfileDetail(TenantId tenantId, DeviceProfileId deviceProfileId) {
        var deviceProfile = deviceProfileService.findDeviceProfileById(tenantId, deviceProfileId);

        DeviceProfileVO deviceProfileVO = new DeviceProfileVO();
        BeanUtils.copyProperties(deviceProfile, deviceProfileVO);
        return deviceProfileVO;
    }

    /**
     * ????????????????????????
     *
     * @param tenantId    ??????Id
     * @param alarmId     ????????????Id
     * @param ts          ??????
     * @param alarmStatus ??????????????????
     */
    @Override
    @Transactional
    public void updateAlarmStatus(TenantId tenantId, AlarmId alarmId, long ts, AlarmStatus alarmStatus) throws ThingsboardException {
        Alarm alarm = this.alarmRepository.findByTenantIdAndId(tenantId.getId(), alarmId.getId())
                .map(AlarmEntity::toData).orElseThrow(() -> new ThingsboardException("???????????????????????????", ThingsboardErrorCode.GENERAL));
        switch (alarmStatus) {
            case ACTIVE_ACK:
                if (!AlarmSimpleStatus.valueOf(alarm.getStatus().toString()).canBeConfirm()) {
                    throw new ThingsboardException("??????????????????????????????", ThingsboardErrorCode.GENERAL);
                }
                alarm.setAckTs(ts);
                break;
            case CLEARED_ACK:
                if (!AlarmSimpleStatus.valueOf(alarm.getStatus().toString()).canBeClear()) {
                    throw new ThingsboardException("??????????????????????????????????????????", ThingsboardErrorCode.GENERAL);
                }
                if (alarm.getAckTs() == 0L)
                    alarm.setAckTs(ts);
                alarm.setClearTs(ts);
                break;
            default:
                throw new ThingsboardException("???????????????????????????", ThingsboardErrorCode.GENERAL);
        }
        alarm.setStatus(alarmStatus);
        this.alarmRepository.save(new AlarmEntity(alarm));
    }

    /**
     * ????????????????????????
     *
     * @param tenantId     ??????Id
     * @param query        ????????????
     * @param timePageLink ??????????????????
     * @return ??????????????????
     * @see AlarmRepository#findAlarms ??????
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
                    .name(device.getRename())
                    .rename(device.getRename())
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
     * ??????????????????????????????
     *
     * @param tenantId ??????Id
     * @param query    ????????????
     * @param pageLink ????????????
     * @return ????????????????????????
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
                CompletableFuture.supplyAsync(() -> this.clientService.listPageDevicesPageByQueryOrderBySort(tenantId, query, pageLink))
                        .thenAcceptAsync(devicePageData -> CompletableFuture.supplyAsync(() -> {
                                    var uuidList = devicePageData.getData().stream().map(Device::getDictDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
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
                                                .name(e.getRename())
                                                .rename(e.getRename())
                                                .image(Optional.ofNullable(e.getDictDeviceId()).map(UUID::toString).map(dictDeviceMap::get).map(DictDevice::getPicture).orElse(null))
                                                .isOnLine(calculateValueInMap(activeStatusMap, idStr))
                                                .build();
                                    }).collect(Collectors.toList());
                                }).thenAcceptAsync(resultList -> result.setDevicePageData(new PageData<>(resultList, devicePageData.getTotalPages(), devicePageData.getTotalElements(), devicePageData.hasNext()))).join()
                        )).join();
        return result;
    }

    /**
     * ??????????????????
     *
     * @param tenantId ??????Id
     * @param id       ??????id
     * @return ????????????
     */
    @Override
    public DeviceDetailResult getRTMonitorDeviceDetail(TenantId tenantId, String id) throws ExecutionException, InterruptedException, ThingsboardException {
        /**???????????????????????? ??????|????????? ??????*/
        var attributeKvMap = this.clientService.listDeviceAttributeKvs(tenantId, toUUID(id)).stream().collect(Collectors.toMap(AttributeKvEntry::getKey, Function.identity(), (a, b) -> a));
        /**??????????????????????????? device ????????? */
        var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(id))).map(DeviceEntity::toData).orElseThrow(() -> new ThingsboardException("???????????????", ThingsboardErrorCode.GENERAL));
        /** ??????????????? ?????? ?????? ??????????????? ???????????? ?????? */
        var deviceBaseDTO = this.clientService.getFactoryBaseInfoByQuery(tenantId, new FactoryDeviceQuery(UUIDToString(device.getFactoryId()), UUIDToString(device.getWorkshopId()), UUIDToString(device.getProductionLineId()), device.getId().toString()));
        /** */
        var kvEntryMap = this.timeseriesService.findAllLatest(tenantId, DeviceId.fromString(id)).get()
                .stream().sorted(Comparator.comparing(TsKvEntry::getKey)).collect(Collectors.toMap(TsKvEntry::getKey, Function.identity(), (key1, key2) -> key1, LinkedHashMap::new));

        Map<String, DictData> dictDataMap = this.dictDataService.getIdToDictDataMap(tenantId);

        List<DictDeviceGroupVO> groupResultList = new ArrayList<>();
        List<DictDeviceComponentVO> componentList = new ArrayList<>();
        List<String> groupPropertyNameList = new ArrayList<>();
        List<DictDeviceGraphVO> dictDeviceGraphList = new ArrayList<>();

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
                        var abData = Optional.ofNullable(attributeKvMap.get(v.getName()));
                        kvData.ifPresent(k -> groupPropertyNameList.add(v.getName()));
                        return DictDeviceGroupPropertyVO.builder()
                                .id(v.getId())
                                .unit(Optional.ofNullable(v.getDictDataId()).map(dictDataMap::get).map(DictData::getUnit).orElse(null))
                                .icon(Optional.ofNullable(v.getDictDataId()).map(dictDataMap::get).map(DictData::getIcon).orElse(null))
                                .picture(Optional.ofNullable(v.getDictDataId()).map(dictDataMap::get).map(DictData::getPicture).orElse(null))
                                .name(v.getName())
                                .title(v.getTitle())
                                .content(kvData.map(this::formatKvEntryValue).orElse(abData.map(this::formatKvEntryValue).orElse(v.getContent())))
                                .createdTime(kvData.map(TsKvEntry::getTs).orElse(abData.map(AttributeKvEntry::getLastUpdateTs).orElse(v.getCreatedTime())))
                                .build();
                    }).collect(Collectors.toList()))
                    .build()).collect(Collectors.toList());

            componentList = dictDeviceDetail.getComponentList();
            this.recursionDealComponentData(componentList, kvEntryMap, attributeKvMap, dictDataMap, groupPropertyNameList);

            dictDeviceGraphList = this.dictDeviceService.listDictDeviceGraphs(tenantId, device.getDictDeviceId());
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
                .name(device.getRename())
                .rename(device.getRename())
                .picture(Optional.ofNullable(dictDevice.getPicture()).orElse(null))
                .isOnLine(calculateValueInMap(this.clientService.listDevicesOnlineStatus(List.of(device.getId().getId())), device.getId().toString()))
                .factoryName(Optional.ofNullable(deviceBaseDTO.getFactory()).map(Factory::getName).orElse(null))
                .workShopName(Optional.ofNullable(deviceBaseDTO.getWorkshop()).map(Workshop::getName).orElse(null))
                .productionLineName(Optional.ofNullable(deviceBaseDTO.getProductionLine()).map(ProductionLine::getName).orElse(null))
                .isUnAllocation(this.isDeviceUnAllocation(device))
                .resultList(groupResultList)
                .componentList(componentList)
                .resultUngrouped(ungrouped)
                .alarmTimesList(this.listAlarmTimesResult(tenantId, List.of(toUUID(id))))
                .dictDeviceGraphs(dictDeviceGraphList)
                .build();
    }

    /**
     * ????????????????????????????????????
     *
     * @param tenantId          ??????Id
     * @param deviceId          ??????Id
     * @param groupPropertyName ????????????
     * @param startTime         ????????????
     * @param endTime           ????????????
     * @return ??????????????????????????????
     */
    @Override
    @SuppressWarnings("Duplicates")
    public HistoryVO getGroupPropertyHistory(TenantId tenantId, String deviceId, String groupPropertyName, Long
            startTime, Long endTime) throws ExecutionException, InterruptedException {
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
     * ????????????????????????????????????
     *
     * @param tenantId         ??????Id
     * @param deviceId         ??????Id
     * @param isShowAttributes ??????????????????
     * @param timePageLink     ??????????????????
     * @return ??????????????????????????????
     */
    @Override
    public PageData<Map<String, Object>> listPageDeviceTelemetryHistories(TenantId tenantId, String deviceId,
                                                                          boolean isShowAttributes, TimePageLink timePageLink) throws ExecutionException, InterruptedException {
        var pageData = this.clientService.listPageTsHistories(tenantId, DeviceId.fromString(deviceId), timePageLink);
        if (isShowAttributes) {
            var attributeData = this.clientService.listDeviceAttributeKvs(tenantId, toUUID(deviceId)).stream().collect(Collectors.toMap(AttributeKvEntry::getKey, AttributeKvEntry::getValueAsString));
            pageData.getData().forEach(v -> v.putAll(attributeData));
        }
        return pageData;
    }

    /**
     * ??????????????????-?????????????????????
     *
     * @param tenantId         ??????Id
     * @param deviceId         ??????Id
     * @param isShowAttributes ??????????????????
     * @return ??????????????????-?????????????????????
     */
    @Override
    @SuppressWarnings("Duplicates")
    public List<DictDeviceGroupPropertyVO> listDeviceTelemetryHistoryTitles(TenantId tenantId, String deviceId,
                                                                            boolean isShowAttributes) throws ExecutionException, InterruptedException {
        List<DictDeviceGroupPropertyVO> propertyVOList = new ArrayList<>() {{
            add(DictDeviceGroupPropertyVO.builder()
                    .name(HSConstants.CREATED_TIME).title(HSConstants.CREATED_TIME).build());
        }};

        var keyList = this.timeseriesService.findAllLatest(tenantId, DeviceId.fromString(deviceId.toString())).get().stream().map(TsKvEntry::getKey).collect(Collectors.toList());
        if (isShowAttributes)
            keyList.addAll(this.clientService.listDeviceAttributeKvs(tenantId, toUUID(deviceId)).stream().map(AttributeKvEntry::getKey).collect(Collectors.toList()));
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
     * ???APP?????????????????????????????????
     *
     * @param tenantId ??????Id
     * @param query    ????????????
     * @param pageLink ????????????
     * @return ????????????????????????
     */
    @Override
    public RTMonitorResult getRTMonitorDataForApp(TenantId tenantId, FactoryDeviceQuery query, PageLink pageLink) {
        return this.getRTMonitorData(tenantId, query, pageLink);
    }

    /**
     * ???APP???????????????????????????
     *
     * @param tenantId ??????Id
     * @param query    ????????????
     * @param pageLink ??????????????????
     * @return ??????????????????
     */
    @Override
    public PageData<AlarmRecordResult> listPageAlarmRecordsForApp(TenantId tenantId, AlarmRecordQuery
            query, TimePageLink pageLink) {
        return this.listPageAlarmRecords(tenantId, query, pageLink);
    }

    /**
     * ??????????????????????????????
     *
     * @param tenantId ??????Id
     * @param query    ????????????
     * @return ????????????????????????
     */
    @Override
    public List<AlarmTimesResult> listAlarmRecordStatisticsForApp(TenantId tenantId, FactoryDeviceQuery query) {
        var allDeviceList = this.clientService.listDevicesByQuery(tenantId, query);
        var allDeviceIdList = allDeviceList.stream().map(e -> e.getId().getId()).collect(Collectors.toList());
        return this.listAlarmTimesResult(tenantId, allDeviceIdList);
    }

    /**
     * ????????????????????????
     *
     * @param tenantId ??????Id
     * @param query    ????????????
     * @return ??????????????????
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
     * ??????????????????????????????????????????
     *
     * @param tenantId  ??????Id
     * @param query     ????????????
     * @param timeQuery ??????????????????
     * @return ????????????????????????
     */
    @Override
    @SuppressWarnings("Duplicates")
    public BoardAlarmResult getAlarmRecordStatisticsForBoard(TenantId tenantId, FactoryDeviceQuery query, TimeQuery
            timeQuery) {
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
     * ??????????????????????????????????????????
     *
     * @param tenantId    ??????Id
     * @param deviceId    ??????Id
     * @param componentId ??????Id
     */
    @Override
    public List<DictDeviceComponentPropertyVO> getRtMonitorDeviceComponentDetailForBoard(TenantId tenantId, UUID
            deviceId, UUID componentId) throws ExecutionException, InterruptedException {
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
     * ???App?????????app??????????????????????????????????????????
     *
     * @param tenantId ??????Id
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
     * ???App???????????????????????????????????????????????????????????????
     *
     * @param tenantId ??????Id
     * @param query    ????????????
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
     * ??????????????????????????????????????????
     *
     * @param tenantId          ??????Id
     * @param deviceId          ??????Id
     * @param groupPropertyName ????????????
     * @param timePageLink      ??????????????????
     * @return ??????????????????????????????
     */
    @Override
    public PageData<DictDeviceGroupPropertyVO> listPageGroupPropertyHistories(TenantId tenantId, String
            deviceId, String groupPropertyName, TimePageLink timePageLink) throws
            ExecutionException, InterruptedException, ThingsboardException {
        return this.clientService.listPageTsHistories(tenantId, DeviceId.fromString(deviceId), groupPropertyName, timePageLink);
    }

    /**
     * ??????????????????????????????
     *
     * @param tenantId        ??????id
     * @param allDeviceIdList ??????id??????
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
     * ??????????????????????????????
     *
     * @param componentList         ????????????
     * @param kvEntryMap            ????????????map
     * @param attributeKvMap        ????????????map
     * @param dictDataMap           ????????????map
     * @param groupPropertyNameList ??????List
     */
    public void recursionDealComponentData
    (List<DictDeviceComponentVO> componentList, Map<String, TsKvEntry> kvEntryMap, Map<String, AttributeKvEntry> attributeKvMap, Map<String, DictData> dictDataMap, List<String> groupPropertyNameList) {
        for (DictDeviceComponentVO componentVO : componentList) {
            for (DictDeviceComponentPropertyVO propertyVO : componentVO.getPropertyList()) {
                var kvData = Optional.ofNullable(kvEntryMap.get(propertyVO.getName()));
                var abData = Optional.ofNullable(attributeKvMap.get(propertyVO.getName()));
                propertyVO.setUnit(Optional.ofNullable(propertyVO.getDictDataId()).map(dictDataMap::get).map(DictData::getUnit).orElse(null));
                kvData.ifPresentOrElse(k -> {
                    groupPropertyNameList.add(propertyVO.getName());
                    propertyVO.setContent(this.formatKvEntryValue(k));
                    propertyVO.setIcon(Optional.ofNullable(propertyVO.getDictDataId()).map(dictDataMap::get).map(DictData::getIcon).orElse(null));
                    propertyVO.setCreatedTime(k.getTs());
                }, () -> abData.ifPresent(v -> {
                    propertyVO.setContent(this.formatKvEntryValue(v));
                    propertyVO.setCreatedTime(v.getLastUpdateTs());
                }));
            }
            if (componentVO.getComponentList() == null || componentVO.getComponentList().isEmpty()) {
                continue;
            }
            this.recursionDealComponentData(componentVO.getComponentList(), kvEntryMap, attributeKvMap, dictDataMap, groupPropertyNameList);
        }
    }

    /**
     * ?????????????????????????????? - ?????????
     *
     * @param tenantId ??????Id
     * @param query    ????????????
     * @param pageLink ????????????
     * @return ????????????????????????
     */
    @Override
    @SuppressWarnings("all")
    public PageData<RTMonitorDeviceResult> getRTMonitorSimplificationData(TenantId tenantId, FactoryDeviceQuery
            query, PageLink pageLink) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("1");
        var devicePageData = this.clientService.listPageDevicesPageByQuery(tenantId, query, pageLink);
        if (devicePageData.getData().isEmpty())
            return new PageData<>(Lists.newArrayList(), devicePageData.getTotalPages(), devicePageData.getTotalElements(), devicePageData.hasNext());
        //??????????????????Future
        MesEquipmentProcedureVo defaulVo = new MesEquipmentProcedureVo();
        CompletableFuture<Map<String, Boolean>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> this.clientService.listDevicesOnlineStatus(devicePageData.getData().stream().map(Device::getId).map(DeviceId::getId).collect(Collectors.toList())));
        stopWatch.stop();
        stopWatch.start("2");
        //??????????????????????????????????????????
        var deviceIds = devicePageData.getData().stream().map(e -> e.getId().getId()).filter(Objects::nonNull).collect(Collectors.toList());
        List<MesDeviceRelationEntity> deviceRelationEntityList = mesDeviceRelationRepository.findAllByDeviceIdIn(deviceIds);
        Map<UUID, UUID> id2mesIdMap = deviceRelationEntityList.stream().collect(Collectors.toMap(MesDeviceRelationEntity::getDeviceId, MesDeviceRelationEntity::getMesDeviceId));
        List<UUID> equipmentIds = deviceRelationEntityList.stream().map(MesDeviceRelationEntity::getMesDeviceId).collect(Collectors.toList());
        List<MesEquipmentProcedureVo> equipmentProcedure = mesProductionService.findEquipmentProcedure(equipmentIds);
        Map<String, MesEquipmentProcedureVo> id2voMap = equipmentProcedure.stream().collect(Collectors.toMap(MesEquipmentProcedureVo::getMesDeviceId, Function.identity(), (v1, v2) -> v1));
        stopWatch.stop();
        stopWatch.start("3");
        PageData<RTMonitorDeviceResult> result = mapCompletableFuture.thenApply(
                activeStatusMap -> devicePageData.getData().stream().map(e -> {
                    UUID id = e.getId().getId();
                    var idStr = e.getId().toString();
                    UUID mesDid = id2mesIdMap.get(id);
                    MesEquipmentProcedureVo mesEquipmentProcedureVo = id2voMap.getOrDefault(mesDid, defaulVo);
                    return RTMonitorDeviceResult.builder()
                            .id(idStr)
                            .name(e.getRename())
                            .rename(e.getRename())
                            .image(e.getPicture())
                            .isOnLine(calculateValueInMap(activeStatusMap, idStr))
                            .cardNo(mesEquipmentProcedureVo.getCardNo())
                            .materialName(mesEquipmentProcedureVo.getMaterialName())
                            .workerGroupName(mesEquipmentProcedureVo.getWorkerGroupName())
                            .build();
                }).collect(Collectors.toList())).thenApplyAsync(resultList -> new PageData<>(resultList, devicePageData.getTotalPages(), devicePageData.getTotalElements(), devicePageData.hasNext())).join();
        stopWatch.stop();
        stopWatch.start("4");
        List<RTMonitorDeviceResult> data = result.getData();
        trepDayStaDetailManager.setRateBatch(data, new Date(), e -> UUID.fromString(e.getId()), RTMonitorDeviceResult::setOperationRate);
        tsKvLatestDaoManager.setStateBatch(data, e -> UUID.fromString(e.getId()), RTMonitorDeviceResult::getIsOnLine, RTMonitorDeviceResult::setState);
        stopWatch.stop();
        log.info("????????????: {}", stopWatch.prettyPrint());
        return result;
    }

    /**
     * ??????????????????????????????-??????????????????
     *
     * @param tenantId ??????Id
     * @param query    ????????????
     * @return ????????????????????????-??????????????????
     */
    @Override
    public RTMonitorDeviceOnlineStatusResult getRTMonitorDeviceOnlineStatusData(TenantId
                                                                                        tenantId, FactoryDeviceQuery query) {
        RTMonitorDeviceOnlineStatusResult result = new RTMonitorDeviceOnlineStatusResult();
        var uuids = this.clientService.listSimpleDevicesByQuery(tenantId, query).stream().map(Device::getId).map(DeviceId::getId).collect(Collectors.toList());
        result.setAllDeviceCount(uuids.size());
        result.setDeviceIdList(uuids);
        result.setOnLineDeviceCount(this.calculateValueInMap(this.clientService.listDevicesOnlineStatus(uuids)));
        result.setOffLineDeviceCount(result.getAllDeviceCount() - result.getOnLineDeviceCount());
        return result;
    }

    /**
     * ??????????????????????????????-??????????????????
     *
     * @param tenantId ??????Id
     * @param query    ????????????
     * @return ????????????????????????-??????????????????
     */
    @Override
    public List<AlarmTimesResult> getRTMonitorDeviceAlarmStatisticsResult(TenantId tenantId, FactoryDeviceQuery
            query) {
        var uuids = this.clientService.listSimpleDevicesByQuery(tenantId, query).stream().map(Device::getId).map(DeviceId::getId).collect(Collectors.toList());
        return this.listAlarmTimesResult(tenantId, uuids);
    }

    /**
     * ??????????????????????????????-????????????keyIds
     *
     * @param tenantId ??????Id
     * @param deviceId ??????Id
     * @return keyIds
     */
    @Override
    public List<Integer> listDeviceKeyIds(TenantId tenantId, UUID deviceId) {
        return this.clientService.listDeviceKeyIds(tenantId, deviceId);
    }

    /**
     * ??????????????????????????????-????????????keys
     *
     * @param tenantId ??????Id
     * @param deviceId ??????Id
     * @return ??????keys
     */
    @Override
    public List<String> listDeviceKeys(TenantId tenantId, UUID deviceId) {
        return this.clientService.listDeviceKeys(tenantId, deviceId);
    }

    /**
     * ????????????????????????-?????????
     *
     * @param tenantId ??????Id
     * @param deviceId ??????Id
     * @param pageLink ????????????
     * @return ??????????????????
     */
    @Override
    public List<Map<String, Object>> listDeviceTelemetryHistories(TenantId tenantId, DeviceId
            deviceId, TimePageLink pageLink) throws ExecutionException, InterruptedException {
        return this.clientService.listTsHistories(tenantId, deviceId, pageLink);
    }

    /**
     * ??????????????????-??????????????????????????????
     *
     * @param tenantId         ??????Id
     * @param deviceId         ??????Id
     * @param tsPropertyName   ?????????????????? ?????????water
     * @param todayStartTime   ????????????
     * @param todayCurrentTime ????????????
     * @return ??????????????????????????????
     */
    @Override
    public HistoryGraphVO getTsPropertyHistoryGraph(TenantId tenantId, UUID deviceId, String tsPropertyName, Long
            todayStartTime, Long todayCurrentTime) throws ThingsboardException {
        var historyGraphVO = new HistoryGraphVO();
        historyGraphVO.setName(tsPropertyName);
        /** 1.?????????????????? ????????????id????????? */
        var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), deviceId)).map(DaoUtil::getData).orElseThrow(() -> new ThingsboardException("???????????????", ThingsboardErrorCode.GENERAL));
        var dictDeviceId = device.getDictDeviceId();
        if (device.getDictDeviceId() != null) {
            DictDeviceTsPropertyVO tsProperty = this.dictDeviceService.getTsPropertyByPropertyName(dictDeviceId, tsPropertyName);
            if (tsProperty != null) {
                Optional.ofNullable(tsProperty.getTitle()).filter(StringUtils::isNotBlank).ifPresent(historyGraphVO::setName);
                var graphId = this.graphItemRepository.findByPropertyIdAndPropertyType(tsProperty.getId(), tsProperty.getPropertyType().getCode())
                        .map(DictDeviceGraphItemEntity::getGraphId).orElse(null);
                if (graphId != null) {
                    var graph = this.graphRepository.findById(graphId).map(DaoUtil::getData).orElseThrow(() -> new ThingsboardException("???????????????", ThingsboardErrorCode.GENERAL));
                    historyGraphVO.setName(graph.getName());
                    historyGraphVO.setEnable(graph.getEnable());
                    historyGraphVO.setProperties(this.graphItemRepository.findAllByGraphIdOrderBySortAsc(graphId)
                            .thenApplyAsync(v -> v.stream()
                                    .map(e -> CompletableFuture.supplyAsync(() -> this.dictDeviceService.getTsPropertyByIdAndType(e.getPropertyId(), DictDevicePropertyTypeEnum.valueOf(e.getPropertyType()))))
                                    .map(e -> e.thenApplyAsync(f -> {
                                        var data = this.listTsKvs(tenantId, DeviceId.fromString(deviceId.toString()), f.getName(), todayStartTime, todayCurrentTime);
                                        return HistoryGraphPropertyVO.builder()
                                                .tsKvs(this.cleanGraphTsKvData(data))
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
                .tsKvs(this.cleanGraphTsKvData(data))
                .unit(Optional.ofNullable(dictDeviceId).map(e -> this.dictDeviceService.getTsPropertyByPropertyName(e, tsPropertyName)).map(DictDeviceTsPropertyVO::getUnit).orElse(null))
                .isShowChart(!data.isEmpty() && isNumberData(data.get(0).getValue()) ? Boolean.TRUE : Boolean.FALSE)
                .build();
        historyGraphVO.setName(property.getTitle());
        historyGraphVO.setEnable(property.getIsShowChart());
        historyGraphVO.setProperties(Lists.newArrayList(property));
        return historyGraphVO;
    }

    /**
     * ???app?????????????????????
     *
     * @param tenantId     ??????Id
     * @param deviceId     ??????Id
     * @param graphId      ??????Id
     * @param timePageLink ??????
     * @return ????????????
     */
    @Override
    public HistoryGraphAppVO getGraphHistoryForApp(TenantId tenantId, UUID deviceId, UUID graphId, TimePageLink
            timePageLink) throws ThingsboardException, ExecutionException, InterruptedException {
        var dictDeviceGraphVO = this.dictDeviceService.getDictDeviceGraphDetail(tenantId, graphId);
        List<HistoryGraphPropertyVO> historyGraphProperties = Lists.newArrayList();
        if (!dictDeviceGraphVO.getProperties().isEmpty()) {
            var firstV = dictDeviceGraphVO.getProperties().get(0);
            var firstVs = this.clientService.listPageTsHistories(tenantId, DeviceId.fromString(UUIDToString(deviceId)), firstV.getName(), timePageLink);
            var tsList = firstVs.getData().stream().map(DictDeviceGroupPropertyVO::getCreatedTime).collect(Collectors.toList());
            Map<String, List<HistoryGraphPropertyTsKvVO>> data = Maps.newHashMap();
            if (!tsList.isEmpty()) {
                var time1 = tsList.get(0);
                var time2 = tsList.get(tsList.size() - 1);
                var others = dictDeviceGraphVO.getProperties().stream().map(DictDeviceGraphPropertyVO::getName).filter(name -> !firstV.getName().equals(name)).collect(Collectors.toList());
                if (!others.isEmpty()) {
                    data = this.clientService.listTsHistoriesByProperties(tenantId, deviceId, Math.min(time2, time1), Math.max(time2, time1), others);
                }
            }
            data.put(firstV.getName(), firstVs.getData().stream().map(v -> HistoryGraphPropertyTsKvVO.builder().ts(v.getCreatedTime()).value(v.getContent()).build()).collect(Collectors.toList()));

            Map<String, List<HistoryGraphPropertyTsKvVO>> finalData = data;
            historyGraphProperties = dictDeviceGraphVO.getProperties().stream().map(v -> {
                var map = finalData.getOrDefault(v.getName(), Lists.newArrayList()).stream().filter(f -> StringUtils.isNotBlank(f.getValue())).collect(Collectors.toMap(HistoryGraphPropertyTsKvVO::getTs, HistoryGraphPropertyTsKvVO::getValue));
                return HistoryGraphPropertyVO.builder()
                        .suffix(v.getSuffix())
                        .unit(v.getUnit())
                        .name(v.getName())
                        .title(v.getTitle())
                        .tsKvs(tsList.stream().map(g -> HistoryGraphPropertyTsKvVO.builder().ts(g).value(map.getOrDefault(g, null)).build()).collect(Collectors.toList()))
                        .build();
            }).collect(Collectors.toList());
        }

        return HistoryGraphAppVO.builder()
                .enable(dictDeviceGraphVO.getEnable())
                .name(dictDeviceGraphVO.getName())
                .properties(historyGraphProperties)
                .build();
    }

    /**
     * ??????????????????????????????
     *
     * @param tenantId ??????Id
     * @param deviceId ??????Id
     * @return ??????????????????
     */
    @Override
    public DeviceKeyParametersResult getDeviceKeyParameters(TenantId tenantId, UUID deviceId) throws
            ThingsboardException {
        var result = new DeviceKeyParametersResult();
        result.setOperationRate(0d);
        result.setQualityRate(0d);
        result.setCapacityEfficiency(0d);

        var now = System.currentTimeMillis();
        var todayStartTime = CommonUtil.getTodayStartTime();
        var tomorrowStartTime = CommonUtil.getTomorrowStartTime();
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> this.orderService.listDeviceMaintainTimes(tenantId, deviceId, todayStartTime, tomorrowStartTime))
                        .thenAcceptAsync(v -> result.setMaintenanceDuration(this.toDoubleHour(this.statisticsTime(v)))),

                CompletableFuture.supplyAsync(() -> this.clientService.getDeviceOEE(tenantId, deviceId, now))
                        .thenAcceptAsync(result::setOee),

                CompletableFuture.runAsync(() -> {
                    var shirtTimes = this.clientService.listDeviceShirtTimes(tenantId, deviceId, todayStartTime, tomorrowStartTime);
                    var shirtTimeL = this.statisticsTime(shirtTimes);
                    result.setShiftDuration(this.toDoubleHour(shirtTimeL));

                    CompletableFuture.allOf(
                            CompletableFuture.runAsync(() -> {
                                var deviceTimeBO = shirtTimes.stream().map(v -> CompletableFuture.supplyAsync(() -> ImmutablePair.of(v, this.clientService.listDeviceTss(tenantId, deviceId, v.getStartTime(), v.getEndTime()))))
                                        .map(v -> v.thenApplyAsync(f -> {
                                            if (f.getRight().isEmpty())
                                                return DeviceTimeBO.builder().startingUpTime(0L).shutdownTime(f.getLeft().getEndTime() - f.getLeft().getStartTime()).build();
                                            else {
                                                var total = 0L;
                                                var cTime = f.getLeft().getStartTime();
                                                for (Long t : f.getRight()) {
                                                    if ((t - cTime) >= 30 * 60 * 1000) {
                                                        total += (t - cTime);
                                                    }
                                                    cTime = t;
                                                }
                                                if ((f.getLeft().getEndTime() - cTime) >= 30 * 60 * 1000)
                                                    total += (f.getLeft().getEndTime() - cTime);
                                                return DeviceTimeBO.builder().startingUpTime((f.getLeft().getEndTime() - f.getLeft().getStartTime()) - total).shutdownTime(total).build();
                                            }
                                        })).map(CompletableFuture::join).reduce(DeviceTimeBO.builder().shutdownTime(0L).startingUpTime(0L).build(), (r, e) -> {
                                            r.setStartingUpTime(r.getStartingUpTime() + e.getStartingUpTime());
                                            r.setShutdownTime(r.getShutdownTime() + e.getShutdownTime());
                                            return r;
                                        }, (a, b) -> null);

                                result.setStartingUpDuration(this.toDoubleHour(deviceTimeBO.getStartingUpTime()));
                                result.setShutdownDuration(this.toDoubleHour(deviceTimeBO.getShutdownTime()));
                                if (shirtTimeL != 0L)
                                    result.setOperationRate(this.formatDoubleData(BigDecimal.valueOf(deviceTimeBO.getStartingUpTime() * 1.0d / shirtTimeL * 100)));
                            }),

                            CompletableFuture.runAsync(() -> {
                                var deviceEntity = this.deviceRepository.findByTenantIdAndId(tenantId.getId(), deviceId);
                                var device = deviceEntity.toData();
                                result.setId(device.getId().toString());
                                result.setName(device.getRename());
                                result.setRename(device.getRename());

                                var plans = this.orderService.listDeviceOrderPlansInActualTimeField(tenantId, deviceId, todayStartTime, tomorrowStartTime);
                                var actualCapacityTotal = plans.stream().reduce(BigDecimal.ZERO, (r, e) -> {
                                    if (e.getActualCapacity() != null) {
                                        return r.add(e.getActualCapacity());
                                    }
                                    return r;
                                }, (a, b) -> null);

                                try {
                                    var ratedCapacity = this.dictDeviceService.getDictDeviceDetail(device.getDictDeviceId().toString(), tenantId).getRatedCapacity();
                                    var trueCapacityTotal = this.clientService.getOrderCapacitiesByTs(plans);
                                    result.setCapacityEfficiency(this.formatDoubleData(trueCapacityTotal.multiply(new BigDecimal(100)).divide(ratedCapacity.multiply(this.toDecimalHour(shirtTimeL)), 2, RoundingMode.HALF_UP)));
                                } catch (Exception ignore) {
                                }

                                var shirtActualCapacityTotal = shirtTimes.stream()
                                        .map(v -> CompletableFuture.supplyAsync(() -> this.orderService.listDeviceOrderPlansInActualTimeField(tenantId, deviceId, v.getStartTime(), v.getEndTime())))
                                        .map(v -> v.thenApplyAsync(f -> this.clientService.getOrderCapacitiesByTs(f)))
                                        .map(CompletableFuture::join)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add, (a, b) -> null);

                                result.setOutput(this.formatDoubleData(shirtActualCapacityTotal));
                                if (shirtActualCapacityTotal.compareTo(BigDecimal.ZERO) > 0)
                                    result.setQualityRate(this.formatDoubleData(actualCapacityTotal.multiply(new BigDecimal(100)).divide(shirtActualCapacityTotal, 2, RoundingMode.HALF_UP)));
                                result.setInQualityNum(this.formatNegativeNumber(this.formatDoubleData(shirtActualCapacityTotal.subtract(actualCapacityTotal))));
                            })
                    ).join();
                })
        ).join();

        return result;
    }

    /**
     * ????????????????????????
     *
     * @param tenantId    ??????Id
     * @param deviceId    ??????Id
     * @param componentId ??????Id
     * @return ????????????
     */
    @Override
    public String getRtMonitorDeviceComponentName(TenantId tenantId, UUID deviceId, UUID componentId) {
        return this.componentRepository.findById(componentId).map(DictDeviceComponentEntity::toData).map(DictDeviceComponent::getName).orElse("");
    }

    /**
     * ??????????????????
     *
     * @param deviceDetailResult ??????????????????
     * @param tenantId           ??????Id
     * @param isFactoryUser      ??????????????????
     */
    @Override
    public DeviceDetailResult filterDeviceDetailResult(TenantId tenantId, DeviceDetailResult
            deviceDetailResult, Boolean isFactoryUser) throws ThingsboardException {
        if (!isFactoryUser) {
            log.info("?????????????????????????????????");
            return deviceDetailResult;
        }
        log.info("???????????????????????????");
        var propertyShowSet = this.dictDeviceService.listDictDeviceSwitches(tenantId, deviceDetailResult.getId())
                .stream().filter(v -> DictDevicePropertySwitchEnum.SHOW.equals(v.getPropertySwitch()))
                .map(DictDevicePropertySwitchVO::getPropertyName)
                .collect(Collectors.toSet());

        if (propertyShowSet.isEmpty())
            return deviceDetailResult;
        else {
            deviceDetailResult.getResultList().forEach(v -> v.getGroupPropertyList().removeIf(f -> !propertyShowSet.contains(f.getName())));
            this.recursionFilterComponentData(deviceDetailResult.getComponentList(), propertyShowSet);
        }
        return deviceDetailResult;
    }

    /**
     * ??????????????????
     *
     * @param tenantId      ??????Id
     * @param properties    ????????????
     * @param deviceId      ??????Id
     * @param isFactoryUser ??????????????????
     */
    @Override
    public List<DictDeviceGroupPropertyVO> filterDictDeviceProperties(TenantId tenantId, String
            deviceId, List<DictDeviceGroupPropertyVO> properties, Boolean isFactoryUser) throws ThingsboardException {
        if (!isFactoryUser)
            return properties;
        var propertyShowSet = this.dictDeviceService.listDictDeviceSwitches(tenantId, deviceId)
                .stream().filter(v -> DictDevicePropertySwitchEnum.SHOW.equals(v.getPropertySwitch()))
                .map(DictDevicePropertySwitchVO::getPropertyName)
                .collect(Collectors.toSet());
        properties.removeIf(v -> !propertyShowSet.contains(v.getName()));
        return properties;
    }

    /**
     * ????????????????????????
     *
     * @param componentList   ????????????
     * @param propertyShowSet ????????????
     */
    public void recursionFilterComponentData
    (List<DictDeviceComponentVO> componentList, Set<String> propertyShowSet) {
        for (DictDeviceComponentVO componentVO : componentList) {
            componentVO.getPropertyList().removeIf(dictDeviceComponentPropertyVO -> !propertyShowSet.contains(dictDeviceComponentPropertyVO.getName()));

            if (componentVO.getComponentList() == null || componentVO.getComponentList().isEmpty()) {
                continue;
            }
            this.recursionFilterComponentData(componentVO.getComponentList(), propertyShowSet);
        }
    }

    /**
     * ????????????????????????
     *
     * @param tenantId  ??????Id
     * @param deviceId  ??????Id
     * @param name      ??????
     * @param startTime ????????????
     * @param endTime   ????????????
     */
    @SuppressWarnings("all")
    public List<HistoryGraphPropertyTsKvVO> listTsKvs(TenantId tenantId, DeviceId deviceId, String name, Long
            startTime, Long endTime) {
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

    @Autowired
    public void setOrderService(OrderRtService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setComponentRepository(DictDeviceComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }
}
