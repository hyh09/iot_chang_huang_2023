package org.thingsboard.server.dao.hs.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.kv.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.deviceoeeeveryhour.DeviceOeeEveryHourService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.factory.FactoryService;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.dao.InitEntity;
import org.thingsboard.server.dao.hs.dao.InitRepository;
import org.thingsboard.server.dao.hs.entity.bo.FactoryDetailBO;
import org.thingsboard.server.dao.hs.entity.bo.OrderCapacityBO;
import org.thingsboard.server.dao.hs.entity.bo.OrderDeviceCapacityBO;
import org.thingsboard.server.dao.hs.entity.dto.DeviceBaseDTO;
import org.thingsboard.server.dao.hs.entity.dto.DeviceListAffiliationDTO;
import org.thingsboard.server.dao.hs.entity.enums.InitScopeEnum;
import org.thingsboard.server.dao.hs.entity.po.DictData;
import org.thingsboard.server.dao.hs.entity.po.OrderPlan;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.hs.utils.CommonComponent;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.model.sql.*;
import org.thingsboard.server.dao.model.sqlts.dictionary.TsKvDictionary;
import org.thingsboard.server.dao.model.sqlts.ts.TsKvEntity;
import org.thingsboard.server.dao.sql.attributes.AttributeKvRepository;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sql.factory.FactoryRepository;
import org.thingsboard.server.dao.sql.mesdevicerelation.MesDeviceRelationRepository;
import org.thingsboard.server.dao.sql.productioncalender.ProductionCalenderRepository;
import org.thingsboard.server.dao.sql.productionline.ProductionLineRepository;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;
import org.thingsboard.server.dao.sql.user.UserRepository;
import org.thingsboard.server.dao.sql.workshop.WorkshopRepository;
import org.thingsboard.server.dao.sqlts.dictionary.TsKvDictionaryRepository;
import org.thingsboard.server.dao.sqlts.latest.TsKvLatestRepository;
import org.thingsboard.server.dao.sqlts.ts.TsKvRepository;
import org.thingsboard.server.dao.timeseries.TimeseriesService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ????????????????????????
 *
 * @author wwj
 * @since 2021.11.1
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ClientServiceImpl extends AbstractEntityService implements ClientService, CommonService {

    @PersistenceContext
    protected EntityManager entityManager;

    @Value("${state.persistToTelemetry:false}")
    @Getter
    private boolean persistToTelemetry;

    // ??????
    CommonComponent commonComponent;

    // ?????????Repository
    InitRepository initRepository;

    // ??????Service
    FactoryService factoryService;

    // ??????Repository
    FactoryRepository factoryRepository;

    // ??????Repository
    WorkshopRepository workshopRepository;

    // ??????Repository
    ProductionLineRepository productionLineRepository;

    // ??????Repository
    DeviceRepository deviceRepository;

    // ??????Repository
    AttributeKvRepository attributeKvRepository;

    // ??????Dictionary Repository
    TsKvDictionaryRepository tsDictionaryRepository;

    // ??????Repository
    TsKvRepository tsRepository;

    // ??????Latest Repository
    TsKvLatestRepository tsLatestRepository;

    // ??????Service
    AttributesService attributesService;

    // ??????Service
    TimeseriesService tsService;

    // ????????????Service
    DictDeviceService dictDeviceService;

    // ??????Repository
    UserRepository userRepository;

    // BulletinBoardSvc
    BulletinBoardSvc bulletinBoardSvc;

    // ProductionCalenderRepository
    ProductionCalenderRepository calenderRepository;

    // DeviceOeeEveryHourService
    DeviceOeeEveryHourService deviceOeeEveryHourService;

    // FactoryDao
    FactoryDao factoryDao;

    // MesDeviceRelationRepository
    MesDeviceRelationRepository mesDeviceRelationRepository;

    /**
     * iot??????Id?????????MesId
     *
     * @param uuids id??????
     */
    @Override
    public List<UUID> toMesDeviceIds(List<UUID> uuids) {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(MesDeviceRelationEntity.class);
        var root = query.from(MesDeviceRelationEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var in = cb.in(root.<UUID>get("deviceId"));
        uuids.forEach(in::value);
        predicates.add(in);

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList()
                .stream().map(AbstractMesDeviceRelationEntity::getMesDeviceId).collect(Collectors.toList());
    }

    /**
     * ??????????????????????????????
     *
     * @param deviceId ??????Id
     */
    @Override
    public Boolean isDeviceOnline(UUID deviceId) {
        return this.getDeviceOnlineStatus(DeviceId.fromString(deviceId.toString()));
    }

    /**
     * ??????????????????
     */
    @Override
    public List<Factory> listFactories() {
        return DaoUtil.convertDataList(this.factoryRepository.findAllSimple());
    }

    /**
     * ????????????
     *
     * @param userId ??????Id
     */
    @Override
    public User getUserByUserId(UserId userId) {
        return this.userRepository.findById(userId.getId()).map(UserEntity::toData).orElse(null);
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param t extends FactoryDeviceQuery
     */
    @Override
    public <T extends FactoryDeviceQuery> DeviceBaseDTO getFactoryBaseInfoByQuery(TenantId tenantId, T t) {
        return DeviceBaseDTO.builder()
                .factory(t.getFactoryId() != null ? DaoUtil.getData(this.factoryRepository.findByTenantIdAndId(tenantId.getId(), toUUID(t.getFactoryId()))) : null)
                .workshop(t.getWorkshopId() != null ? DaoUtil.getData(this.workshopRepository.findByTenantIdAndId(tenantId.getId(), toUUID(t.getWorkshopId()))) : null)
                .productionLine(t.getProductionLineId() != null ? DaoUtil.getData(this.productionLineRepository.findByTenantIdAndId(tenantId.getId(), toUUID(t.getProductionLineId()))) : null)
                .device(t.getDeviceId() != null ? DaoUtil.getData(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), toUUID(t.getDeviceId()))) : null)
                .build();
    }

    /**
     * ??????????????????
     *
     * @param tenantId ??????Id
     * @param t        extends FactoryDeviceQuery
     */
    @Override
    public <T extends FactoryDeviceQuery> List<Device> listDevicesByQuery(TenantId tenantId, T t) {
        return DaoUtil.convertDataList(this.deviceRepository.findAll(this.getDeviceQuerySpecification(tenantId, t)));
    }

    /**
     * ????????????id??????
     *
     * @param tenantId ??????Id
     * @param t        extends FactoryDeviceQuery
     */
    @Override
    @SuppressWarnings("all")
    public <T extends FactoryDeviceQuery> List<Device> listSimpleDevicesByQuery(TenantId tenantId, T t) {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(DeviceEntity.class);
        var root = query.from(DeviceEntity.class);
        query.multiselect(root.<UUID>get("id"), root.get("name"), root.get("rename"));
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.<UUID>get("tenantId"), tenantId.getId()));
        predicates.add(cb.or(cb.isNull(root.<String>get("additionalInfo")), cb.equal(cb.locate(root.<String>get("additionalInfo"), "\"gateway\":true"), 0)));
        if (Boolean.TRUE.equals(t.getIsQueryAll())) {
            // do nothing
        } else if (!StringUtils.isBlank(t.getDeviceId())) {
            predicates.add(cb.equal(root.<UUID>get("id"), toUUID(t.getDeviceId())));
        } else if (!StringUtils.isBlank(t.getProductionLineId())) {
            predicates.add(cb.equal(root.<UUID>get("productionLineId"), toUUID(t.getProductionLineId())));
        } else if (!StringUtils.isBlank(t.getWorkshopId())) {
            predicates.add(cb.equal(root.<UUID>get("workshopId"), toUUID(t.getWorkshopId())));
        } else if (!StringUtils.isBlank(t.getFactoryId())) {
            predicates.add(cb.equal(root.<UUID>get("factoryId"), toUUID(t.getFactoryId())));
        } else {
            predicates.add(cb.isNull(root.<UUID>get("productionLineId")));
        }
        query.orderBy(cb.desc(root.get("createdTime"))).orderBy(cb.desc(root.get("name")));
        query.where(predicates.toArray(new Predicate[0]));

        return DaoUtil.convertDataList(entityManager.createQuery(query).getResultList());
    }

    /**
     * ????????????????????????
     *
     * @param tenantId ??????Id
     * @param t        extends FactoryDeviceQuery
     * @param pageLink ????????????
     */
    @Override
    public <T extends FactoryDeviceQuery> PageData<Device> listPageDevicesPageByQuery(TenantId tenantId, T t, PageLink pageLink) {
        return DaoUtil.toPageData(this.deviceRepository.findAll(this.getDeviceQuerySpecification(tenantId, t), DaoUtil.toPageable(pageLink)));
    }

    /**
     * ????????????????????????-????????????????????????
     *
     * @param tenantId ??????Id
     * @param t        extends FactoryDeviceQuery
     * @param pageLink ????????????
     */
    @Override
    public <T extends FactoryDeviceQuery> PageData<Device> listPageDevicesPageByQueryOrderBySort(TenantId tenantId, T t, PageLink pageLink) {
        return DaoUtil.toPageData(this.deviceRepository.findAll(this.getDeviceQuerySpecificationOrderBySort(tenantId, t), DaoUtil.toPageable(pageLink)));
    }

    /**
     * ?????????????????????????????????
     *
     * @param allDeviceIdList ?????????UUID??????
     */
    @Override
    public Map<String, Boolean> listDevicesOnlineStatus(List<UUID> allDeviceIdList) {
        if (allDeviceIdList.isEmpty())
            return Maps.newHashMap();
        if (persistToTelemetry) {
            Map<String, Boolean> map = new HashMap<>();
            for (UUID uuid : allDeviceIdList) {
                map.put(uuid.toString(), this.getDeviceOnlineStatus(DeviceId.fromString(UUIDToString(uuid))));
            }
            return map;
        } else {
            return allDeviceIdList.stream().map(v -> CompletableFuture.supplyAsync(() -> ImmutablePair.of(v, this.getDeviceOnlineStatus(DeviceId.fromString(v.toString())))))
                    .map(CompletableFuture::join).collect(Collectors.toMap(v -> v.getLeft().toString(), ImmutablePair::getRight));
        }
    }

    /**
     * ????????????????????????
     *
     * @param deviceId ??????Id
     */
    @Override
    public Device getSimpleDevice(UUID deviceId) {
        return this.deviceRepository.findSimpleById(deviceId).toData();
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param deviceList ????????????
     */
    @Override
    public DeviceListAffiliationDTO getDevicesAffiliationInfo(List<Device> deviceList) {
        List<UUID> factoryIds = deviceList.stream().map(Device::getFactoryId).distinct().collect(Collectors.toList());
        List<UUID> workshopIds = deviceList.stream().map(Device::getWorkshopId).distinct().collect(Collectors.toList());
        List<UUID> productionLineIds = deviceList.stream().map(Device::getProductionLineId).distinct().collect(Collectors.toList());

        return DeviceListAffiliationDTO.builder()
                .factoryMap(DaoUtil.convertDataList(Lists.newArrayList(this.factoryRepository.findAllById(factoryIds))).stream()
                        .collect(Collectors.toMap(Factory::getId, Function.identity(), (a, b) -> a)))
                .workshopMap(DaoUtil.convertDataList(Lists.newArrayList(this.workshopRepository.findAllById(workshopIds))).stream()
                        .collect(Collectors.toMap(Workshop::getId, Function.identity(), (a, b) -> a)))
                .productionLineMap(DaoUtil.convertDataList(Lists.newArrayList(this.productionLineRepository.findAllById(productionLineIds))).stream()
                        .collect(Collectors.toMap(ProductionLine::getId, Function.identity(), (a, b) -> a)))
                .build();
    }

    /**
     * ?????????????????????????????????
     */
    @Override
    public List<DictDeviceGroupVO> getDictDeviceInitData() {
        List<DictDeviceGroupVO> list = Lists.newArrayList();
        var jsonNodeOptional = this.initRepository.findByScope(InitScopeEnum.DICT_DEVICE_GROUP.getCode()).map(InitEntity::getInitData);
        if (jsonNodeOptional.isEmpty())
            return list;

        var jsonNode = jsonNodeOptional.get();

        jsonNode.forEach(e -> list.add(convertValue(e, DictDeviceGroupVO.class)));
        return list;
    }

    /**
     * ??????????????????
     *
     * @param tenantId ??????Id
     */
    @Override
    public List<Factory> listFactories(TenantId tenantId) {
        return DaoUtil.convertDataList(this.factoryRepository.findAllByTenantIdOrderByCreatedTimeDesc(tenantId.getId()));
    }


    /**
     * ??????:wwj
     * ??????: 2021-11-29
     * ????????????: ??????????????????????????????
     *
     * @param tenantId  ??????Id
     * @param factoryId ??????Id
     * @return
     */
    @Override
    public List<Workshop> listWorkshopsByFactoryId(TenantId tenantId, UUID factoryId) {
        return DaoUtil.convertDataList(this.workshopRepository.findAllByTenantIdAndFactoryIdOrderByCreatedTimeDesc(tenantId.getId(), factoryId));
    }

    /**
     * ???????????????????????????
     *
     * @param tenantId   ??????Id
     * @param workshopId ??????Id
     */
    @Override
    public List<ProductionLine> listProductionLinesByWorkshopId(TenantId tenantId, UUID workshopId) {
        return DaoUtil.convertDataList(this.productionLineRepository.findAllByTenantIdAndWorkshopIdOrderByCreatedTimeDesc(tenantId.getId(), workshopId));
    }

    /**
     * ???????????????????????????????????????
     *
     * @param tenantId ??????Id
     * @param userId   ??????Id
     * @return ????????????
     */
    @Override
    public List<Factory> listFactoriesByUserId(TenantId tenantId, UserId userId) {
        return Optional.ofNullable(this.factoryService.findFactoryListByLoginRole(userId.getId(), tenantId.getId())).orElse(Lists.newArrayList());
    }

    /**
     * ????????????????????????????????????
     *
     * @param tenantId    ??????Id
     * @param factoryName ????????????
     * @return ????????????
     */
    @Override
    public List<Factory> listFactoriesByFactoryName(TenantId tenantId, String factoryName) {
        return DaoUtil.convertDataList(this.factoryRepository.findAllByTenantIdAndNameLike(tenantId.getId(), factoryName).join());
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param tenantId    ??????Id
     * @param userId      ??????Id
     * @param factoryName ????????????
     * @return ????????????
     */
    @Override
    public List<Factory> listFactoriesByUserIdAndFactoryName(TenantId tenantId, UserId userId, String factoryName) {
        var ids = this.listFactoriesByFactoryName(tenantId, factoryName).stream().map(Factory::getId).collect(Collectors.toSet());
        return this.listFactoriesByUserId(tenantId, userId).stream().filter(v -> ids.contains(v.getId())).collect(Collectors.toList());
    }

    /**
     * ??????????????????????????????
     *
     * @param tenantId     ??????Id
     * @param deviceId     ??????Id
     * @param timePageLink ??????????????????
     * @return ??????????????????
     */
    @Override
    @SuppressWarnings("all")
    public PageData<Map<String, Object>> listPageTsHistories(TenantId tenantId, DeviceId deviceId, TimePageLink timePageLink) throws ExecutionException, InterruptedException {
        long sta = System.currentTimeMillis();
        if (this.commonComponent.isPersistToCassandra()) {
            var keyList = this.tsService.findAllLatest(tenantId, DeviceId.fromString(deviceId.toString())).get().stream().map(TsKvEntry::getKey).collect(Collectors.toList());
            if (keyList.isEmpty())
                return new PageData<>(Lists.newArrayList(), 0, 0L, false);

            List<ReadTsKvQuery> tempQueries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, timePageLink.getStartTime(), timePageLink.getEndTime(), timePageLink.getEndTime() - timePageLink.getStartTime(), 1, Aggregation.COUNT, timePageLink.getSortOrder().getDirection().toString()))
                    .collect(Collectors.toList());

            var temp = this.tsService.findAll(tenantId, deviceId, tempQueries).get()
                    .stream().map(KvEntry::getValue).map(e -> Integer.valueOf(String.valueOf(e))).collect(Collectors.toList());
            if (temp.isEmpty())
                return new PageData<>(Lists.newArrayList(), 0, 0L, false);
            var count = temp.stream().mapToInt(Integer::intValue).sum();

            List<ReadTsKvQuery> queries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, timePageLink.getStartTime(), timePageLink.getEndTime(), timePageLink.getEndTime() - timePageLink.getStartTime(), count, Aggregation.NONE, timePageLink.getSortOrder().getDirection().toString()))
                    .collect(Collectors.toList());

            var KvResult = this.tsService.findAll(tenantId, deviceId, queries).get();
            List<Map<String, Object>> result = new ArrayList<>();
            Map<Long, Map<String, Object>> resultMap = Maps.newLinkedHashMap();
            if (SortOrder.Direction.ASC.equals(timePageLink.getSortOrder().getDirection()))
                KvResult.stream().sorted(Comparator.comparing(TsKvEntry::getTs)).forEach(v -> resultMap.computeIfAbsent(v.getTs(), k -> new HashMap<>()).put(v.getKey(), this.formatKvEntryValue(v)));
            else
                KvResult.stream().sorted(Comparator.comparing(TsKvEntry::getTs).reversed()).forEach(v -> resultMap.computeIfAbsent(v.getTs(), k -> new HashMap<>()).put(v.getKey(), this.formatKvEntryValue(v)));
            resultMap.forEach((k, v) -> {
                v.put(HSConstants.CREATED_TIME, k);
                result.add(v);
            });
            var total = result.size();

            var totalPage = Double.valueOf(Math.ceil(Double.parseDouble(String.valueOf(total)) / timePageLink.getPageSize())).intValue();
            var subList = result.subList(Math.min(timePageLink.getPageSize() * timePageLink.getPage(), total), Math.min(timePageLink.getPageSize() * (timePageLink.getPage() + 1), total));
            return new PageData<>(subList, totalPage, Long.parseLong(String.valueOf(total)), timePageLink.getPage() + 1 < totalPage);
        } else {
            var keyIds = this.tsLatestRepository.findAllKeyIdsByEntityId(deviceId.getId());
            if (keyIds.isEmpty())
                return new PageData<>(Lists.newArrayList(), 0, 0L, false);
            var latestKeyId = Optional.ofNullable(this.tsLatestRepository.findLatestKey(deviceId.getId())).orElse(keyIds.get(0));
            var keyIdToKeyMap = this.tsDictionaryRepository.findAllByKeyIdIn(Sets.newHashSet(keyIds)).stream().collect(Collectors.toMap(TsKvDictionary::getKeyId, TsKvDictionary::getKey, (a, b) -> a));

            var pageData = this.tsRepository.findTss(deviceId.getId(), latestKeyId, timePageLink.getStartTime(), timePageLink.getEndTime(), DaoUtil.toPageable(timePageLink));
            if (pageData.getContent().isEmpty())
                return new PageData<>(Lists.newArrayList(), 0, 0L, false);

            var time1 = pageData.getContent().get(0);
            var time2 = pageData.getContent().get(pageData.getContent().size() - 1);
            var kvEntityResult = this.tsRepository.findAllByStartTsAndEndTsOrderByTsDesc(deviceId.getId(), Sets.newHashSet(keyIds), Math.min(time1, time2), Math.max(time1, time2));

            List<Map<String, Object>> result = new ArrayList<>();
            Map<Long, Map<String, Object>> resultMap = Maps.newLinkedHashMap();
            kvEntityResult.stream().map(e -> {
                e.setStrKey(keyIdToKeyMap.getOrDefault(e.getKey(), HSConstants.NULL_STR));
                return e;
            }).map(TsKvEntity::toData).forEach(v -> {
                resultMap.computeIfAbsent(v.getTs(), k -> new HashMap<>()).put(v.getKey(), this.formatKvEntryValue(v));
            });
            resultMap.forEach((k, v) -> {
                v.put(HSConstants.CREATED_TIME, k);
                result.add(v);
            });

            return new PageData<>(result, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
        }
    }

    /**
     * ??????????????????Key??????????????????
     *
     * @param tenantId          ??????Id
     * @param deviceId          ??????Id
     * @param groupPropertyName ??????key
     * @param timePageLink      ??????????????????
     * @return ??????????????????
     */
    @Override
    @SuppressWarnings("all")
    public PageData<DictDeviceGroupPropertyVO> listPageTsHistories(TenantId tenantId, DeviceId deviceId, String groupPropertyName, TimePageLink timePageLink) throws ExecutionException, InterruptedException, ThingsboardException {
        if (this.commonComponent.isPersistToCassandra()) {
            List<String> keyList = new ArrayList<>() {{
                add(groupPropertyName);
            }};
            List<ReadTsKvQuery> tempQueries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, timePageLink.getStartTime(), timePageLink.getEndTime(), timePageLink.getEndTime() - timePageLink.getStartTime(), 1, Aggregation.COUNT, "desc"))
                    .collect(Collectors.toList());

            var tempResult = this.tsService.findAll(tenantId, deviceId, tempQueries).get()
                    .stream().collect(Collectors.toMap(TsKvEntry::getKey, Function.identity()));
            if (tempResult.isEmpty())
                return new PageData<>();
            int count = Integer.parseInt(String.valueOf(tempResult.get(groupPropertyName).getValue()));
            if (count == 0)
                return new PageData<>();

            int queryCount = Math.min((timePageLink.getPage() + 1) * timePageLink.getPageSize(), count);

            List<ReadTsKvQuery> queries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, timePageLink.getStartTime(), timePageLink.getEndTime(), timePageLink.getEndTime() - timePageLink.getStartTime(), Math.min(queryCount, count), Aggregation.NONE, "desc"))
                    .collect(Collectors.toList());

            Map<String, DictData> rMap = new HashMap<>();
            var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), deviceId.getId())).map(DeviceEntity::toData).orElse(null);
            if (device != null && device.getDictDeviceId() != null) {
                rMap = this.dictDeviceService.getNameToDictDataMap(tenantId, device.getDictDeviceId());
            }
            Map<String, DictData> finalRMap = rMap;

            var result = this.tsService.findAll(tenantId, deviceId, queries).get()
                    .stream().sorted(Comparator.comparing(TsKvEntry::getTs).reversed()).map(e -> DictDeviceGroupPropertyVO.builder()
                            .content(this.formatKvEntryValue(e))
                            .unit(Optional.ofNullable(finalRMap.getOrDefault(groupPropertyName, null))
                                    .map(DictData::getUnit).orElse(null))
                            .createdTime(e.getTs())
                            .build())
                    .collect(Collectors.toList());

            var subList = result.subList(Math.min(timePageLink.getPageSize() * timePageLink.getPage(), queryCount), Math.min(timePageLink.getPageSize() * (timePageLink.getPage() + 1), queryCount));
            var totalPage = Double.valueOf(Math.ceil(Double.parseDouble(String.valueOf(count)) / timePageLink.getPageSize())).intValue();
            return new PageData<>(subList, totalPage, Long.parseLong(String.valueOf(count)), timePageLink.getPage() + 1 < totalPage);
        } else {
            var keyId = this.tsDictionaryRepository.findByKey(groupPropertyName).map(TsKvDictionary::getKeyId)
                    .orElseThrow(() -> new ThingsboardException("keyId?????????", ThingsboardErrorCode.GENERAL));
            var pageData = this.tsRepository.findAll(deviceId.getId(), keyId, timePageLink.getStartTime(), timePageLink.getEndTime(), DaoUtil.toPageable(timePageLink));
            if (pageData.getContent().isEmpty())
                return new PageData<>(Lists.newArrayList(), 0, 0L, false);

            Map<String, DictData> rMap = new HashMap<>();
            var device = Optional.ofNullable(this.deviceRepository.findByTenantIdAndId(tenantId.getId(), deviceId.getId())).map(DeviceEntity::toData).orElse(null);
            if (device != null && device.getDictDeviceId() != null) {
                rMap = this.dictDeviceService.getNameToDictDataMap(tenantId, device.getDictDeviceId());
            }
            Map<String, DictData> finalRMap = rMap;

            pageData.getContent().forEach(e -> e.setStrKey(groupPropertyName));
            var r = pageData.getContent().stream().map(TsKvEntity::toData).map(v -> DictDeviceGroupPropertyVO.builder()
                    .content(this.formatKvEntryValue(v))
                    .unit(Optional.ofNullable(finalRMap.getOrDefault(groupPropertyName, null))
                            .map(DictData::getUnit).orElse(null))
                    .createdTime(v.getTs())
                    .build()).collect(Collectors.toList());
            return new PageData<>(r, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
        }
    }

    /**
     * ??????????????????
     *
     * @param factoryIds ??????Id??????
     */
    @Override
    public Map<UUID, Factory> mapIdToFactory(List<UUID> factoryIds) {
        if (factoryIds.isEmpty())
            return Maps.newHashMap();
        return DaoUtil.convertDataList(Lists.newArrayList(this.factoryRepository.findAllById(factoryIds))).stream()
                .collect(Collectors.toMap(Factory::getId, Function.identity()));
    }

    /**
     * ??????????????????
     *
     * @param workshopIds ??????Id??????
     */
    @Override
    public Map<UUID, Workshop> mapIdToWorkshop(List<UUID> workshopIds) {
        if (workshopIds.isEmpty())
            return Maps.newHashMap();
        return DaoUtil.convertDataList(Lists.newArrayList(this.workshopRepository.findAllById(workshopIds))).stream()
                .collect(Collectors.toMap(Workshop::getId, Function.identity()));
    }

    /**
     * ??????????????????
     *
     * @param productionLineIds ??????Id??????
     */
    @Override
    public Map<UUID, ProductionLine> mapIdToProductionLine(List<UUID> productionLineIds) {
        if (productionLineIds.isEmpty())
            return Maps.newHashMap();
        return DaoUtil.convertDataList(Lists.newArrayList(this.productionLineRepository.findAllById(productionLineIds))).stream()
                .collect(Collectors.toMap(ProductionLine::getId, Function.identity()));
    }

    /**
     * ??????????????????
     *
     * @param deviceIds ??????Id??????
     */
    @Override
    public Map<UUID, Device> mapIdToDevice(List<UUID> deviceIds) {
        if (deviceIds.isEmpty())
            return Maps.newHashMap();
        return DaoUtil.convertDataList(Lists.newArrayList(this.deviceRepository.findAllById(deviceIds))).stream()
                .collect(Collectors.toMap(v -> v.getId().getId(), Function.identity()));
    }

    /**
     * ??????????????????
     *
     * @param userIds ??????Id??????
     */
    @Override
    public Map<UUID, User> mapIdToUser(List<UUID> userIds) {
        if (userIds.isEmpty())
            return Maps.newHashMap();
        return DaoUtil.convertDataList(Lists.newArrayList(this.userRepository.findAllById(userIds))).stream()
                .collect(Collectors.toMap(e -> e.getId().getId(), Function.identity()));
    }

    /**
     * ??????????????????
     *
     * @param plans ??????????????????
     */
    @Override
    public BigDecimal getOrderCapacitiesByTs(List<OrderPlan> plans) {
        return this.getOrderCapacitiesByTs(plans, null).getCapacities();
    }

    /**
     * ??????????????????
     *
     * @param plans   ??????????????????
     * @param orderId ??????Id
     */
    @Override
    public OrderCapacityBO getOrderCapacitiesByTs(List<OrderPlan> plans, UUID orderId) {
        if (plans.isEmpty()) {
            log.trace("????????????????????????????????????" + "empty");
            return OrderCapacityBO.builder().orderId(orderId).capacities(BigDecimal.ZERO).deviceCapacities(Lists.newArrayList()).build();
        } else {
            var dataMap = this.bulletinBoardSvc.queryCapacityValueByDeviceIdAndTime(plans.stream().map(OrderPlan::toDeviceCapacityVO).filter(f -> f.getStartTime() != null).filter(f -> f.getEndTime() != null).collect(Collectors.toList()));
            log.trace("????????????????????????????????????" + dataMap);
            var deviceCapacities = plans.stream().map(v -> OrderDeviceCapacityBO.builder()
                    .planId(toUUID(v.getId()))
                    .enabled(v.getEnabled())
                    .capacities(Optional.ofNullable(dataMap.get(CommonUtil.toUUIDNullable(v.getId()))).map(BigDecimal::new).orElse(BigDecimal.ZERO))
                    .build()).collect(Collectors.toList());
            return OrderCapacityBO.builder()
                    .orderId(orderId)
                    .deviceCapacities(deviceCapacities)
                    .capacities(deviceCapacities.stream().filter(OrderDeviceCapacityBO::getEnabled).map(OrderDeviceCapacityBO::getCapacities).reduce(BigDecimal.ZERO, BigDecimal::add))
                    .build();
        }
    }

    /**
     * ??????????????????
     *
     * @param plans ??????????????????
     */
    @Override
    public BigDecimal getOrderCapacities(List<OrderPlan> plans) {
        return this.getOrderCapacities(plans, null).getCapacities();
    }

    /**
     * ??????????????????
     *
     * @param plans   ??????????????????
     * @param orderId ??????Id
     */
    @Override
    public OrderCapacityBO getOrderCapacities(List<OrderPlan> plans, UUID orderId) {
        if (plans.isEmpty()) {
            log.trace("????????????????????????????????????" + "empty");
            return OrderCapacityBO.builder().orderId(orderId).capacities(BigDecimal.ZERO).deviceCapacities(Lists.newArrayList()).build();
        } else {
            return OrderCapacityBO.builder()
                    .orderId(orderId)
                    .deviceCapacities(
                            plans.stream().map(v -> OrderDeviceCapacityBO.builder()
                                    .planId(toUUID(v.getId()))
                                    .enabled(v.getEnabled())
                                    .capacities(Optional.ofNullable(v.getActualCapacity()).orElse(BigDecimal.ZERO))
                                    .build()).collect(Collectors.toList())
                    )
                    .capacities(plans.stream().filter(OrderPlan::getEnabled).map(OrderPlan::getActualCapacity).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add))
                    .build();
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param tenantId    ??????Id
     * @param factoryName ????????????
     * @return ??????
     */
    @Override
    public Factory getFactoryByFactoryNameExactly(TenantId tenantId, String factoryName) {
        if (StringUtils.isBlank(factoryName))
            return null;
        var pageData = DaoUtil.toPageData(this.factoryRepository.findAllByTenantIdAndName(tenantId.getId(), factoryName, CommonUtil.singleDataPage()).join());
        return pageData.getData().isEmpty() ? null : pageData.getData().get(0);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param tenantId    ??????Id
     * @param factoryName ????????????
     * @return ??????
     */
    @Override
    public Factory getFirstFactoryByFactoryName(TenantId tenantId, String factoryName) {
        if (StringUtils.isBlank(factoryName))
            return null;
        var pageData = DaoUtil.toPageData(this.factoryRepository.findAllByTenantIdAndNameLike(tenantId.getId(), factoryName, CommonUtil.singleDataPage()).join());
        return pageData.getData().isEmpty() ? null : pageData.getData().get(0);
    }

    /**
     * ???????????????????????????Id?????????????????????
     *
     * @param tenantId     ??????Id
     * @param factoryId    ??????Id
     * @param workshopName ????????????
     * @return ??????
     */
    @Override
    public Workshop getFirstWorkshopByFactoryIdAndWorkshopName(TenantId tenantId, UUID factoryId, String workshopName) {
        if (StringUtils.isBlank(workshopName))
            return null;
        var pageData = DaoUtil.toPageData(this.workshopRepository.findAllByTenantIdAndFactoryIdAndNameLike(tenantId.getId(), factoryId, workshopName, CommonUtil.singleDataPage()).join());
        return pageData.getData().isEmpty() ? null : pageData.getData().get(0);
    }

    /**
     * ???????????????????????????Id???????????????????????????
     *
     * @param tenantId     ??????Id
     * @param factoryId    ??????Id
     * @param workshopName ????????????
     * @return ??????
     */
    @Override
    public Workshop getWorkshopByFactoryIdAndWorkshopNameExactly(TenantId tenantId, UUID factoryId, String workshopName) {
        if (StringUtils.isBlank(workshopName))
            return null;
        var pageData = DaoUtil.toPageData(this.workshopRepository.findAllByTenantIdAndFactoryIdAndName(tenantId.getId(), factoryId, workshopName, CommonUtil.singleDataPage()).join());
        return pageData.getData().isEmpty() ? null : pageData.getData().get(0);
    }

    /**
     * ???????????????????????????Id???????????????????????????
     *
     * @param tenantId           ??????Id
     * @param workshopId         ??????Id
     * @param productionLineName ????????????
     * @return ??????
     */
    @Override
    public ProductionLine getProductionLineByWorkshopIdAndProductionLineNameExactly(TenantId tenantId, UUID workshopId, String productionLineName) {
        if (StringUtils.isBlank(productionLineName))
            return null;
        var pageData = DaoUtil.toPageData(this.productionLineRepository.findAllByTenantIdAndWorkshopIdAndName(tenantId.getId(), workshopId, productionLineName, CommonUtil.singleDataPage()).join());
        return pageData.getData().isEmpty() ? null : pageData.getData().get(0);
    }

    /**
     * ???????????????????????????Id?????????????????????
     *
     * @param tenantId           ??????Id
     * @param workshopId         ??????Id
     * @param productionLineName ????????????
     * @return ??????
     */
    @Override
    public ProductionLine getFirstProductionLineByWorkshopIdAndProductionLineName(TenantId tenantId, UUID workshopId, String productionLineName) {
        if (StringUtils.isBlank(productionLineName))
            return null;
        var pageData = DaoUtil.toPageData(this.productionLineRepository.findAllByTenantIdAndWorkshopIdAndNameLike(tenantId.getId(), workshopId, productionLineName, CommonUtil.singleDataPage()).join());
        return pageData.getData().isEmpty() ? null : pageData.getData().get(0);
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
        return this.tsLatestRepository.findAllKeyIdsByEntityId(deviceId);
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
        var keyIds = this.tsLatestRepository.findAllKeyIdsByEntityId(deviceId);
        if (keyIds.isEmpty())
            return Lists.newArrayList();
        return this.tsDictionaryRepository.findAllByKeyIdIn(Sets.newHashSet(keyIds)).stream().map(TsKvDictionary::getKey).collect(Collectors.toList());
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param tenantId  ??????Id
     * @param factoryId ??????Id
     * @return ??????????????????
     */
    @Override
    public FactoryDetailBO getFactoryHierarchy(TenantId tenantId, UUID factoryId) {
        FactoryDetailBO factoryBO = new FactoryDetailBO();
        CompletableFuture.allOf(this.factoryRepository.findAllIdAndNameAndSortByTenantIdOrderBySortAsc(tenantId.getId()).thenAcceptAsync(v -> factoryBO.setFactories(v.stream().filter(e -> factoryId == null || factoryId.equals(e.getId())).map(DaoUtil::getData).collect(Collectors.toList()))),
                this.workshopRepository.findAllIdAndNameAndSortByTenantIdOrderBySortAsc(tenantId.getId()).thenAcceptAsync(v -> factoryBO.setWorkshops(v.stream().filter(e -> factoryId == null || factoryId.equals(e.getFactoryId())).map(DaoUtil::getData).collect(Collectors.toList()))),
                this.productionLineRepository.findAllIdAndNameAndSortByTenantIdOrderBySortAsc(tenantId.getId()).thenAcceptAsync(v -> factoryBO.setProductionLines(v.stream().filter(e -> factoryId == null || factoryId.equals(e.getFactoryId())).map(DaoUtil::getData).collect(Collectors.toList()))),
                this.deviceRepository.findAllIdAndNameAndSortByTenantIdOrderBySortAsc(tenantId.getId()).thenAcceptAsync(v -> factoryBO.setDevices(v.stream().filter(e -> e.getAdditionalInfo() == null || e.getAdditionalInfo().get("gateway") == null || !"true".equals(e.getAdditionalInfo().get("gateway").asText())).filter(e -> factoryId == null || factoryId.equals(e.getFactoryId())).map(DaoUtil::getData).collect(Collectors.toList())))).join();
        return factoryBO;
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param tenantId  ??????Id
     * @param factoryId ??????Id
     * @return ????????????????????????????????????????????????
     */
    @Override
    public Map<String, Boolean> getDeviceOnlineStatusMap(TenantId tenantId, UUID factoryId) {
        var devices = DaoUtil.convertDataList(this.deviceRepository.findAllIdAndNameByTenantIdOrderByCreatedTimeDesc(tenantId.getId()).join());
        var deviceIds = devices.stream().filter(e -> e.getAdditionalInfo() == null || e.getAdditionalInfo().get("gateway") == null || !"true".equals(e.getAdditionalInfo().get("gateway").asText())).filter(e -> factoryId == null || factoryId.equals(e.getFactoryId())).map(Device::getId).map(DeviceId::getId).collect(Collectors.toList());
        return this.listDevicesOnlineStatus(deviceIds);
    }

    /**
     * ?????????????????????????????????
     *
     * @param tenantId ??????Id
     * @return ?????????????????????????????????
     */
    @Override
    public Map<String, Boolean> getDeviceOnlineStatusMap(TenantId tenantId) {
        var devices = DaoUtil.convertDataList(this.deviceRepository.findAllIdAndNameByTenantIdOrderByCreatedTimeDesc(tenantId.getId()).join());
        var deviceIds = devices.stream().filter(e -> e.getAdditionalInfo() == null || e.getAdditionalInfo().get("gateway") == null || !"true".equals(e.getAdditionalInfo().get("gateway").asText())).map(Device::getId).map(DeviceId::getId).collect(Collectors.toList());
        return this.listDevicesOnlineStatus(deviceIds);
    }

    /**
     * ????????????????????????
     *
     * @param tenantId  ??????Id
     * @param deviceId  ??????Id
     * @param startTime ????????????
     * @param endTime   ????????????
     */
    @Override
    public List<DeviceKeyParamShiftResult> listDeviceShirtTimes(TenantId tenantId, UUID deviceId, Long startTime, Long endTime) {
        return this.calenderRepository.findAllCross(tenantId.getId(), deviceId, startTime, endTime)
                .thenApplyAsync(calenders ->
                        calenders.stream().map(ProductionCalenderEntity::toData)
                                .map(v -> DeviceKeyParamShiftResult.builder()
                                        .startTime(v.getStartTime() < startTime ? startTime : v.getStartTime())
                                        .endTime(v.getEndTime() > endTime ? endTime : v.getEndTime())
                                        .build()).collect(Collectors.toList())).join();
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param tenantId  ??????Id
     * @param deviceId  ??????Id
     * @param startTime ????????????
     * @param endTime   ????????????
     */
    @Override
    public List<Long> listDeviceTss(TenantId tenantId, UUID deviceId, Long startTime, Long endTime) {
        var keyIds = this.tsLatestRepository.findAllKeyIdsByEntityId(deviceId);
        if (keyIds.isEmpty())
            return Lists.newArrayList();
        return this.tsRepository.findTss(deviceId, Sets.newHashSet(keyIds), startTime, endTime);
    }

    /**
     * ?????????????????????????????????
     *
     * @param tenantId  ??????Id
     * @param deviceId  ??????Id
     * @param startTime ????????????
     * @param endTime   ????????????
     */
    @Override
    public List<ProductionCalender> listProductionCalenders(TenantId tenantId, UUID deviceId, Long startTime, Long endTime) {
        return DaoUtil.convertDataList(this.calenderRepository.findAllByTenantIdAndDeviceIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(tenantId.getId(), deviceId, startTime, endTime));
    }

    /**
     * ??????????????????????????????
     *
     * @param factoryId ??????Id
     */
    @Override
    public Boolean isFactoryOnline(UUID factoryId) {
        try {
            return factoryDao.checkoutFactoryStatus(factoryId);
        } catch (ThingsboardException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param tenantId  ??????Id
     * @param factoryId ??????Id
     * @return map
     */
    @Override
    public Map<String, Boolean> getFactoryOnlineStatusMap(TenantId tenantId, UUID factoryId) throws ThingsboardException {
        return listFactoryGatewayDevices(tenantId, factoryId).stream()
                .map(v -> CompletableFuture.supplyAsync(() -> {
                    var result = v.getGatewayDeviceIds().stream()
                            .map(i -> CompletableFuture.supplyAsync(() -> this.getDeviceOnlineStatus(DeviceId.fromString(i.toString()))))
                            .map(CompletableFuture::join)
                            .filter(Boolean.FALSE::equals)
                            .findAny();
                    if (result.isPresent())
                        return ImmutablePair.of(v.getFactoryId(), Boolean.FALSE);
                    else
                        return ImmutablePair.of(v.getFactoryId(), Boolean.TRUE);
                })).map(CompletableFuture::join).collect(Collectors.toMap(v -> v.getLeft().toString(), ImmutablePair::getRight));
    }

    /**
     * ??????????????????Id????????????????????????
     *
     * @param profileId ????????????Id
     */
    @Override
    public List<Device> listDevicesByProfileId(DeviceProfileId profileId) {
        return this.deviceRepository.findAllByDeviceProfileId(profileId.getId()).thenApplyAsync(DaoUtil::convertDataList).join();
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param tenantId  ??????Id
     * @param factoryId ??????Id
     * @return ????????????
     */
    @Override
    public List<FactoryGatewayDevicesResult> listFactoryGatewayDevices(TenantId tenantId, UUID factoryId) {
        if (factoryId == null)
            return this.factoryRepository.findAllByTenantId(tenantId.getId())
                    .thenCombineAsync(this.deviceRepository.findAllByTenantId(tenantId.getId()).thenApplyAsync(g -> DaoUtil.convertDataList(g).stream()
                                    .filter(v -> v.getFactoryId() != null)
                                    .filter(e -> e.getAdditionalInfo() != null && e.getAdditionalInfo().get("gateway") != null && "true".equals(e.getAdditionalInfo().get("gateway").asText()))
                                    .collect(Collectors.groupingBy(Device::getFactoryId))
                            ), (factories, map) -> factories.stream().map(FactoryEntity::toData).map(v -> new FactoryGatewayDevicesResult(v.getId(), Optional.ofNullable(map.get(v.getId())).map(j -> j.stream().map(f -> f.getId().getId()).collect(Collectors.toList())).orElse(Lists.newArrayList()))).collect(Collectors.toList())
                    ).join();
        else
            return Lists.newArrayList(new FactoryGatewayDevicesResult(factoryId, DaoUtil.convertDataList(this.deviceRepository.findAllByTenantIdAndFactoryId(tenantId.getId(), factoryId).join()).stream().filter(e -> e.getAdditionalInfo() != null && e.getAdditionalInfo().get("gateway") != null && "true".equals(e.getAdditionalInfo().get("gateway").asText())).map(Device::getId).map(DeviceId::getId).collect(Collectors.toList())));
    }

    /**
     * ????????????oee
     *
     * @param tenantId    ??????Id
     * @param deviceId    ??????Id
     * @param currentTime ????????????
     */
    @Override
    public Double getDeviceOEE(TenantId tenantId, UUID deviceId, Long currentTime) {
        return this.formatDoubleData(Optional.ofNullable(this.deviceOeeEveryHourService.getStatisticOeeDeviceByCurrentDay(deviceId)).orElse(BigDecimal.ZERO));
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param tenantId   ??????Id
     * @param deviceId   ??????Id
     * @param startTime  ????????????
     * @param endTime    ????????????
     * @param properties ??????
     */
    @Override
    public Map<String, List<HistoryGraphPropertyTsKvVO>> listTsHistoriesByProperties(TenantId tenantId, UUID deviceId, Long startTime, Long endTime, List<String> properties) throws ExecutionException, InterruptedException {
        if (this.commonComponent.isPersistToCassandra()) {
            List<ReadTsKvQuery> queries = properties.stream().map(key -> new BaseReadTsKvQuery(key, startTime, endTime, endTime - startTime, Integer.MAX_VALUE, Aggregation.NONE, "desc"))
                    .collect(Collectors.toList());
            Map<String, List<HistoryGraphPropertyTsKvVO>> map = new HashMap<>();
            this.tsService.findAll(tenantId, DeviceId.fromString(deviceId.toString()), queries).get()
                    .stream().sorted(Comparator.comparing(TsKvEntry::getTs).reversed())
                    .collect(Collectors.toList()).forEach(v -> map.computeIfAbsent(v.getKey(), f -> Lists.newArrayList()).add(HistoryGraphPropertyTsKvVO.builder().ts(v.getTs()).value(this.formatKvEntryValue(v)).build()));
            return map;
        } else {
            var keyIds = this.tsLatestRepository.findAllKeyIdsByEntityId(deviceId);
            if (keyIds.isEmpty())
                return Maps.newHashMap();
            var keyIdToKeyMap = this.tsDictionaryRepository.findAllByKeyIn(properties).stream().collect(Collectors.toMap(TsKvDictionary::getKeyId, TsKvDictionary::getKey));
            var kvEntityResult = this.tsRepository.findAllByStartTsAndEndTsOrderByTsDesc(deviceId, Sets.newHashSet(keyIds), startTime, endTime);
            Map<String, List<HistoryGraphPropertyTsKvVO>> map = Maps.newHashMap();

            kvEntityResult.forEach(v -> map.computeIfAbsent(keyIdToKeyMap.get(v.getKey()), k -> Lists.newArrayList()).add(HistoryGraphPropertyTsKvVO.builder()
                    .ts(v.getTs())
                    .value(this.formatKvEntryValue(v.toData()))
                    .build()));
            return map;
        }

    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param tenantId ??????Id
     * @param deviceId ??????Id
     */
    @Override
    public List<AttributeKvEntry> listDeviceAttributeKvs(TenantId tenantId, UUID deviceId) {
        return Arrays.stream(DataConstants.allScopes()).map(v -> CompletableFuture.supplyAsync(() -> {
            try {
                return this.attributesService.findAll(tenantId, new DeviceId(deviceId), v).get();
            } catch (Exception ignore) {
                return null;
            }
        })).map(CompletableFuture::join).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * ????????????????????????
     *
     * @param tenantId     ??????Id
     * @param deviceId     ??????Id
     * @param timePageLink ??????????????????
     * @return ??????????????????
     */
    @Override
    @SuppressWarnings("all")
    public List<Map<String, Object>> listTsHistories(TenantId tenantId, DeviceId deviceId, TimePageLink timePageLink) throws ExecutionException, InterruptedException {
        if (this.commonComponent.isPersistToCassandra()) {
            var keyList = this.tsService.findAllLatest(tenantId, DeviceId.fromString(deviceId.toString())).get().stream().map(TsKvEntry::getKey).collect(Collectors.toList());
            if (keyList.isEmpty())
                return Lists.newArrayList();

            List<ReadTsKvQuery> tempQueries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, timePageLink.getStartTime(), timePageLink.getEndTime(), timePageLink.getEndTime() - timePageLink.getStartTime(), 1, Aggregation.COUNT, timePageLink.getSortOrder().getDirection().toString()))
                    .collect(Collectors.toList());

            var temp = this.tsService.findAll(tenantId, deviceId, tempQueries).get()
                    .stream().map(KvEntry::getValue).map(e -> Integer.valueOf(String.valueOf(e))).collect(Collectors.toList());
            if (temp.isEmpty())
                return Lists.newArrayList();
            var count = temp.stream().mapToInt(Integer::intValue).sum();

            List<ReadTsKvQuery> queries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, timePageLink.getStartTime(), timePageLink.getEndTime(), timePageLink.getEndTime() - timePageLink.getStartTime(), count, Aggregation.NONE, timePageLink.getSortOrder().getDirection().toString()))
                    .collect(Collectors.toList());

            var KvResult = this.tsService.findAll(tenantId, deviceId, queries).get();
            List<Map<String, Object>> result = new ArrayList<>();
            Map<Long, Map<String, Object>> resultMap = Maps.newLinkedHashMap();
            if (SortOrder.Direction.ASC.equals(timePageLink.getSortOrder().getDirection()))
                KvResult.stream().sorted(Comparator.comparing(TsKvEntry::getTs)).forEach(v -> resultMap.computeIfAbsent(v.getTs(), k -> new HashMap<>()).put(v.getKey(), this.formatKvEntryValue(v)));
            else
                KvResult.stream().sorted(Comparator.comparing(TsKvEntry::getTs).reversed()).forEach(v -> resultMap.computeIfAbsent(v.getTs(), k -> new HashMap<>()).put(v.getKey(), this.formatKvEntryValue(v)));
            resultMap.forEach((k, v) -> {
                v.put(HSConstants.CREATED_TIME, k);
                result.add(v);
            });
            var total = result.size();

            return result.subList(Math.min(timePageLink.getPageSize() * timePageLink.getPage(), total), Math.min(timePageLink.getPageSize() * (timePageLink.getPage() + 1), total));
        } else {
            var keyIds = this.tsLatestRepository.findAllKeyIdsByEntityId(deviceId.getId());
            if (keyIds.isEmpty())
                return Lists.newArrayList();
            var latestKeyId = Optional.ofNullable(this.tsLatestRepository.findLatestKey(deviceId.getId())).orElse(keyIds.get(0));
            var keyIdToKeyMap = this.tsDictionaryRepository.findAllByKeyIdIn(Sets.newHashSet(keyIds)).stream().collect(Collectors.toMap(TsKvDictionary::getKeyId, TsKvDictionary::getKey, (a, b) -> a));

            var pageData = this.tsRepository.findTss(deviceId.getId(), latestKeyId, timePageLink.getStartTime(), timePageLink.getEndTime(), timePageLink.getPageSize(), Math.max(0L, (timePageLink.getPage() - 1) * timePageLink.getPageSize()));
            if (pageData.isEmpty())
                return Lists.newArrayList();

            var time1 = pageData.get(0);
            var time2 = pageData.get(pageData.size() - 1);
            var kvEntityResult = this.tsRepository.findAllByStartTsAndEndTsOrderByTsDesc(deviceId.getId(), Sets.newHashSet(keyIds), Math.min(time1, time2), Math.max(time1, time2));
            List<Map<String, Object>> result = new ArrayList<>();
            Map<Long, Map<String, Object>> resultMap = Maps.newLinkedHashMap();
            kvEntityResult.stream().map(e -> {
                e.setStrKey(keyIdToKeyMap.getOrDefault(e.getKey(), HSConstants.NULL_STR));
                return e;
            }).map(TsKvEntity::toData).forEach(v -> {
                resultMap.computeIfAbsent(v.getTs(), k -> new HashMap<>()).put(v.getKey(), this.formatKvEntryValue(v));
            });
            resultMap.forEach((k, v) -> {
                v.put(HSConstants.CREATED_TIME, k);
                result.add(v);
            });

            return result;
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param deviceId ??????Id
     */
    @SuppressWarnings("Duplicates")
    public boolean getDeviceOnlineStatus(DeviceId deviceId) {
        try {
            if (persistToTelemetry) {
                List<TsKvEntry> tData = tsService.findLatest(TenantId.SYS_TENANT_ID, deviceId, Lists.newArrayList(HSConstants.ATTR_ACTIVE)).get();
                if (tData != null) {
                    for (KvEntry entry : tData) {
                        if (entry != null && !org.springframework.util.StringUtils.isEmpty(entry.getKey()) && entry.getKey().equals(HSConstants.ATTR_ACTIVE)) {
                            return entry.getBooleanValue().orElse(false);
                        }
                    }
                }
            } else {
                List<AttributeKvEntry> aData = attributesService.find(TenantId.SYS_TENANT_ID, deviceId, DataConstants.SERVER_SCOPE, Lists.newArrayList(HSConstants.ATTR_ACTIVE)).get();
                if (aData != null) {
                    for (KvEntry entry : aData) {
                        if (entry != null && !org.springframework.util.StringUtils.isEmpty(entry.getKey()) && entry.getKey().equals(HSConstants.ATTR_ACTIVE)) {
                            return entry.getBooleanValue().orElse(false);
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ?????????????????? specification
     *
     * @param tenantId ??????Id
     * @param t        extends FactoryDeviceQuery
     */
    @SuppressWarnings("all")
    public <T extends FactoryDeviceQuery> Specification<DeviceEntity> getDeviceQuerySpecification(TenantId tenantId, T t) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.<UUID>get("tenantId"), tenantId.getId()));
            predicates.add(cb.or(cb.isNull(root.<String>get("additionalInfo")), cb.equal(cb.locate(root.<String>get("additionalInfo"), "\"gateway\":true"), 0)));

            if (Boolean.TRUE.equals(t.getIsQueryAll())) {
                // do nothing
            } else if (!StringUtils.isBlank(t.getDeviceId())) {
                predicates.add(cb.equal(root.<UUID>get("id"), toUUID(t.getDeviceId())));
            } else if (!StringUtils.isBlank(t.getDeviceName())) {
                predicates.add(cb.like(root.get("rename"), "%" + t.getDeviceName().trim() + "%"));

                if (!StringUtils.isBlank(t.getFactoryId()))
                    predicates.add(cb.equal(root.<UUID>get("factoryId"), toUUID(t.getFactoryId())));
            } else if (!StringUtils.isBlank(t.getProductionLineId())) {
                predicates.add(cb.equal(root.<UUID>get("productionLineId"), toUUID(t.getProductionLineId())));
            } else if (!StringUtils.isBlank(t.getWorkshopId())) {
                predicates.add(cb.equal(root.<UUID>get("workshopId"), toUUID(t.getWorkshopId())));
            } else if (!StringUtils.isBlank(t.getFactoryId())) {
                predicates.add(cb.equal(root.<UUID>get("factoryId"), toUUID(t.getFactoryId())));
            } else {
                predicates.add(cb.isNull(root.<UUID>get("productionLineId")));
            }

            query.orderBy(cb.desc(root.get("createdTime"))).orderBy(cb.desc(root.get("name")));
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }

    /**
     * ?????????????????? specification
     *
     * @param tenantId ??????Id
     * @param t        extends FactoryDeviceQuery
     */
    @SuppressWarnings("all")
    public <T extends FactoryDeviceQuery> Specification<DeviceEntity> getDeviceQuerySpecificationOrderBySort(TenantId tenantId, T t) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.<UUID>get("tenantId"), tenantId.getId()));
            predicates.add(cb.or(cb.isNull(root.<String>get("additionalInfo")), cb.equal(cb.locate(root.<String>get("additionalInfo"), "\"gateway\":true"), 0)));

            if (Boolean.TRUE.equals(t.getIsQueryAll())) {
                // do nothing
            } else if (!StringUtils.isBlank(t.getDeviceId())) {
                predicates.add(cb.equal(root.<UUID>get("id"), toUUID(t.getDeviceId())));
            } else if (!StringUtils.isBlank(t.getProductionLineId())) {
                predicates.add(cb.equal(root.<UUID>get("productionLineId"), toUUID(t.getProductionLineId())));
            } else if (!StringUtils.isBlank(t.getWorkshopId())) {
                predicates.add(cb.equal(root.<UUID>get("workshopId"), toUUID(t.getWorkshopId())));
            } else if (!StringUtils.isBlank(t.getFactoryId())) {
                predicates.add(cb.equal(root.<UUID>get("factoryId"), toUUID(t.getFactoryId())));
            } else {
                predicates.add(cb.isNull(root.<UUID>get("productionLineId")));
            }

            query.orderBy(cb.asc(root.get("sort"))).orderBy(cb.desc(root.get("createdTime"))).orderBy(cb.desc(root.get("name")));
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }

    @Autowired
    public void setInitRepository(InitRepository initRepository) {
        this.initRepository = initRepository;
    }

    @Autowired
    public void setFactoryRepository(FactoryRepository factoryRepository) {
        this.factoryRepository = factoryRepository;
    }

    @Autowired
    public void setWorkshopRepository(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    @Autowired
    public void setProductionLineRepository(ProductionLineRepository productionLineRepository) {
        this.productionLineRepository = productionLineRepository;
    }

    @Autowired
    public void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Autowired
    public void setAttributeKvRepository(AttributeKvRepository attributeKvRepository) {
        this.attributeKvRepository = attributeKvRepository;
    }

    @Autowired
    public void setAttributesService(AttributesService attributesService) {
        this.attributesService = attributesService;
    }

    @Autowired
    public void setTsService(TimeseriesService tsService) {
        this.tsService = tsService;
    }

    @Autowired
    public void setFactoryService(FactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @Autowired
    public void setTsRepository(TsKvRepository tsRepository) {
        this.tsRepository = tsRepository;
    }

    @Autowired
    public void setTsLatestRepository(TsKvLatestRepository tsLatestRepository) {
        this.tsLatestRepository = tsLatestRepository;
    }

    @Autowired
    public void setTsDictionaryRepository(TsKvDictionaryRepository tsDictionaryRepository) {
        this.tsDictionaryRepository = tsDictionaryRepository;
    }

    @Autowired
    public void setCommonComponent(CommonComponent commonComponent) {
        this.commonComponent = commonComponent;
    }

    @Autowired
    public void setDictDeviceService(DictDeviceService dictDeviceService) {
        this.dictDeviceService = dictDeviceService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setBulletinBoardSvc(BulletinBoardSvc bulletinBoardSvc) {
        this.bulletinBoardSvc = bulletinBoardSvc;
    }

    @Autowired
    public void setCalenderRepository(ProductionCalenderRepository calenderRepository) {
        this.calenderRepository = calenderRepository;
    }

    @Autowired
    public void setDeviceOeeEveryHourService(DeviceOeeEveryHourService deviceOeeEveryHourService) {
        this.deviceOeeEveryHourService = deviceOeeEveryHourService;
    }

    @Autowired
    public void setFactoryDao(FactoryDao factoryDao) {
        this.factoryDao = factoryDao;
    }

    @Autowired
    public void setMesDeviceRelationRepository(MesDeviceRelationRepository mesDeviceRelationRepository) {
        this.mesDeviceRelationRepository = mesDeviceRelationRepository;
    }
}
