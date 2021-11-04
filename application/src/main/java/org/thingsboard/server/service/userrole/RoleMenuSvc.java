package org.thingsboard.server.service.userrole;

import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.RoleMenuVo;

import java.util.List;


/**
 * 角色-菜单 接口
 */
public interface RoleMenuSvc {

    Object  binding(RoleMenuVo  vo);

    //搞复杂了
    Object  queryAll(InMenuByUserVo vo);

    List<TenantMenu> queryAllNew(InMenuByUserVo vo) throws Exception;

    List<TenantMenu> queryByUser(InMenuByUserVo vo) throws Exception;

}
