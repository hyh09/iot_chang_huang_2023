package org.thingsboard.server.dao.menu;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface MenuDao extends Dao<Menu>{
    PageData<Menu> findMenusByRegion(MenuId menuId, String region, PageLink pageLink);

    /**
     * 根据菜单名称查询
     * @param Name
     * @return
     */
    List<Menu> findMenusByName(String menuType,String Name);

    /**
     * 查询同级目录下最大排序值
     * @param parentId
     * @return
     */
    Integer getMaxSortByParentId(UUID parentId);

    List<Menu> findRearList(Integer sort, UUID parentId);

    /**
     * 查询一级菜单
     * @param menuType
     * @return
     */
    List<Menu> getOneLevel(String menuType);

    /**
     * 查询系统菜单列表分页
     * @param menu
     * @param pageLink
     * @return
     */
    PageData<Menu> getMenuPage(Menu menu, PageLink pageLink) throws ThingsboardException;

    /**
     * 条件查询系统菜单列表
     * @param menu
     * @return
     */
    List<Menu> getMenuListByCdn(Menu menu);

    Menu saveMenu(Menu menu)throws ThingsboardException;

    void delMenu(UUID id);

    Menu getMenuById(UUID id);

    /**
     * 查询同级别下菜单
     * @param parentId
     * @param name
     * @return
     */
    Menu findSameLevelName(UUID parentId,String name);

    /**
     * 根据菜单id,查询菜单下按钮
     * @param ids
     * @return
     */
    List<Menu> getButtonListByIds(List<UUID> ids);

}
