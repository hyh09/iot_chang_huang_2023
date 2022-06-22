package org.thingsboard.server.dao.sql.role.entity.month;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.sql.role.entity.hour.EffectHourEntity;
import org.thingsboard.server.dao.util.sql.entity.AbstractStatisticalDataEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Project Name: thingsboard
 * File Name: EffectMonthEntity
 * Package Name: org.thingsboard.server.dao.sql.role.entity.month
 * Date: 2022/6/16 13:30
 * author: wb04
 * 业务中文描述: 月维度-查询天的表
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = ModelConstants.DAY_TABLE_NAME)
//@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = ModelConstants.MONTH_TABLE_NAME_MAP,
                classes = {
                        @ConstructorResult(
                                targetClass = EffectMonthEntity.class,
                                columns = {
                                        @ColumnResult(name = ModelConstants.HOUR_TIME, type = String.class),
                                        @ColumnResult(name = ModelConstants.ABSTRACT_WATER_ADDED_VALUE_COLUMN, type = String.class),
                                        @ColumnResult(name = ModelConstants.ABSTRACT_ELECTRIC_ADDED_VALUE_COLUMN, type = String.class),
                                        @ColumnResult(name = ModelConstants.ABSTRACT_GAS_ADDED_VALUE_COLUMN, type = String.class),
                                }
                        ),
                }

        ),


})
@ToString
public class EffectMonthEntity extends AbstractStatisticalDataEntity implements Serializable {
    @Transient
    @Column(name=ModelConstants.HOUR_TIME)
    private  String monthTime;

    public EffectMonthEntity(String monthTime, String  waterAddedValue,String electricAddedValue,String gasAddedValue) {
        this.monthTime=monthTime;
        this.waterAddedValue = waterAddedValue;
        this.electricAddedValue=electricAddedValue;
        this.gasAddedValue =gasAddedValue;
    }

    public EffectMonthEntity() {
    }

}
