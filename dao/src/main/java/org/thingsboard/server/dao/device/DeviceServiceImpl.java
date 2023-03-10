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
package org.thingsboard.server.dao.device;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.device.DeviceSearchQuery;
import org.thingsboard.server.common.data.device.credentials.BasicMqttCredentials;
import org.thingsboard.server.common.data.device.data.*;
import org.thingsboard.server.common.data.devicecomponent.DeviceComponent;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.id.productionline.ProductionLineId;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.common.data.security.DeviceCredentialsType;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.common.data.vo.device.AppCapacityDeviceVo;
import org.thingsboard.server.common.data.vo.device.CapacityDeviceVo;
import org.thingsboard.server.common.data.vo.device.DeviceDataVo;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.device.provision.ProvisionFailedException;
import org.thingsboard.server.dao.device.provision.ProvisionRequest;
import org.thingsboard.server.dao.device.provision.ProvisionResponseStatus;
import org.thingsboard.server.dao.devicecomponent.DeviceComponentDao;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.event.EventService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.dao.DictDeviceComponentEntity;
import org.thingsboard.server.dao.hs.dao.DictDeviceComponentRepository;
import org.thingsboard.server.dao.hs.dao.DictDeviceEntity;
import org.thingsboard.server.dao.hs.dao.DictDeviceRepository;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.model.sql.FactoryEntity;
import org.thingsboard.server.dao.ota.OtaPackageService;
import org.thingsboard.server.dao.productionline.ProductionLineDao;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.sql.factory.FactoryRepository;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantDao;
import org.thingsboard.server.dao.workshop.WorkshopDao;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.thingsboard.server.common.data.CacheConstants.DEVICE_CACHE;
import static org.thingsboard.server.dao.DaoUtil.toUUIDs;
import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;
import static org.thingsboard.server.dao.service.Validator.*;

@Service
@Slf4j
public class DeviceServiceImpl extends AbstractEntityService implements DeviceService {

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_DEVICE_PROFILE_ID = "Incorrect deviceProfileId ";
    public static final String INCORRECT_PAGE_LINK = "Incorrect page link ";
    public static final String INCORRECT_CUSTOMER_ID = "Incorrect customerId ";
    public static final String INCORRECT_DEVICE_ID = "Incorrect deviceId ";
    public static final String INCORRECT_EDGE_ID = "Incorrect edgeId ";
    public static final String SAVE_TYPE_ADD = "add ";
    public static final String SAVE_TYPE_ADD_UPDATE = "update ";
    public static final String GATEWAY = "gateway";



    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private TenantDao tenantDao;
    @Autowired
    private FactoryDao factoryDao;

     @Autowired
     private WorkshopDao workshopDao;
     @Autowired
     private ProductionLineDao productionLineDao;
     @Autowired
     private FactoryRepository factoryRepository;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DeviceCredentialsService deviceCredentialsService;

    @Autowired
    private DeviceProfileService deviceProfileService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EventService eventService;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    @Autowired
    private OtaPackageService otaPackageService;

    @Autowired
    private DeviceComponentDao deviceComponentDao;

    @Autowired
    private DictDeviceComponentRepository componentRepository;
    @Autowired private DictDeviceRepository dictDeviceRepository;

    @Autowired
    private DictDeviceService dictDeviceService;

    @Override
    public DeviceInfo findDeviceInfoById(TenantId tenantId, DeviceId deviceId) {
        log.trace("Executing findDeviceInfoById [{}]", deviceId);
        validateId(deviceId, INCORRECT_DEVICE_ID + deviceId);
        return deviceDao.findDeviceInfoById(tenantId, deviceId.getId());
    }

    @Cacheable(cacheNames = DEVICE_CACHE, key = "{#tenantId, #deviceId}")
    @Override
    public Device findDeviceById(TenantId tenantId, DeviceId deviceId) {
        log.trace("Executing findDeviceById [{}]", deviceId);
        validateId(deviceId, INCORRECT_DEVICE_ID + deviceId);
        if (TenantId.SYS_TENANT_ID.equals(tenantId)) {
            return deviceDao.findById(tenantId, deviceId.getId());
        } else {
            return deviceDao.findDeviceByTenantIdAndId(tenantId, deviceId.getId());
        }
    }

    @Override
    public List<Device> getYunDeviceList(Device device) {
        return deviceDao.getYunDeviceList(device);
    }

    @Override
    public ListenableFuture<Device> findDeviceByIdAsync(TenantId tenantId, DeviceId deviceId) {
        log.trace("Executing findDeviceById [{}]", deviceId);
        validateId(deviceId, INCORRECT_DEVICE_ID + deviceId);
        if (TenantId.SYS_TENANT_ID.equals(tenantId)) {
            return deviceDao.findByIdAsync(tenantId, deviceId.getId());
        } else {
            return deviceDao.findDeviceByTenantIdAndIdAsync(tenantId, deviceId.getId());
        }
    }

    @Cacheable(cacheNames = DEVICE_CACHE, key = "{#tenantId, #name}")
    @Override
    public Device findDeviceByTenantIdAndName(TenantId tenantId, String name) {
        log.trace("Executing findDeviceByTenantIdAndName [{}][{}]", tenantId, name);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        Optional<Device> deviceOpt = deviceDao.findDeviceByTenantIdAndName(tenantId.getId(), name);
        return deviceOpt.orElse(null);
    }

    @Caching(evict= {
            @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#device.tenantId, #device.name}"),
            @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#device.tenantId, #device.id}")
    })
    @Transactional
    @Override
    public Device saveDeviceWithAccessToken(Device device, String accessToken) throws ThingsboardException {
        //同租户下，设备名称不能重复
        /* 2022-8-3注释代码。需求变更为设备名称可重复
        List<Device> deviceListByCdn =deviceDao.queryAllByTenantIdAndName(device.getTenantId(),device.getName());
        if(!CollectionUtils.isEmpty(deviceListByCdn)){
            if(device.getId() == null){
                throw new ThingsboardException("设备名称重复！", ThingsboardErrorCode.FAIL_VIOLATION);
            }else {
                if(device.getName() != null && !device.getId().getId().toString().equals(deviceListByCdn.get(0).getId().getId().toString())){
                    throw new ThingsboardException("设备名称重复！", ThingsboardErrorCode.FAIL_VIOLATION);
                }
            }
        }*/
        return doSaveDevice(device, accessToken, true);
    }

    @Caching(evict= {
            @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#device.tenantId, #device.name}"),
            @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#device.tenantId, #device.id}")
    })
    @Override
    public Device saveDevice(Device device, boolean doValidate) {
        return doSaveDevice(device, null, doValidate);
    }

    @Caching(evict= {
            @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#device.tenantId, #device.name}"),
            @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#device.tenantId, #device.id}")
    })
    @Override
    public Device saveDevice(Device device) {
        return doSaveDevice(device, null, true);
    }

