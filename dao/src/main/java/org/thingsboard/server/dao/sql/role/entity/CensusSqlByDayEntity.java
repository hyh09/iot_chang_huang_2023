package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.common.data.vo.home.ResultHomeCapAppVo;
import org.thingsboard.server.dao.util.sql.entity.AbstractStatisticalDataEntity;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @program: thingsboard
 * @description: 按天统计（只返回sum的数据)
 * @author: HU.YUNHUI
 * @create: 2021-12-24 10:21
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "TB_STATISTICAL_DATA")
//@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "censusSqlByDayEntity_01",
                classes = {
                        @ConstructorResult(
                                targetClass = CensusSqlByDayEntity.class,
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
//extends StatisticalDataEntity
public class CensusSqlByDayEntity  extends AbstractStatisticalDataEntity {

    @Transient
    @Column(name = "increment_capacity")
    private  String incrementCapacity;


    @Transient
    @Column(name ="history_capacity")
    private  String historyCapacity;


    @Transient
    @Column(name ="increment_electric")
    private  String incrementElectric;

    @Transient
    @Column(name ="history_electric")
    private  String historyElectric;


    @Transient
    @Column(name ="increment_gas")
    private  String incrementGas;

    @Transient
    @Column(name ="history_gas")
    private  String historyGas;

    @Transient
    @Column(name ="increment_water")
    private  String incrementWater;

    @Transient
    @Column(name ="history_water")
    private  String historyWater;


    public CensusSqlByDayEntity(
            LocalDate date,
            String incrementCapacity, String historyCapacity, String incrementElectric,
            String historyElectric, String incrementGas, String historyGas,
            String incrementWater, String historyWater) {

        this.date = date;
        this.incrementCapacity = incrementCapacity;
        this.historyCapacity = historyCapacity;
        this.incrementElectric = incrementElectric;
        this.historyElectric = historyElectric;
        this.incrementGas = incrementGas;
        this.historyGas = historyGas;
        this.incrementWater = incrementWater;
        this.historyWater = historyWater;
    }



}
