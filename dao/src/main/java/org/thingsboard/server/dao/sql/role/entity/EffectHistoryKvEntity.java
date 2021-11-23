package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.dao.model.sql.AbstractTsKvEntity;
import org.thingsboard.server.dao.model.sqlts.timescale.ts.TimescaleTsKvCompositeKey;

import javax.persistence.*;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: PC能耗统计历史数据
 * @author: HU.YUNHUI
 * @create: 2021-11-18 13:46
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ts_kv")
@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "EffectHistoryKvEntityMap",
                classes = {
                        @ConstructorResult(
                                targetClass = EffectHistoryKvEntity.class,
                                columns = {


                                }
                        ),
                }

        )

})
@ToString
public class EffectHistoryKvEntity extends AbstractTsKvEntity {





    @Override
    public boolean isNotEmpty() {
        return false;
    }
}
