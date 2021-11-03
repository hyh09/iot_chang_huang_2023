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
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.ota.OtaPackageUtil;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
@Component
@Slf4j
public class JpaDeviceDao extends JpaAbstractSearchTextDao<DeviceEntity, Device> implements DeviceDao {

    @Autowired
    private DeviceRepository deviceRepository;

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

}
