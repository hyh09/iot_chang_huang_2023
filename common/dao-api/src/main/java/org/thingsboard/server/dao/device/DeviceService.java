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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.device.DeviceSearchQuery;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.common.data.vo.device.AppCapacityDeviceVo;
import org.thingsboard.server.common.data.vo.device.CapacityDeviceVo;
import org.thingsboard.server.common.data.vo.device.DeviceDataVo;
import org.thingsboard.server.dao.device.provision.ProvisionRequest;

import java.util.List;
import java.util.UUID;

public interface DeviceService {

    DeviceInfo findDeviceInfoById(TenantId tenantId, DeviceId deviceId);

    Device findDeviceById(TenantId tenantId, DeviceId deviceId);

    List<Device> getYunDeviceList(Device device);

    ListenableFuture<Device> findDeviceByIdAsync(TenantId tenantId, DeviceId deviceId);

    Device findDeviceByTenantIdAndName(TenantId tenantId, String name);

    Device saveDevice(Device device, boolean doValidate);

    Device saveDevice(Device device);

    Device saveDeviceWithAccessToken(Device device, String accessToken) throws ThingsboardException;

    Device saveDeviceWithCredentials(Device device, DeviceCredentials deviceCredentials);

    Device saveDevice(ProvisionRequest provisionRequest, DeviceProfile profile);

    Device assignDeviceToCustomer(TenantId tenantId, DeviceId deviceId, CustomerId customerId);

    Device unassignDeviceFromCustomer(TenantId tenantId, DeviceId deviceId);

    void deleteDevice(TenantId tenantId, DeviceId deviceId);

    PageData<Device> findDevicesByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<DeviceInfo> findDeviceInfosByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<Device> findDevicesByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    PageData<Device> findDevicesByTenantIdAndTypeAndEmptyOtaPackage(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType type, PageLink pageLink);

    Long countDevicesByTenantIdAndDeviceProfileIdAndEmptyOtaPackage(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType otaPackageType);

    PageData<DeviceInfo> findDeviceInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    PageData<DeviceInfo> findDeviceInfosByTenantIdAndDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId, PageLink pageLink);

    ListenableFuture<List<Device>> findDevicesByTenantIdAndIdsAsync(TenantId tenantId, List<DeviceId> deviceIds);

    void deleteDevicesByTenantId(TenantId tenantId);

    PageData<Device> findDevicesByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    PageData<Device> findDevicesByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    PageData<DeviceInfo> findDeviceInfosByTenantIdAndCustomerIdAndDeviceProfileId(TenantId tenantId, CustomerId customerId, DeviceProfileId deviceProfileId, PageLink pageLink);

    ListenableFuture<List<Device>> findDevicesByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<DeviceId> deviceIds);

    void unassignCustomerDevices(TenantId tenantId, CustomerId customerId);

    ListenableFuture<List<Device>> findDevicesByQuery(TenantId tenantId, DeviceSearchQuery query);

    ListenableFuture<List<EntitySubtype>> findDeviceTypesByTenantId(TenantId tenantId);

    Device assignDeviceToTenant(TenantId tenantId, Device device);

    PageData<UUID> findDevicesIdsByDeviceProfileTransportType(DeviceTransportType transportType, PageLink pageLink);

    Device assignDeviceToEdge(TenantId tenantId, DeviceId deviceId, EdgeId edgeId);

    Device unassignDeviceFromEdge(TenantId tenantId, DeviceId deviceId, EdgeId edgeId);

    PageData<Device> findDevicesByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    PageData<Device> findDevicesByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink);

    long countByTenantId(TenantId tenantId);

    /**
     * 保存/修改设备
     * @param device
     * @return
     */
    Device saveOrUpdDevice(Device device) throws ThingsboardException;

    /**
     * 保存/修改设备构成
     * @param device
     * @throws ThingsboardException
     */
    void saveOrUpdDeviceComponentList(Device device,UUID deviceId,String saveType)  throws ThingsboardException;

    /**
     * 分配产线设备
     * @param device
     * @throws ThingsboardException
     */
    void distributionDevice(Device device) throws ThingsboardException;

    /**
     * 建立设备产线实体关系
     * @param device
     */
    void createRelationDeviceFromProductionLine(Device device);
    /**
     * 移除产线设备
     * @param device
     * @throws ThingsboardException
     */
    void removeDevice(Device device) throws ThingsboardException;

    /**
     * 查询工厂下具有最新版本的一个网关设备
     * @param factoryId
     * @return
     */
    List<Device> findGatewayNewVersionByFactory(List<UUID> factoryId) throws ThingsboardException;

    /**
     * 查询工厂下所有网关设备
     * @param factoryIds
     * @return
     */
    List<Device> findGatewayListVersionByFactory(List<UUID> factoryIds) throws ThingsboardException;

    /**
     *平台设备列表查询
     * @param device
     * @param pageLink
     * @return
     */
    PageData<Device> getTenantDeviceInfoList(Device device, PageLink pageLink);

    /**
     * 获取设备详情
     */
    Device getDeviceInfo(UUID id);

    PageData<DeviceDataVo> queryAllByNameLike(UUID factoryId, String name, PageLink pageLink);

    /**
     * 查询租户下未分配设备
     * @param tenantId
     * @return
     */
    List<Device> getNotDistributionDevice(TenantId tenantId);

    /**
     * 多条件查询设备
     * @param device
     * @return
     */
    List<Device> findDeviceListByCdn(Device device,String orderValue,String descOrAsc);

    PageData<CapacityDeviceVo> queryPage(CapacityDeviceVo  vo, PageLink pageLink) throws JsonProcessingException;

    PageData<AppCapacityDeviceVo> appQueryPage(CapacityDeviceVo  vo, PageLink pageLink) throws JsonProcessingException;

    void updateFlgById(Boolean deviceFlg, UUID id);

    /**
     * 查询设备字典下发的设备列表
     * @param device
     * @return
     */
    List<Device> findDeviceIssueListByCdn(Device device);

    /**
     * 设备ID批量查询
     * @param deviceIdList
     * @return
     */
    List<Device> findDevicesByIds(List<UUID> deviceIdList);

    long  countAllByDictDeviceIdAndTenantId(UUID dictDeviceId,UUID tenantId);



     List<Device> findAllBy();

}
