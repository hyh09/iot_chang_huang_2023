package org.thingsboard.server.service.userrole;

import org.thingsboard.server.entity.role.UserRoleVo;

import java.util.List;
import java.util.UUID;

public interface UserRoleMemuSvc {

    //1.用户角色数据绑定
   Object relationUser(UserRoleVo vo) ;

    void relationUserBach(List<UUID> rId,UUID uuid) ;

    //删除用户所关联的角色数据
    void  deleteRoleByUserId(UUID userId);

}
