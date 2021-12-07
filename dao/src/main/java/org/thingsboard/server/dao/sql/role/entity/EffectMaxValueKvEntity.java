package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.dao.model.sql.AbstractTsKvEntity;
import org.thingsboard.server.dao.model.sqlts.timescale.ts.TimescaleTsKvCompositeKey;

import javax.persistence.*;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 最大的值，用于历史产能的，历史能耗的统计
 * @author: HU.YUNHUI
 * @create: 2021-12-07 11:17
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ts_kv")
@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "effectMaxValueKvEntityMap",
                classes = {
                        @ConstructorResult(
                                targetClass = EffectMaxValueKvEntity.class,
                                columns = {
                                        @ColumnResult(name = "maxValue",type = String.class)

                                }
                        ),
                }

        )

})
public class EffectMaxValueKvEntity  extends AbstractTsKvEntity {

    /**
     *
     */
    @Transient
    private  String  deviceId;
    /**
     *
     */
    @Transient
    private  String maxValue;


    public EffectMaxValueKvEntity(String maxValue) {
        this.maxValue = maxValue;
    }

    public EffectMaxValueKvEntity(String deviceId, String maxValue) {
        this.deviceId = deviceId;
        this.maxValue = maxValue;
    }




    @Override
    public boolean isNotEmpty() {
        return false;
    }



}
