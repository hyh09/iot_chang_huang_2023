package org.thingsboard.server.dao.tenantmenu;

import org.thingsboard.server.common.data.tenantmenu.TenantMenu;

import java.util.List;
import java.util.UUID;

public interface TenantMenuService {

    /**
     *新增/修改租户菜单
     * @param pcList
     * @param appList
     */
    void saveOrUpdTenantMenu(List<TenantMenu> tenantMenuList);

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
     * @param tenantId
     * @return
     */
    List<TenantMenu> getTenantMenuList(String menuType, String tenantId, String tenentMenuName);

    TenantMenu findById(UUID tenantMenuId);

    /**
     * id的批量查询
     * @param ids
     * @return
     */
    List<TenantMenu> findByIdIn(List<UUID> ids);

}
