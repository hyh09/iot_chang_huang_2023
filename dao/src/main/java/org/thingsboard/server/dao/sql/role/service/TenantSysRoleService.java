package org.thingsboard.server.dao.sql.role.service;	
	
import org.springframework.data.domain.Page;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.UserEntity;
import org.thingsboard.server.dao.util.sql.JpaQueryHelper;
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;
import org.thingsboard.server.dao.sql.role.dao.TenantSysRoleDao;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;	
import org.thingsboard.server.dao.util.BeanToMap;	
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;	
import org.slf4j.Logger;	
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.List;	
/**
  创建时间: 2021-10-22 18:05:08	
  创建人: HU.YUNHUI	
  描述: 【角色】 对应的service
  Service层	
*/	
@Service	
public class TenantSysRoleService  extends BaseSQLServiceImpl<TenantSysRoleEntity, UUID, TenantSysRoleDao> {	
	
  	protected Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	
    /**
     *根据实体保存
     * @param tenantSysRole
     * @return TenantSysRoleEntity
     */
    @Transactional
    public TenantSysRoleEntity save(TenantSysRoleEntity tenantSysRole){
            return super.save(tenantSysRole);
    }
	
    /**	
     * 根据实体类的查询	
     * @param tenantSysRole  实体对象	
     * @return List<TenantSysRoleEntity> list对象	
     * @throws Exception	
     */	
    public  List<TenantSysRoleEntity> findAllByTenantSysRoleEntity(TenantSysRoleEntity tenantSysRole) throws Exception {	
        List<TenantSysRoleEntity> tenantSysRolelist = findAll(BeanToMap.beanToMapByJackson(tenantSysRole));	
        return  tenantSysRolelist;	
    }



    /**
     * 根据实体类的查询
     * @return List<TenantSysRoleEntity> list对象
     * @throws Exception
     */
    public  PageData<TenantSysRoleEntity> pageQuery(Map<String, Object> queryParam, PageLink pageLink) {
        Page<TenantSysRoleEntity> page = super.findAll(queryParam,  pageLink);
        System.out.println("打印当前的数据:"+page);
          return new PageData<TenantSysRoleEntity>(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }


    /**	
      *根据实体更新	
      * @param tenantSysRole	
      * @return TenantSysRoleEntity	
      */
      @Transactional
      public TenantSysRoleEntity updateRecord(TenantSysRoleEntity tenantSysRole) throws ThingsboardException {
            if (tenantSysRole.getId() == null) {
                throw new ThingsboardException("Requested id wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
            }
            return updateNonNull(tenantSysRole.getId(), tenantSysRole);
     }






}	
