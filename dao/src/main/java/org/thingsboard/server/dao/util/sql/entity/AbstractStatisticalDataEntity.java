package org.thingsboard.server.dao.util.sql.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-22 15:13
 **/
@Data
@MappedSuperclass
public class AbstractStatisticalDataEntity extends TenantBaseEntity{

    /**
     *中文描述: 设备id
     */
    @Column(name="entity_id")
    protected UUID entityId;
    /**
     *中文描述: 遥测的时间ts
     */
    @Column(name="ts")
    protected long ts;


    /**
     *中文描述: 水的增量值
     */
    @Column(name="water_added_value")
    protected String waterAddedValue;

    @Column(name="water_first_value")
    protected  String waterFirstValue;
    /**
     *中文描述: 水的当前值(可以用于天的历史数据)
     */
    @Column(name="water_value")
    protected String waterValue;

    @Column(name="water_first_time")
    protected  Long  waterFirstTime;

    @Column(name="water_last_time")
    protected  Long waterLastTime;


        /**
         *中文描述: 电的增量值
         */
        @Column(name="electric_added_value")
        protected String electricAddedValue;

        @Column(name="electric_first_value")
        protected  String electricFirstValue;

        @Column(name="electric_value")
        protected String electricValue; //当前值

       @Column(name="electric_first_time")
       protected  Long electricFirstTime;

       @Column(name="electric_last_time")
       protected  Long electricLastTime;




    /**
     *中文描述: 气的增量值
     */
    @Column(name="gas_added_value")
    protected String gasAddedValue;

    @Column(name="gas_first_value")
    protected  String gasFirstValue;

    @Column(name="gas_value")
    protected String gasValue;

    @Column(name="gas_first_time")
    protected  Long gasFirstTime;
    @Column(name="gas_last_time")
    protected  Long gasLastTime;


    /**
     *中文描述: 产能的增量值
     */
    @Column(name="capacity_added_value")
    protected String capacityAddedValue="0";

    @Column(name="capacity_first_value")
    protected  String capacityFirstValue;

    @Column(name="capacity_value")
    protected String capacityValue;

    @Column(name="capacity_first_time")
    protected  Long capacityFirstTime;
    @Column(name="capacity_last_time")
    protected  Long capacityLastTime;




    /**
     * 当前的天
     */
    @Column(name="date")
    protected LocalDate date;

}
