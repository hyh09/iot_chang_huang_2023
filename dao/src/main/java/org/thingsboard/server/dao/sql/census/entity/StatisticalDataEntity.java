package org.thingsboard.server.dao.sql.census.entity;	
	
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**	
  创建时间: 2021-12-21 11:26:27	
  创建人: HU.YUNHUI	
  对应的数据库表: STATISTICAL_DATA	
   描述: 【当天的产能能耗的增量数据和当天历史数据】 对应的实体	
*/	
@Data	
@Entity	
@Table(name="STATISTICAL_DATA")	
@DynamicInsert	
@DynamicUpdate	
//@Proxy(lazy = false)	
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})	
public class StatisticalDataEntity  extends TenantBaseEntity {	
	
	
	
    /**	
     *中文描述: 设备id	
     */	
    @Column(name="entity_id")	
    private UUID entityId;
    /**	
     *中文描述: 遥测的时间ts	
     */	
    @Column(name="ts")	
    private long ts;
    /**	
     *中文描述: 水的增量值	
     */	
    @Column(name="water_added_value")	
    private String waterAddedValue;


    @Column(name="water_first_value")
    private  String waterFirstValue;
    /**	
     *中文描述: 水的当前值(可以用于天的历史数据)	
     */	
    @Column(name="water_value")	
    private String waterValue;


    /**	
     *中文描述: 电的增量值	
     */	
    @Column(name="electric_added_value")	
    private String electricAddedValue;

    @Column(name="electric_first_value")
    private  String electricFirstValue;
    /**	
     *中文描述: 电的当前值	
     */	
    @Column(name="electric_value")	
    private String electricValue;



    /**	
     *中文描述: 气的增量值	
     */	
    @Column(name="gas_added_value")	
    private String gasAddedValue;


    @Column(name="gas_first_value")
    private  String gasFirstValue;
    /**	
     *中文描述: 气的当前值	
     */	
    @Column(name="gas_value")	
    private String gasValue;	
    /**	
     *中文描述: 产能的增量值	
     */	
    @Column(name="capacity_added_value")	
    private String capacityAddedValue;

    @Column(name="capacity_first_value")
    private  String capacityFirstValue;
    /**	
     *中文描述: 产能的当前值	
     */	
    @Column(name="capacity_value")	
    private String capacityValue;

    /**
     * 当前的天
     */
    @Column(name="date")
    private LocalDate date;

 }	
