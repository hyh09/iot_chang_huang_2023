package org.thingsboard.server.dao.sql.role.service;

import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;
import org.thingsboard.server.dao.sql.role.dao.UserMenuRoleDao;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
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
  创建时间: 2021-10-25 17:39:26
  创建人: HU.YUNHUI
  描述: 【用户角色关系数据】 对应的service
*/
@Service
public class UserMenuRoleService  extends BaseSQLServiceImpl<UserMenuRoleEntity, UUID, UserMenuRoleDao> {

  	protected Logger logger = LoggerFactory.getLogger(this.getClass());




    /**
     *根据实体保存
     * @param userMenuRole
     * @return UserMenuRoleEntity
     */
    @Transactional
    public UserMenuRoleEntity saveEntity(UserMenuRoleEntity userMenuRole){
        return save(userMenuRole);
    }

   /**
    * 根据实体类的查询
    * @param userMenuRole  实体对象
    * @return List<UserMenuRoleEntity> list对象
    * @throws Exception
    */
  public  List<UserMenuRoleEntity> findAllByUserMenuRoleEntity(UserMenuRoleEntity userMenuRole) throws Exception {
            List<UserMenuRoleEntity> userMenuRolelist = findAll( BeanToMap.beanToMapByJackson(userMenuRole));
            return  userMenuRolelist;
   }

    /**
      *根据实体更新
      * @param userMenuRole
      * @return UserMenuRoleEntity
      */
      public UserMenuRoleEntity updateRecord(UserMenuRoleEntity userMenuRole) throws ThingsboardException {

            if (userMenuRole.getId() == null) {
          throw new ThingsboardException("Requested id wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
             }
            return this.updateNonNull(userMenuRole.getId(), userMenuRole);
        }

    public  List<UserMenuRoleEntity>   queryByRoleIdAndUserId(UserMenuRoleEntity  userMenuRoleEntity)
    {
      return    this.dao.queryAllByUserIdAndTenantSysRoleId(userMenuRoleEntity.getUserId(),userMenuRoleEntity.getTenantSysRoleId());

    }

    public  void deleteByUserId(UUID userId)
    {
        this.dao.deleteByUserId(userId);
    }


    public  void deleteByTenantSysRoleId(UUID  roleId)
    {
        this.dao.deleteByTenantSysRoleId(roleId);
    }





}
