package org.thingsboard.server.dao.sql.role.dao;	
	
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.util.sql.BaseSqlDao;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;	
	
	
/**	
  创建时间: 2021-10-22 18:05:08	
  创建人: HU.YUNHUI	
  描述: 【租户系统角色】 对应的dao层接口	
*/	
public interface TenantSysRoleDao extends BaseSqlDao<TenantSysRoleEntity,UUID> {


    @Query("SELECT a FROM TenantSysRoleEntity a ,UserMenuRoleEntity b  WHERE a.id = b.tenantSysRoleId AND b.userId = :userId ")
    List<TenantSysRoleEntity> queryByUserId(@Param("userId") UUID userId);


    @Query("select d.roleCode  from TenantSysRoleEntity d where  d.tenantId =:tenantId ")
    List<String> findAllCodesByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT a FROM TenantSysRoleEntity a  where  a.roleCode=:roleCode and a.tenantId = :tenantId ")
    TenantSysRoleEntity  queryAllByRoleCode(@Param("roleCode") String roleCode,@Param("tenantId") UUID tenantId);

    @Query("SELECT a FROM TenantSysRoleEntity a  where  a.roleCode=:roleCode and a.tenantId = :tenantId and a.factoryId=:factoryId ")
    TenantSysRoleEntity  queryAllByFactoryId(@Param("roleCode") String roleCode,@Param("tenantId") UUID tenantId,@Param("factoryId") UUID factoryId);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update TenantSysRoleEntity  d set d.operationType= :operationType where d.id= :roleId")
    int updateOperationType(@Param("roleId") UUID  userId,@Param("operationType") Integer  operationType);
	
	
}	
