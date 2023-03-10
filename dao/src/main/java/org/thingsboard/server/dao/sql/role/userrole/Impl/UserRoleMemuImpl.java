package org.thingsboard.server.dao.sql.role.userrole.Impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryUserVo;
import org.thingsboard.server.common.data.vo.rolevo.RoleBindUserVo;
import org.thingsboard.server.common.data.vo.user.FindUserVo;
import org.thingsboard.server.common.data.vo.user.enums.UserLeveEnums;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.sql.role.service.UserMenuRoleService;
import org.thingsboard.server.dao.sql.role.userrole.ResultVo;
import org.thingsboard.server.dao.sql.role.userrole.SqlSplicingSvc;
import org.thingsboard.server.dao.sql.role.userrole.UserRoleMemuSvc;
import org.thingsboard.server.dao.sql.role.userrole.sqldata.SqlVo;
import org.thingsboard.server.dao.sql.role.userrole.user.UserRoleVo;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.util.JsonUtils;
import org.thingsboard.server.dao.util.sql.jpa.repository.SortRowName;
//import org.thingsboard.server.entity.ResultVo;
//import org.thingsboard.server.entity.role.UserRoleVo;
//import org.thingsboard.server.service.userrole.SqlSplicingSvc;
//import org.thingsboard.server.service.userrole.UserRoleMemuSvc;
//import org.thingsboard.server.service.userrole.sqldata.SqlVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserRoleMemuImpl implements UserRoleMemuSvc {


    @Autowired  private UserMenuRoleService userMenuRoleService;
    @Autowired  private UserService userService;
    @Autowired private TenantSysRoleService roleService;
    @Autowired private SqlSplicingSvc splicingSvc;
    @Autowired private TenantSysRoleService tenantSysRoleService;


    /**
     * 用户角色关系数据的绑定
     * @param vo
     * @return
     */
    @Override
    public Object relationUser(UserRoleVo vo) {
        try {
            log.info("【用户角色绑定接口 UserRoleMemuSvc.relationUser】入参:{}", vo);
            User user = userService.findUserById(null, new UserId(vo.getUserId()));
            if (user == null ) {
                return ResultVo.getFail("当前用户id不可用,或者不存在此用户!");
            }
            TenantSysRoleEntity roleEntity = roleService.findById(vo.getTenantSysRoleId());
            if (roleEntity == null) {
                return ResultVo.getFail("当前传入得角色id[tenantSysRoleId]错误,请检查!");
            }
            UserMenuRoleEntity userMenuRoleEntity = JsonUtils.beanToBean(vo, UserMenuRoleEntity.class);
            userMenuRoleEntity.setTenantId(user.getTenantId().getId());
            List<UserMenuRoleEntity> list =  userMenuRoleService.queryByRoleIdAndUserId(userMenuRoleEntity);
            if (CollectionUtils.isNotEmpty(list)) {
                return ResultVo.getFail("当前的用户已经绑定了该角色!");
            }
            log.info("【用户角色绑定接口 UserRoleMemuSvc.relationUser 转换后的对象】入参:{}", vo);
            return userMenuRoleService.saveEntity(userMenuRoleEntity);
        }catch (Exception e)
        {
            e.printStackTrace();
            log.error("用户角色绑定接口 【入参】"+vo);
            log.error("用户角色绑定接口 【异常信息】"+e);
            return ResultVo.getFail("服务器异常!"+e);

        }
    }

    /**
     * 一个角色绑定多个用户
     * @param vo
     * @return
     */
    @Override
    public Object relationUserAndRole(RoleBindUserVo vo, TenantId tenantId  ) {
        TenantSysRoleEntity roleEntity = roleService.findById(vo.getTenantSysRoleId());
        if (roleEntity == null) {
            return ResultVo.getFail("当前传入得角色id[tenantSysRoleId]错误,请检查!");
        }
        List<User>   userIdS =   getUserAc(vo.getUserIds());
        if(CollectionUtils.isEmpty(userIdS))
        {
            return ResultVo.getFail("传入的用户id不存在!");

        }
       List<UUID> userIdInDB= userIdS.stream().map(User::getUuidId).collect(Collectors.toList());
        relationUserBachByRole(userIdInDB,roleEntity,  tenantId);

        return   ResultVo.getSuccessFul(null);

    }

    /**
     * 解绑用户角色
     * @param vo
     * @return
     */
    @Override
    public Object unboundUser(RoleBindUserVo vo) {
        log.info("解绑角色下的用户:{}",vo);
        userMenuRoleService.deleteBatch(vo.getUserIds(),vo.getTenantSysRoleId());
        return ResultVo.getSuccessFul(null);
    }


    /**
     * 批量绑定
     *  用户绑定角色
     */
    @Override
    @Transactional
    public void  relationUserBach(List<UUID> rId, UUID uuid, TenantId  tenantId) {
        if(CollectionUtils.isEmpty(rId)){
            return ;
        }
        rId.forEach(item ->{
            TenantSysRoleEntity  roleEntity=   roleService.findById(item);
            if(roleEntity != null){
                UserMenuRoleEntity entity = new UserMenuRoleEntity();
                entity.setUserId(uuid);
                entity.setTenantSysRoleId(item);
                entity.setTenantId(tenantId.getId());
                userMenuRoleService.saveEntity(entity);
                if(roleEntity.getUserLevel() == UserLeveEnums.TENANT_ADMIN.getCode())
                {
                    //租户管理员角色--看成 相对于用户的系统角色
                    userService.updateLevel(uuid,UserLeveEnums.USER_SYSTEM_ADMIN.getCode());
                }
            }


        });
    }



    /**
     * 批量绑定 一个角色
     */
    @Transactional
    public void  relationUserBachByRole(List<UUID> userIds, TenantSysRoleEntity roleEntity, TenantId  tenantId) {
        if(CollectionUtils.isEmpty(userIds)){
            return ;
        }
        userIds.forEach(item ->{
            UserMenuRoleEntity entity = new UserMenuRoleEntity();
            entity.setUserId(item);
            entity.setTenantSysRoleId(roleEntity.getId());
            entity.setTenantId(tenantId.getId());
            userMenuRoleService.saveEntity(entity);
            if(roleEntity.getUserLevel() == UserLeveEnums.TENANT_ADMIN.getCode())
            {
                //租户管理员角色--看成 相对于用户的系统角色
                userService.updateLevel(item,UserLeveEnums.USER_SYSTEM_ADMIN.getCode());
            }

        });
    }

    @Override
    @Transactional
    public void deleteRoleByUserId(UUID userId) {
        log.info("删除用户的时候清空此用户对应的关系数据:{}",userId);
        userMenuRoleService.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void updateRoleByUserId(List<UUID> rId, UUID uuid,TenantId tenantId) {
       log.info("删除此用户绑定的之前数据:{}",uuid);
        deleteRoleByUserId(uuid);
        relationUserBach(rId,uuid,tenantId);
    }

    @Override
    public void deleteRoleByRole(UUID roleId) {
        log.info("删除此角色绑定得之前得用户关系数据:{}",roleId);
        userMenuRoleService.deleteByTenantSysRoleId(roleId);
    }

    @Override
   public Object getUserByInRole( QueryUserVo user, PageLink pageLink,SortRowName sortRowName )
    {
        log.info("查询当前角色下的用户绑定数据{}",user);
        SqlVo sqlVo =  splicingSvc.getUserByInRole(user);
        Page<FindUserVo> page=  userMenuRoleService.querySql(sqlVo.getSql(),sqlVo.getParam(), FindUserVo.class,DaoUtil.toPageable(pageLink),sortRowName);
       List<FindUserVo> list = page.getContent();
        log.info("查询当前角色下的用户绑定数据list{}",list);
        return new PageData<FindUserVo>(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }

    @Override
    public Object getUserByNotInRole(QueryUserVo user, PageLink pageLink,SortRowName sortRowName) {
        log.info("查询当前角色下的用户绑定数据",user);
        SqlVo sqlVo =  splicingSvc.getUserByNotInRole(user);
        Page<FindUserVo> page=  userMenuRoleService.querySql(sqlVo.getSql(),sqlVo.getParam(), FindUserVo.class,DaoUtil.toPageable(pageLink),sortRowName);
        return new PageData<FindUserVo>(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }


    private  List<User> getUserAc( List<UUID> userIds)
    {

        //ser user = userService.findAll(null, new UserId(vo.getUserId()));
        Map<String, Object> queryParam  =new HashMap<>();
        queryParam.put("idlist",userIds);
        List<User>  userList=   userService.findAll(queryParam);
        log.info("打印当前的查询结果:{}",userList);
        return  userList;

    }


}
