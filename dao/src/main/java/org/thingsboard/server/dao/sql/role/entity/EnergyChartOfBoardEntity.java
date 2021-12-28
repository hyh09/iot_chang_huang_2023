package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.dao.util.sql.entity.AbstractStatisticalDataEntity;

import javax.persistence.*;
import java.time.LocalDate;
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
@Table(name = "TB_ENERGY_CHART")
//@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "EnergyChartOfBoardEntityMap",
                classes = {
                        @ConstructorResult(
                                targetClass = EnergyChartOfBoardEntity.class,
                                columns = {
                                        @ColumnResult(name = "date", type = LocalDate.class),
                                        @ColumnResult(name = "increment_capacity",type = String.class),
                                        @ColumnResult(name = "history_capacity",type = String.class),

                                        @ColumnResult(name = "increment_electric",type = String.class),
                                        @ColumnResult(name = "history_electric",type = String.class),

                                        @ColumnResult(name = "increment_gas",type = String.class),
                                        @ColumnResult(name = "history_gas",type = String.class),

                                        @ColumnResult(name = "increment_water",type = String.class),
                                        @ColumnResult(name = "history_water",type = String.class),



                                }
                        ),
                }

        )


})
@ToString
public class EnergyChartOfBoardEntity extends AbstractStatisticalDataEntity {






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


}
