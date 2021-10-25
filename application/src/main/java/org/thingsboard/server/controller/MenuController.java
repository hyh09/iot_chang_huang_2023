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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.memu.MenuInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.entity.menu.dto.AddMenuDto;
import org.thingsboard.server.entity.menu.vo.MenuVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.Operation;

import java.util.List;

@Api(value="系统菜单Controller",tags={"系统菜单口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/menu")
public class MenuController extends BaseController {

    public static final String MENU_ID = "menuId";
    public static final String TENANT_ID = "tenantId";
    private MenuVo menuVo = new MenuVo();

    /**
     * 新增/更新系统菜单
     * @param addMenuDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增/更新系统菜单")
    @ApiImplicitParam(name = "addMenuDto",value = "入参实体",dataType = "AddMenuDto",paramType="body")
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public MenuVo saveMenu(@RequestBody AddMenuDto addMenuDto) throws ThingsboardException {
        try {
            checkNotNull(addMenuDto);
            Menu menu = new Menu();
            if(addMenuDto.getId() == null){
                menu =checkAddMenuList(addMenuDto);
                menu = checkNotNull(menuService.saveMenu(menu));
            }else {
                checkParameter("id",addMenuDto.getId());
                menu = checkNotNull(menuService.updateMenu(addMenuDto.toMenu()));
            }
            if(menu != null){
                menuVo = new MenuVo(menu);
            }
            return menuVo;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
    *  查询系统菜单列表分页
     */
    @ApiOperation("查询系统菜单列表分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "textSearch",value = "菜单类型（PC/APP）",dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "sortProperty",value = "排序字段",dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "sortOrder",value = "排序方式（DESC/ASC）",dataType = "string",paramType = "query")})
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/getMenus", params = {"pageSize", "page"}, method = RequestMethod.GET)
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
    @ApiOperation(value="根据菜单标识查询菜单详情信息")
    @ApiImplicitParam(name = "id",value = "当前菜单id",dataType = "String",paramType="path",required = true)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public MenuVo getTenantById(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        try {
            MenuId menuId = new MenuId(toUUID(id));
            Menu menu = checkMenuId(menuId, Operation.READ);
            return new MenuVo(menu);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询系统菜单列表（标记被当前租户绑定过的）
     * @param tenantId
     * @return
     */
    @ApiOperation("查询系统菜单列表（标记被当前租户绑定过的）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuType",value = "菜单类型（PC/APP）",dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "tenantId",value = "租户标识",dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "name",value = "菜单名称",dataType = "string",paramType = "query")})
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/getTenantMenuListByTenantId", method = RequestMethod.GET)
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
