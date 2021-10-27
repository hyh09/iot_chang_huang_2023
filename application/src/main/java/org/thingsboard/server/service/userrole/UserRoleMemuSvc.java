package org.thingsboard.server.service.userrole;

import org.thingsboard.server.entity.role.UserRoleVo;

import java.util.List;
import java.util.UUID;

/**
 * 用户和角色  得关系数据处理业务
 */
public interface UserRoleMemuSvc {

    //1.用户角色数据绑定
   Object relationUser(UserRoleVo vo) ;

    void relationUserBach(List<UUID> rId,UUID uuid) ;

    //删除用户所关联的角色数据
    void  deleteRoleByUserId(UUID userId);

    //更新用户得时候更新角色
    void  updateRoleByUserId(List<UUID> rId,UUID uuid);

    /**
     * 删除角色下得关系数据
     */
    void  deleteRoleByRole(UUID  roleId);


}
