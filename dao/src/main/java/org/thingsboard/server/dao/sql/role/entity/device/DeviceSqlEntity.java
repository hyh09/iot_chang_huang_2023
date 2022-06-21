package org.thingsboard.server.dao.sql.role.entity.device;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.sql.AbstractDeviceEntity;
import org.thingsboard.server.dao.sql.role.entity.month.EffectMonthEntity;

import javax.persistence.*;
import java.util.UUID;

/**
 * Project Name: thingsboard
 * File Name: DeviceSqlEntity
 * Package Name: org.thingsboard.server.dao.sql.role.entity.device
 * Date: 2022/6/21 11:15
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = ModelConstants.DEVICE_COLUMN_FAMILY_NAME)
//@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = ModelConstants.DEVICE_COLUMN_FAMILY_NAME+"map1",
                classes = {
                        @ConstructorResult(
                                targetClass = DeviceSqlEntity.class,
                                columns = {
                                        @ColumnResult(name = ModelConstants.ID_PROPERTY, type = UUID.class),

                                }
                        ),
                }

        ),


})
@ToString
public class DeviceSqlEntity extends AbstractDeviceEntity {


    public DeviceSqlEntity(UUID   id) {
        super.id =id;
    }

    @Override
    public Device toData() {
        return super.toDevice();
    }
}
