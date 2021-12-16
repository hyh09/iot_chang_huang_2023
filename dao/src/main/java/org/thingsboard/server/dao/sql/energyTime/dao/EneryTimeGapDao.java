package org.thingsboard.server.dao.sql.energyTime.dao;	
	
import org.thingsboard.server.dao.util.sql.BaseSqlDao;	
import org.thingsboard.server.dao.sql.energyTime.entity.EneryTimeGapEntity;	
import org.springframework.data.jpa.repository.Modifying;	
import org.springframework.data.jpa.repository.Query;	
import org.springframework.data.repository.query.Param;	
	
import javax.transaction.Transactional;	
import java.util.Collection;	
import java.util.List;	
import java.util.UUID;	
	
	
/**	
  创建时间: 2021-12-16 11:17:13	
  创建人: HU.YUNHUI	
  描述: 【能耗超过30分钟的时间的遥测时间差保存】  dao层接口	
	
*/	
public interface EneryTimeGapDao extends BaseSqlDao<EneryTimeGapEntity,UUID> {	
	
	
	
	
}	
