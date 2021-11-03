package org.thingsboard.server.dao.menu;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.memu.MenuInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;
import java.util.UUID;

public interface MenuService {

    Menu saveMenu(Menu menu)throws ThingsboardException;

    Menu updateMenu(Menu menu) throws ThingsboardException;

    void delMenu(UUID id);
    /**
     * 调整排序
     * @param id
     * @param frontId
     * @return
     */
    Menu updMenuSort(String id, String frontId);

    Menu findMenuById(MenuId menuId);

    PageData<Menu> findMenus(PageLink pageLink);
    /**
     *查询系统菜单列表（标记被当前租户绑定过的）
     */
    List<MenuInfo> getTenantMenuListByTenantId(String menuType,String tenantId,String name);

    /**
     * 条件查询系统菜单列表
     * @param menuType
     * @param name
     * @return
     */
    List<Menu> getMenuListByCdn(String menuType,String name);

    /**
     * 查询系统菜单列表分页
     * @param menu
     * @param pageLink
     * @return
     */
    PageData<Menu> getMenuPage(Menu menu, PageLink pageLink) throws ThingsboardException ;

    /**
     * 查询一级菜单
     * @param menuType
     * @return
     */
    List<Menu> getOneLevel(String menuType);

    Menu getTenantById(UUID id);

}
