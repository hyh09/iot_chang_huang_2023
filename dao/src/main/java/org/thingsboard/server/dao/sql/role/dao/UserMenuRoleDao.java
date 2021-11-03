package org.thingsboard.server.dao.sql.role.dao;
	
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
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

     @Modifying
     @Transactional
     @Query("delete from UserMenuRoleEntity where userId=:userId ")
     void deleteByUserId( @Param("userId") UUID userId);

     @Modifying
     @Transactional
     @Query("delete from UserMenuRoleEntity where tenantSysRoleId=:tenantSysRoleId ")
     void deleteByTenantSysRoleId(@Param("tenantSysRoleId") UUID tenantSysRoleId);


     @Query("select rm  from UserMenuRoleEntity  rm where rm.userId=:userId ")
     List<UserMenuRoleEntity> queryRoleIdByUserId(@Param("userId") UUID userId);

     @Modifying
     @Transactional
     @Query("delete from UserMenuRoleEntity s where s.userId in (?1) and s.tenantSysRoleId = ?2 ")
     void deleteBatch(List<UUID> ids,UUID tenantSysRoleId);

}
