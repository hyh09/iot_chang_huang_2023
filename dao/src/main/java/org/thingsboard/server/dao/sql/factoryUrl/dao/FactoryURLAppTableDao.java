package org.thingsboard.server.dao.sql.factoryUrl.dao;	
	
import org.thingsboard.server.dao.sql.factoryUrl.entity.FactoryURLAppTableEntity;
import org.thingsboard.server.dao.util.sql.BaseSqlDao;

import java.util.UUID;	
	
	
/**	
  创建时间: 2022-01-20 12:28:03	
  创建人: HU.YUNHUI	
  描述: 【当天的产能能耗的增量数据和当天历史数据】  dao层接口	
	
*/	
public interface FactoryURLAppTableDao extends BaseSqlDao<FactoryURLAppTableEntity,UUID> {	
	
	
	FactoryURLAppTableEntity  queryAllByAppUrl(String appUrl);
	
}	
