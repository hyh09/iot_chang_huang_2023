package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.dao.util.sql.entity.AbstractStatisticalDataEntity;

import javax.persistence.*;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 看板 能耗趋势图
 * @author: HU.YUNHUI
 * @create: 2021-12-27 16:36
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hs_energy_chart")
//@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "energyChartOfBoardEntityMap",
                classes = {
                        @ConstructorResult(
                                targetClass = EnergyChartOfBoardEntity.class,
                                columns = {
                                        @ColumnResult(name = "entity_id", type = UUID.class),
                                        @ColumnResult(name = "ts", type = Long.class),


                                        //水
                                        @ColumnResult(name = "water_added_value",type = String.class),
                                        @ColumnResult(name = "water_first_time",type = Long.class),
                                        @ColumnResult(name = "water_last_time",type = Long.class),


                                        //电
                                        @ColumnResult(name = "electric_added_value",type = String.class),
                                        @ColumnResult(name = "electric_first_time",type = Long.class),
                                        @ColumnResult(name = "electric_last_time",type = Long.class),

                                        //气
                                        @ColumnResult(name = "gas_added_value",type = String.class),
                                        @ColumnResult(name = "gas_first_time",type = Long.class),
                                        @ColumnResult(name = "gas_last_time",type = Long.class),



                                }
                        ),
                }

        ),
        @SqlResultSetMapping(
                name = "getCapacityValueByDeviceIdAndInTime",
                classes = {
                        @ConstructorResult(
                                targetClass = EnergyChartOfBoardEntity.class,
                                columns = {
                                        @ColumnResult(name = "entity_id", type = UUID.class),
                                        @ColumnResult(name = "maxValue01",type = String.class),
                                        @ColumnResult(name = "minValue02",type = String.class)
                               }
                        ),
                }

        )


})
@ToString
public class EnergyChartOfBoardEntity extends AbstractStatisticalDataEntity {


    @Transient
    private  String  maxValue01;
    @Transient
    private  String  minValue02;


    public EnergyChartOfBoardEntity(UUID entityId, Long ts,
                                    String waterAddedValue, Long waterFirstTime, Long waterLastTime,
                                    String electricAddedValue, Long electricFirstTime, Long electricLastTime,
                                    String gasAddedValue, Long gasFirstTime, Long gasLastTime
                              ) {
        this.entityId = entityId;
        this.ts = ts;

        //水
        this.waterAddedValue = waterAddedValue;
        this.waterFirstTime =waterFirstTime;
        this.waterLastTime =waterLastTime;

        //电
        this.electricAddedValue = electricAddedValue;
        this.electricFirstTime =electricFirstTime;
        this.electricLastTime = electricLastTime;

        //气
        this.gasAddedValue = gasAddedValue;
        this.gasFirstTime =gasFirstTime;
        this.gasLastTime =gasLastTime;


    }


    public EnergyChartOfBoardEntity(UUID entityId,String maxValue01, String minValue02) {
        this.entityId = entityId;
        this.maxValue01 = maxValue01;
        this.minValue02 = minValue02;
    }
}