    @Caching(evict= {
            @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#device.tenantId, #device.name}"),
            @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#device.tenantId, #device.id}")
    })
    @Transactional
    @Override
    public Device saveDeviceWithCredentials(Device device, DeviceCredentials deviceCredentials) {
        if (device.getId() == null) {
            Device deviceWithName = this.findDeviceByTenantIdAndName(device.getTenantId(), device.getName());
            device = deviceWithName == null ? device : deviceWithName.updateDevice(device);
        }
        Device savedDevice = this.saveDeviceWithoutCredentials(device, true);
        deviceCredentials.setDeviceId(savedDevice.getId());
        if (device.getId() == null) {
            deviceCredentialsService.createDeviceCredentials(savedDevice.getTenantId(), deviceCredentials);
        } else {
            DeviceCredentials foundDeviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(device.getTenantId(), savedDevice.getId());
            if (foundDeviceCredentials == null) {
                deviceCredentialsService.createDeviceCredentials(savedDevice.getTenantId(), deviceCredentials);
            } else {
                deviceCredentialsService.updateDeviceCredentials(device.getTenantId(), deviceCredentials);
            }
        }
        return savedDevice;
    }

    private Device doSaveDevice(Device device, String accessToken, boolean doValidate) {
        //如果设备字典为空，则要添加默认字典
        if(device.getDictDeviceId() == null || StringUtils.isEmpty(device.getDictDeviceId().toString())){
            device.setDictDeviceId(dictDeviceService.getDefaultDictDeviceId(device.getTenantId()));
        }
        //保存设备
        Device savedDevice = this.saveDeviceWithoutCredentials(device, doValidate);
        if (device.getId() == null) {
            DeviceCredentials deviceCredentials = new DeviceCredentials();
            deviceCredentials.setDeviceId(new DeviceId(savedDevice.getUuidId()));
            deviceCredentials.setCredentialsType(DeviceCredentialsType.ACCESS_TOKEN);
            deviceCredentials.setCredentialsId(!StringUtils.isEmpty(accessToken) ? accessToken : RandomStringUtils.randomAlphanumeric(20));
            deviceCredentialsService.createDeviceCredentials(device.getTenantId(), deviceCredentials);
        }
        return savedDevice;
    }

    private Device saveDeviceWithoutCredentials(Device device, boolean doValidate) {
        log.trace("Executing saveDevice [{}]", device);
        if (doValidate) {
            deviceValidator.validate(device, Device::getTenantId);
        }
        try {
            DeviceProfile deviceProfile;
            if (device.getDeviceProfileId() == null) {
                if (!StringUtils.isEmpty(device.getType())) {
                    deviceProfile = this.deviceProfileService.findOrCreateDeviceProfile(device.getTenantId(), device.getType());
                } else {
                    deviceProfile = this.deviceProfileService.findDefaultDeviceProfile(device.getTenantId());
                }
                device.setDeviceProfileId(new DeviceProfileId(deviceProfile.getId().getId()));
            } else {
                deviceProfile = this.deviceProfileService.findDeviceProfileById(device.getTenantId(), device.getDeviceProfileId());
                if (deviceProfile == null) {
                    throw new DataValidationException("Device is referencing non existing device profile!");
                }
            }
            device.setType(deviceProfile.getName());
            device.setDeviceData(syncDeviceData(deviceProfile, device.getDeviceData()));
            this.swapNameAndRename(device);
            return deviceDao.save(device.getTenantId(), device);
        } catch (Exception t) {
            ConstraintViolationException e = extractConstraintViolationException(t).orElse(null);
            if (e != null && e.getConstraintName() != null && e.getConstraintName().equalsIgnoreCase("device_name_unq_key")) {
                // remove device from cache in case null value cached in the distributed redis.
                removeDeviceFromCacheByName(device.getTenantId(), device.getName());
                removeDeviceFromCacheById(device.getTenantId(), device.getId());
                throw new DataValidationException("Device with such name already exists!");
            } else {
                throw t;
            }
        }
    }

    /**
     * 2022-8-3变更
     * 转换设备名称
     * name 设备唯一码
     * rename 设备名称
     * @param device
     */
    public void swapNameAndRename(Device device){
        //新增需要初始值
        if(device.getId() == null || device.getId().getId() == null){
            //设备名称要存到rename
            device.setRename(device.getName());
            //name作为唯一编码使用
            if(device.getName().isBlank()){
                device.setName(Uuids.timeBased().toString());
            }
        }else {
            //查询编码
            DeviceInfo deviceInfoById = deviceDao.findDeviceInfoById(device.getTenantId(), device.getId().getId());
            //设备名称要存到rename
            device.setRename(device.getName());
            //name作为唯一编码使用
            device.setName(deviceInfoById.getName());
        }
    }

    private DeviceData syncDeviceData(DeviceProfile deviceProfile, DeviceData deviceData) {
        if (deviceData == null) {
            deviceData = new DeviceData();
        }
        if (deviceData.getConfiguration() == null || !deviceProfile.getType().equals(deviceData.getConfiguration().getType())) {
            switch (deviceProfile.getType()) {
                case DEFAULT:
                    deviceData.setConfiguration(new DefaultDeviceConfiguration());
                    break;
            }
        }
        if (deviceData.getTransportConfiguration() == null || !deviceProfile.getTransportType().equals(deviceData.getTransportConfiguration().getType())) {
            switch (deviceProfile.getTransportType()) {
                case DEFAULT:
                    deviceData.setTransportConfiguration(new DefaultDeviceTransportConfiguration());
                    break;
                case MQTT:
                    deviceData.setTransportConfiguration(new MqttDeviceTransportConfiguration());
                    break;
                case COAP:
                    deviceData.setTransportConfiguration(new CoapDeviceTransportConfiguration());
                    break;
                case LWM2M:
                    deviceData.setTransportConfiguration(new Lwm2mDeviceTransportConfiguration());
                    break;
                case SNMP:
                    deviceData.setTransportConfiguration(new SnmpDeviceTransportConfiguration());
                    break;
            }
        }
        return deviceData;
    }

    @Override
    public Device assignDeviceToCustomer(TenantId tenantId, DeviceId deviceId, CustomerId customerId) {
        Device device = findDeviceById(tenantId, deviceId);
        device.setCustomerId(customerId);
        Device savedDevice = saveDevice(device);
        removeDeviceFromCacheByName(tenantId, device.getName());
        removeDeviceFromCacheById(tenantId, device.getId());
        return savedDevice;
    }

    @Override
    public Device unassignDeviceFromCustomer(TenantId tenantId, DeviceId deviceId) {
        Device device = findDeviceById(tenantId, deviceId);
        device.setCustomerId(null);
        Device savedDevice = saveDevice(device);
        removeDeviceFromCacheByName(tenantId, device.getName());
        removeDeviceFromCacheById(tenantId, device.getId());
        return savedDevice;
    }

