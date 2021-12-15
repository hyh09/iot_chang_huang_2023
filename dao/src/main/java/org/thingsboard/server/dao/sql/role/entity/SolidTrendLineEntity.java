package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.dao.model.sql.AbstractTsKvEntity;
import org.thingsboard.server.dao.model.sqlts.timescale.ts.TimescaleTsKvCompositeKey;

import javax.persistence.*;

/**
 * @program: thingsboard
 * @description: 看板的趋势图-实线
 * @author: HU.YUNHUI
 * @create: 2021-12-15 13:42
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ts_kv")
@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "solidTrendLineEntityMap",
                classes = {
                        @ConstructorResult(
                                targetClass = SolidTrendLineEntity.class,
                                columns = {
                                        @ColumnResult(name = "days",type = String.class),
                                        @ColumnResult(name = "sumValue",type = String.class),
                                        @ColumnResult(name = "time01",type = Long.class),
                                        @ColumnResult(name = "time02",type = String.class)

                                }
                        ),
                }

        )

})
public class SolidTrendLineEntity extends AbstractTsKvEntity {


    /**
     * 当前的时间 运算的时间  天的维度
     *
     */
    @Transient
    private  String days;

    /**
     * 当天的总值
     */
    @Transient
    private  String sumValue;

    /**
     * 当前的时间
     */
    private  Long  time01;

    /**
     * 格式话的时间， 用于后端接口测试观察
     */
    private  String time02;


    public SolidTrendLineEntity(String days, String sumValue, Long time01, String time02) {
        this.days = days;
        this.sumValue = sumValue;
        this.time01 = time01;
        this.time02 = time02;
    }

    /**
     * 本类无使用意义的
     * @return
     */
    @Override
    public boolean isNotEmpty() {
        return false;
    }
}
