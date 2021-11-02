package org.thingsboard.server.service.userrole;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryUserVo;
import org.thingsboard.server.common.data.vo.rolevo.RoleBindUserVo;
import org.thingsboard.server.entity.role.UserRoleVo;

import java.util.List;
import java.util.UUID;

/**
 * 用户和角色  得关系数据处理业务
 */
public interface UserRoleMemuSvc {

    //1.用户角色数据绑定
   Object relationUser(UserRoleVo vo) ;

    Object relationUserAndRole(RoleBindUserVo vo) ;

    Object unboundUser(RoleBindUserVo vo);


    void relationUserBach(List<UUID> rId,UUID uuid) ;

    //删除用户所关联的角色数据
    void  deleteRoleByUserId(UUID userId);

    //更新用户得时候更新角色
    void  updateRoleByUserId(List<UUID> rId,UUID uuid);

    /**
     * 删除角色下得关系数据
     */
    void  deleteRoleByRole(UUID  roleId);


 /**
  * 查询当前角色下的用户
  * @param user
  * @param pageLink
  * @return
  */
 Object getUserByInRole( QueryUserVo user, PageLink pageLink);

 Object getUserByNotInRole( QueryUserVo user, PageLink pageLink);


}