    @Override
    public void deleteDevice(TenantId tenantId, DeviceId deviceId) {
        log.trace("Executing deleteDevice [{}]", deviceId);
        validateId(deviceId, INCORRECT_DEVICE_ID + deviceId);

        Device device = deviceDao.findById(tenantId, deviceId.getId());
        try {
            List<EntityView> entityViews = entityViewService.findEntityViewsByTenantIdAndEntityIdAsync(device.getTenantId(), deviceId).get();
            if (entityViews != null && !entityViews.isEmpty()) {
                throw new DataValidationException("Can't delete device that has entity views!");
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("Exception while finding entity views for deviceId [{}]", deviceId, e);
            throw new RuntimeException("Exception while finding entity views for deviceId [" + deviceId + "]", e);
        }

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, deviceId);
        if (deviceCredentials != null) {
            deviceCredentialsService.deleteDeviceCredentials(tenantId, deviceCredentials);
        }
        deleteEntityRelations(tenantId, deviceId);

        removeDeviceFromCacheByName(tenantId, device.getName());
        removeDeviceFromCacheById(tenantId, device.getId());

        deviceDao.removeById(tenantId, deviceId.getId());
    }

    private void removeDeviceFromCacheByName(TenantId tenantId, String name) {
        Cache cache = cacheManager.getCache(DEVICE_CACHE);
        cache.evict(Arrays.asList(tenantId, name));
    }

    private void removeDeviceFromCacheById(TenantId tenantId, DeviceId deviceId) {
        if (deviceId == null) {
            return;
        }
        Cache cache = cacheManager.getCache(DEVICE_CACHE);
        cache.evict(Arrays.asList(tenantId, deviceId));
    }

