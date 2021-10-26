package org.thingsboard.server.dao.sql.role.dao;	
	
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.dao.util.sql.BaseSqlDao;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;

import java.util.List;
import java.util.UUID;
	
	
/**	
  创建时间: 2021-10-25 17:39:26	
  创建人: HU.YUNHUI	
  描述: 【用户角色关系数据】  dao层接口	
	
*/	
public interface UserMenuRoleDao extends BaseSqlDao<UserMenuRoleEntity,UUID> {	
	

     List<UserMenuRoleEntity> queryAllByUserIdAndTenantSysRoleId(UUID userId,UUID tenantSysRoleId);
	
	
}	
