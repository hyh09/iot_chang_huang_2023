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
package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.device.data.DeviceData;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.NoXss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Device extends SearchTextBasedWithAdditionalInfo<DeviceId> implements HasName, HasTenantId, HasCustomerId, HasOtaPackage {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    private String name;
    @NoXss
    private String type;
    @NoXss
    private String label;
    private DeviceProfileId deviceProfileId;
    private transient DeviceData deviceData;
    @JsonIgnore
    private byte[] deviceDataBytes;

    private OtaPackageId firmwareId;
    private OtaPackageId softwareId;

    private UUID workshopId;
    private UUID factoryId;
    private String code;
    private UUID productionLineId;
    private String images;
    private String icon;

    public Device() {
        super();
    }

    public Device(DeviceId id) {
        super(id);
    }

    public Device(Device device) {
        super(device);
        this.tenantId = device.getTenantId();
        this.customerId = device.getCustomerId();
        this.name = device.getName();
        this.type = device.getType();
        this.label = device.getLabel();
        this.deviceProfileId = device.getDeviceProfileId();
        this.setDeviceData(device.getDeviceData());
        this.firmwareId = device.getFirmwareId();
        this.softwareId = device.getSoftwareId();
    }

    public Device updateDevice(Device device) {
        this.tenantId = device.getTenantId();
        this.customerId = device.getCustomerId();
        this.name = device.getName();
        this.type = device.getType();
        this.label = device.getLabel();
        this.deviceProfileId = device.getDeviceProfileId();
        this.setDeviceData(device.getDeviceData());
        this.setFirmwareId(device.getFirmwareId());
        this.setSoftwareId(device.getSoftwareId());
        return this;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DeviceProfileId getDeviceProfileId() {
        return deviceProfileId;
    }

    public void setDeviceProfileId(DeviceProfileId deviceProfileId) {
        this.deviceProfileId = deviceProfileId;
    }

    public DeviceData getDeviceData() {
        if (deviceData != null) {
            return deviceData;
        } else {
            if (deviceDataBytes != null) {
                try {
                    deviceData = mapper.readValue(new ByteArrayInputStream(deviceDataBytes), DeviceData.class);
                } catch (IOException e) {
                    log.warn("Can't deserialize device data: ", e);
                    return null;
                }
                return deviceData;
            } else {
                return null;
            }
        }
    }

    public void setDeviceData(DeviceData data) {
        this.deviceData = data;
        try {
            this.deviceDataBytes = data != null ? mapper.writeValueAsBytes(data) : null;
        } catch (JsonProcessingException e) {
            log.warn("Can't serialize device data: ", e);
        }
    }

    @Override
    public String getSearchText() {
        return getName();
    }

    public OtaPackageId getFirmwareId() {
        return firmwareId;
    }

    public void setFirmwareId(OtaPackageId firmwareId) {
        this.firmwareId = firmwareId;
    }

    public OtaPackageId getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(OtaPackageId softwareId) {
        this.softwareId = softwareId;
    }

    public UUID getProductionLineId() {
        return productionLineId;
    }

    public void setProductionLineId(UUID productionLineId) {
        this.productionLineId = productionLineId;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public UUID getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(UUID workshopId) {
        this.workshopId = workshopId;
    }

    public UUID getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(UUID factoryId) {
        this.factoryId = factoryId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Device [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", label=");
        builder.append(label);
        builder.append(", deviceProfileId=");
        builder.append(deviceProfileId);
        builder.append(", deviceData=");
        builder.append(firmwareId);
        builder.append(", firmwareId=");
        builder.append(deviceData);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
