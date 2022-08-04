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
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.device.data.DeviceData;
import org.thingsboard.server.common.data.devicecomponent.DeviceComponent;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.validation.NoXss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Device extends SearchTextBasedWithAdditionalInfo<DeviceId> implements HasName, HasTenantId, HasCustomerId, HasOtaPackage {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    @ApiModelProperty("设备名称")
    private String name;
    //设备配置名称
    @NoXss
    @ApiModelProperty("设备配置名称")
    private String type;
    @NoXss
    @ApiModelProperty("标签")
    private String label;
    @ApiModelProperty("设备配置id")
    private DeviceProfileId deviceProfileId;
    @ApiModelProperty("设备详细信息")
    private transient DeviceData deviceData;
    @JsonIgnore
    private byte[] deviceDataBytes;

    private OtaPackageId firmwareId;
    private OtaPackageId softwareId;
    @ApiModelProperty("车间标识")
    private UUID workshopId;
    @ApiModelProperty("工厂标识")
    private UUID factoryId;
    @ApiModelProperty("设备字典标识")
    private UUID dictDeviceId;
    @ApiModelProperty("设备编码")
    private String code;
    @ApiModelProperty("产线标识")
    private UUID productionLineId;
    @ApiModelProperty("设备图片")
    private String picture;
    @ApiModelProperty("设备图标")
    private String icon;
    @ApiModelProperty("设备备注")
    private String comment;
    @ApiModelProperty("设备机台编号")
    private String deviceNo;
    @ApiModelProperty("排序值")
    private Integer sort;
    public long createdTime;

    public UUID createdUser;

    private long updatedTime;

    private UUID updatedUser;

    @ApiModelProperty("设备重命名")
    private String rename;

    /**********************************以下是非数据库字段***************************************/

    private List<UUID> deviceIdList;
    //网关版本
    private Boolean active;
    //网关版本
    private String gatewayVersion;
    //网关版本更新时间
    private Long gatewayUpdateTs;
    //("true-已分配，false-未分配（默认值）")
    private Boolean isAllot = false;
    //设备构成
    private List<DeviceComponent> deviceComponentList;
    //工厂名称
    private String factoryName;
    //车间名称
    private String workshopName;
    //产线名称
    private String productionLineName;
    private List<UUID> productionLineIds;
    //是否过滤掉网关true是，false否
    private Boolean filterGatewayFlag = false;
    //设备配置名称
    private String deviceProfileName;
    @ApiModelProperty(name = "所属网关名称")
    public String gatewayName;
    @ApiModelProperty("所属网关标识")
    private UUID gatewayId;
    //是否只查网关  默认否
    private Boolean onlyGatewayFlag = false;
    private Boolean deviceFlg=false;
    @ApiModelProperty("是否过滤设备图片")
    private Boolean filterPictureFlag = false;
    @ApiModelProperty("是否过滤设备图标")
    private Boolean filterIconFlag = false;
    /**********************************以上是非数据库字段***************************************/



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
        this.deviceProfileName = device.getDeviceProfileName();
        this.deviceData =device.getDeviceData();
        this.firmwareId = device.getFirmwareId();
        this.softwareId = device.getSoftwareId();
        this.workshopId = device.getWorkshopId();
        this.factoryId = device.getFactoryId();
        this.dictDeviceId = device.getDictDeviceId();
        this.productionLineId = device.getProductionLineId();
        this.picture = device.getPicture();
        this.icon = device.getIcon();
        this.comment = device.getComment();
        this.deviceNo = device.getDeviceNo();
        this.createdUser = device.getCreatedUser();
        this.createdTime = device.getCreatedTime();
        this.updatedUser = device.getUpdatedUser();
        this.updatedTime = device.getUpdatedTime();
        this.factoryName = device.getFactoryName();
        this.workshopName = device.getWorkshopName();
        this.productionLineName = device.getProductionLineName();
        this.rename = device.getRename();
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

    public Device (Factory factory,List<UUID> productionLineIds){
        this.setFilterGatewayFlag(true);
        this.setProductionLineIds(productionLineIds);
        this.setTenantId(new TenantId(factory.getTenantId()));
        this.setName(factory.getDeviceName());
        this.setFilterPictureFlag(true);
    }

    public Device(TenantId tenantId, String name){
        this.setTenantId(tenantId);
        this.setName(name);
    }

    public Device(UUID tenantId, UUID factoryId, UUID workshopId,Boolean filterGatewayFlag) {
        this.tenantId = new TenantId(tenantId);
        this.factoryId = factoryId;
        this.workshopId = workshopId;
        this.filterGatewayFlag = filterGatewayFlag;
    }


    public Device (UUID tenantId){
        this.setTenantId(new TenantId(tenantId));
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public UUID getDictDeviceId() {
        return dictDeviceId;
    }

    public void setDictDeviceId(UUID dictDeviceId) {
        this.dictDeviceId = dictDeviceId;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public UUID getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(UUID createdUser) {
        this.createdUser = createdUser;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public UUID getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(UUID updatedUser) {
        this.updatedUser = updatedUser;
    }

    public List<UUID> getDeviceIdList() {
        return deviceIdList;
    }

    public void setDeviceIdList(List<UUID> deviceIdList) {
        this.deviceIdList = deviceIdList;
    }

    public String getGatewayVersion() {
        return gatewayVersion;
    }

    public void setGatewayVersion(String gatewayVersion) {
        this.gatewayVersion = gatewayVersion;
    }

    public Long getGatewayUpdateTs() {
        return gatewayUpdateTs;
    }

    public void setGatewayUpdateTs(Long gatewayUpdateTs) {
        this.gatewayUpdateTs = gatewayUpdateTs;
    }

    public Boolean getAllot() {
        return isAllot;
    }

    public void setAllot(Boolean allot) {
        isAllot = allot;
    }

    public List<DeviceComponent> getDeviceComponentList() {
        return deviceComponentList;
    }

    public void setDeviceComponentList(List<DeviceComponent> deviceComponentList) {
        this.deviceComponentList = deviceComponentList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    public String getProductionLineName() {
        return productionLineName;
    }

    public void setProductionLineName(String productionLineName) {
        this.productionLineName = productionLineName;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public List<UUID> getProductionLineIds() {
        return productionLineIds;
    }

    public void setProductionLineIds(List<UUID> productionLineIds) {
        this.productionLineIds = productionLineIds;
    }

    public Boolean getFilterGatewayFlag() {
        return filterGatewayFlag;
    }

    public void setFilterGatewayFlag(Boolean filterGatewayFlag) {
        this.filterGatewayFlag = filterGatewayFlag;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
