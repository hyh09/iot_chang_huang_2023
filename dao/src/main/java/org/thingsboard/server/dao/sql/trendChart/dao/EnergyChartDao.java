package org.thingsboard.server.dao.sql.trendChart.dao;	
	
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.sql.trendChart.entity.EnergyChartEntity;
import org.thingsboard.server.dao.util.sql.BaseSqlDao;

import java.util.UUID;
	
	
/**	
  创建时间: 2021-12-27 13:29:55	
  创建人: HU.YUNHUI	
  描述: 【当天的产能能耗的增量数据和当天历史数据】  dao层接口	
	
*/	
public interface EnergyChartDao extends BaseSqlDao<EnergyChartEntity,UUID> {

    @Query(value = "select t  from  EnergyChartEntity t  where t.entityId= :entityId AND t.ts = :ts")
    EnergyChartEntity queryAllByEntityIdAndDate(@Param("entityId") UUID entityId, @Param("ts") Long ts);
	
	
}	
