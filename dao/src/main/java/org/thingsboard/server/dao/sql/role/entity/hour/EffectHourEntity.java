package org.thingsboard.server.dao.sql.role.entity.hour;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.dao.model.sql.AbstractTsKvEntity;
import org.thingsboard.server.dao.sql.role.entity.EnergyEffciencyNewEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.sql.entity.AbstractStatisticalDataEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Project Name: thingsboard
 * File Name: EffectHourEntity
 * Package Name: org.thingsboard.server.dao.sql.role.entity.hour
 * Date: 2022/6/15 18:17
 * author: wb04
 * 业务中文描述: 小时维度的趋势图
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = ModelConstants.HOUR_TABLE_NAME)
//@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = ModelConstants.HOUR_TABLE_NAME_MAP,
                classes = {
                        @ConstructorResult(
                                targetClass = EffectHourEntity.class,
                                columns = {
                                        @ColumnResult(name = ModelConstants.HOUR_TIME, type = Long.class),
                                        @ColumnResult(name = ModelConstants.ABSTRACT_WATER_ADDED_VALUE_COLUMN, type = String.class),
                                        @ColumnResult(name = ModelConstants.ABSTRACT_ELECTRIC_ADDED_VALUE_COLUMN, type = String.class),
                                        @ColumnResult(name = ModelConstants.ABSTRACT_GAS_ADDED_VALUE_COLUMN, type = String.class),
                                }
                        ),
                }

        ),


})
@ToString
public class EffectHourEntity extends AbstractStatisticalDataEntity implements Serializable {

    @Transient
    private Long tsTime;
    @Transient
    private String tsValue;


    public EffectHourEntity(Long tsTime, String  waterAddedValue,String electricAddedValue,String gasAddedValue) {
        this.tsTime = tsTime;
        this.waterAddedValue = waterAddedValue;
        this.electricAddedValue=electricAddedValue;
        this.gasAddedValue =gasAddedValue;
    }

    public EffectHourEntity() {
    }




    public Long toHourTime()
    {
        return CommonUtils.getConversionHours(this.getTsTime());
    }
}
