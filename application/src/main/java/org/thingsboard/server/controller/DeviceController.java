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
package org.thingsboard.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.nimbusds.jose.util.JSONObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.rule.engine.api.msg.DeviceCredentialsUpdateNotificationMsg;
import org.thingsboard.rule.engine.api.msg.DeviceEdgeUpdateMsg;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.device.DeviceSearchQuery;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.device.DeviceDataVo;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgDataType;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.config.RedisMessagePublish;
import org.thingsboard.server.dao.device.claim.ClaimResponse;
import org.thingsboard.server.dao.device.claim.ClaimResult;
import org.thingsboard.server.dao.device.claim.ReclaimResult;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.hs.dao.FileEntity;
import org.thingsboard.server.dao.hs.dao.FileRepository;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceVO;
import org.thingsboard.server.dao.hs.service.FileService;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.util.ReflectionUtils;
import org.thingsboard.server.entity.device.dto.*;
import org.thingsboard.server.entity.device.enums.ReadWriteEnum;
import org.thingsboard.server.entity.device.vo.DeviceIssueVo;
import org.thingsboard.server.entity.device.vo.DeviceVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import javax.annotation.Nullable;
import javax.persistence.Column;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.thingsboard.server.controller.EdgeController.EDGE_ID;
@Slf4j
@Api(value="设备管理Controller",tags={"设备管理口"})
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class DeviceController extends BaseController {

    private static final String DEVICE = "DEVICE";
    private static final String SHARED_SCOPE = "SHARED_SCOPE";
    private static final String DEVICE_ID = "deviceId";
    private static final String DEVICE_NAME = "deviceName";
    private static final String TENANT_ID = "tenantId";
    public static final String SAVE_TYPE_ADD = "add ";
    public static final String SAVE_TYPE_ADD_UPDATE = "update ";
    public static final String GATEWAY = "gateway";

    @Autowired
    private TelemetryController telemetryController;

    @Autowired
    private RedisMessagePublish pub;
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;


    @ApiOperation("云对接查设备详情")
    @ApiImplicitParam(name = "deviceId",value = "当前id",dataType = "String",paramType="path",required = true)
    @RequestMapping(value = "/yun/device/{deviceId}", method = RequestMethod.GET)
    @ResponseBody
    public Device getYunDeviceById(@PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            return deviceService.getDeviceInfo(toUUID(strDeviceId));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation("云对接查设备列表")
    @ApiImplicitParam(name = "device",value = "入参实体",dataType = "YunDeviceDto",paramType="query")
    @RequestMapping(value = "/yun/devices", method = RequestMethod.GET)
    @ResponseBody
    public List<Device> getYunDeviceList(YunDeviceDto device) throws ThingsboardException {
        try {
            Device deviceQry = device.toDevice();
            if(StringUtils.isNotEmpty(device.getTenantId())){
                deviceQry.setTenantId(new TenantId(toUUID(device.getTenantId())));
            }
            return deviceService.getYunDeviceList(deviceQry);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/device/{deviceId}", method = RequestMethod.GET)
    @ResponseBody
    public Device getDeviceById(@PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            return checkDeviceId(deviceId, Operation.READ);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/device/info/{deviceId}", method = RequestMethod.GET)
    @ResponseBody
    public DeviceInfo getDeviceInfoById(@PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            DeviceInfo deviceInfo = checkDeviceInfoId(deviceId, Operation.READ);
            deviceInfo.renameDevice();
            return deviceInfo;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation("系统原生平台设备添加接口")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/device", method = RequestMethod.POST)
    @ResponseBody
    public Device saveDevice(@RequestBody Device device,
                             @RequestParam(name = "accessToken", required = false) String accessToken) throws ThingsboardException {
        boolean created = device.getId() == null;
        try {
            device.setTenantId(getCurrentUser().getTenantId());

            Device oldDevice = null;
            if (!created) {
                oldDevice = checkDeviceId(device.getId(), Operation.WRITE);
            } else {
                checkEntity(null, device, Resource.DEVICE);
            }

            Device savedDevice = checkNotNull(deviceService.saveDeviceWithAccessToken(device, accessToken));

            tbClusterService.onDeviceUpdated(savedDevice, oldDevice);

            logEntityAction(savedDevice.getId(), savedDevice,
                    savedDevice.getCustomerId(),
                    created ? ActionType.ADDED : ActionType.UPDATED, null);
            //初始化网关版本
            this.saveAttributesInit(savedDevice);
            return savedDevice;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE), device,
                    null, created ? ActionType.ADDED : ActionType.UPDATED, e);
            throw handleException(e);
        }

    }
    /**
     * 初始化属性
     * @param device
     */
    private void saveAttributesInit(Device device) throws ThingsboardException, JsonProcessingException {
        //网关共享属性version
        if(device.getAdditionalInfo() != null && device.getAdditionalInfo().get(GATEWAY).asBoolean()){
            String json = "{\"version\":\"0.0.1\"}";
            JsonNode request = new ObjectMapper().readTree(json);
            telemetryController.saveEntityAttributesV1(DEVICE,device.getId().getId().toString(),SHARED_SCOPE,request);
        }
    }


    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/device/{deviceId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteDevice(@PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            Device device = checkDeviceId(deviceId, Operation.DELETE);

            List<EdgeId> relatedEdgeIds = findRelatedEdgeIds(getTenantId(), deviceId);

            deviceService.deleteDevice(getCurrentUser().getTenantId(), deviceId);

            tbClusterService.onDeviceDeleted(device, null);

            logEntityAction(deviceId, device,
                    device.getCustomerId(),
                    ActionType.DELETED, null, strDeviceId);

            sendDeleteNotificationMsg(getTenantId(), deviceId, relatedEdgeIds);
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE),
                    null,
                    null,
                    ActionType.DELETED, e, strDeviceId);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/{customerId}/device/{deviceId}", method = RequestMethod.POST)
    @ResponseBody
    public Device assignDeviceToCustomer(@PathVariable("customerId") String strCustomerId,
                                         @PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            Customer customer = checkCustomerId(customerId, Operation.READ);

            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            checkDeviceId(deviceId, Operation.ASSIGN_TO_CUSTOMER);

            Device savedDevice = checkNotNull(deviceService.assignDeviceToCustomer(getCurrentUser().getTenantId(), deviceId, customerId));

            logEntityAction(deviceId, savedDevice,
                    savedDevice.getCustomerId(),
                    ActionType.ASSIGNED_TO_CUSTOMER, null, strDeviceId, strCustomerId, customer.getName());

            sendEntityAssignToCustomerNotificationMsg(savedDevice.getTenantId(), savedDevice.getId(),
                    customerId, EdgeEventActionType.ASSIGNED_TO_CUSTOMER);

            return savedDevice;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strDeviceId, strCustomerId);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/device/{deviceId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Device unassignDeviceFromCustomer(@PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            Device device = checkDeviceId(deviceId, Operation.UNASSIGN_FROM_CUSTOMER);
            if (device.getCustomerId() == null || device.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
                throw new IncorrectParameterException("Device isn't assigned to any customer!");
            }
            Customer customer = checkCustomerId(device.getCustomerId(), Operation.READ);

            Device savedDevice = checkNotNull(deviceService.unassignDeviceFromCustomer(getCurrentUser().getTenantId(), deviceId));

            logEntityAction(deviceId, device,
                    device.getCustomerId(),
                    ActionType.UNASSIGNED_FROM_CUSTOMER, null, strDeviceId, customer.getId().toString(), customer.getName());

            sendEntityAssignToCustomerNotificationMsg(savedDevice.getTenantId(), savedDevice.getId(),
                    customer.getId(), EdgeEventActionType.UNASSIGNED_FROM_CUSTOMER);

            return savedDevice;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.UNASSIGNED_FROM_CUSTOMER, e, strDeviceId);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/public/device/{deviceId}", method = RequestMethod.POST)
    @ResponseBody
    public Device assignDeviceToPublicCustomer(@PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            Device device = checkDeviceId(deviceId, Operation.ASSIGN_TO_CUSTOMER);
            Customer publicCustomer = customerService.findOrCreatePublicCustomer(device.getTenantId());
            Device savedDevice = checkNotNull(deviceService.assignDeviceToCustomer(getCurrentUser().getTenantId(), deviceId, publicCustomer.getId()));

            logEntityAction(deviceId, savedDevice,
                    savedDevice.getCustomerId(),
                    ActionType.ASSIGNED_TO_CUSTOMER, null, strDeviceId, publicCustomer.getId().toString(), publicCustomer.getName());

            return savedDevice;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strDeviceId);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/device/{deviceId}/credentials", method = RequestMethod.GET)
    @ResponseBody
    public DeviceCredentials getDeviceCredentialsByDeviceId(@PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            Device device = checkDeviceId(deviceId, Operation.READ_CREDENTIALS);
            DeviceCredentials deviceCredentials = checkNotNull(deviceCredentialsService.findDeviceCredentialsByDeviceId(getCurrentUser().getTenantId(), deviceId));
            logEntityAction(deviceId, device,
                    device.getCustomerId(),
                    ActionType.CREDENTIALS_READ, null, strDeviceId);
            return deviceCredentials;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.CREDENTIALS_READ, e, strDeviceId);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/device/credentials", method = RequestMethod.POST)
    @ResponseBody
    public DeviceCredentials saveDeviceCredentials(@RequestBody DeviceCredentials deviceCredentials) throws ThingsboardException {
        checkNotNull(deviceCredentials);
        try {
            Device device = checkDeviceId(deviceCredentials.getDeviceId(), Operation.WRITE_CREDENTIALS);
            DeviceCredentials result = checkNotNull(deviceCredentialsService.updateDeviceCredentials(getCurrentUser().getTenantId(), deviceCredentials));
            tbClusterService.pushMsgToCore(new DeviceCredentialsUpdateNotificationMsg(getCurrentUser().getTenantId(), deviceCredentials.getDeviceId(), result), null);

            sendEntityNotificationMsg(getTenantId(), device.getId(), EdgeEventActionType.CREDENTIALS_UPDATED);

            logEntityAction(device.getId(), device,
                    device.getCustomerId(),
                    ActionType.CREDENTIALS_UPDATED, null, deviceCredentials);
            return result;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.CREDENTIALS_UPDATED, e, deviceCredentials);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/devices", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Device> getTenantDevices(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            if (type != null && type.trim().length() > 0) {
                return checkNotNull(deviceService.findDevicesByTenantIdAndType(tenantId, type, pageLink));
            } else {
                return checkNotNull(deviceService.findDevicesByTenantId(tenantId, pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/deviceInfos", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<DeviceInfo> getTenantDeviceInfos(
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String deviceProfileId,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            if (type != null && type.trim().length() > 0) {
                return checkNotNull(deviceService.findDeviceInfosByTenantIdAndType(tenantId, type, pageLink));
            } else if (deviceProfileId != null && deviceProfileId.length() > 0) {
                DeviceProfileId profileId = new DeviceProfileId(toUUID(deviceProfileId));
                return checkNotNull(deviceService.findDeviceInfosByTenantIdAndDeviceProfileId(tenantId, profileId, pageLink));
            } else {
                return checkNotNull(deviceService.findDeviceInfosByTenantId(tenantId, pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/devices", params = {"deviceName"}, method = RequestMethod.GET)
    @ResponseBody
    public Device getTenantDevice(
            @RequestParam String deviceName) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(deviceService.findDeviceByTenantIdAndName(tenantId, deviceName));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/{customerId}/devices", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Device> getCustomerDevices(
            @PathVariable("customerId") String strCustomerId,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId, Operation.READ);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            if (type != null && type.trim().length() > 0) {
                return checkNotNull(deviceService.findDevicesByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
            } else {
                return checkNotNull(deviceService.findDevicesByTenantIdAndCustomerId(tenantId, customerId, pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/{customerId}/deviceInfos", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<DeviceInfo> getCustomerDeviceInfos(
            @PathVariable("customerId") String strCustomerId,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String deviceProfileId,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId, Operation.READ);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            if (type != null && type.trim().length() > 0) {
                return checkNotNull(deviceService.findDeviceInfosByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
            } else if (deviceProfileId != null && deviceProfileId.length() > 0) {
                DeviceProfileId profileId = new DeviceProfileId(toUUID(deviceProfileId));
                return checkNotNull(deviceService.findDeviceInfosByTenantIdAndCustomerIdAndDeviceProfileId(tenantId, customerId, profileId, pageLink));
            } else {
                return checkNotNull(deviceService.findDeviceInfosByTenantIdAndCustomerId(tenantId, customerId, pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/devices", params = {"deviceIds"}, method = RequestMethod.GET)
    @ResponseBody
    public List<Device> getDevicesByIds(
            @RequestParam("deviceIds") String[] strDeviceIds) throws ThingsboardException {
        checkArrayParameter("deviceIds", strDeviceIds);
        try {
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            CustomerId customerId = user.getCustomerId();
            List<DeviceId> deviceIds = new ArrayList<>();
            for (String strDeviceId : strDeviceIds) {
                deviceIds.add(new DeviceId(toUUID(strDeviceId)));
            }
            ListenableFuture<List<Device>> devices;
            if (customerId == null || customerId.isNullUid()) {
                devices = deviceService.findDevicesByTenantIdAndIdsAsync(tenantId, deviceIds);
            } else {
                devices = deviceService.findDevicesByTenantIdCustomerIdAndIdsAsync(tenantId, customerId, deviceIds);
            }
            return checkNotNull(devices.get());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/devices", method = RequestMethod.POST)
    @ResponseBody
    public List<Device> findByQuery(@RequestBody DeviceSearchQuery query) throws ThingsboardException {
        checkNotNull(query);
        checkNotNull(query.getParameters());
        checkNotNull(query.getDeviceTypes());
        checkEntityId(query.getParameters().getEntityId(), Operation.READ);
        try {
            List<Device> devices = checkNotNull(deviceService.findDevicesByQuery(getCurrentUser().getTenantId(), query).get());
            devices = devices.stream().filter(device -> {
                try {
                    accessControlService.checkPermission(getCurrentUser(), Resource.DEVICE, Operation.READ, device.getId(), device);
                    return true;
                } catch (ThingsboardException e) {
                    return false;
                }
            }).collect(Collectors.toList());
            return devices;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/device/types", method = RequestMethod.GET)
    @ResponseBody
    public List<EntitySubtype> getDeviceTypes() throws ThingsboardException {
        try {
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            ListenableFuture<List<EntitySubtype>> deviceTypes = deviceService.findDeviceTypesByTenantId(tenantId);
            return checkNotNull(deviceTypes.get());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('CUSTOMER_USER')")
    @RequestMapping(value = "/customer/device/{deviceName}/claim", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> claimDevice(@PathVariable(DEVICE_NAME) String deviceName,
                                                      @RequestBody(required = false) ClaimRequest claimRequest) throws ThingsboardException {
        checkParameter(DEVICE_NAME, deviceName);
        try {
            final DeferredResult<ResponseEntity> deferredResult = new DeferredResult<>();

            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            CustomerId customerId = user.getCustomerId();

            Device device = checkNotNull(deviceService.findDeviceByTenantIdAndName(tenantId, deviceName));
            accessControlService.checkPermission(user, Resource.DEVICE, Operation.CLAIM_DEVICES,
                    device.getId(), device);
            String secretKey = getSecretKey(claimRequest);

            ListenableFuture<ClaimResult> future = claimDevicesService.claimDevice(device, customerId, secretKey);
            Futures.addCallback(future, new FutureCallback<ClaimResult>() {
                @Override
                public void onSuccess(@Nullable ClaimResult result) {
                    HttpStatus status;
                    if (result != null) {
                        if (result.getResponse().equals(ClaimResponse.SUCCESS)) {
                            status = HttpStatus.OK;
                            deferredResult.setResult(new ResponseEntity<>(result, status));

                            try {
                                logEntityAction(user, device.getId(), result.getDevice(), customerId, ActionType.ASSIGNED_TO_CUSTOMER, null,
                                        device.getId().toString(), customerId.toString(), customerService.findCustomerById(tenantId, customerId).getName());
                            } catch (ThingsboardException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            status = HttpStatus.BAD_REQUEST;
                            deferredResult.setResult(new ResponseEntity<>(result.getResponse(), status));
                        }
                    } else {
                        deferredResult.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    deferredResult.setErrorResult(t);
                }
            }, MoreExecutors.directExecutor());
            return deferredResult;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/device/{deviceName}/claim", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public DeferredResult<ResponseEntity> reClaimDevice(@PathVariable(DEVICE_NAME) String deviceName) throws ThingsboardException {
        checkParameter(DEVICE_NAME, deviceName);
        try {
            final DeferredResult<ResponseEntity> deferredResult = new DeferredResult<>();

            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();

            Device device = checkNotNull(deviceService.findDeviceByTenantIdAndName(tenantId, deviceName));
            accessControlService.checkPermission(user, Resource.DEVICE, Operation.CLAIM_DEVICES,
                    device.getId(), device);

            ListenableFuture<ReclaimResult> result = claimDevicesService.reClaimDevice(tenantId, device);
            Futures.addCallback(result, new FutureCallback<>() {
                @Override
                public void onSuccess(ReclaimResult reclaimResult) {
                    deferredResult.setResult(new ResponseEntity(HttpStatus.OK));

                    Customer unassignedCustomer = reclaimResult.getUnassignedCustomer();
                    if (unassignedCustomer != null) {
                        try {
                            logEntityAction(user, device.getId(), device, device.getCustomerId(), ActionType.UNASSIGNED_FROM_CUSTOMER, null,
                                    device.getId().toString(), unassignedCustomer.getId().toString(), unassignedCustomer.getName());
                        } catch (ThingsboardException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    deferredResult.setErrorResult(t);
                }
            }, MoreExecutors.directExecutor());
            return deferredResult;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private String getSecretKey(ClaimRequest claimRequest) throws IOException {
        String secretKey = claimRequest.getSecretKey();
        if (secretKey != null) {
            return secretKey;
        }
        return DataConstants.DEFAULT_SECRET_KEY;
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/{tenantId}/device/{deviceId}", method = RequestMethod.POST)
    @ResponseBody
    public Device assignDeviceToTenant(@PathVariable(TENANT_ID) String strTenantId,
                                       @PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(TENANT_ID, strTenantId);
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            Device device = checkDeviceId(deviceId, Operation.ASSIGN_TO_TENANT);

            TenantId newTenantId = new TenantId(toUUID(strTenantId));
            Tenant newTenant = tenantService.findTenantById(newTenantId);
            if (newTenant == null) {
                throw new ThingsboardException("Could not find the specified Tenant!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }

            Device assignedDevice = deviceService.assignDeviceToTenant(newTenantId, device);

            logEntityAction(getCurrentUser(), deviceId, assignedDevice,
                    assignedDevice.getCustomerId(),
                    ActionType.ASSIGNED_TO_TENANT, null, strTenantId, newTenant.getName());

            Tenant currentTenant = tenantService.findTenantById(getTenantId());
            pushAssignedFromNotification(currentTenant, newTenantId, assignedDevice);

            return assignedDevice;
        } catch (Exception e) {
            logEntityAction(getCurrentUser(), emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.ASSIGNED_TO_TENANT, e, strTenantId);
            throw handleException(e);
        }
    }

    private void pushAssignedFromNotification(Tenant currentTenant, TenantId newTenantId, Device assignedDevice) {
        String data = entityToStr(assignedDevice);
        if (data != null) {
            TbMsg tbMsg = TbMsg.newMsg(DataConstants.ENTITY_ASSIGNED_FROM_TENANT, assignedDevice.getId(), assignedDevice.getCustomerId(), getMetaDataForAssignedFrom(currentTenant), TbMsgDataType.JSON, data);
            tbClusterService.pushMsgToRuleEngine(newTenantId, assignedDevice.getId(), tbMsg, null);
        }
    }

    private TbMsgMetaData getMetaDataForAssignedFrom(Tenant tenant) {
        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue("assignedFromTenantId", tenant.getId().getId().toString());
        metaData.putValue("assignedFromTenantName", tenant.getName());
        return metaData;
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/edge/{edgeId}/device/{deviceId}", method = RequestMethod.POST)
    @ResponseBody
    public Device assignDeviceToEdge(@PathVariable(EDGE_ID) String strEdgeId,
                                     @PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
            Edge edge = checkEdgeId(edgeId, Operation.READ);

            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            checkDeviceId(deviceId, Operation.READ);

            Device savedDevice = checkNotNull(deviceService.assignDeviceToEdge(getCurrentUser().getTenantId(), deviceId, edgeId));

            tbClusterService.pushMsgToCore(new DeviceEdgeUpdateMsg(savedDevice.getTenantId(),
                    savedDevice.getId(), edgeId), null);

            logEntityAction(deviceId, savedDevice,
                    savedDevice.getCustomerId(),
                    ActionType.ASSIGNED_TO_EDGE, null, strDeviceId, strEdgeId, edge.getName());

            sendEntityAssignToEdgeNotificationMsg(getTenantId(), edgeId, savedDevice.getId(), EdgeEventActionType.ASSIGNED_TO_EDGE);

            return savedDevice;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.ASSIGNED_TO_EDGE, e, strDeviceId, strEdgeId);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/edge/{edgeId}/device/{deviceId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Device unassignDeviceFromEdge(@PathVariable(EDGE_ID) String strEdgeId,
                                         @PathVariable(DEVICE_ID) String strDeviceId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        checkParameter(DEVICE_ID, strDeviceId);
        try {
            EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
            Edge edge = checkEdgeId(edgeId, Operation.READ);

            DeviceId deviceId = new DeviceId(toUUID(strDeviceId));
            Device device = checkDeviceId(deviceId, Operation.READ);

            Device savedDevice = checkNotNull(deviceService.unassignDeviceFromEdge(getCurrentUser().getTenantId(), deviceId, edgeId));

            tbClusterService.pushMsgToCore(new DeviceEdgeUpdateMsg(savedDevice.getTenantId(),
                    savedDevice.getId(), null), null);

            logEntityAction(deviceId, device,
                    device.getCustomerId(),
                    ActionType.UNASSIGNED_FROM_EDGE, null, strDeviceId, strEdgeId, edge.getName());

            sendEntityAssignToEdgeNotificationMsg(getTenantId(), edgeId, savedDevice.getId(), EdgeEventActionType.UNASSIGNED_FROM_EDGE);

            return savedDevice;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.DEVICE), null,
                    null,
                    ActionType.UNASSIGNED_FROM_EDGE, e, strDeviceId, strEdgeId);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/edge/{edgeId}/devices", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Device> getEdgeDevices(
            @PathVariable(EDGE_ID) String strEdgeId,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
            checkEdgeId(edgeId, Operation.READ);
            TimePageLink pageLink = createTimePageLink(pageSize, page, textSearch, sortProperty, sortOrder, startTime, endTime);
            PageData<Device> nonFilteredResult;
            if (type != null && type.trim().length() > 0) {
                nonFilteredResult = deviceService.findDevicesByTenantIdAndEdgeIdAndType(tenantId, edgeId, type, pageLink);
            } else {
                nonFilteredResult = deviceService.findDevicesByTenantIdAndEdgeId(tenantId, edgeId, pageLink);
            }
            List<Device> filteredDevices = nonFilteredResult.getData().stream().filter(device -> {
                try {
                    accessControlService.checkPermission(getCurrentUser(), Resource.DEVICE, Operation.READ, device.getId(), device);
                    return true;
                } catch (ThingsboardException e) {
                    return false;
                }
            }).collect(Collectors.toList());
            PageData<Device> filteredResult = new PageData<>(filteredDevices,
                    nonFilteredResult.getTotalPages(),
                    nonFilteredResult.getTotalElements(),
                    nonFilteredResult.hasNext());
            return checkNotNull(filteredResult);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/devices/count/{otaPackageType}/{deviceProfileId}", method = RequestMethod.GET)
    @ResponseBody
    public Long countByDeviceProfileAndEmptyOtaPackage(@PathVariable("otaPackageType") String otaPackageType,
                                                       @PathVariable("deviceProfileId") String deviceProfileId) throws ThingsboardException {
        checkParameter("OtaPackageType", otaPackageType);
        checkParameter("DeviceProfileId", deviceProfileId);
        try {
            return deviceService.countDevicesByTenantIdAndDeviceProfileIdAndEmptyOtaPackage(
                    getTenantId(),
                    new DeviceProfileId(UUID.fromString(deviceProfileId)),
                    OtaPackageType.valueOf(otaPackageType));
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    /*******************************************新增业务接口*******************************************/
    /*******************************************新增业务接口*******************************************/
    /*******************************************新增业务接口*******************************************/


    @ApiOperation("平台设备列表查询（重写平台原来的列表查询）")
    @RequestMapping(value = "/tenant/deviceInfoList", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParam(name = "deviceQry",value = "多条件入参",dataType = "DeviceQry",paramType = "query")
    @ResponseBody
    public PageData<DeviceInfo> getTenantDeviceInfoList(@RequestParam int pageSize, @RequestParam int page, DeviceListQry deviceListQry) throws ThingsboardException {
        try {
            PageData<DeviceInfo> resultPage = new PageData<>();
            List<DeviceInfo> resultDevices = new ArrayList<>();
            TenantId tenantId = getCurrentUser().getTenantId();
            PageLink pageLink = createPageLink(pageSize, page, deviceListQry.getSearchText(), deviceListQry.getSortProperty(), deviceListQry.getSortOrder());
            Device device = deviceListQry.toDevice();
            device.setTenantId(tenantId);
            //查询设备
            PageData<Device> menuPageData = deviceService.getTenantDeviceInfoList(device, pageLink);
            List<Device> deviceList = menuPageData.getData();
            if(!CollectionUtils.isEmpty(deviceList)){
                deviceList.forEach(i->{
                    resultDevices.add(new DeviceInfo(i));
                });
            }
            resultPage = new PageData<>(resultDevices,menuPageData.getTotalPages(),menuPageData.getTotalElements(),menuPageData.hasNext());
            return resultPage;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 新增/更新设备
     * @param addDeviceDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增/更新设备")
    @ApiImplicitParam(name = "addDeviceDto",value = "入参实体",dataType = "AddDeviceDto",paramType="body")
    @RequestMapping(value = "/saveOrUpdDevice", method = RequestMethod.POST)
    @ResponseBody
    public DeviceVo saveOrUpdDevice(@RequestBody AddDeviceDto addDeviceDto)throws ThingsboardException {
        try {
            checkNotNull(addDeviceDto);
            boolean created = addDeviceDto.getId() == null;
            Device device = addDeviceDto.toDevice();
            device.setTenantId(getCurrentUser().getTenantId());
            Device oldDevice = null;
            String saveType = null;
            //TransportMqttClient.TYPE yunMqttTopic = TransportMqttClient.TYPE.POST_DEVICE_ADD;
            if (!created) {
                oldDevice = checkDeviceId(device.getId(), Operation.WRITE);
                saveType = SAVE_TYPE_ADD_UPDATE;
                //yunMqttTopic = TransportMqttClient.TYPE.POST_DEVICE_UPDATE;
            } else {
                checkEntity(null, device, Resource.DEVICE);
                saveType = SAVE_TYPE_ADD;
            }
            //保存设备，生成设备凭证
            Device savedDevice = checkNotNull(deviceService.saveDeviceWithAccessToken(device, null));
            //设备配置
            tbClusterService.onDeviceUpdated(savedDevice, oldDevice);
            logEntityAction(savedDevice.getId(), savedDevice,
                    savedDevice.getCustomerId(),
                    created ? ActionType.ADDED : ActionType.UPDATED, null);
            //保存或修改设备构成
            deviceService.saveOrUpdDeviceComponentList(device,savedDevice.getId().getId(),saveType);
            if(savedDevice.getProductionLineId() != null){
                //建立实体关系
                deviceService.createRelationDeviceFromProductionLine(savedDevice);
            }
            //云云对接,过滤网关
            /*if(addDeviceDto.getAdditionalInfo() != null && addDeviceDto.getAdditionalInfo().get(GATEWAY) != null && !addDeviceDto.getAdditionalInfo().get(GATEWAY).booleanValue()){
                transportService.publishDevice(device.getTenantId(),savedDevice.getId(), yunMqttTopic);
            }*/
            savedDevice.setFactoryName(addDeviceDto.getFactoryName());
            savedDevice.setWorkshopName(addDeviceDto.getWorkshopName());
            savedDevice.setProductionLineName(addDeviceDto.getProductionLineName());
            return new DeviceVo(savedDevice);
        } catch (Exception e) {
            log.error("异常",e);
            throw handleException(e);
        }
    }

    /**
     * 分配产线设备
     * @param distributionDeviceDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("分配产线设备")
    @ApiImplicitParam(name = "distributionDeviceDto",value = "入参实体",dataType = "DistributionDeviceDto",paramType="body")
    @RequestMapping(value = "/distributionDevice", method = RequestMethod.POST)
    @ResponseBody
    public void distributionDevice(@RequestBody DistributionDeviceDto distributionDeviceDto) throws ThingsboardException {
        try {
            checkParameter("deviceId",distributionDeviceDto.getDeviceIdList());
            //支持分配工厂/产线
//            checkParameter("productionLineId",distributionDeviceDto.getProductionLineId());
//            checkParameter("workshopId",distributionDeviceDto.getWorkshopId());
//            checkParameter("factoryId",distributionDeviceDto.getFactoryId());
            Device device = distributionDeviceDto.toDevice();
            device.setUpdatedUser(getCurrentUser().getUuidId());
            deviceService.distributionDevice(device);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 移除产线设备
     * @param distributionDeviceDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("移除产线设备")
    @ApiImplicitParam(name = "distributionDeviceDto",value = "入参实体",dataType = "DistributionDeviceDto",paramType="body")
    @RequestMapping(value = "/removeDevice", method = RequestMethod.POST)
    @ResponseBody
    public void removeDevice(@RequestBody DistributionDeviceDto distributionDeviceDto) throws ThingsboardException {
        try {
            checkParameter("deviceIdList",distributionDeviceDto.getDeviceIdList());
            Device device = distributionDeviceDto.toDevice();
            device.setUpdatedUser(getCurrentUser().getUuidId());
            deviceService.removeDevice(device);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 获取设备详情
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("获取设备详情")
    @ApiImplicitParam(name = "id",value = "当前id",dataType = "String",paramType="path",required = true)
    @RequestMapping(value = "/device/getDeviceInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DeviceVo getDeviceInfo(@PathVariable("id") String id) throws ThingsboardException {
        try {
            checkParameter("id",id);
            DeviceVo resultDeviceVo = new DeviceVo(checkNotNull(deviceService.getDeviceInfo(toUUID(id))));
            if(resultDeviceVo != null && resultDeviceVo.getDictDeviceId() != null && StringUtils.isNotEmpty(resultDeviceVo.getDictDeviceId().toString())){
                //查询设备字典
                DictDeviceVO dictDeviceVO = dictDeviceService.getDictDeviceDetail(resultDeviceVo.getDictDeviceId().toString(),getCurrentUser().getTenantId());
                resultDeviceVo.setDictDeviceVO(dictDeviceVO);
                //如果设备picture、icon、comment为空，则使用数据字典的。
                if(StringUtils.isEmpty(resultDeviceVo.getPicture())){
                    resultDeviceVo.setPicture(dictDeviceVO.getPicture());
                }
                if(StringUtils.isEmpty(resultDeviceVo.getIcon())){
                    resultDeviceVo.setIcon(dictDeviceVO.getIcon());
                }
                if(StringUtils.isEmpty(resultDeviceVo.getComment())){
                    resultDeviceVo.setComment(dictDeviceVO.getComment());
                }
            }
            return resultDeviceVo;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /***
     * 设备的模糊查询 只根据设备的名称查询  queryAllByNameLike
     */
    @ApiOperation("app端调用设备的名字模糊查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂id"),
            @ApiImplicitParam(name = "name", value = "设备的名称"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小"),
            @ApiImplicitParam(name = "page", value = "起始页默认0开始"),
    })
   @RequestMapping(value = "app/device/queryAllByNameLike", params = {"pageSize", "page"}, method = RequestMethod.GET)
   public  PageData<DeviceDataVo>  queryAllByNameLike(@RequestParam(value = "factoryId",required = false) UUID factoryId,
                                                      @RequestParam(value = "name",required = false) String  name,
                                                      @RequestParam int pageSize,
                                                      @RequestParam int page,
                                                      @RequestParam(required = false) String textSearch,
                                                      @RequestParam(required = false) String sortProperty,
                                                      @RequestParam(required = false) String sortOrder  )
    {
        try {
            if(StringUtils.isEmpty(sortProperty))
            {
                sortProperty="createdTime";
                sortOrder="";
            }
            Field field=  ReflectionUtils.getAccessibleField(new DeviceEntity(),sortProperty);
            Column annotation = field.getAnnotation(Column.class);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, annotation.name(),sortOrder);

          return   deviceService.queryAllByNameLike(factoryId,name,pageLink);

        } catch (ThingsboardException e) {
            e.printStackTrace();
            throw  new CustomException("501","获取当前数据异常");
        }

    }

    @ApiOperation("自定义条件查询设备列表")
    @ApiImplicitParam(name = "deviceQry",value = "入参实体",dataType = "DeviceQry",paramType="body")
    @RequestMapping(value = "/findDeviceListByCdn", method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceVo> findDeviceListByCdn(DeviceQry deviceQry) throws ThingsboardException{
        List<DeviceVo> result = new ArrayList<>();
        var tenantId = getTenantId();
        try {
            deviceQry.setTenantId(getCurrentUser().getTenantId().getId());
            List<Device> deviceListByCdn = deviceService.findDeviceListByCdn(deviceQry.toDevice(),null,null);
            if (deviceQry.getHasScene() && deviceListByCdn != null && !deviceListByCdn.isEmpty())
                deviceListByCdn = this.fileService.filterDeviceSceneDevices(tenantId, deviceListByCdn);

            if(!CollectionUtils.isEmpty(deviceListByCdn)){
                for (Device device:deviceListByCdn){
                    result.add(new DeviceVo(device));
                }
                //是否只查有设备模型的设备
                if(deviceQry.getHasModel() != null && deviceQry.getHasModel()){
                    result = this.findDeviceByHasModel(result);
                }
            }
        } catch (ThingsboardException e) {
            log.error("自定义条件查询设备列表异常-"+e.getMessage(),e);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 只查有设备模型的设备
     * @param result
     * @return
     */
    private List<DeviceVo> findDeviceByHasModel(List<DeviceVo> result)throws ThingsboardException{
        try {
            if(!CollectionUtils.isEmpty(result)){
                List<DeviceVo> filterDevice = result.stream().filter(f -> f.getDictDeviceId() != null && StringUtils.isNotEmpty(f.getDictDeviceId().toString())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(filterDevice)){
                    List<UUID> collect = filterDevice.stream().map(m -> m.getDictDeviceId()).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(collect)){
                        collect = collect.stream().distinct().collect(Collectors.toList());
                        List<FileEntity> deviceModelCountByDeviceIds = fileRepository.findDeviceModelByDictDeviceIds(collect);
                        if(!CollectionUtils.isEmpty(deviceModelCountByDeviceIds)){
                            Iterator<DeviceVo> iterator = result.iterator();
                            while (iterator.hasNext()){
                                DeviceVo deviceVo = iterator.next();
                                Boolean HasModel = false;
                                for (FileEntity i:deviceModelCountByDeviceIds) {
                                    if(deviceVo.getDictDeviceId().toString().equals(i.getEntityId().toString())){
                                        HasModel = true;
                                        break;
                                    }
                                }
                                if(!HasModel){
                                    iterator.remove();
                                }
                            }
                            return result;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("【查询设备模型异常】"+e.getMessage(),e);
            throw new ThingsboardException("查询设备模型异常", ThingsboardErrorCode.FAIL_VIOLATION);
        }
        return null;
    }

    @ApiOperation("查询设备字典下发的设备列表")
    @ApiImplicitParam(name = "deviceQry" ,value = "入参实体",dataType = "DeviceIssueQry",paramType="query")
    @RequestMapping(value = "/findDeviceIssueListByCdn", method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceIssueVo> findDeviceIssueListByCdn(DeviceIssueQry deviceQry) throws ThingsboardException{
        List<DeviceIssueVo> result = new ArrayList<>();
        Device device = deviceQry.toDevice();
        device.setTenantId(new TenantId(getCurrentUser().getTenantId().getId()));
        List<Device> deviceListByCdn = deviceService.findDeviceIssueListByCdn(device);
        if(!CollectionUtils.isEmpty(deviceListByCdn)){
            deviceListByCdn.forEach(i->{
                result.add(new DeviceIssueVo(i));
            });
        }
        return result;

    }


    @ApiOperation("设备配置下发")
    @ApiImplicitParam(name = "deviceIssueDto" ,value = "入参实体",dataType = "DeviceIssueDto",paramType="body")
    @RequestMapping(value = "/deviceIssue", method = RequestMethod.PUT)
    @ResponseBody
    public String deviceIssue(@RequestBody DeviceIssueDto deviceIssueDto) throws ThingsboardException, ExecutionException, InterruptedException {
        log.info("/deviceIssue设备字典下发"+ new Gson().toJson(deviceIssueDto));
        //下发入参
        Map mapIssue = new HashMap();
        //设备信息
        List listIssueDevice = new ArrayList();
        //分组
        Map<String,List<Map<String,String>>> groupMap = new HashMap<>();

        mapIssue.put("DEVICE",listIssueDevice);
        mapIssue.put("DRIVER_CONFIG",groupMap);

        if(!CollectionUtils.isEmpty(deviceIssueDto.getDriverConfigList())){
            //协议类型
            mapIssue.put("PROTOCOL_TYPE",deviceIssueDto.getType());
            //驱动版本号
            mapIssue.put("DRIVER_VERSION",deviceIssueDto.getDriverVersion());
            for (DeviceIssueDto.DriveConig driveConig : deviceIssueDto.getDriverConfigList()){
                String codeByDesc = ReadWriteEnum.getCodeByDesc(driveConig.getReadWrite());
                //校验
                if(StringUtils.isEmpty(codeByDesc)){
                    throw new ThingsboardException("读写方向的值不符合规范！",ThingsboardErrorCode.FAIL_VIOLATION);
                }else {
                    driveConig.setReadWrite(codeByDesc);
                }
                //点位(点位详细信息的集合，一条就是一个点位)
                List<Map<String,String>> pointList = new ArrayList<>();
                //点位详细信息
                Map<String,String> map = driveConig.savePointMap();
                pointList.add(map);
                //保存点位分组
                if(StringUtils.isNotEmpty(driveConig.getCategory())){
                    if(groupMap.get(driveConig.getCategory()) != null){
                        //相同类型的点位，归到同一个分组内
                        pointList.addAll(groupMap.get(driveConig.getCategory()));
                    }
                    groupMap.put(driveConig.getCategory(),pointList);
                }else {
                    groupMap.put("custom",pointList);
                }
            }
            //设备信息
            if(!CollectionUtils.isEmpty(deviceIssueDto.getDeviceList())){
                listIssueDevice.addAll(deviceIssueDto.getDeviceList().stream().map(s->s.getDeviceName()).collect(Collectors.toList()));
            }
            mapIssue.put("DEVICE",listIssueDevice);
            mapIssue.put("DRIVER_CONFIG",groupMap);
            //下发网关
            if(!CollectionUtils.isEmpty(deviceIssueDto.getDeviceList())){
                List<String> gatewayIds = deviceIssueDto.getDeviceList().stream().map(e -> e.getGatewayId()).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(gatewayIds)){
                    gatewayIds = gatewayIds.stream().distinct().collect(Collectors.toList());
                }
                if(!CollectionUtils.isEmpty(gatewayIds)){
                    Map publishRedisMap = new HashMap<>();
                    publishRedisMap.put("body",mapIssue);
                    publishRedisMap.put("topic",gatewayIds);
                    pub.sendMessage("dictIssue", JSONObjectUtils.toJSONString(publishRedisMap));
                }
            }
        }
        return mapIssue != null ?JSONObjectUtils.toJSONString(mapIssue) : null;
    }


}
