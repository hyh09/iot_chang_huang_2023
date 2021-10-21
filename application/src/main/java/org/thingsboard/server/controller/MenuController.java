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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.memu.MenuInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.List;

@Api(value="系统菜单Controller",tags={"系统菜单列表口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class MenuController extends BaseController {

    public static final String MENU_ID = "menuId";
    public static final String TENANT_ID = "tenantId";

    /**
     * 新增/更新系统菜单
     * @param menu
     * @return
     * @throws ThingsboardException
     */
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/menu/save", method = RequestMethod.POST)
    @ResponseBody
    public Menu saveMenu(@RequestBody Menu menu) throws ThingsboardException {
        try {
            boolean newMenu = menu.getId() == null;
            if(newMenu){
                checkEntity(menu.getId(), menu, Resource.MENU);
                menu = checkNotNull(menuService.saveMenu(menu));
            }else {
                menu = checkNotNull(menuService.updateMenu(menu));
            }
            return menu;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
    *  查询系统菜单列表分页
     */
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/menu/getMenus", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Menu> getMenus(@RequestParam int pageSize,
                                           @RequestParam int page,
                                           @RequestParam(required = false) String textSearch,
                                           @RequestParam(required = false) String sortProperty,
                                           @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            return checkNotNull(menuService.findMenus(pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
    * 根据菜单标识查询菜单详情信息
     */
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/menu/{menuId}", method = RequestMethod.GET)
    @ResponseBody
    public Menu getTenantById(@PathVariable("menuId") String strMenuId) throws ThingsboardException {
        checkParameter("strMenuId", strMenuId);
        try {
            MenuId menuId = new MenuId(toUUID(strMenuId));
            Menu menu = checkMenuId(menuId, Operation.READ);
            if(!menu.getAdditionalInfo().isNull()) {
                processDashboardIdFromAdditionalInfo((ObjectNode) menu.getAdditionalInfo(), HOME_DASHBOARD);
            }
            return menu;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询系统菜单列表（标记被当前租户绑定过的）
     * @param tenantId
     * @return
     */
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/menu/getTenantMenuListByTenantId", method = RequestMethod.GET)
    @ResponseBody
    public List<MenuInfo> getTenantMenuListByTenantId(@RequestParam String menuType,@RequestParam String tenantId,@RequestParam(required = false) String name)throws ThingsboardException{
        try {
            checkParameter(tenantId,tenantId);
            checkParameter(menuType,menuType);
            return checkNotNull(menuService.getTenantMenuListByTenantId(menuType,tenantId,name));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}
