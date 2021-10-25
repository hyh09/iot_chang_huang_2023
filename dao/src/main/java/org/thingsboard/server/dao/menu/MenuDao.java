package org.thingsboard.server.dao.menu;

import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;

public interface MenuDao extends Dao<Menu>{
    PageData<Menu> findMenusByRegion(MenuId menuId, String region, PageLink pageLink);

    /**
     * 根据菜单名称查询
     * @param Name
     * @return
     */
    List<Menu> findMenusByName(String menuType,String Name);

}
