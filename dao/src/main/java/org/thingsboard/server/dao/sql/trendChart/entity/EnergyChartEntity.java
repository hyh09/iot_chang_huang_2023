package org.thingsboard.server.dao.sql.trendChart.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.thingsboard.server.dao.util.sql.entity.AbstractStatisticalDataEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
	
/**	
  创建时间: 2021-12-27 13:29:55	
  创建人: HU.YUNHUI	
  对应的数据库表: ENERGY_CHART	
   描述: 【当天的产能能耗的增量数据和当天历史数据】 对应的实体	
*/	
@Data	
@Entity	
@Table(name="TB_ENERGY_CHART")
@DynamicInsert	
@DynamicUpdate	
//@Proxy(lazy = false)	
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})	
public class EnergyChartEntity  extends AbstractStatisticalDataEntity {
	
	


 }	
