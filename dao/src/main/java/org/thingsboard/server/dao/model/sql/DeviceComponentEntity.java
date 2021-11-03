package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.devicecomponent.DeviceComponent;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 设备字部件实体类
 * @param <T>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.DEVICE_COMPONENT_COLUMN_FAMILY_NAME)
public class DeviceComponentEntity<T> extends AbstractDeviceComponentEntity<DeviceComponent> {
    public DeviceComponentEntity(){super();}
    public DeviceComponentEntity(DeviceComponent deviceComponent){super(deviceComponent);}
}
