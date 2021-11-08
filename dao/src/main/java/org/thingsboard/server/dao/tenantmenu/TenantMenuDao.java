package org.thingsboard.server.dao.tenantmenu;

import org.thingsboard.server.common.data.id.tenantmenu.TenantMenuId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface TenantMenuDao extends Dao<TenantMenu>{

    /**
     * 根据租户删除菜单
     * @param tenantId
     */
    void deletedByTenant(UUID tenantId);

    /**
     * 根据系统菜单删除菜单
     * @param menuId
     */
    void delByMenuId(UUID menuId);

    /**
     *新增/修改租户菜单
     * @param tenantMenuList
     */
    void saveOrUpdTenantMenu(List<TenantMenu> tenantMenuList);

    PageData<TenantMenu> findTenantMenusByRegion(TenantMenuId tenantMenuId, String region, PageLink pageLink);

    /**
     * 查询同级目录下最大排序值
     * @param parentId
     * @return
     */
    Integer getMaxSortByParentId(UUID parentId);

    TenantMenu save(TenantMenu tenantMenu);

    List<TenantMenu> findRearList(Integer sort,UUID parentId);

    /**
     * 查询租户PC/APP菜单列表
     * @param menuType
     * @param tenantId
     * @return
     */
    List<TenantMenu> getTenantMenuList(String menuType,String tenantId,String tenantMenuName);

    List<TenantMenu>  findByIdIn(List<UUID> ids);

     List<TenantMenu>  getTenantMenuListByTenantId(String menuType,UUID tenantId);

     List<TenantMenu> getTenantMenuListByIds(String menuType, UUID tenantId, List<UUID> id);


    /**
     * 自定义查询菜单列表
     * @param tenantMenu
     * @return
     */
    List<TenantMenu> findAllByCdn(TenantMenu tenantMenu);
}
