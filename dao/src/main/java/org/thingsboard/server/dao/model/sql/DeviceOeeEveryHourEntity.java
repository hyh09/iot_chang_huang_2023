package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.deviceoeeeveryhour.DeviceOeeEveryHour;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.DEVICE_OEE_EVERY_HOUR_COLUMN_FAMILY_NAME)
public class DeviceOeeEveryHourEntity  extends AbstractDeviceOeeEveryHourEntity<DeviceOeeEveryHour> {
    @Override
    public DeviceOeeEveryHour toData() {
        return super.toDeviceOeeEveryHour();
    }


    public DeviceOeeEveryHourEntity() {
        super();
    }
    public DeviceOeeEveryHourEntity(DeviceOeeEveryHour deviceOeeEveryHour) {
        super(deviceOeeEveryHour);
    }

    public void setFactoryAndWorkshopAndProductionLine(UUID factoryId,UUID workshopId,UUID productionLineId){
        super.factoryId = factoryId;
        super.workshopId = workshopId;
        super.productionLineId = productionLineId;
    }
}
