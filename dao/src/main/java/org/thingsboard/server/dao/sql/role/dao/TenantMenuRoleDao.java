package org.thingsboard.server.dao.sql.role.dao;	
	
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.util.sql.BaseSqlDao;
import org.thingsboard.server.dao.sql.role.entity.TenantMenuRoleEntity;	
import org.springframework.data.jpa.repository.Modifying;	
import org.springframework.data.jpa.repository.Query;	
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;	
import java.util.UUID;	
	
	
/**	
  创建时间: 2021-10-26 18:16:59	
  创建人: HU.YUNHUI	
  描述: 【角色-菜单 关系数据】  dao层接口	
	
*/	
public interface TenantMenuRoleDao extends BaseSqlDao<TenantMenuRoleEntity,UUID> {	
	


    @Modifying
    @Transactional
    @Query("delete from TenantMenuRoleEntity where tenantSysRoleId=:tenantSysRoleId ")
    void deleteByTenantSysRoleId(@Param("tenantSysRoleId") UUID tenantSysRoleId);


    @Query("select  t from TenantMenuRoleEntity t ,UserMenuRoleEntity b where t.tenantSysRoleId = b.tenantSysRoleId and b.userId =:userId")
    List<TenantMenuRoleEntity> queryMenuIdByRole(@Param("userId") UUID userId);


    @Modifying
    @Transactional
    @Query("delete from TenantMenuRoleEntity where tenantMenuId in (:MenuIds) ")
    void deleteByMenuIds(@Param("MenuIds") List<UUID> MenuIds);

}	
