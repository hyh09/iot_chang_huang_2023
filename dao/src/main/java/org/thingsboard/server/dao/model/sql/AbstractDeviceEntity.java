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
package org.thingsboard.server.dao.model.sql;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.device.data.DeviceData;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.SearchTextEntity;
import org.thingsboard.server.dao.util.mapping.JsonBinaryType;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@MappedSuperclass
public abstract class AbstractDeviceEntity<T extends Device> extends BaseSqlEntity<T> implements SearchTextEntity<T> {

    @Column(name = ModelConstants.DEVICE_TENANT_ID_PROPERTY, columnDefinition = "uuid")
    private UUID tenantId;

    @Column(name = ModelConstants.DEVICE_CUSTOMER_ID_PROPERTY, columnDefinition = "uuid")
    private UUID customerId;

    @Column(name = ModelConstants.DEVICE_TYPE_PROPERTY)
    private String type;

    @Column(name = ModelConstants.DEVICE_NAME_PROPERTY)
    private String name;

    @Column(name = ModelConstants.DEVICE_LABEL_PROPERTY)
    private String label;

    @Column(name = ModelConstants.SEARCH_TEXT_PROPERTY)
    private String searchText;

    @Type(type = "json")
    @Column(name = ModelConstants.DEVICE_ADDITIONAL_INFO_PROPERTY)
    protected JsonNode additionalInfo;

    @Column(name = ModelConstants.DEVICE_DEVICE_PROFILE_ID_PROPERTY, columnDefinition = "uuid")
    private UUID deviceProfileId;

    @Column(name = ModelConstants.DEVICE_FIRMWARE_ID_PROPERTY, columnDefinition = "uuid")
    private UUID firmwareId;

    @Column(name = ModelConstants.DEVICE_SOFTWARE_ID_PROPERTY, columnDefinition = "uuid")
    private UUID softwareId;

    @Type(type = "jsonb")
    @Column(name = ModelConstants.DEVICE_DEVICE_DATA_PROPERTY, columnDefinition = "jsonb")
    private JsonNode deviceData;

    @Column(name = ModelConstants.DEVICE_PRODUCTION_LINE_ID_PROPERTY, columnDefinition = "uuid")
    private UUID productionLineId;

    @Column(name = ModelConstants.DEVICE_DICT_DEVICE_ID_PROPERTY, columnDefinition = "uuid")
    private UUID dictDeviceId;

    @Column(name = ModelConstants.DEVICE_PICTURE_PROPERTY)
    private String picture;

    @Column(name = ModelConstants.DEVICE_ICON_PROPERTY)
    private String icon;

    @Column(name = "workshop_id")
    private UUID workshopId;

    /**
     * 工厂id
     * 2021-10-29 属于在原thingsboard 上额外添加的字段;
     */
    @Column(name = "factory_id")
    private UUID factoryId;

    @Column(name = "code")
    private String code;

    @Column(name = "comment")
    private String comment;

    @Column(name = "device_no")
    private String deviceNo;

    @Column(name = "created_time")
    private long createdTime;

    @Column(name = "created_user")
    private UUID createdUser;

    @Column(name = "updated_time")
    private long updatedTime;

    @Column(name = "updated_user")
    private UUID updatedUser;

    @Column(name = "flg")
    private Boolean deviceFlg = false;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "rename")
    private String rename;

    public AbstractDeviceEntity() {
        super();
    }

    public AbstractDeviceEntity(Device device) {
        if (device.getId() != null) {
            this.setUuid(device.getUuidId());
            this.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
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
        this.picture = device.getPicture();
        this.icon = device.getIcon();
        this.dictDeviceId = device.getDictDeviceId();
        this.comment = device.getComment();
        this.deviceNo = device.getDeviceNo();
        this.deviceFlg = device.getDeviceFlg();
        if (device.getSort() == null) {
            this.sort = 0;
        } else {
            this.sort = device.getSort();
        }
        this.rename = device.getRename();
    }

    public AbstractDeviceEntity(DeviceEntity deviceEntity) {
        if (deviceEntity.getId() != null) {
            this.setId(deviceEntity.getId());
            this.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
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
        this.picture = deviceEntity.getPicture();
        this.icon = deviceEntity.getIcon();
        this.dictDeviceId = deviceEntity.getDictDeviceId();
        this.comment = deviceEntity.getComment();
        this.deviceNo = deviceEntity.getDeviceNo();
        this.deviceFlg = deviceEntity.getDeviceFlg();
        this.sort = deviceEntity.getSort();
        this.rename = deviceEntity.getRename();
    }

    @Override
    public String getSearchTextSource() {
        return name;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    protected Device toDevice() {
        Device device = new Device(new DeviceId(getUuid()));
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
        device.setPicture(picture);
        device.setIcon(icon);
        device.setCode(code);
        device.setDictDeviceId(dictDeviceId);
        device.setComment(this.comment);
        device.setDeviceNo(this.deviceNo);
        device.setDeviceFlg(this.deviceFlg);
        device.setSort(this.sort);
        device.setRename(this.rename);
        return device;
    }

}
