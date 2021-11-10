/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.device;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.apache.commons.collections.CollectionUtils;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.ota.OtaPackageUtil;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.attributes.AttributesDao;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
@Component
@Slf4j
public class JpaDeviceDao extends JpaAbstractSearchTextDao<DeviceEntity, Device> implements DeviceDao {

    public static final String ATTRIBUTE_VERSION = "version";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AttributesDao attributesDao;

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
    public List<DeviceEntity> findDeviceListBuyCdn(DeviceEntity deviceEntity){
        if(deviceEntity != null){
            Specification<DeviceEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("tenantId"),deviceEntity.getTenantId()));
                if(org.thingsboard.server.common.data.StringUtils.isNotEmpty(deviceEntity.getName())){
                    predicates.add(cb.like(root.get("name"),"%" + deviceEntity.getName().trim() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            return deviceRepository.findAll(specification);
        }
        return new ArrayList<>();
    }

    /**
     * 保存/修改
     * @param device
     * @return
     */
    @Override
    public Device saveOrUpdDevice(Device device){
        DeviceEntity deviceEntity = new DeviceEntity(device);
        if (deviceEntity.getUuid() == null) {
            UUID uuid = Uuids.timeBased();
            deviceEntity.setUuid(uuid);
            deviceEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
        }else{
            deviceRepository.deleteById(deviceEntity.getUuid());
            deviceEntity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        DeviceEntity entity = deviceRepository.save(deviceEntity);
        if(entity != null){
            return entity.toData();
        }
        return null;
    }

    /**
     * 移除产线设备
     * @param deviceIdList
     * @throws ThingsboardException
     */
    @Override
    public void removeProductionLine(List<UUID> deviceIdList,UUID updatedUser) throws ThingsboardException{
        deviceIdList.forEach(deviceId->{
            if(deviceId != null){
                DeviceEntity entity = new DeviceEntity();
                entity.setId(deviceId);
                entity.setFactoryId(null);
                entity.setWorkshopId(null);
                entity.setProductionLineId(null);
                entity.setUpdatedUser(updatedUser);
                entity.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
                deviceRepository.save(entity);
            }
        });

    }

    /**
     * 分配产线设备
     * @param device
     * @throws ThingsboardException
     */
    @Override
    public void addProductionLine(Device device) throws ThingsboardException{
        device.getDeviceIdList().forEach(deviceId->{
            if(deviceId != null){
                DeviceEntity entity = new DeviceEntity();
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
     * @param factoryIds 工厂标识
     * @return
     */
    @Override
    public List<Device> findGatewayNewVersionByFactory(List<UUID> factoryIds) throws ThingsboardException {
        //返回工厂最新版本网关
        List<Device> resultList = new ArrayList<>();
        try {
            //1.查询工厂关联的所有设备
            Specification<DeviceEntity> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if(factoryIds != null && factoryIds.size() > 0){
                    predicates.add(cb.in(root.get("factoryId").in(factoryIds)));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            List<DeviceEntity> deviceEntityList = deviceRepository.findAll(specification);
            //2.筛选出网关设备
            List<Device> gatewayList = new ArrayList<>();
            if(gatewayList != null && gatewayList.size() > 0){
                //筛选出网关设备
                Iterator<DeviceEntity> iterator = deviceEntityList.listIterator();
                while (iterator.hasNext()){
                    DeviceEntity deviceEntity = iterator.next();
                    JsonNode gateway = deviceEntity.getAdditionalInfo().get("gateway");
                    if(gateway != null && gateway.asBoolean()){
                        gatewayList.add(deviceEntity.toData());
                        continue;
                    }else {
                        iterator.remove();
                    }
                }
            }
            //3.查询网关设备“版本”共享属性值
            if(gatewayList != null && gatewayList.size() > 0){
                List<UUID> deviceIds = gatewayList.stream().map(Device::getId).collect(Collectors.toList()).stream().map(DeviceId::getId).collect(Collectors.toList());
                List<AttributeKvEntity> attributeKvEntities = attributesDao.findAllByEntityIds(deviceIds, DataConstants.SHARED_SCOPE,this.ATTRIBUTE_VERSION);
                if(!CollectionUtils.isEmpty(attributeKvEntities)){
                    gatewayList.forEach(i->{
                        attributeKvEntities.forEach(j->{
                            UUID entityId = j.getId().getEntityId();
                            String attributeKey = j.getId().getAttributeKey();
                            if(i.getId().getId().toString().equals(entityId.toString()) && this.ATTRIBUTE_VERSION.equals(attributeKey)){
                                i.setGatewayVersion(j.getStrValue());
                                i.setGatewayUpdateTs(j.getLastUpdateTs());
                                return;
                            }
                        });
                    });
                    //筛选，一个工厂只保留一个最新版本的网关设备。双重自循环筛选最大值
                    //存放最大值
                    Iterator<Device> gatewayIterator = gatewayList.iterator();
                    gatewayList.forEach(i->{
                        while (gatewayIterator.hasNext()){
                            Device gateway = gatewayIterator.next();
                            if(gateway.getFactoryId().toString().equals(i.getFactoryId().toString()) && !gateway.getId().toString().equals(i.getId().toString())){
                                if(StringUtils.isNotEmpty(gateway.getGatewayVersion()) && StringUtils.isNotEmpty(i.getGatewayVersion())){
                                    if(this.compareVersion(gateway.getGatewayVersion(),i.getGatewayVersion()) == -1){
                                        //把所有相同工厂下，版本小的数据移除掉
                                        //小于要过滤掉
                                        gatewayIterator.remove();
                                    }else {
                                        //大于或等于，重新筛选最大值
                                        if(CollectionUtils.isNotEmpty(resultList)){
                                            Iterator<Device> it = resultList.iterator();
                                            while (it.hasNext()){
                                                Device gatewayMax = it.next();
                                                if(gatewayMax.getFactoryId().toString().equals(gateway.getFactoryId().toString())){
                                                    if(this.compareVersion(gateway.getGatewayVersion(),gatewayMax.getGatewayVersion()) == 1){
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
        }catch (Exception e){
            log.error("JpaDeviceDao.findGatewayNewVersionByFactory执行异常",e);
            throw new ThingsboardException("JpaDeviceDao.findGatewayNewVersionByFactory执行异常", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        return resultList;
    }

    /**
     * 比较版本号大小
     * source大于target,返回值为1
     * source等于target,返回值为0
     * source小于target,返回值为-1
     * @param source
     * @param target
     * @return
     */
    private Integer compareVersion(String source,String target){
        Integer result = null;
        String[] sourceArray = source.split("\\.");
        String[] targetArray = target.split("\\.");
        int sourceLength = sourceArray.length;
        int targetLength = targetArray.length;
        for (int i =0; i < (sourceLength >targetLength?sourceLength:targetLength);i++){
            Integer v1 = 0;
            Integer v2 = 0;
            if(i < sourceLength){
                v1 = Integer.parseInt(sourceArray[i]);
            }
            if(i < targetLength){
                v2 = Integer.parseInt(targetArray[i]);
            }
            result = Integer.compare(v1,v2);
            if(result != 0){
                break;
            }
        }
        return result;
    }

}
