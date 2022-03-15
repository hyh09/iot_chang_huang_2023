package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.common.data.deviceoeeeveryhour.DeviceOeeEveryHour;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractDeviceOeeEveryHourEntity<T extends DeviceOeeEveryHour> extends BaseSqlEntity<T> {

    @Column(name = "id")
    public UUID id;
    @Column(name = "device_id")
    public UUID deviceId;
    @Column(name = "ts")
    public Long ts;
    @Column(name = "oee_value")
    public BigDecimal oeeValue;
    @Column(name = "factory_id")
    public UUID factoryId;
    @Column(name = "workshop_id")
    public UUID workshopId;
    @Column(name = "production_line_id")
    public UUID productionLineId;
    @Column(name = "tenant_id")
    public UUID tenantId;
    @Column(name = "created_time")
    public long createdTime;


    public AbstractDeviceOeeEveryHourEntity() {
    }

    public AbstractDeviceOeeEveryHourEntity(DeviceOeeEveryHour deviceOeeEveryHour) {
        if (deviceOeeEveryHour != null) {
            if (deviceOeeEveryHour.getId() != null) {
                this.id = deviceOeeEveryHour.getId();
            }
            this.deviceId = deviceOeeEveryHour.getDeviceId();
            this.ts = deviceOeeEveryHour.getTs();
            this.oeeValue = deviceOeeEveryHour.getOeeValue();
            this.factoryId = deviceOeeEveryHour.getFactoryId();
            this.workshopId = deviceOeeEveryHour.getWorkshopId();
            this.productionLineId = deviceOeeEveryHour.getProductionLineId();
            this.tenantId = deviceOeeEveryHour.getTenantId();
            this.createdTime = deviceOeeEveryHour.getCreatedTime();
        }
    }

    public AbstractDeviceOeeEveryHourEntity(AbstractDeviceOeeEveryHourEntity abstractDeviceOeeEveryHourEntity) {
        if (abstractDeviceOeeEveryHourEntity != null) {
            if (abstractDeviceOeeEveryHourEntity.getId() != null) {
                this.id = abstractDeviceOeeEveryHourEntity.getId();
            }
            this.deviceId = abstractDeviceOeeEveryHourEntity.getDeviceId();
            this.ts = abstractDeviceOeeEveryHourEntity.getTs();
            this.oeeValue = abstractDeviceOeeEveryHourEntity.getOeeValue();
            this.factoryId = abstractDeviceOeeEveryHourEntity.getFactoryId();
            this.workshopId = abstractDeviceOeeEveryHourEntity.getWorkshopId();
            this.productionLineId = abstractDeviceOeeEveryHourEntity.getProductionLineId();
            this.tenantId = abstractDeviceOeeEveryHourEntity.getTenantId();
            this.createdTime = abstractDeviceOeeEveryHourEntity.getCreatedTime();
        }
    }

    public DeviceOeeEveryHour toDeviceOeeEveryHour() {
        DeviceOeeEveryHour deviceOeeEveryHour = new DeviceOeeEveryHour();
        if (deviceOeeEveryHour != null) {
            deviceOeeEveryHour.setId(this.id);
            deviceOeeEveryHour.setDeviceId(this.deviceId);
            deviceOeeEveryHour.setTs(this.ts);
            deviceOeeEveryHour.setOeeValue(this.oeeValue);
            deviceOeeEveryHour.setFactoryId(this.factoryId);
            deviceOeeEveryHour.setWorkshopId(this.workshopId);
            deviceOeeEveryHour.setProductionLineId(this.productionLineId);
            deviceOeeEveryHour.setTenantId(this.tenantId);
            deviceOeeEveryHour.setCreatedTime(this.createdTime);
        }
        return deviceOeeEveryHour;
    }


}
