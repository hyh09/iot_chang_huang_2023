package org.thingsboard.server.dao.sql.role.service;	
	
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;	
import org.thingsboard.server.common.data.exception.ThingsboardException;	
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;	
import org.thingsboard.server.dao.sql.role.dao.TenantMenuRoleDao;	
import org.thingsboard.server.dao.sql.role.entity.TenantMenuRoleEntity;	
import org.thingsboard.server.dao.util.BeanToMap;	
import org.apache.commons.collections.CollectionUtils;	
import org.springframework.stereotype.Service;	
import org.springframework.transaction.annotation.Transactional;	
import org.slf4j.Logger;	
import org.slf4j.LoggerFactory;	
import org.apache.commons.lang3.StringUtils;	
import java.util.UUID;	
	
	
import java.util.List;	
import java.util.stream.Collectors;	
/**	
  创建时间: 2021-10-26 18:16:59	
  创建人: HU.YUNHUI	
  描述: 【角色-菜单 关系数据】 对应的service	
*/	
@Service	
public class TenantMenuRoleService  extends BaseSQLServiceImpl<TenantMenuRoleEntity, UUID, TenantMenuRoleDao> {	
	
  	protected Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	
    /**	
     *根据实体保存	
     * @param tenantMenuRole	
     * @return TenantMenuRoleEntity	
     */	
    @Transactional	
    public TenantMenuRoleEntity saveEntity(TenantMenuRoleEntity tenantMenuRole){
        return save(tenantMenuRole);
    }	
	
   /**	
    * 根据实体类的查询	
    * @param tenantMenuRole  实体对象	
    * @return List<TenantMenuRoleEntity> list对象	
    * @throws Exception	
    */	
  public  List<TenantMenuRoleEntity> findAllByTenantMenuRoleEntity(TenantMenuRoleEntity tenantMenuRole) throws Exception {	
            List<TenantMenuRoleEntity> tenantMenuRolelist = findAll( BeanToMap.beanToMapByJackson(tenantMenuRole));	
            return  tenantMenuRolelist;	
   }	
	
    /**	
      *根据实体更新	
      * @param tenantMenuRole	
      * @return TenantMenuRoleEntity	
      */	
      public TenantMenuRoleEntity updateRecord(TenantMenuRoleEntity tenantMenuRole)  throws ThingsboardException {	
	
            if (tenantMenuRole.getId() == null) {	
          throw new ThingsboardException("Requested id wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);	
             }	
            return this.updateNonNull(tenantMenuRole.getId(), tenantMenuRole);	
        }

    /**
     * 删除该角色对应的菜单关系数据
      * @param roleId 角色id
     */
    public  void  deleteByTenantSysRoleId(UUID roleId)
      {
          dao.deleteByTenantSysRoleId(roleId);
      }

      public  List<TenantMenuRoleEntity> queryMenuIdByRole(UUID userId){
        return  dao.queryMenuIdByRole(userId);
      }

}	
