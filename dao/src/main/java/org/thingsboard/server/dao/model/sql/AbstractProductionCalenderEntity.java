package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractProductionCalenderEntity <T extends ProductionCalender> extends BaseSqlEntity<T> {

    @Column(name = "id")
    public UUID id;
    @Column(name = "device_id")
    public UUID deviceId;
    @Column(name = "device_name")
    public String deviceName;
    @Column(name = "factory_id")
    public UUID factoryId;
    @Column(name = "factory_name")
    public String factoryName;
    @Column(name = "start_time")
    public Long startTime;
    @Column(name = "end_time")
    public Long endTime;

    @Column(name = "tenant_id")
    public UUID tenantId;

    @Column(name = "created_time")
    public long createdTime;

    @Column(name = "created_user")
    public UUID createdUser;

    @Column(name = "updated_time")
    public long updatedTime;

    @Column(name = "updated_user")
    public UUID updatedUser;

    public AbstractProductionCalenderEntity(){}
    public AbstractProductionCalenderEntity(ProductionCalender productionCalender){
        if(productionCalender != null){
            if(productionCalender.getId() != null){
                this.id = productionCalender.getId();
            }
            this.deviceId = productionCalender.getDeviceId();
            this.deviceName = productionCalender.getDeviceName();
            this.factoryId = productionCalender.getFactoryId();
            this.factoryName = productionCalender.getFactoryName();
            this.startTime = productionCalender.getStartTime();
            this.endTime = productionCalender.getEndTime();
            this.tenantId = productionCalender.getTenantId();
            this.createdUser = productionCalender.getCreatedUser();
            this.createdTime = productionCalender.getCreatedTime();
            this.updatedTime = productionCalender.getUpdatedTime();
            this.updatedUser = productionCalender.getUpdatedUser();
        }
    }
    public AbstractProductionCalenderEntity(AbstractProductionCalenderEntity<T> productionCalender){
        if(productionCalender != null){
            if(productionCalender.getId() != null){
                this.id = productionCalender.getId();
            }
            this.deviceId = productionCalender.getDeviceId();
            this.deviceName = productionCalender.getDeviceName();
            this.factoryId = productionCalender.getFactoryId();
            this.factoryName = productionCalender.getFactoryName();
            this.startTime = productionCalender.getStartTime();
            this.endTime = productionCalender.getEndTime();
            this.tenantId = productionCalender.getTenantId();
            this.createdUser = productionCalender.getCreatedUser();
            this.createdTime = productionCalender.getCreatedTime();
            this.updatedTime = productionCalender.getUpdatedTime();
            this.updatedUser = productionCalender.getUpdatedUser();
        }
    }
    public ProductionCalender toProductionCalender(){
        ProductionCalender productionCalender = new ProductionCalender();
        if(productionCalender != null){
            if(productionCalender.getId() != null){
                this.id = productionCalender.getId();
            }
            productionCalender.setDeviceId(this.deviceId);
            productionCalender.setDeviceName(this.deviceName);
            productionCalender.setFactoryId(this.factoryId);
            productionCalender.setFactoryName(this.factoryName);
            productionCalender.setStartTime(this.startTime);
            productionCalender.setEndTime(this.endTime);
            productionCalender.setTenantId(this.tenantId);
            productionCalender.setCreatedUser(this.createdUser);
            productionCalender.setCreatedTime(this.createdTime);
            productionCalender.setUpdatedTime(this.updatedTime);
            productionCalender.setUpdatedUser(this.updatedUser);
        }
        return productionCalender;
    }
}