    @Override
    public PageData<Device> findDevicesByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findDevicesByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return deviceDao.findDevicesByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findDeviceInfosByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return deviceDao.findDeviceInfosByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
        log.trace("Executing findDevicesByTenantIdAndType, tenantId [{}], type [{}], pageLink [{}]", tenantId, type, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return deviceDao.findDevicesByTenantIdAndType(tenantId.getId(), type, pageLink);
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndTypeAndEmptyOtaPackage(TenantId tenantId,
                                                                           DeviceProfileId deviceProfileId,
                                                                           OtaPackageType type,
                                                                           PageLink pageLink) {
        log.trace("Executing findDevicesByTenantIdAndTypeAndEmptyOtaPackage, tenantId [{}], deviceProfileId [{}], type [{}], pageLink [{}]",
                tenantId, deviceProfileId, type, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(tenantId, INCORRECT_DEVICE_PROFILE_ID + deviceProfileId);
        validatePageLink(pageLink);
        return deviceDao.findDevicesByTenantIdAndTypeAndEmptyOtaPackage(tenantId.getId(), deviceProfileId.getId(), type, pageLink);
    }

    @Override
    public Long countDevicesByTenantIdAndDeviceProfileIdAndEmptyOtaPackage(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType type) {
        log.trace("Executing countDevicesByTenantIdAndDeviceProfileIdAndEmptyOtaPackage, tenantId [{}], deviceProfileId [{}], type [{}]", tenantId, deviceProfileId, type);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(tenantId, INCORRECT_DEVICE_PROFILE_ID + deviceProfileId);
        return deviceDao.countDevicesByTenantIdAndDeviceProfileIdAndEmptyOtaPackage(tenantId.getId(), deviceProfileId.getId(), type);
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
        log.trace("Executing findDeviceInfosByTenantIdAndType, tenantId [{}], type [{}], pageLink [{}]", tenantId, type, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return deviceDao.findDeviceInfosByTenantIdAndType(tenantId.getId(), type, pageLink);
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId, PageLink pageLink) {
        log.trace("Executing findDeviceInfosByTenantIdAndDeviceProfileId, tenantId [{}], deviceProfileId [{}], pageLink [{}]", tenantId, deviceProfileId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(deviceProfileId, INCORRECT_DEVICE_PROFILE_ID + deviceProfileId);
        validatePageLink(pageLink);
        return deviceDao.findDeviceInfosByTenantIdAndDeviceProfileId(tenantId.getId(), deviceProfileId.getId(), pageLink);
    }

    @Override
    public ListenableFuture<List<Device>> findDevicesByTenantIdAndIdsAsync(TenantId tenantId, List<DeviceId> deviceIds) {
        log.trace("Executing findDevicesByTenantIdAndIdsAsync, tenantId [{}], deviceIds [{}]", tenantId, deviceIds);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateIds(deviceIds, "Incorrect deviceIds " + deviceIds);
        return deviceDao.findDevicesByTenantIdAndIdsAsync(tenantId.getId(), toUUIDs(deviceIds));
    }


    @Override
    public void deleteDevicesByTenantId(TenantId tenantId) {
        log.trace("Executing deleteDevicesByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        tenantDevicesRemover.removeEntities(tenantId, tenantId);
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink) {
        log.trace("Executing findDevicesByTenantIdAndCustomerId, tenantId [{}], customerId [{}], pageLink [{}]", tenantId, customerId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validatePageLink(pageLink);
        return deviceDao.findDevicesByTenantIdAndCustomerId(tenantId.getId(), customerId.getId(), pageLink);
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink) {
        log.trace("Executing findDeviceInfosByTenantIdAndCustomerId, tenantId [{}], customerId [{}], pageLink [{}]", tenantId, customerId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validatePageLink(pageLink);
        return deviceDao.findDeviceInfosByTenantIdAndCustomerId(tenantId.getId(), customerId.getId(), pageLink);
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink) {
        log.trace("Executing findDevicesByTenantIdAndCustomerIdAndType, tenantId [{}], customerId [{}], type [{}], pageLink [{}]", tenantId, customerId, type, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return deviceDao.findDevicesByTenantIdAndCustomerIdAndType(tenantId.getId(), customerId.getId(), type, pageLink);
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink) {
        log.trace("Executing findDeviceInfosByTenantIdAndCustomerIdAndType, tenantId [{}], customerId [{}], type [{}], pageLink [{}]", tenantId, customerId, type, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return deviceDao.findDeviceInfosByTenantIdAndCustomerIdAndType(tenantId.getId(), customerId.getId(), type, pageLink);
    }

    @Override
    public PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerIdAndDeviceProfileId(TenantId tenantId, CustomerId customerId, DeviceProfileId deviceProfileId, PageLink pageLink) {
        log.trace("Executing findDeviceInfosByTenantIdAndCustomerIdAndDeviceProfileId, tenantId [{}], customerId [{}], deviceProfileId [{}], pageLink [{}]", tenantId, customerId, deviceProfileId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validateId(deviceProfileId, INCORRECT_DEVICE_PROFILE_ID + deviceProfileId);
        validatePageLink(pageLink);
        return deviceDao.findDeviceInfosByTenantIdAndCustomerIdAndDeviceProfileId(tenantId.getId(), customerId.getId(), deviceProfileId.getId(), pageLink);
    }

    @Override
    public ListenableFuture<List<Device>> findDevicesByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<DeviceId> deviceIds) {
        log.trace("Executing findDevicesByTenantIdCustomerIdAndIdsAsync, tenantId [{}], customerId [{}], deviceIds [{}]", tenantId, customerId, deviceIds);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validateIds(deviceIds, "Incorrect deviceIds " + deviceIds);
        return deviceDao.findDevicesByTenantIdCustomerIdAndIdsAsync(tenantId.getId(),
                customerId.getId(), toUUIDs(deviceIds));
    }

    @Override
    public void unassignCustomerDevices(TenantId tenantId, CustomerId customerId) {
        log.trace("Executing unassignCustomerDevices, tenantId [{}], customerId [{}]", tenantId, customerId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        customerDeviceUnasigner.removeEntities(tenantId, customerId);
    }

    @Override
    public ListenableFuture<List<Device>> findDevicesByQuery(TenantId tenantId, DeviceSearchQuery query) {
        ListenableFuture<List<EntityRelation>> relations = relationService.findByQuery(tenantId, query.toEntitySearchQuery());
        ListenableFuture<List<Device>> devices = Futures.transformAsync(relations, r -> {
            EntitySearchDirection direction = query.toEntitySearchQuery().getParameters().getDirection();
            List<ListenableFuture<Device>> futures = new ArrayList<>();
            for (EntityRelation relation : r) {
                EntityId entityId = direction == EntitySearchDirection.FROM ? relation.getTo() : relation.getFrom();
                if (entityId.getEntityType() == EntityType.DEVICE) {
                    futures.add(findDeviceByIdAsync(tenantId, new DeviceId(entityId.getId())));
                }
            }
            return Futures.successfulAsList(futures);
        }, MoreExecutors.directExecutor());

        devices = Futures.transform(devices, new Function<List<Device>, List<Device>>() {
            @Nullable
            @Override
            public List<Device> apply(@Nullable List<Device> deviceList) {
                return deviceList == null ? Collections.emptyList() : deviceList.stream().filter(device -> query.getDeviceTypes().contains(device.getType())).collect(Collectors.toList());
            }
        }, MoreExecutors.directExecutor());

        return devices;
    }

    @Override
    public ListenableFuture<List<EntitySubtype>> findDeviceTypesByTenantId(TenantId tenantId) {
        log.trace("Executing findDeviceTypesByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        ListenableFuture<List<EntitySubtype>> tenantDeviceTypes = deviceDao.findTenantDeviceTypesAsync(tenantId.getId());
        return Futures.transform(tenantDeviceTypes,
                deviceTypes -> {
                    deviceTypes.sort(Comparator.comparing(EntitySubtype::getType));
                    return deviceTypes;
                }, MoreExecutors.directExecutor());
    }

    @Transactional
    @Override
    public Device assignDeviceToTenant(TenantId tenantId, Device device) {
        log.trace("Executing assignDeviceToTenant [{}][{}]", tenantId, device);

        try {
            List<EntityView> entityViews = entityViewService.findEntityViewsByTenantIdAndEntityIdAsync(device.getTenantId(), device.getId()).get();
            if (!CollectionUtils.isEmpty(entityViews)) {
                throw new DataValidationException("Can't assign device that has entity views to another tenant!");
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("Exception while finding entity views for deviceId [{}]", device.getId(), e);
            throw new RuntimeException("Exception while finding entity views for deviceId [" + device.getId() + "]", e);
        }

        eventService.removeEvents(device.getTenantId(), device.getId());

        relationService.removeRelations(device.getTenantId(), device.getId());

        TenantId oldTenantId = device.getTenantId();

        device.setTenantId(tenantId);
        device.setCustomerId(null);
        Device savedDevice = doSaveDevice(device, null, true);

        // explicitly remove device with previous tenant id from cache
        // result device object will have different tenant id and will not remove entity from cache
        removeDeviceFromCacheByName(oldTenantId, device.getName());
        removeDeviceFromCacheById(oldTenantId, device.getId());

        return savedDevice;
    }

    @Override
    @CacheEvict(cacheNames = DEVICE_CACHE, key = "{#profile.tenantId, #provisionRequest.deviceName}")
    @Transactional
    public Device saveDevice(ProvisionRequest provisionRequest, DeviceProfile profile) {
        Device device = new Device();
        device.setName(provisionRequest.getDeviceName());
        device.setType(profile.getName());
        device.setTenantId(profile.getTenantId());
        Device savedDevice = saveDevice(device);
        if (!StringUtils.isEmpty(provisionRequest.getCredentialsData().getToken()) ||
                !StringUtils.isEmpty(provisionRequest.getCredentialsData().getX509CertHash()) ||
                !StringUtils.isEmpty(provisionRequest.getCredentialsData().getUsername()) ||
                !StringUtils.isEmpty(provisionRequest.getCredentialsData().getPassword()) ||
                !StringUtils.isEmpty(provisionRequest.getCredentialsData().getClientId())) {
            DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(savedDevice.getTenantId(), savedDevice.getId());
            if (deviceCredentials == null) {
                deviceCredentials = new DeviceCredentials();
            }
            deviceCredentials.setDeviceId(savedDevice.getId());
            deviceCredentials.setCredentialsType(provisionRequest.getCredentialsType());
            switch (provisionRequest.getCredentialsType()) {
                case ACCESS_TOKEN:
                    deviceCredentials.setCredentialsId(provisionRequest.getCredentialsData().getToken());
                    break;
                case MQTT_BASIC:
                    BasicMqttCredentials mqttCredentials = new BasicMqttCredentials();
                    mqttCredentials.setClientId(provisionRequest.getCredentialsData().getClientId());
                    mqttCredentials.setUserName(provisionRequest.getCredentialsData().getUsername());
                    mqttCredentials.setPassword(provisionRequest.getCredentialsData().getPassword());
                    deviceCredentials.setCredentialsValue(JacksonUtil.toString(mqttCredentials));
                    break;
                case X509_CERTIFICATE:
                    deviceCredentials.setCredentialsValue(provisionRequest.getCredentialsData().getX509CertHash());
                    break;
                case LWM2M_CREDENTIALS:
                    break;
            }
            try {
                deviceCredentialsService.updateDeviceCredentials(savedDevice.getTenantId(), deviceCredentials);
            } catch (Exception e) {
                throw new ProvisionFailedException(ProvisionResponseStatus.FAILURE.name());
            }
        }
        removeDeviceFromCacheById(savedDevice.getTenantId(), savedDevice.getId()); // eviction by name is described as annotation @CacheEvict above
        return savedDevice;
    }

    @Override
    public PageData<UUID> findDevicesIdsByDeviceProfileTransportType(DeviceTransportType transportType, PageLink pageLink) {
        return deviceDao.findDevicesIdsByDeviceProfileTransportType(transportType, pageLink);
    }

    @Override
    public Device assignDeviceToEdge(TenantId tenantId, DeviceId deviceId, EdgeId edgeId) {
        Device device = findDeviceById(tenantId, deviceId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't assign device to non-existent edge!");
        }
        if (!edge.getTenantId().getId().equals(device.getTenantId().getId())) {
            throw new DataValidationException("Can't assign device to edge from different tenant!");
        }
        try {
            createRelation(tenantId, new EntityRelation(edgeId, deviceId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            log.warn("[{}] Failed to create device relation. Edge Id: [{}]", deviceId, edgeId);
            throw new RuntimeException(e);
        }
        return device;
    }

    @Override
    public Device unassignDeviceFromEdge(TenantId tenantId, DeviceId deviceId, EdgeId edgeId) {
        Device device = findDeviceById(tenantId, deviceId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't unassign device from non-existent edge!");
        }

        checkAssignedEntityViewsToEdge(tenantId, deviceId, edgeId);

        try {
            deleteRelation(tenantId, new EntityRelation(edgeId, deviceId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            log.warn("[{}] Failed to delete device relation. Edge Id: [{}]", deviceId, edgeId);
            throw new RuntimeException(e);
        }
        return device;
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink) {
        log.trace("Executing findDevicesByTenantIdAndEdgeId, tenantId [{}], edgeId [{}], pageLink [{}]", tenantId, edgeId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(edgeId, INCORRECT_EDGE_ID + edgeId);
        validatePageLink(pageLink);
        return deviceDao.findDevicesByTenantIdAndEdgeId(tenantId.getId(), edgeId.getId(), pageLink);
    }

    @Override
    public PageData<Device> findDevicesByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink) {
        log.trace("Executing findDevicesByTenantIdAndEdgeIdAndType, tenantId [{}], edgeId [{}], type [{}] pageLink [{}]", tenantId, edgeId, type, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(edgeId, INCORRECT_EDGE_ID + edgeId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return deviceDao.findDevicesByTenantIdAndEdgeIdAndType(tenantId.getId(), edgeId.getId(), type, pageLink);
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return deviceDao.countByTenantId(tenantId);
    }

    private DataValidator<Device> deviceValidator =
            new DataValidator<Device>() {

                @Override
                protected void validateCreate(TenantId tenantId, Device device) {
                    DefaultTenantProfileConfiguration profileConfiguration =
                            (DefaultTenantProfileConfiguration) tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
                    long maxDevices = profileConfiguration.getMaxDevices();
                    validateNumberOfEntitiesPerTenant(tenantId, deviceDao, maxDevices, EntityType.DEVICE);
                }

                @Override
                protected void validateUpdate(TenantId tenantId, Device device) {
                    Device old = deviceDao.findById(device.getTenantId(), device.getId().getId());
                    if (old == null) {
                        throw new DataValidationException("Can't update non existing device!");
                    }
                    if (!old.getName().equals(device.getName())) {
                        removeDeviceFromCacheByName(tenantId, old.getName());
                        removeDeviceFromCacheById(tenantId, device.getId());
                    }
                }

                @Override
                protected void validateDataImpl(TenantId tenantId, Device device) {
                    if (StringUtils.isEmpty(device.getName()) || device.getName().trim().length() == 0) {
                        throw new DataValidationException("Device name should be specified!");
                    }
                    if (device.getTenantId() == null) {
                        throw new DataValidationException("Device should be assigned to tenant!");
                    } else {
                        Tenant tenant = tenantDao.findById(device.getTenantId(), device.getTenantId().getId());
                        if (tenant == null) {
                            throw new DataValidationException("Device is referencing to non-existent tenant!");
                        }
                    }
                    if (device.getCustomerId() == null) {
                        device.setCustomerId(new CustomerId(NULL_UUID));
                    } else if (!device.getCustomerId().getId().equals(NULL_UUID)) {
                        Customer customer = customerDao.findById(device.getTenantId(), device.getCustomerId().getId());
                        if (customer == null) {
                            throw new DataValidationException("Can't assign device to non-existent customer!");
                        }
                        if (!customer.getTenantId().getId().equals(device.getTenantId().getId())) {
                            throw new DataValidationException("Can't assign device to customer from different tenant!");
                        }
                    }
                    Optional.ofNullable(device.getDeviceData())
                            .flatMap(deviceData -> Optional.ofNullable(deviceData.getTransportConfiguration()))
                            .ifPresent(DeviceTransportConfiguration::validate);

                    if (device.getFirmwareId() != null) {
                        OtaPackage firmware = otaPackageService.findOtaPackageById(tenantId, device.getFirmwareId());
                        if (firmware == null) {
                            throw new DataValidationException("Can't assign non-existent firmware!");
                        }
                        if (!firmware.getType().equals(OtaPackageType.FIRMWARE)) {
                            throw new DataValidationException("Can't assign firmware with type: " + firmware.getType());
                        }
                        if (firmware.getData() == null && !firmware.hasUrl()) {
                            throw new DataValidationException("Can't assign firmware with empty data!");
                        }
                        if (!firmware.getDeviceProfileId().equals(device.getDeviceProfileId())) {
                            throw new DataValidationException("Can't assign firmware with different deviceProfile!");
                        }
                    }

                    if (device.getSoftwareId() != null) {
                        OtaPackage software = otaPackageService.findOtaPackageById(tenantId, device.getSoftwareId());
                        if (software == null) {
                            throw new DataValidationException("Can't assign non-existent software!");
                        }
                        if (!software.getType().equals(OtaPackageType.SOFTWARE)) {
                            throw new DataValidationException("Can't assign software with type: " + software.getType());
                        }
                        if (software.getData() == null && !software.hasUrl()) {
                            throw new DataValidationException("Can't assign software with empty data!");
                        }
                        if (!software.getDeviceProfileId().equals(device.getDeviceProfileId())) {
                            throw new DataValidationException("Can't assign firmware with different deviceProfile!");
                        }
                    }
                }
            };

    private PaginatedRemover<TenantId, Device> tenantDevicesRemover =
            new PaginatedRemover<TenantId, Device>() {

                @Override
                protected PageData<Device> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
                    return deviceDao.findDevicesByTenantId(id.getId(), pageLink);
                }

                @Override
                protected void removeEntity(TenantId tenantId, Device entity) {
                    deleteDevice(tenantId, new DeviceId(entity.getUuidId()));
                }
            };

    private PaginatedRemover<CustomerId, Device> customerDeviceUnasigner = new PaginatedRemover<CustomerId, Device>() {

        @Override
        protected PageData<Device> findEntities(TenantId tenantId, CustomerId id, PageLink pageLink) {
            return deviceDao.findDevicesByTenantIdAndCustomerId(tenantId.getId(), id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, Device entity) {
            unassignDeviceFromCustomer(tenantId, new DeviceId(entity.getUuidId()));
        }
    };



    /**
     * 保存/修改设备
     * @param device
     * @return
     */
    @Override
    public Device saveOrUpdDevice(Device device) throws ThingsboardException {
        //保存或修改设备信息
        Device saveDevice = deviceDao.saveOrUpdDevice(device);
        if(device.getId() == null || device.getId().getId() == null || StringUtils.isEmpty(device.getId().getId().toString())){
            if(device.getDictDeviceId() != null && !StringUtils.isEmpty(device.getDictDeviceId().toString())){
                //添加设备构成
                this.saveDeviceComponentList(device,saveDevice.getId().getId(),this.SAVE_TYPE_ADD);
            }
        }else {
            if(device.getDictDeviceId() != null){
                //查询修改前设备信息
                DeviceInfo BeforeUpdate = deviceDao.findDeviceInfoById(null, device.getId().getId());
                if(BeforeUpdate != null && BeforeUpdate.getDictDeviceId() != null){
                    if(!BeforeUpdate.getDictDeviceId().toString().equals(device.getDictDeviceId().toString())){
                        //删除设备构成
                        deviceComponentDao.delDeviceComponentByDeviceId(device.getDictDeviceId());
                        //添加构成
                        this.saveDeviceComponentList(device,saveDevice.getId().getId(),this.SAVE_TYPE_ADD_UPDATE);
                    }
                }else {
                    //添加构成
                    this.saveDeviceComponentList(device,saveDevice.getId().getId(),this.SAVE_TYPE_ADD);
                }
            }
        }
        return saveDevice;
    }

    /**
     * 添加/更新设备构成
     * @param device 请求数据
     * @param deviceId 设备id
     * @param saveType
     */
    private void saveDeviceComponentList(Device device,UUID deviceId,String saveType)  throws ThingsboardException{
        List<DeviceComponent> deviceComponentList = new ArrayList<>();
        //查询设备的构成
        List<DictDeviceComponentEntity> allByDictDeviceId = componentRepository.findAllByDictDeviceId(device.getDictDeviceId());
        if(!CollectionUtils.isEmpty(allByDictDeviceId)){
            allByDictDeviceId.forEach(i->{
                DeviceComponent deviceComponent = new DeviceComponent();
                BeanUtils.copyProperties(i,deviceComponent);
                deviceComponent.setDeviceId(deviceId);
                if(saveType.equals(this.SAVE_TYPE_ADD)){
                    deviceComponent.setCreatedUser(device.getCreatedUser());
                }else {
                    deviceComponent.setUpdatedUser(device.getUpdatedUser());
                }
                deviceComponentList.add(deviceComponent);
            });
        }
        //保存设备构成
        deviceComponentDao.saveDeviceComponentList(deviceComponentList);
    }
    @Override
    public void saveOrUpdDeviceComponentList(Device device,UUID deviceId,String saveType) throws ThingsboardException{
        this.saveDeviceComponentList(device,deviceId,saveType);
    }

    /**
     * 分配产线设备
     * @param device
     * @throws ThingsboardException
     */
    @Override
    public void distributionDevice(Device device) throws ThingsboardException {
        //移除产线原先的设备（清空该设备的产线、车间、工厂数据）
        deviceDao.removeProductionLine(device.getDeviceIdList(), device.getUpdatedUser());
        //添加设备给指定产线
        deviceDao.addProductionLine(device);
        //建立实体关系
        if(!CollectionUtils.isEmpty(device.getDeviceIdList())){
            device.getDeviceIdList().forEach(i->{
                Device dev = new Device();
                dev.setId(new DeviceId(i));
                dev.setProductionLineId(device.getProductionLineId());
                this.createRelationDeviceFromProductionLine(dev);
            });
        }
    }

    @Override
    public void createRelationDeviceFromProductionLine(Device device){
        if(device != null){
            if(device.getProductionLineId() != null){
                //建立实体关系
                //设备到产线
                EntityRelation relation = new EntityRelation(
                        new ProductionLineId(device.getProductionLineId()),device.getId(), EntityRelation.CONTAINS_TYPE
                );
                relationService.saveRelation(device.getTenantId(), relation);
            }
        }
    }

    /**
     * 移除产线设备
     * @param device
     * @throws ThingsboardException
     */
    @Override
    public void removeDevice(Device device) throws ThingsboardException{
        //移除产线原先的设备（清空该设备的产线、车间、工厂数据）
        deviceDao.removeProductionLine(device.getDeviceIdList(),device.getUpdatedUser());
    }

    /**
     * 查询工厂下具有最新版本的一个网关设备
     * @param factoryIds
     * @return
     */
    @Override
    public List<Device> findGatewayNewVersionByFactory(List<UUID> factoryIds) throws ThingsboardException{
        return deviceDao.findGatewayNewVersionByFactory(factoryIds);
    }

    /**
     * 查询工厂下所有网关设备
     * @param factoryIds
     * @return
     */
    @Override
    public List<Device> findGatewayListVersionByFactory(List<UUID> factoryIds) throws ThingsboardException{
        return deviceDao.findGatewayListVersionByFactory(factoryIds);
    }

    /**
     *平台设备列表查询
     * @param device
     * @param pageLink
     * @return
     */
    @Override
    public PageData<Device> getTenantDeviceInfoList(Device device,PageLink pageLink){
        PageData<Device> pageData = deviceDao.getTenantDeviceInfoList(device, pageLink);
        /*if(pageData.getData() != null){
            this.getDeviceProfileName(pageData.getData());
        }*/
        return pageData;
    }

    /**
     * 批量查询设备配置名称
     * @param dataList
     */
    private void getDeviceProfileName(List<Device> dataList){
        if(!CollectionUtils.isEmpty(dataList)){
            List<UUID> profileIds = dataList.stream().map(m -> m.getDeviceProfileId().getId()).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(profileIds)){
                List<DeviceProfile> deviceProfileByIds = deviceProfileService.findDeviceProfileByIds(profileIds);
                if(!CollectionUtils.isEmpty(deviceProfileByIds)){
                    //循环赋值
                    dataList.forEach(i->{
                        deviceProfileByIds.forEach(j->{
                            if(i.getDeviceProfileId().getId().toString().equals(j.getId().getId().toString())){
                                i.setDeviceProfileName(j.getName());
                            }
                        });
                    });


                }

            }

        }
    }

    /**
     * 获取设备详情
     */
    @Override
    public Device getDeviceInfo(UUID id){
        //查询设备信息
        Device device = deviceDao.getDeviceInfo(id);
        device.setDeviceComponentList(deviceComponentDao.getDeviceComponentByDeviceId(id));
        return device;

    }
    @Override
    public PageData<DeviceDataVo> queryAllByNameLike(UUID factoryId, String name, PageLink pageLink){
        return deviceDao.queryAllByNameLike(factoryId,name,pageLink);
    }

    /**
     * 查询租户下未分配设备
     * @param tenantId
     * @return
     */
    @Override
    public List<Device> getNotDistributionDevice(TenantId tenantId){
        return deviceDao.getNotDistributionDevice(tenantId);
    }

    /**
     * 多条件查询设备
     * @param device
     * @return
     */
    @Override
    public List<Device> findDeviceListByCdn(Device device,String orderValue,String descOrAsc){
        return deviceDao.findDeviceListByCdn(device,orderValue,descOrAsc);
    }


    @Override
    public PageData<CapacityDeviceVo> queryPage(CapacityDeviceVo vo, PageLink pageLink) throws JsonProcessingException {
        log.info("分页查询产能运算配置的接口如参:{}",vo);
        PageData<Device> pageData =  deviceDao.queryPage(vo,pageLink);
       List<Device> devices =  pageData.getData();

       UUID  tenantId = vo.getTenantId();

      List<CapacityDeviceVo> voList =   devices.stream().map(d1->{
            CapacityDeviceVo  vo1 = new CapacityDeviceVo();
            d1.getPicture();
          vo1.setDeviceName(d1.getName());
          vo1.setDeviceId(d1.getUuidId());
          vo1.setFlg(d1.getDeviceFlg());
          vo1.setStatus(getStatusByDevice(d1));
          DictDeviceEntity  dictDeviceEntity =  getDictName(tenantId,d1.getDictDeviceId());
          vo1.setDictName(dictDeviceEntity.getName());
          vo1.setDeviceNo(dictDeviceEntity.getModel());
          vo1.setDeviceFileName(getDictFileName(tenantId,d1.getDeviceProfileId()));
          vo1.setCreatedTime(d1.getCreatedTime());
            return  vo1;
        }).collect(Collectors.toList());


         return new PageData<>(voList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());

    }


    @Override
    public PageData<AppCapacityDeviceVo> appQueryPage(CapacityDeviceVo vo, PageLink pageLink) throws JsonProcessingException {
        log.info("分页查询产能运算配置的接口如参:{}",vo);
        PageData<Device> pageData =  deviceDao.queryPage(vo,pageLink);
        List<Device> devices =  pageData.getData();

        List<UUID> dictDeviceIds = pageData.getData().stream().map(Device::getDictDeviceId).filter(Objects::nonNull).collect(Collectors.toList());
        HashMap<String, DictDevice> finalMap = new HashMap<>();

        if (!dictDeviceIds.isEmpty()){
            finalMap = DaoUtil.convertDataList(this.dictDeviceRepository.findAllByTenantIdAndIdIn(vo.getTenantId(), dictDeviceIds)).stream()
                    .collect(Collectors.toMap(DictDevice::getId, java.util.function.Function.identity(), (a, b)->a, HashMap::new));
        }
        HashMap<String, DictDevice> finalMap1 = finalMap;
        UUID  tenantId = vo.getTenantId();
        List<AppCapacityDeviceVo> voList =   devices.stream().map(d1->{
            AppCapacityDeviceVo  vo1 = new AppCapacityDeviceVo();
            vo1.setPicture(Optional.ofNullable(d1.getPicture()).orElse(Optional.ofNullable(d1.getDictDeviceId()).map(UUID::toString).map(finalMap1::get).map(DictDevice::getPicture).orElse(null)));
            vo1.setDeviceName(d1.getName());
            vo1.setDeviceId(d1.getUuidId());
            vo1.setFlg(d1.getDeviceFlg());
            vo1.setStatus(getStatusByDevice(d1));


            vo1.setCreatedTime(d1.getCreatedTime());
             vo1.setFactoryName(getNameById(tenantId,d1.getFactoryId(),"1"));
             vo1.setWorkshopName(getNameById(tenantId,d1.getWorkshopId(),"2"));
             vo1.setProductionLineName(getNameById(tenantId,d1.getProductionLineId(),"3"));
            return  vo1;
        }).collect(Collectors.toList());


        return new PageData<>(voList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    @Override
    public void updateFlgById(Boolean deviceFlg, UUID id) {
        deviceDao.updateFlgById(deviceFlg,id);
    }

    /**
     * 查询设备字典下发的设备列表
     * @param device
     * @return
     */
    @Override
    public List<Device> findDeviceIssueListByCdn(Device device){
        List<Device> result = new ArrayList<>();
        //查询所有设备
        List<Device> deviceListByCdn = deviceDao.findDeviceListByCdn(device,null,null);
        if(!CollectionUtils.isEmpty(deviceListByCdn)){
            //只要网关创建的设备
            result = this.filterDeviceFromGateway(deviceListByCdn);

            //过滤查询条件
            Iterator<Device> iterator = result.iterator();
            while (iterator.hasNext()){
                Device filter = iterator.next();
                if(!StringUtils.isEmpty(device.getFactoryName()) &&
                        (StringUtils.isEmpty(filter.getFactoryName()) || filter.getFactoryName().indexOf(device.getFactoryName()) == -1)){
                    iterator.remove();
                    continue;
                }
                if(!StringUtils.isEmpty(device.getWorkshopName()) &&
                        (StringUtils.isEmpty(filter.getWorkshopName()) || filter.getWorkshopName().indexOf(device.getWorkshopName())==-1)){
                    iterator.remove();
                    continue;
                }
                if(!StringUtils.isEmpty(device.getProductionLineName()) &&
                        (StringUtils.isEmpty(filter.getProductionLineName()) || filter.getProductionLineName().indexOf(device.getProductionLineName())==-1)){
                    iterator.remove();
                    continue;
                }
                if(!StringUtils.isEmpty(device.getGatewayName()) &&
                        (StringUtils.isEmpty(filter.getGatewayName()) || filter.getGatewayName().indexOf(device.getGatewayName())==-1) ){
                    iterator.remove();
                    continue;
                }
            }
        }
        return result;
    }

    /**
     * 只要网关创建的设备
     * @param deviceList
     */
    public List<Device> filterDeviceFromGateway(List<Device> deviceList){
        //返回值
        List<Device> result = new ArrayList<>();
        //过滤出所有网关
        List<Device> gateways = new ArrayList<>();

        if(!CollectionUtils.isEmpty(deviceList)){
            //查询该租户下所有的网关设备
            Device qryGateway = new Device();
            qryGateway.setTenantId(deviceList.get(0).getTenantId());
            qryGateway.setOnlyGatewayFlag(true);
            List<Device> gatewayListByTenant = deviceDao.findDeviceListByCdn(qryGateway,null,null);
            if(!CollectionUtils.isEmpty(gatewayListByTenant)){
                gateways = gatewayListByTenant.stream().distinct().collect(Collectors.toList());
            }

            //1.过滤出所有网关
            Iterator<Device> it = deviceList.iterator();
            while (it.hasNext()){
                Device filter = it.next();
                if (!StringUtils.isEmpty(filter.getAdditionalInfo())
                        && filter.getAdditionalInfo().get(GATEWAY) != null
                        && filter.getAdditionalInfo().get(GATEWAY).booleanValue() ) {
                    gateways.add(filter);
                    it.remove();
                    continue;
                }
            }
            //2.查找出网关下的设备
            if(!CollectionUtils.isEmpty(gateways)){
                //查询源于这些网关的设备
                List<UUID> fromIds = gateways.stream().map(s -> s.getId().getId()).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(fromIds)){
                    fromIds = fromIds.stream().distinct().collect(Collectors.toList());
                }
                List<EntityRelation> byFromIds = relationService.findByFromIds(fromIds, RelationTypeGroup.COMMON);
                if(!CollectionUtils.isEmpty(byFromIds)){
                    Iterator<Device> iterator = deviceList.iterator();
                    while (iterator.hasNext()){
                        Device device = iterator.next();
                        for(EntityRelation relation:byFromIds){
                            if(device.getId().getId().toString().equals(relation.getTo().getId().toString())){
                                //赋值所属网关
                                gateways.forEach(gateway->{
                                    if(gateway.getName() != null && gateway.getId().getId().toString().equals(relation.getFrom().getId().toString())){
                                        device.setGatewayName(gateway.getName());
                                        device.setGatewayId(gateway.getId().getId());
                                    }
                                });
                                result.add(device);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 批量查询工厂名称
     * @return
     */
    public List<Device> findFactorysByIds(List<Device> deviceList){
        List<Device> result = deviceList;
        if(!CollectionUtils.isEmpty(result)){
            List<UUID> factoryIds = deviceList.stream().map(s -> s.getFactoryId()).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(factoryIds)){
                factoryIds = factoryIds.stream().distinct().collect(Collectors.toList());
            }
            if(!CollectionUtils.isEmpty(factoryIds)){
                List<Factory> factoryByIdList = factoryDao.getFactoryByIdList(factoryIds);
                for(Device device : result){
                    for (Factory factory : factoryByIdList){
                        if(device.getFactoryId() != null && device.getFactoryId().toString().equals(factory.getId().toString())){
                            device.setFactoryName(factory.getName());
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 批量查询车间名称
     * @return
     */
    public List<Device> findWorkshopsByIds(List<Device> deviceList){
        List<Device> result = deviceList;
        if(!CollectionUtils.isEmpty(deviceList)){
            List<UUID> workshopIds = deviceList.stream().map(s -> s.getWorkshopId()).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(workshopIds)){
                workshopIds = workshopIds.stream().distinct().collect(Collectors.toList());
            }
            if(!CollectionUtils.isEmpty(workshopIds)){
                List<Workshop> workshopByIdList = workshopDao.getWorkshopByIdList(workshopIds);
                for(Device device : result){
                    for (Workshop workshop : workshopByIdList){
                        if(device.getWorkshopId() != null && device.getWorkshopId().toString().equals(workshop.getId().toString())){
                            device.setWorkshopName(workshop.getName());
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 批量查询产线名称
     * @return
     */
    public List<Device> findProductionLinesByIds(List<Device> deviceList){
        List<Device> result = deviceList;
        if(!CollectionUtils.isEmpty(deviceList)){
            List<UUID> productionIds = deviceList.stream().map(s -> s.getProductionLineId()).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(productionIds)){
                List<ProductionLine> productionLineByIdList = productionLineDao.getProductionLineByIdList(productionIds);
                for(Device device : result){
                    for (ProductionLine productionLine : productionLineByIdList){
                        if(device.getProductionLineId() != null && device.getProductionLineId().toString().equals(productionLine.getId().toString())){
                            device.setProductionLineName(productionLine.getName());
                        }
                    }
                }
            }
        }
        return result;
    }
    @Override
    public List<Device> findDevicesByIds(List<UUID> deviceIdList){
        return deviceDao.getDeviceByIdList(deviceIdList);
    }

    @Override
    public long countAllByDictDeviceIdAndTenantId(UUID dictDeviceId, UUID tenantId) {
        return this.deviceDao.countAllByDictDeviceIdAndTenantId(dictDeviceId,tenantId);
    }


    @Override
    public List<Device> findAllBy() {
        return this.deviceDao.findAllBy();
    }

    private final String Yes="已匹配";

    private  String  getStatusByDevice(Device  device)
    {
//         Boolean   flg= false;
        if(device.getFactoryId() != null )
        {
           return  Yes;
        }
        if(device.getWorkshopId() != null)
        {
            return  Yes;
        }
        if(device.getProductionLineId() != null)
        {
            return  Yes;
        }
        return "未匹配";

    }

    /**
     * 查询设备字典
     *
     */
    private DictDeviceEntity getDictName(UUID tenantId, UUID id)
    {
        DictDeviceEntity dictDeviceEntity = new DictDeviceEntity();
        if(id == null)
        {
            return  dictDeviceEntity;
        }
        Optional<DictDeviceEntity>  dictDataEntity=   dictDeviceRepository.findByTenantIdAndId(tenantId,id);
       return (dictDataEntity.isPresent()?dictDataEntity.get():dictDeviceEntity);
    }


    /**
     * 查询设备字典
     *
     */
    private String getDictFileName(UUID tenantId, DeviceProfileId deviceProfileId)
    {
        if(deviceProfileId == null)
        {
            return "";
        }

        if(deviceProfileId.getId() == null)
        {
            return  "";
        }

        DeviceProfileInfo  dictDataEntity=   deviceProfileService.findDeviceProfileInfoById(new TenantId(tenantId), deviceProfileId);
        return (dictDataEntity !=null?dictDataEntity.getName():"");
    }


    private  String getNameById(UUID tenantId, UUID id,String type)
    {
        if(id == null)
        {
            return  "";
        }

       if(type.equals("1")) {
           Optional<FactoryEntity> entity = factoryRepository.findByTenantIdAndId(tenantId, id);
           return entity.isPresent() ? entity.get().getName() : "";
       }
       if(type.equals("2"))
       {
             Workshop workshop =       workshopDao.findById(id);
             return  (workshop !=null? workshop.getName():"");
       }
       if(type.equals("3"))
       {
            ProductionLine productionLine=        productionLineDao.findById(id);
             return  (productionLine !=null? productionLine.getName():"");
       }

       return  "";

    }
}
