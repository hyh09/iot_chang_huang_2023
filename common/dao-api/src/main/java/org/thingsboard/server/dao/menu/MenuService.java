package org.thingsboard.server.dao.menu;

import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.memu.MenuInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;

public interface MenuService{

    Menu saveMenu(Menu menu);

    Menu updateMenu(Menu menu);

    Menu findMenuById(MenuId menuId);

    PageData<Menu> findMenus(PageLink pageLink);
    /**
     *查询系统菜单列表（标记被当前租户绑定过的）
     */
    List<MenuInfo> getTenantMenuListByTenantId(String menuType,String tenantId,String name);

}
