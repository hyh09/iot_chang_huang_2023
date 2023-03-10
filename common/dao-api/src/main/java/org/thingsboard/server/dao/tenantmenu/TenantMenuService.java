package org.thingsboard.server.dao.tenantmenu;

import org.thingsboard.server.common.data.tenantmenu.TenantMenu;

import java.util.List;
import java.util.UUID;

public interface TenantMenuService {

    /**
     *新增/修改租户菜单
     * @param tenantMenuList
     * @param tenantId
     */
    void saveOrUpdTenantMenu(List<TenantMenu> tenantMenuList, UUID tenantId);

    List<TenantMenu> saveTenantMenuList(List<TenantMenu> tenantMenu);

    /**
     * 修改后刷新值
     * @param tenantMenu
     * @return
     */
    List<TenantMenu> updTenantMenu(TenantMenu tenantMenu);

    /**
     * 调整排序
     * @param id
     * @param frontId
     * @return
     */
    List<TenantMenu> updTenantMenuSort(String id,String frontId);
    /**
     * 删除后刷新值
     * @param id
     * @param tenantId
     * @return
     */
    List<TenantMenu> delTenantMenu(String id,String tenantId);

    /**
     * 修改系统菜单
     * @param tenantMenu
     * @return
     */
    TenantMenu updateTenantMenu(TenantMenu tenantMenu);

    /**
     * 查询租户菜单
     * @param tenantMenu
     * @return
     */
    List<TenantMenu> getTenantMenuList(TenantMenu tenantMenu);

    TenantMenu findById(UUID tenantMenuId);

    /**
     * id的批量查询
     * @param ids
     * @return
     */
    List<TenantMenu> findByIdIn(List<UUID> ids);

    /**
     * 获取租户下菜单
     * @param menuType
     * @param tenantId
     * @return
     */
    List<TenantMenu>  getTenantMenuListByTenantId(String menuType,UUID tenantId);


    List<TenantMenu>  getTenantMenuListByIds(String menuType,UUID tenantId,List<UUID> ids);

}
