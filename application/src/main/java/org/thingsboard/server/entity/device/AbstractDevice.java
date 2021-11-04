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
package org.thingsboard.server.entity.device;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.device.data.DeviceData;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.dao.model.sql.DeviceEntity;

import java.util.UUID;

@Data
public abstract class AbstractDevice{
    @ApiModelProperty("设备标识")
    private UUID id;

    @ApiModelProperty("租户标识")
    private UUID tenantId;

    @ApiModelProperty("客户标识")
    private UUID customerId;

    @ApiModelProperty("")
    private String type;

    @ApiModelProperty("设备名称")
    private String name;

    @ApiModelProperty("标签")
    private String label;

    private String searchText;

    private JsonNode additionalInfo;

    private UUID deviceProfileId;

    private UUID firmwareId;

    private UUID softwareId;

    private JsonNode deviceData;

    @ApiModelProperty("产线标识")
    private UUID productionLineId;

    @ApiModelProperty("车间标识")
    private UUID workshopId;

    @ApiModelProperty("工厂标识")
    private UUID factoryId;

    @ApiModelProperty("设备字典标识")
    private UUID dictDeviceId;

    @ApiModelProperty("设备图片")
    private String images;

    @ApiModelProperty("设备图标")
    private String icon;

    @ApiModelProperty("设备编码")
    private String code;

    public long createdTime;

    public UUID createdUser;

    private long updatedTime;

    private UUID updatedUser;

    public AbstractDevice() {
        super();
    }

    public AbstractDevice(Device device) {
        if (device.getId() != null) {
            this.setId(device.getUuidId());
        }
        this.setCreatedTime(device.getCreatedTime());
        if (device.getTenantId() != null) {
            this.tenantId = device.getTenantId().getId();
        }
        if (device.getCustomerId() != null) {
            this.customerId = device.getCustomerId().getId();
        }
        if (device.getDeviceProfileId() != null) {
            this.deviceProfileId = device.getDeviceProfileId().getId();
        }
        if (device.getFirmwareId() != null) {
            this.firmwareId = device.getFirmwareId().getId();
        }
        if (device.getSoftwareId() != null) {
            this.softwareId = device.getSoftwareId().getId();
        }
        this.deviceData = JacksonUtil.convertValue(device.getDeviceData(), ObjectNode.class);
        this.name = device.getName();
        this.type = device.getType();
        this.label = device.getLabel();
        this.additionalInfo = device.getAdditionalInfo();
        this.code = device.getCode();
        this.factoryId = device.getFactoryId();
        this.workshopId = device.getWorkshopId();
        this.productionLineId = device.getProductionLineId();
        this.images = device.getImages();
        this.icon = device.getIcon();
        this.dictDeviceId = device.getDictDeviceId();
    }

    public AbstractDevice(DeviceEntity deviceEntity) {
        this.setId(deviceEntity.getId());
        this.setCreatedTime(deviceEntity.getCreatedTime());
        this.tenantId = deviceEntity.getTenantId();
        this.customerId = deviceEntity.getCustomerId();
        this.deviceProfileId = deviceEntity.getDeviceProfileId();
        this.deviceData = deviceEntity.getDeviceData();
        this.type = deviceEntity.getType();
        this.name = deviceEntity.getName();
        this.label = deviceEntity.getLabel();
        this.searchText = deviceEntity.getSearchText();
        this.additionalInfo = deviceEntity.getAdditionalInfo();
        this.firmwareId = deviceEntity.getFirmwareId();
        this.softwareId = deviceEntity.getSoftwareId();
        this.code = deviceEntity.getCode();
        this.factoryId = deviceEntity.getFactoryId();
        this.workshopId = deviceEntity.getWorkshopId();
        this.productionLineId = deviceEntity.getProductionLineId();
        this.images = deviceEntity.getImages();
        this.icon = deviceEntity.getIcon();
        this.dictDeviceId = deviceEntity.getDictDeviceId();
    }

    public Device toDevice() {
        Device device = new Device(new DeviceId(getId()));
        device.setCreatedTime(createdTime);
        if (tenantId != null) {
            device.setTenantId(new TenantId(tenantId));
        }
        if (customerId != null) {
            device.setCustomerId(new CustomerId(customerId));
        }
        if (deviceProfileId != null) {
            device.setDeviceProfileId(new DeviceProfileId(deviceProfileId));
        }
        if (firmwareId != null) {
            device.setFirmwareId(new OtaPackageId(firmwareId));
        }
        if (softwareId != null) {
            device.setSoftwareId(new OtaPackageId(softwareId));
        }
        device.setDeviceData(JacksonUtil.convertValue(deviceData, DeviceData.class));
        device.setName(name);
        device.setType(type);
        device.setLabel(label);
        device.setAdditionalInfo(additionalInfo);
        device.setFactoryId(factoryId);
        device.setWorkshopId(workshopId);
        device.setProductionLineId(productionLineId);
        device.setProductionLineId(productionLineId);
        device.setImages(images);
        device.setIcon(icon);
        device.setCode(code);
        device.setDictDeviceId(dictDeviceId);
        return device;
    }

}
