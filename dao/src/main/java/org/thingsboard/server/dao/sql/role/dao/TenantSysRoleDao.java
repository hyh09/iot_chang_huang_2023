package org.thingsboard.server.dao.sql.role.dao;	
	
import org.thingsboard.server.dao.util.sql.BaseSqlDao;	
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;	
import org.springframework.data.jpa.repository.Modifying;	
import org.springframework.data.jpa.repository.Query;	
import org.springframework.data.repository.query.Param;	
	
import javax.transaction.Transactional;	
import java.util.Collection;	
import java.util.List;	
import java.util.UUID;	
	
	
/**	
  创建时间: 2021-10-22 18:05:08	
  创建人: HU.YUNHUI	
  描述: 【租户系统角色】 对应的dao层接口	
*/	
public interface TenantSysRoleDao extends BaseSqlDao<TenantSysRoleEntity,UUID> {	
	
	
	
	
}	
