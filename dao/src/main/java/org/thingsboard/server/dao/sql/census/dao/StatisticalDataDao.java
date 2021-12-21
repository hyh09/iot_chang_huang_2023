package org.thingsboard.server.dao.sql.census.dao;	
	
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.sql.census.entity.StatisticalDataEntity;
import org.thingsboard.server.dao.util.sql.BaseSqlDao;

import java.time.LocalDate;
import java.util.UUID;
	
	
/**	
  创建时间: 2021-12-21 11:26:27	
  创建人: HU.YUNHUI	
  描述: 【当天的产能能耗的增量数据和当天历史数据】  dao层接口	
	
*/	
public interface StatisticalDataDao extends BaseSqlDao<StatisticalDataEntity,UUID> {


//    StatisticalDataEntity  queryAllByEntityIdAndCreatedTime();
    @Query(value = "select t  from  StatisticalDataEntity t  where t.entityId= :entityId AND t.date = :date")
    StatisticalDataEntity  queryAllByEntityIdAndDate(@Param("entityId") UUID entityId, @Param("date") LocalDate date);
	
	
}	
