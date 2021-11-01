package org.thingsboard.server.service.userrole;

import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.RoleMenuVo;


/**
 * 角色-菜单 接口
 */
public interface RoleMenuSvc {

    Object  binding(RoleMenuVo vo);

    Object  queryAll(InMenuByUserVo vo);
}
