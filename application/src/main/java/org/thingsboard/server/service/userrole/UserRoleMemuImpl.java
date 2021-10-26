package org.thingsboard.server.service.userrole;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.sql.role.service.UserMenuRoleService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.util.JsonUtils;
import org.thingsboard.server.entity.role.ResultVo;
import org.thingsboard.server.entity.role.UserRoleVo;

import java.util.List;

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
            if (user == null || user.getActiveStatus().equals("0")) {
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
            log.error("用户角色绑定接口 【入参】"+vo);
            log.error("用户角色绑定接口 【异常信息】"+e);
            return ResultVo.getFail("服务器异常!"+e);

        }
    }
}
