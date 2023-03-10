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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonBinaryType;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Table(name = ModelConstants.DEVICE_COLUMN_FAMILY_NAME)
public class DeviceEntity extends AbstractDeviceEntity<Device> {


    public DeviceEntity() {
        super();
    }

    public DeviceEntity(Device device) {
        super(device);
    }

    public DeviceEntity(Factory factory) {
        this.setName(factory.getWorkshopName());
        this.setTenantId(factory.getTenantId());
    }

    public DeviceEntity(UUID id, String name) {
        super();
        this.id = id;
        this.setName(name);
    }

    public DeviceEntity(UUID id, UUID tenantId) {
        super();
        this.id = id;
        this.setTenantId(tenantId);
    }

    public DeviceEntity(UUID id, String name, String rename) {
        super();
        this.id = id;
        this.setName(name);
        this.setRename(rename);
    }

    public DeviceEntity(UUID id, String name, String rename, UUID factoryId, UUID workshopId, UUID productionLineId, Object additionalInfo) throws JsonProcessingException {
        super();
        this.id = id;
        this.setName(name);
        this.setRename(rename);
        this.setFactoryId(factoryId);
        this.setWorkshopId(workshopId);
        this.setProductionLineId(productionLineId);
        try {
            this.setAdditionalInfo(new ObjectMapper().readValue(additionalInfo.toString(), JsonNode.class));
        } catch (Exception ignore) {
        }
    }

    public DeviceEntity(UUID id, String name, String rename, UUID factoryId, UUID workshopId, UUID productionLineId, Object additionalInfo, Integer sort) throws JsonProcessingException {
        super();
        this.id = id;
        this.setName(name);
        this.setRename(rename);
        this.setFactoryId(factoryId);
        this.setWorkshopId(workshopId);
        this.setProductionLineId(productionLineId);
        this.setSort(sort);
        try {
            this.setAdditionalInfo(new ObjectMapper().readValue(additionalInfo.toString(), JsonNode.class));
        } catch (Exception ignore) {
        }
    }

    public DeviceEntity(UUID tenantId) {
        this.setTenantId(tenantId);
    }

    @Override
    public Device toData() {
        return super.toDevice();
    }


}
