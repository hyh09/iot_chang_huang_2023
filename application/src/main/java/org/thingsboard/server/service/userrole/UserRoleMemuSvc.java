package org.thingsboard.server.service.userrole;

import org.thingsboard.server.entity.role.UserRoleVo;

public interface UserRoleMemuSvc {

    //1.用户角色数据绑定
   Object relationUser(UserRoleVo vo) ;
}
