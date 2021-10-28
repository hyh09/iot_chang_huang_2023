package org.thingsboard.server.service.userrole.Impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.sql.role.service.UserMenuRoleService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.util.JsonUtils;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.role.UserRoleVo;
import org.thingsboard.server.service.userrole.UserRoleMemuSvc;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserRoleMemuImpl implements UserRoleMemuSvc {


    @Autowired  private UserMenuRoleService userMenuRoleService;
    @Autowired  private UserService userService;
    @Autowired private TenantSysRoleService roleService;

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
            List<UserMenuRoleEntity> list =  userMenuRoleService.queryByRoleIdAndUserId(userMenuRoleEntity);
//            List<UserMenuRoleEntity> list = userMenuRoleService.findAllByUserMenuRoleEntity(userMenuRoleEntity);
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
     * 批量绑定
     */
    @Override
    @Transactional
    public void  relationUserBach(List<UUID> rId, UUID uuid) {
        if(CollectionUtils.isEmpty(rId)){
            return ;
        }
        rId.forEach(item ->{
            UserMenuRoleEntity entity = new UserMenuRoleEntity();
            entity.setUserId(uuid);
            entity.setTenantSysRoleId(item);
            userMenuRoleService.saveEntity(entity);
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
    public void updateRoleByUserId(List<UUID> rId, UUID uuid) {
       log.info("删除此用户绑定的之前数据:{}",uuid);
        deleteRoleByUserId(uuid);
        relationUserBach(rId,uuid);
    }

    @Override
    public void deleteRoleByRole(UUID roleId) {
        log.info("删除此角色绑定得之前得用户关系数据:{}",roleId);
        userMenuRoleService.deleteByTenantSysRoleId(roleId);
    }


}
