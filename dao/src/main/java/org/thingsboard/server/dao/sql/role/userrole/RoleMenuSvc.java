package org.thingsboard.server.dao.sql.role.userrole;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.vo.menu.TenantMenuVo;
import org.thingsboard.server.dao.sql.role.service.rolemenu.InMenuByUserVo;
import org.thingsboard.server.dao.sql.role.service.rolemenu.RoleMenuVo;

import java.util.List;
import java.util.UUID;


/**
 * 角色-菜单 接口
 */
public interface RoleMenuSvc {

    void   binding(RoleMenuVo vo) throws ThingsboardException;

    //搞复杂了
    Object  queryAll(InMenuByUserVo vo);

    List<TenantMenuVo> queryAllNew(InMenuByUserVo vo) throws Exception;

    /**
     * 查询当前用户所拥有的菜单
     * @param vo
     * @return
     * @throws Exception
     */
    List<TenantMenuVo> queryByUser(InMenuByUserVo vo) throws Exception;

    /**
     * 删除批量角色 菜单关系
     */
    void deleteMenuIdByIds(List<UUID> ids);

}
