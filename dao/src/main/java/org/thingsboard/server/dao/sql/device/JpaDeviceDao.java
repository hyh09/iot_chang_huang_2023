/**
 * Copyright © 2016-2021 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.device;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.productionline.ProductionLineId;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.ota.OtaPackageUtil;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.vo.device.CapacityDeviceVo;
import org.thingsboard.server.common.data.vo.device.DeviceDataSvc;
import org.thingsboard.server.common.data.vo.device.DeviceDataVo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.attributes.AttributesDao;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceInfoEntity;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.relation.RelationDao;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.util.BeanToMap;
import org.thingsboard.server.dao.util.sql.JpaQueryHelper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
@Component
@Slf4j
public class JpaDeviceDao extends JpaAbstractSearchTextDao<DeviceEntity, Device> implements DeviceDao {

    //版本
    public static final String ATTRIBUTE_VERSION = "version";
    //在线状态
    public static final String ATTRIBUTE_ACTIVE = "active";
    //倒序
    public static final String DESC = "DESC";
    //升序
    public static final String ASC = "ASC";
    public static final String SORT = "sort";

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ClientService clientService;

    @Autowired
    private AttributesDao attributesDao;

    @Autowired
    private ProductionLineDao productionLineDao;

    @Autowired
    private FactoryDao factoryDao;

    @Autowired
    private RelationDao relationDao;

    @Override
    protected Class<DeviceEntity> getEntityClass() {
        return DeviceEntity.class;
    }

    @Override
    protected CrudRepository<DeviceEntity, UUID> getCrudRepository() {
        return deviceRepository;
    }

    @Override
    public DeviceInfo findDeviceInfoById(TenantId tenantId, UUID deviceId) {
        return DaoUtil.getData(deviceRepository.findDeviceInfoById(deviceId));
    }

    @Override
    public List<Device> getYunDeviceList(Device device) {
        List<Device> result = new ArrayList<>();
        Specification<DeviceEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (device != null) {
                if (device.getTenantId() != null && device.getTenantId().getId() != null) {
                    predicates.add(cb.equal(root.get("tenantId"), device.getTenantId().getId()));
                }
                if (device.getId() != null && device.getId().getId() != null) {
                    predicates.add(cb.equal(root.get("id"), device.getId().getId()));
                }
                if (device.getUpdatedTime() != 0) {
                    // 下面是一个区间查询
                    predicates.add(cb.greaterThanOrEqualTo(root.get("updatedTime"), device.getUpdatedTime()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<DeviceEntity> all = deviceRepository.findAll(specification);
        if (CollectionUtils.isNotEmpty(all)) {
            all.forEach(i -> {
                result.add(i.toData());
            });
        }
        return result;
    }

    @Override
    public PageData<Device> findDevicesByTenantId(UUID tenantId, PageLink pageLink) {
        if (StringUtils.isEmpty(pageLink.getTextSearch())) {
            return DaoUtil.toPageData(
                    deviceRepository.findByTenantId(
                            tenantId,
                            DaoUtil.toPageable(pageLink)));
        } else {
            return DaoUtil.toPageData(
                    deviceRepository.findByTenantId(
                            tenantId,
                            Objects.toString(pageLink.getTextSearch(), ""),
                            DaoUtil.toPageable(pageLink)));
        }
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantId(
                        tenantId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public ListenableFuture<List<Device>> findDevicesByTenantIdAndIdsAsync(UUID tenantId, List<UUID> deviceIds) {
        return service.submit(() -> DaoUtil.convertDataList(deviceRepository.findDevicesByTenantIdAndIdIn(tenantId, deviceIds)));
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndProfileId(UUID tenantId, UUID profileId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findByTenantIdAndProfileId(
                        tenantId,
                        profileId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<UUID> findDevicesIdsByDeviceProfileTransportType(DeviceTransportType transportType, PageLink pageLink) {
        return DaoUtil.pageToPageData(deviceRepository.findIdsByDeviceProfileTransportType(transportType, DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public ListenableFuture<List<Device>> findDevicesByTenantIdCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> deviceIds) {
        return service.submit(() -> DaoUtil.convertDataList(
                deviceRepository.findDevicesByTenantIdAndCustomerIdAndIdIn(tenantId, customerId, deviceIds)));
    }

    @Override
    public Optional<Device> findDeviceByTenantIdAndName(UUID tenantId, String name) {
        Device device = DaoUtil.getData(deviceRepository.findByTenantIdAndName(tenantId, name));
        return Optional.ofNullable(device);
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findByTenantIdAndType(
                        tenantId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndTypeAndEmptyOtaPackage(UUID tenantId,
                                                                           UUID deviceProfileId,
                                                                           OtaPackageType type,
                                                                           PageLink pageLink) {
        Pageable pageable = DaoUtil.toPageable(pageLink);
        String searchText = Objects.toString(pageLink.getTextSearch(), "");
        Page<DeviceEntity> page = OtaPackageUtil.getByOtaPackageType(
                () -> deviceRepository.findByTenantIdAndTypeAndFirmwareIdIsNull(tenantId, deviceProfileId, searchText, pageable),
                () -> deviceRepository.findByTenantIdAndTypeAndSoftwareIdIsNull(tenantId, deviceProfileId, searchText, pageable),
                type
        );
        return DaoUtil.toPageData(page);
    }

    @Override
    public Long countDevicesByTenantIdAndDeviceProfileIdAndEmptyOtaPackage(UUID tenantId, UUID deviceProfileId, OtaPackageType type) {
        return OtaPackageUtil.getByOtaPackageType(
                () -> deviceRepository.countByTenantIdAndDeviceProfileIdAndFirmwareIdIsNull(tenantId, deviceProfileId),
                () -> deviceRepository.countByTenantIdAndDeviceProfileIdAndSoftwareIdIsNull(tenantId, deviceProfileId),
                type
        );
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantIdAndType(
                        tenantId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndDeviceProfileId(UUID tenantId, UUID deviceProfileId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantIdAndDeviceProfileId(
                        tenantId,
                        deviceProfileId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerIdAndDeviceProfileId(UUID tenantId, UUID customerId, UUID deviceProfileId, PageLink pageLink) {
        return DaoUtil.toPageData(
                deviceRepository.findDeviceInfosByTenantIdAndCustomerIdAndDeviceProfileId(
                        tenantId,
                        customerId,
                        deviceProfileId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink, DeviceInfoEntity.deviceInfoColumnMap)));
    }

    @Override
    public ListenableFuture<List<EntitySubtype>> findTenantDeviceTypesAsync(UUID tenantId) {
        return service.submit(() -> convertTenantDeviceTypesToDto(tenantId, deviceRepository.findTenantDeviceTypes(tenantId)));
    }

    @Override
    public Device findDeviceByTenantIdAndId(TenantId tenantId, UUID id) {
        return DaoUtil.getData(deviceRepository.findByTenantIdAndId(tenantId.getId(), id));
    }

    @Override
    public ListenableFuture<Device> findDeviceByTenantIdAndIdAsync(TenantId tenantId, UUID id) {
        return service.submit(() -> DaoUtil.getData(deviceRepository.findByTenantIdAndId(tenantId.getId(), id)));
    }

    @Override
    public Long countDevicesByDeviceProfileId(TenantId tenantId, UUID deviceProfileId) {
        return deviceRepository.countByDeviceProfileId(deviceProfileId);
    }

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return deviceRepository.countByTenantId(tenantId.getId());
    }

    private List<EntitySubtype> convertTenantDeviceTypesToDto(UUID tenantId, List<String> types) {
        List<EntitySubtype> list = Collections.emptyList();
        if (types != null && !types.isEmpty()) {
            list = new ArrayList<>();
            for (String type : types) {
                list.add(new EntitySubtype(new TenantId(tenantId), EntityType.DEVICE, type));
            }
        }
        return list;
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink) {
        log.debug("Try to find devices by tenantId [{}], edgeId [{}] and pageLink [{}]", tenantId, edgeId, pageLink);
        return DaoUtil.toPageData(deviceRepository
                .findByTenantIdAndEdgeId(
                        tenantId,
                        edgeId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndEdgeIdAndType(UUID tenantId, UUID edgeId, String type, PageLink pageLink) {
        log.debug("Try to find devices by tenantId [{}], edgeId [{}], type [{}] and pageLink [{}]", tenantId, edgeId, type, pageLink);
        return DaoUtil.toPageData(deviceRepository
                .findByTenantIdAndEdgeIdAndType(
                        tenantId,
                        edgeId,
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public List<Device> findDeviceListByCdn(Device device, String orderValue, String descOrAsc) {
        List<Device> resultList = new ArrayList<>();
        if (device != null) {
            Specification<DeviceEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (device.getTenantId() != null && org.thingsboard.server.common.data.StringUtils.isNotEmpty(device.getTenantId().toString())) {
                    predicates.add(cb.equal(root.get("tenantId"), device.getTenantId().getId()));
                }
                if (org.thingsboard.server.common.data.StringUtils.isNotEmpty(device.getName())) {
                    predicates.add(cb.like(root.get("name"), "%" + device.getName().trim() + "%"));
                }
                if (org.thingsboard.server.common.data.StringUtils.isNotEmpty(device.getRename())) {
                    predicates.add(cb.like(root.get("rename"), "%" + device.getRename().trim() + "%"));
                }
                if (device.getFactoryId() != null && StringUtils.isNotEmpty(device.getFactoryId().toString())) {
                    predicates.add(cb.equal(root.get("factoryId"), device.getFactoryId()));
                }
                if (device.getWorkshopId() != null && StringUtils.isNotEmpty(device.getWorkshopId().toString())) {
                    predicates.add(cb.equal(root.get("workshopId"), device.getWorkshopId()));
                }
                if (device.getProductionLineId() != null && StringUtils.isNotEmpty(device.getProductionLineId().toString())) {
                    predicates.add(cb.equal(root.get("productionLineId"), device.getProductionLineId()));
                }
                if (device.getDictDeviceId() != null && StringUtils.isNotEmpty(device.getDictDeviceId().toString())) {
                    predicates.add(cb.equal(root.get("dictDeviceId"), device.getDictDeviceId()));
                }
                //产线id批量
                if (CollectionUtils.isNotEmpty(device.getProductionLineIds())) {
                    // 下面是一个 IN查询
                    CriteriaBuilder.In<UUID> in = cb.in(root.get("productionLineId"));
                    device.getProductionLineIds().forEach(in::value);
                    predicates.add(in);
                }
                //排序
                Order sort = cb.asc(root.get("sort"));
                if (StringUtils.isNotEmpty(orderValue) && StringUtils.isNotEmpty(descOrAsc)) {
                    Order order = null;
                    if (descOrAsc.equals(DESC)) {
                        order = cb.desc(root.get(orderValue));
                    } else {
                        order = cb.asc(root.get(orderValue));
                    }
                    return query.orderBy(sort, order).where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
                }
                return query.orderBy(sort).where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            };
            List<DeviceEntity> all = deviceRepository.findAll(specification);
            if (CollectionUtils.isNotEmpty(all)) {
                for (DeviceEntity i : all) {
                    Device deviceBo = i.toData();
                    //是否只要网关
                    if (device.getOnlyGatewayFlag()) {
                        JsonNode additionalInfo = i.getAdditionalInfo();
                        if (additionalInfo == null) {
                            continue;
                        } else {
                            JsonNode gateway = additionalInfo.get("gateway");
                            if (gateway == null || !gateway.asBoolean()) {
                                continue;
                            }
                        }
                    }
                    //是否过滤掉网关
                    if (device.getFilterGatewayFlag()) {
                        //过滤网关
                        JsonNode additionalInfo = i.getAdditionalInfo();
                        if (additionalInfo != null) {
                            JsonNode gateway = additionalInfo.get("gateway");
                            if (gateway != null && gateway.asBoolean()) {
                                continue;
                            }
                        }
                    }
                    //是否过滤图片
                    if (device.getFilterIconFlag()) {
                        deviceBo.setIcon(null);
                    }
                    if (device.getFilterPictureFlag()) {
                        deviceBo.setPicture(null);
                    }
                    resultList.add(deviceBo);
                }
            }
        }
        return this.getParentNameByList(resultList);
    }

    /**
     * 保存/修改
     *
     * @param device
     * @return
     */
    @Override
    public Device saveOrUpdDevice(Device device) {
        DeviceEntity deviceEntity = new DeviceEntity(device);
        if (deviceEntity.getUuid() == null) {
            UUID uuid = Uuids.timeBased();
            deviceEntity.setUuid(uuid);
            deviceEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
        } else {
            deviceRepository.deleteById(deviceEntity.getUuid());
            deviceEntity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        DeviceEntity entity = deviceRepository.save(deviceEntity);
        if (entity != null) {
            return entity.toData();
        }
        return null;
    }

    /**
     * 移除产线设备
     *
     * @param deviceIdList
     * @throws ThingsboardException
     */
    @Override
    public void removeProductionLine(List<UUID> deviceIdList, UUID updatedUser) throws ThingsboardException {
        deviceIdList.forEach(deviceId -> {
            if (deviceId != null) {
                DeviceEntity entity = deviceRepository.findById(deviceId).get();
                UUID productionLineId = entity.getProductionLineId();
                entity.setId(deviceId);
                entity.setFactoryId(null);
                entity.setWorkshopId(null);
                entity.setProductionLineId(null);
                entity.setUpdatedUser(updatedUser);
                entity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
                deviceRepository.save(entity);
                //清除实体关系
                if (productionLineId != null) {
                    EntityRelation relation = new EntityRelation(
                            new ProductionLineId(productionLineId), new DeviceId(entity.getId()), EntityRelation.CONTAINS_TYPE
                    );
                    relationDao.deleteRelation(new TenantId(entity.getTenantId()), relation);
                }
            }
        });

    }

    /**
     * 分配产线设备
     *
     * @param device
     * @throws ThingsboardException
     */
    @Override
    public void addProductionLine(Device device) throws ThingsboardException {
        device.getDeviceIdList().forEach(deviceId -> {
            if (deviceId != null) {
                DeviceEntity entity = deviceRepository.findById(deviceId).get();
                entity.setId(deviceId);
                entity.setFactoryId(device.getFactoryId());
                entity.setWorkshopId(device.getWorkshopId());
                entity.setProductionLineId(device.getProductionLineId());
                entity.setUpdatedUser(device.getUpdatedUser());
                entity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
                deviceRepository.save(entity);
            }
        });
    }

    /**
     * 查询工厂下具有最新版本的一个网关设备
     *
     * @param factoryIds 工厂标识
     * @return
     */
    @Override
    public List<Device> findGatewayNewVersionByFactory(List<UUID> factoryIds) throws ThingsboardException {
        //返回工厂最新版本网关
        List<Device> resultList = new ArrayList<>();
        try {
            //1.查询工厂关联的所有设备
            List<DeviceEntity> deviceEntityList = this.getDevicesByIds(factoryIds);
            //2.筛选出网关设备
            List<Device> gatewayList = this.getGatewayList(deviceEntityList);
            //3.查询网关设备“版本”和“在线状态”
            this.getAttributeResult(gatewayList);
            //4.筛选，一个工厂只保留一个最新版本的网关设备。双重自循环筛选最大值
            this.filterMaxVersion(resultList, gatewayList);

        } catch (Exception e) {
            log.error("JpaDeviceDao.findGatewayNewVersionByFactory执行异常", e);
            throw new ThingsboardException("JpaDeviceDao.findGatewayNewVersionByFactory执行异常", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        return resultList;
    }


    /**
     * 查询工厂下所有网关设备
     *
     * @param factoryIds
     * @return
     */
    @Override
    public List<Device> findGatewayListVersionByFactory(List<UUID> factoryIds) throws ThingsboardException {
        List<Device> resultList = new ArrayList<>();
        try {
            //1.查询工厂关联的所有设备
            List<DeviceEntity> deviceEntityList = this.getDevicesByIds(factoryIds);
            //2.筛选出网关设备
            List<Device> gatewayList = this.getGatewayList(deviceEntityList);
            //3.查询网关设备“版本”和“在线状态”
            this.getAttributeResult(gatewayList);
            resultList = gatewayList;
        } catch (Exception e) {
            log.error("JpaDeviceDao.findGatewayNewVersionByFactory执行异常", e);
            throw new ThingsboardException("JpaDeviceDao.findGatewayNewVersionByFactory执行异常", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        return resultList;
    }

    /**
     * 筛选，一个工厂只保留一个最新版本的网关设备。双重自循环筛选最大值
     *
     * @param resultList
     * @param gatewayList
     */
    private void filterMaxVersion(List<Device> resultList, List<Device> gatewayList) {
        if (CollectionUtils.isNotEmpty(gatewayList)) {
            //4.筛选，一个工厂只保留一个最新版本的网关设备。双重自循环筛选最大值
            //存放最大值
            Iterator<Device> gatewayIterator = gatewayList.iterator();
            gatewayList.forEach(i -> {
                while (gatewayIterator.hasNext()) {
                    Device gateway = gatewayIterator.next();
                    if (gateway.getFactoryId().toString().equals(i.getFactoryId().toString()) && !gateway.getId().toString().equals(i.getId().toString())) {
                        if (StringUtils.isNotEmpty(gateway.getGatewayVersion()) && StringUtils.isNotEmpty(i.getGatewayVersion())) {
                            if (this.compareVersion(gateway.getGatewayVersion(), i.getGatewayVersion()) == -1) {
                                //把所有相同工厂下，版本小的数据移除掉
                                //小于要过滤掉
                                gatewayIterator.remove();
                            } else {
                                //大于或等于，重新筛选最大值
                                if (CollectionUtils.isNotEmpty(resultList)) {
                                    Iterator<Device> it = resultList.iterator();
                                    while (it.hasNext()) {
                                        Device gatewayMax = it.next();
                                        if (gatewayMax.getFactoryId().toString().equals(gateway.getFactoryId().toString())) {
                                            if (this.compareVersion(gateway.getGatewayVersion(), gatewayMax.getGatewayVersion()) == 1) {
                                                it.remove();
                                                resultList.add(gateway);
                                                break;
                                            }
                                        }
                                    }
                                }
                                resultList.add(gateway);
                            }
                        }

                    }
                }
            });
        }
    }

    /**
     * 查询网关设备“版本”和“在线状态”
     *
     * @param gatewayList
     */
    private void getAttributeResult(List<Device> gatewayList) {
        if (gatewayList != null && gatewayList.size() > 0) {
            List<UUID> deviceIds = gatewayList.stream().map(Device::getId).collect(Collectors.toList()).stream().map(DeviceId::getId).collect(Collectors.toList());
            //查询版本（共享属性值）
            List<AttributeKvEntity> versionAttributeKvEntities = attributesDao.findAllByEntityIds(deviceIds, DataConstants.SHARED_SCOPE, this.ATTRIBUTE_VERSION);
            if (!CollectionUtils.isEmpty(versionAttributeKvEntities)) {
                //查询在线状态（服务端属性）
                List<AttributeKvEntity> activeAttributeKvEntities = attributesDao.findAllByEntityIds(deviceIds, DataConstants.SERVER_SCOPE, this.ATTRIBUTE_ACTIVE);
                //给网关设备属性赋值
                gatewayList.forEach(i -> {
                    versionAttributeKvEntities.forEach(j -> {
                        UUID entityId = j.getId().getEntityId();
                        String attributeKey = j.getId().getAttributeKey();
                        if (i.getId().getId().toString().equals(entityId.toString()) && this.ATTRIBUTE_VERSION.equals(attributeKey)) {
                            i.setGatewayVersion(j.getStrValue());
                            i.setGatewayUpdateTs(j.getLastUpdateTs());
                            //拿到网关在线状态
                            if (CollectionUtils.isNotEmpty(activeAttributeKvEntities)) {
                                activeAttributeKvEntities.forEach(active -> {
                                    if (j.getId().getEntityId() != null && active.getId().getEntityId() != null && j.getId().getEntityId().toString().equals(active.getId().getEntityId().toString())) {
                                        i.setActive(active.getBooleanValue());
                                        return;
                                    }
                                });
                            }
                            return;
                        }
                    });
                });
            }
        }
    }

    /**
     * 筛选出网关设备
     *
     * @param deviceEntityList
     * @return
     */
    @NotNull
    private List<Device> getGatewayList(List<DeviceEntity> deviceEntityList) {
        List<Device> gatewayList = new ArrayList<>();
        if (deviceEntityList != null && deviceEntityList.size() > 0) {
            //筛选出网关设备
            Iterator<DeviceEntity> iterator = deviceEntityList.listIterator();
            while (iterator.hasNext()) {
                DeviceEntity deviceEntity = iterator.next();
                if (deviceEntity.getAdditionalInfo() != null) {
                    JsonNode gateway = deviceEntity.getAdditionalInfo().get("gateway");
                    if (gateway != null && gateway.asBoolean()) {
                        gatewayList.add(deviceEntity.toData());
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
        return gatewayList;
    }

    /**
     * 查询工厂关联的所有设备
     *
     * @param factoryIds
     * @return
     */
    @NotNull
    private List<DeviceEntity> getDevicesByIds(List<UUID> factoryIds) {
        Specification<DeviceEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (factoryIds != null && factoryIds.size() > 0) {
                // 下面是一个 IN查询
                CriteriaBuilder.In<UUID> in = cb.in(root.get("factoryId"));
                factoryIds.forEach(in::value);
                predicates.add(in);
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<DeviceEntity> deviceEntityList = deviceRepository.findAll(specification);
        return deviceEntityList;
    }

    /**
     * 分页查询工厂关联的所有设备
     *
     * @param factoryIds
     * @return
     */
    @NotNull
    private PageData<DeviceEntity> getDevicesPagesByIds(List<UUID> factoryIds, PageLink pageLink) {
        Specification<DeviceEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (factoryIds != null && factoryIds.size() > 0) {
                // 下面是一个 IN查询
                CriteriaBuilder.In<UUID> in = cb.in(root.get("factoryId"));
                factoryIds.forEach(in::value);
                predicates.add(in);
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Pageable pageable = DaoUtil.toPageable(pageLink);
        Page<DeviceEntity> page = deviceRepository.findAll(specification, pageable);
        return DaoUtil.pageToPageData(deviceRepository.findAll(specification, pageable));
    }


    @Override
    public List<DeviceEntity> queryAllByIds(List<UUID> ids) {
        return deviceRepository.queryAllByIds(ids);
    }

    /**
     * 比较版本号大小
     * source大于target,返回值为1
     * source等于target,返回值为0
     * source小于target,返回值为-1
     *
     * @param source
     * @param target
     * @return
     */
    private Integer compareVersion(String source, String target) {
        Integer result = null;
        String[] sourceArray = source.split("\\.");
        String[] targetArray = target.split("\\.");
        int sourceLength = sourceArray.length;
        int targetLength = targetArray.length;
        for (int i = 0; i < (sourceLength > targetLength ? sourceLength : targetLength); i++) {
            Integer v1 = 0;
            Integer v2 = 0;
            if (i < sourceLength) {
                v1 = Integer.parseInt(sourceArray[i]);
            }
            if (i < targetLength) {
                v2 = Integer.parseInt(targetArray[i]);
            }
            result = Integer.compare(v1, v2);
            if (result != 0) {
                break;
            }
        }
        return result;
    }

    /**
     * 平台设备列表查询
     *
     * @param device
     * @param pageLink
     * @return
     */
    @Override
    public PageData<Device> getTenantDeviceInfoList(Device device, PageLink pageLink) {
        // 动态条件查询
        Specification<DeviceEntity> specification = this.queryCondition(device, pageLink);
        Pageable pageable = DaoUtil.toPageable(pageLink);
        Page<DeviceEntity> deviceEntities = deviceRepository.findAll(specification, pageable);
        PageData<Device> resultPage = new PageData<>();
        resultPage = new PageData<Device>(this.resultList(deviceEntities.getContent()), deviceEntities.getTotalPages(), deviceEntities.getTotalElements(), deviceEntities.hasNext());
        return resultPage;
    }

    @Override
    public PageData<DeviceDataVo> queryAllByNameLike(UUID factoryId, String name, PageLink pageLink) {
        Pageable pageable = DaoUtil.toPageable(pageLink);
//        Page<DeviceDataVo> deviceEntityPage =  deviceRepository.queryAllByNameLike(factoryId,name,pageable);
        Page<DeviceDataSvc> deviceEntityPage = deviceRepository.queryAllByNameLikeNativeQuery(factoryId, name, pageable);
        List<DeviceDataVo> deviceDataVoList = DeviceDataVo.toData(deviceEntityPage.getContent());
        List<UUID> idList = deviceDataVoList.stream().map(DeviceDataVo::getDeviceId).collect(Collectors.toList());
        Map<String, Boolean> map1 = clientService.listDevicesOnlineStatus(idList);
        log.info("打印的queryAllByNameLike.map1{}", map1);
        deviceDataVoList.stream().forEach(d1 -> {
            Boolean str = map1.get(d1.getDeviceId().toString());
            d1.setOnlineStatus((str) ? "1" : "0");
        });

        return new PageData<DeviceDataVo>((deviceDataVoList), deviceEntityPage.getTotalPages(), deviceEntityPage.getTotalElements(), deviceEntityPage.hasNext());
    }

    /**
     * 获取设备详情
     *
     * @param id
     * @return
     */
    @Override
    public Device getDeviceInfo(UUID id) {
        DeviceEntity entity = deviceRepository.findById(id).get();
        Device device = entity.toData();
        if (device.getProductionLineId() != null && StringUtils.isNotEmpty(device.getProductionLineId().toString())) {
            ProductionLine productionLine = productionLineDao.findById(device.getProductionLineId());
            if (productionLine != null) {
                device.setFactoryName(productionLine.getFactoryName());
                device.setWorkshopName(productionLine.getWorkshopName());
                device.setProductionLineName(productionLine.getName());
            }
        }
        return device;
    }

    /**
     * 获取设备详情
     *
     * @param id
     * @return
     */
    @Override
    public Device findById(UUID id) {
        DeviceEntity entity = deviceRepository.findById(id).get();
        if (entity != null) {
            return entity.toData();
        }
        return null;
    }

    /**
     * 批量查询
     *
     * @param ids
     * @return
     */
    @Override
    public List<Device> getDeviceByIdList(List<UUID> ids) {
        List<Device> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ids)) {
            Specification<DeviceEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                // 下面是一个 IN查询
                CriteriaBuilder.In<UUID> in = cb.in(root.get("id"));
                ids.forEach(in::value);
                predicates.add(in);

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            List<DeviceEntity> all = deviceRepository.findAll(specification);
            if (CollectionUtils.isNotEmpty(all)) {
                //查询产线名称
                List<UUID> productionLineIds = all.stream().map(s -> s.getProductionLineId()).collect(Collectors.toList());
                if (!org.springframework.util.CollectionUtils.isEmpty(productionLineIds)) {
                    productionLineIds = productionLineIds.stream().distinct().collect(Collectors.toList());
                }
                List<ProductionLine> productionLineList = productionLineDao.getProductionLineByIdList(productionLineIds);
                all.forEach(i -> {
                    Device device = i.toData();
                    if (CollectionUtils.isNotEmpty(productionLineList)) {
                        productionLineList.forEach(j -> {
                            if (i.getProductionLineId() != null && i.getProductionLineId().toString().equals(j.getId().toString())) {
                                device.setFactoryName(j.getFactoryName());
                                device.setWorkshopName(j.getWorkshopName());
                                device.setProductionLineName(j.getName());
                            }
                        });
                    }
                    resultList.add(device);
                });
            }
        }
        return resultList;
    }

    /**
     * 查询租户下未分配设备
     *
     * @param tenantId
     * @return
     */
    @Override
    public List<Device> getNotDistributionDevice(TenantId tenantId) {
        Device device = new Device();
        device.setTenantId(tenantId);
        device.setAllot(false);
        List<Device> collect = this.queryList(device).stream().map(m -> {
            m.setPicture(null);
            return m;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public PageData<Device> queryPage(CapacityDeviceVo vo, PageLink pageLink) throws JsonProcessingException {
        Pageable pageable = DaoUtil.toPageable(pageLink);
        Map<String, Object> queryParam = BeanToMap.beanToMapByJackson(vo);
        if (vo.getDeviceId() != null) {
            queryParam.put("id", vo.getDeviceId());
        }
        if (StringUtils.isNotEmpty(vo.getDeviceName())) {
            queryParam.put("name", vo.getDeviceName());

        }
        Page<DeviceEntity> pageData = deviceRepository.findAll(JpaQueryHelper.createQueryDeviceByMap(queryParam, DeviceEntity.class), pageable);
        return DaoUtil.toPageData(pageData);
    }


    @Override
    public void updateFlgById(Boolean deviceFlg, UUID id) {
        deviceRepository.updateFlgById(deviceFlg, id);
    }

    @Override
    public List<Device> queryAllByTenantIdAndName(TenantId tenantId, String name) {
        return DaoUtil.convertDataList(deviceRepository.queryAllByTenantIdAndName(tenantId.getId(), name));
    }

    /**
     * 查询设备排除网关，返回工厂名
     *
     * @param tenantId
     * @return
     */
    @Override
    public List<Device> findDeviceFilterGatewayByTenantId(UUID tenantId) {
        List<Device> resultList = new ArrayList<>();
        List<DeviceEntity> deviceFilterGatewayByTenantId = deviceRepository.findDeviceFilterGatewayByTenantId(tenantId);
        if (CollectionUtils.isNotEmpty(deviceFilterGatewayByTenantId)) {
            deviceFilterGatewayByTenantId.forEach(i -> {
                resultList.add(i.toData());
            });
        }
        return this.getFactoryByList(resultList);
    }


    @Override
    public long countAllByDictDeviceIdAndTenantId(UUID dictDeviceId, UUID tenantId) {
        return this.deviceRepository.countAllByDictDeviceIdAndTenantId(dictDeviceId, tenantId);
    }

    /**
     * 查询工厂下网关
     *
     * @param factoryId
     * @return
     */
    @Override
    public List<Device> findGatewayByFactoryId(UUID factoryId) {
        List<Device> deviceList = new ArrayList<>();
        List<DeviceEntity> deviceFilterGatewayByFactoryId = deviceRepository.findGatewayByFactoryId(factoryId);
        if (CollectionUtils.isNotEmpty(deviceFilterGatewayByFactoryId)) {
            deviceFilterGatewayByFactoryId.forEach(i -> {
                deviceList.add(i.toData());
            });
        }
        return deviceList;
    }


    @Override
    public List<Device> findAllBy() {
        return DaoUtil.convertDataList(deviceRepository.findAllBy());
    }

    /**
     * ————————推荐的方法
     * 作者：wb04
     * 时间: 2022-11-28
     * 接口描述: 通用精确查询设备(过滤掉网关设备)方法  #目前支持 tenantId,factoryId,workshopId,productionLineId,id
     * 过滤掉网关设备：如果 gateway 不为null ，且 gateway 为true ; 都是网关设备
     *
     * @param userExtension
     * @return
     */
    @Override
    public List<DeviceEntity> findAllByEntity(DeviceEntity userExtension) {
        List<DeviceEntity> entities = this.deviceRepository.findAll(JpaDeviceSpecificationUtil.build.specification(userExtension));
        if (CollectionUtils.isEmpty(entities)) {
            return entities;
        }
        List<DeviceEntity> entities1 = new ArrayList<>();
        entities.stream().forEach(m1 -> {
            if (m1.getAdditionalInfo() == null) {
                entities1.add(m1);
            } else {
                JsonNode gateway = m1.getAdditionalInfo().get("gateway");
                if (gateway != null && !gateway.asBoolean()) {
                    entities1.add(m1);
                }
                if (gateway == null) {
                    entities1.add(m1);
                }
            }
        });
        return entities1;
    }

    /**
     * 查工厂名称
     *
     * @param deviceList
     * @return
     */
    public List<Device> getFactoryByList(List<Device> deviceList) {
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //查询产线名称
            List<UUID> factoryIds = deviceList.stream().map(s -> s.getProductionLineId()).collect(Collectors.toList());
            if (!org.springframework.util.CollectionUtils.isEmpty(factoryIds)) {
                factoryIds = factoryIds.stream().distinct().collect(Collectors.toList());
            }
            List<Factory> resultFactory = factoryDao.getFactoryByIdList(factoryIds);
            deviceList.forEach(i -> {
                if (CollectionUtils.isNotEmpty(resultFactory)) {
                    resultFactory.forEach(j -> {
                        if (i.getFactoryId() != null && i.getFactoryId().toString().equals(j.getId().toString())) {
                            i.setFactoryName(j.getName());
                        }
                    });
                }
            });
        }
        return deviceList;
    }


    /**
     * 获取父级名称
     *
     * @param deviceList
     * @return
     */
    public List<Device> getParentNameByList(List<Device> deviceList) {
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //查询产线名称
            List<UUID> productionLineIds = deviceList.stream().map(s -> s.getProductionLineId()).collect(Collectors.toList());
            if (!org.springframework.util.CollectionUtils.isEmpty(productionLineIds)) {
                productionLineIds = productionLineIds.stream().distinct().collect(Collectors.toList());
            }
            List<ProductionLine> productionLineList = productionLineDao.getProductionLineByIdList(productionLineIds);
            deviceList.forEach(i -> {
                if (CollectionUtils.isNotEmpty(productionLineList)) {
                    productionLineList.forEach(j -> {
                        if (i.getProductionLineId() != null && i.getProductionLineId().toString().equals(j.getId().toString())) {
                            i.setFactoryName(j.getFactoryName());
                            i.setWorkshopName(j.getWorkshopName());
                            i.setProductionLineName(j.getName());
                        }
                    });
                }
            });
        }
        return deviceList;
    }

    /**
     * 条件查询，返回list
     *
     * @param device
     * @return
     */
    private List<Device> queryList(Device device) {
        return this.resultList(deviceRepository.findAll(this.queryCondition(device, null)));
    }

    /**
     * 构造查询条件,需要家条件在这里面加
     *
     * @param device
     * @param pageLink
     * @return
     */
    private Specification<DeviceEntity> queryCondition(Device device, PageLink pageLink) {
        // 动态条件查询
        Specification<DeviceEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (device != null) {
                if (device.getTenantId() != null && device.getTenantId().getId() != null) {
                    predicates.add(cb.equal(root.get("tenantId"), device.getTenantId().getId()));
                }
                if (device.getAllot() != null) {
                    if (device.getAllot()) {
                        //已分配。根据工厂id或车间id不为空来查询
                        List<Predicate> factoryOrProductionLine = new ArrayList<>();
                        factoryOrProductionLine.add(cb.isNotNull(root.get("factoryId")));
                        factoryOrProductionLine.add(cb.isNotNull(root.get("productionLineId")));
                        /* 下面这一行代码很重要。
                         * criteriaBuilder.or(Predicate... restrictions) 接收多个Predicate，可变参数；
                         * 这多个 Predicate条件之间，是使用OR连接的；该方法最终返回 一个Predicate对象；
                         */
                        predicates.add(cb.or(factoryOrProductionLine.toArray(new Predicate[0])));
                    } else {
                        //未分配。根据工厂id或车间id为空来查询
                        predicates.add(cb.isNull(root.get("factoryId")));
                        predicates.add(cb.isNull(root.get("productionLineId")));
                    }
                }
                if (StringUtils.isNotEmpty(device.getName())) {
                    predicates.add(cb.like(root.get("name"), "%" + device.getName().trim() + "%"));
                }
                if (StringUtils.isNotEmpty(device.getRename())) {
                    predicates.add(cb.like(root.get("rename"), "%" + device.getRename().trim() + "%"));
                }
                if (StringUtils.isNotEmpty(device.getType())) {
                    predicates.add(cb.like(root.get("type"), "%" + device.getType().trim() + "%"));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return specification;
    }

    /**
     * 返回值，List
     *
     * @param deviceList
     * @return
     */
    private List<Device> resultList(List<DeviceEntity> deviceList) {
        List<Device> resultDeviceList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(deviceList)) {
            deviceList.forEach(i -> {
                Device device = i.toData();
                if (i.getProductionLineId() != null && StringUtils.isNotEmpty(i.getProductionLineId().toString())) {
                    ProductionLine productionLine = productionLineDao.findById(i.getProductionLineId());
                    if (device != null && productionLine != null) {
                        device.setFactoryName(productionLine.getFactoryName());
                        device.setWorkshopName(productionLine.getWorkshopName());
                        device.setProductionLineName(productionLine.getName());
                    }
                } else if (i.getFactoryId() != null && StringUtils.isNotEmpty(i.getFactoryId().toString())) {
                    Factory factory = factoryDao.findById(i.getFactoryId());
                    if (device != null && factory != null) {
                        device.setFactoryName(factory.getName());
                    }

                }
                resultDeviceList.add(device);
            });
        }
        return resultDeviceList;
    }

}
