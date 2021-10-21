/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Operation;

import java.util.List;

@Api(value="租户菜单Controller",tags={"租户菜单列表口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/tenantMenu")
public class TenantMenuController extends BaseController {

    public static final String TENANT_MENU_ID = "tenantMenuId";
    public static final String MENU_TYPE = "menuType";

    /**
     * 新增租户菜单
     * @param tenantMenuList
     * @return
     * @throws ThingsboardException
     */
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/saveTenantMenus", method = RequestMethod.POST)
    @ResponseBody
    public List<TenantMenu> saveTenantMenus(@RequestBody List<TenantMenu> tenantMenuList) throws ThingsboardException {
        try {
            //校验参数
            checkTenantMenuList(tenantMenuList);
            tenantMenuList = checkNotNull(tenantMenuService.saveTenantMenuList(tenantMenuList));
            return tenantMenuList;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 修改租户菜单
     * @param tenantMenu
     * @return
     * @throws ThingsboardException
     */
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/updTenantMenu", method = RequestMethod.PUT)
    @ResponseBody
    public List<TenantMenu> updTenantMenu(@RequestBody TenantMenu tenantMenu) throws ThingsboardException {
        try {
            //校验参数
            checkNotNull(tenantMenu);
            checkParameter("tenantId",tenantMenu.getTenantId());
            checkParameter("id",tenantMenu.getId());
            checkParameter("menuType",tenantMenu.getMenuType());
            return checkNotNull(tenantMenuService.updTenantMenu(tenantMenu));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 修改租户菜单排序
     * @param id
     * @param frontId  id前面一个菜单
     * @return
     * @throws ThingsboardException
     */
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/updTenantMenuSort", method = RequestMethod.PUT)
    @ResponseBody
    public List<TenantMenu> updTenantMenuSort(@RequestParam String id,@RequestParam String frontId) throws ThingsboardException {
        try {
            //校验参数
            checkParameter("id",id);
            checkParameter("前面一个菜单",frontId);
            return checkNotNull(tenantMenuService.updTenantMenuSort(id,frontId));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 删除租户菜单
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/delTenantMenu", method = RequestMethod.DELETE)
    @ResponseBody
    public List<TenantMenu> delTenantMenu(@RequestParam(required = true) String id,@RequestParam(required = true) String tenantId) throws ThingsboardException {
        try {
            //校验参数
            checkParameter("id",id);
            checkParameter("tenantId",tenantId);
            return checkNotNull(tenantMenuService.delTenantMenu(id,tenantId));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询租户菜单列表
     * @param tenantId
     * @return
     */

    @ApiModelProperty(value="查询租户菜单列表")
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/getTenantMenuList", method = RequestMethod.GET)
    @ResponseBody
    public List<TenantMenu> getTenantMenuList(@RequestParam String menuType,@RequestParam String tenantId,@RequestParam(required = false) String name)throws ThingsboardException{
        try {
            checkParameter(TENANT_MENU_ID,tenantId);
            checkParameter(MENU_TYPE,menuType);
            return checkNotNull(tenantMenuService.getTenantMenuList(menuType,tenantId,name));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
    * 根据菜单标识查询菜单详情信息
     */
    @ApiModelProperty(value="查询租户菜单列表")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/{menuId}", method = RequestMethod.GET)
    @ResponseBody
    public Menu getTenantById(@PathVariable("menuId") String menuId) throws ThingsboardException {
        checkParameter("menuId", menuId);
        try {
            Menu menu = checkMenuId(new MenuId(toUUID(menuId)), Operation.READ);
            if(!menu.getAdditionalInfo().isNull()) {
                processDashboardIdFromAdditionalInfo((ObjectNode) menu.getAdditionalInfo(), HOME_DASHBOARD);
            }
            return menu;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}
