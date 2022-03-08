package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.PRODUCTION_CALENDAR_COLUMN_FAMILY_NAME)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "productionCalendarEntity_01",
                classes = {
                        @ConstructorResult(
                                targetClass = ProductionCalenderEntity.class,
                                columns = {
                                      /*  @ColumnResult(name = "id", type = UUID.class),*/
                                        @ColumnResult(name = "device_id", type = UUID.class),
                                        @ColumnResult(name = "device_name",type = String.class),
                                       /* @ColumnResult(name = "factory_id",type = UUID.class),*/
                                        @ColumnResult(name = "factory_name",type = String.class),
                                        @ColumnResult(name = "start_time",type = Long.class),
                                        @ColumnResult(name = "end_time",type = Long.class),
                                        /*@ColumnResult(name = "tenant_id",type = UUID.class),
                                        @ColumnResult(name = "created_time",type = Long.class),
                                        @ColumnResult(name = "created_user",type = UUID.class),
                                        @ColumnResult(name = "updated_time",type = Long.class),
                                        @ColumnResult(name = "updated_user",type = UUID.class)*/
                                }
                        ),
                }

        )
})
public class ProductionCalenderEntity extends AbstractProductionCalenderEntity<ProductionCalender> {

    public ProductionCalenderEntity() {
        super();
    }

    public ProductionCalenderEntity(ProductionCalender productionCalender) {
        super(productionCalender);
    }

    @Override
    public ProductionCalender toData() {
        return super.toProductionCalender();
    }


    public ProductionCalenderEntity(UUID deviceId, String deviceName,String factoryName, Long startTime, Long endTime) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.factoryName = factoryName;
        this.startTime = startTime;
        this.endTime = endTime;
    }
/*

    public ProductionCalenderEntity(UUID id, UUID deviceId, String deviceName, UUID factoryId, String factoryName, long startTime, long endTime, UUID tenantId, long createdTime, UUID createdUser, long updatedTime, UUID updatedUser) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.factoryId = factoryId;
        this.factoryName = factoryName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tenantId = tenantId;
        this.createdTime = createdTime;
        this.createdUser = createdUser;
        this.updatedTime = updatedTime;
        this.updatedUser = updatedUser;
    }
*/


}
