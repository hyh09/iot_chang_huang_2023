package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.dao.util.sql.entity.AbstractStatisticalDataEntity;

import javax.persistence.*;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 新的返回实体
 * @author: HU.YUNHUI
 * @create: 2021-12-22 10:28
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hs_statistical_data")
//@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "energyEffciencyNewEntity_01",
                classes = {
                        @ConstructorResult(
                                targetClass = EnergyEffciencyNewEntity.class,
                                columns = {
                                        @ColumnResult(name = "entity_id", type = UUID.class),


                                        @ColumnResult(name = "dictDeviceId", type = UUID.class),
                                        @ColumnResult(name = "deviceName",type = String.class),
                                        @ColumnResult(name = "picture",type = String.class),
                                        @ColumnResult(name = "factoryId",type = UUID.class),
                                        @ColumnResult(name = "workshopId",type = UUID.class),
                                        @ColumnResult(name = "productionLineId",type = UUID.class),

                                        @ColumnResult(name = "capacity_added_value",type = String.class)
                                }
                        ),
                }

        ),
        @SqlResultSetMapping(
                name = "energyEffciencyNewEntity_02",
                classes = {
                        @ConstructorResult(
                                targetClass = EnergyEffciencyNewEntity.class,
                                columns = {
                                        @ColumnResult(name = "entity_id", type = UUID.class),


                                        @ColumnResult(name = "dictDeviceId", type = UUID.class),
                                        @ColumnResult(name = "deviceName",type = String.class),
                                        @ColumnResult(name = "picture",type = String.class),
                                        @ColumnResult(name = "factoryId",type = UUID.class),
                                        @ColumnResult(name = "workshopId",type = UUID.class),
                                        @ColumnResult(name = "productionLineId",type = UUID.class),

                                        //产能
                                        @ColumnResult(name = "capacity_added_value",type = String.class),

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
                        )
                }

        )

})
@ToString
//extends StatisticalDataEntity
public class EnergyEffciencyNewEntity extends AbstractStatisticalDataEntity {

    @Transient
    private UUID entityId;

    /**
     * 设备字典id
     */
    @Transient
    private  UUID dictDeviceId;


    /**
     * 设备的名称
     */
    @Transient
    private  String  deviceName;

    /**
     * 设备的图片
     */
    @Transient
    private  String  picture;

    /**
     * 工厂
     */
    @Transient
    private UUID factoryId;
    /**
     * 车间
     */
    @Transient
    private UUID workshopId;
    /**
     * 生产线
     */
    @Transient
    private UUID productionLineId;

    /**
     * 暂时没有返回的；
     */
    @Transient
    private  Boolean  flg=false;


    public EnergyEffciencyNewEntity(
                UUID entityId,UUID dictDeviceId, String deviceName, String picture, UUID factoryId, UUID workshopId, UUID productionLineId,
                String capacityAddedValue
       ) {
        this.entityId = entityId;
        this.dictDeviceId = dictDeviceId;
        this.deviceName = deviceName;
        this.picture = picture;
        this.factoryId = factoryId;
        this.workshopId = workshopId;
        this.productionLineId = productionLineId;

        this.capacityAddedValue =capacityAddedValue;
    }


    public EnergyEffciencyNewEntity(
            UUID entityId,UUID dictDeviceId, String deviceName, String picture, UUID factoryId, UUID workshopId, UUID productionLineId,
            String capacityAddedValue,
            String waterAddedValue,Long waterFirstTime,Long waterLastTime,
            String electricAddedValue,Long electricFirstTime,Long electricLastTime,
            String gasAddedValue,Long gasFirstTime,Long gasLastTime
    ) {
        this.entityId = entityId;
        this.dictDeviceId = dictDeviceId;
        this.deviceName = deviceName;
        this.picture = picture;
        this.factoryId = factoryId;
        this.workshopId = workshopId;
        this.productionLineId = productionLineId;

        this.capacityAddedValue =capacityAddedValue;

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
