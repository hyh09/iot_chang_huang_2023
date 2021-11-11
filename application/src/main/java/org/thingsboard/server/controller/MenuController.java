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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.menu.MenuId;
import org.thingsboard.server.common.data.memu.Menu;
import org.thingsboard.server.common.data.memu.MenuInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.entity.menu.dto.AddMenuDto;
import org.thingsboard.server.entity.menu.qry.MenuQueryCdnQry;
import org.thingsboard.server.entity.menu.vo.MenuVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
     * 新增/修改系统菜单
     * @param addMenuDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增/修改系统菜单")
    @ApiImplicitParam(name = "addMenuDto",value = "入参实体",dataType = "AddMenuDto",paramType="body")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public MenuVo saveMenu(@RequestBody AddMenuDto addMenuDto) throws ThingsboardException {
        try {
            checkNotNull(addMenuDto);
            checkSameLevelNameRepetition(addMenuDto);
            Menu menu = checkAddMenuList(addMenuDto);
            if(addMenuDto.getId() == null){
                menu.setCreatedUser(getCurrentUser().getUuidId());
                menu = checkNotNull(menuService.saveMenu(menu));
                if(menu != null){
                    menuVo = new MenuVo(menu);
                }
            }else {
                checkParameter("id",addMenuDto.getId());
                menu.setUpdatedUser(getCurrentUser().getUuidId());
                menu = menuService.updateMenu(menu);
                if(menu != null){
                    menuVo = new MenuVo(menu);
                }
            }
            return menuVo;
        } catch (Exception e) {
            throw handleException(e);
        }
    }
/*

    */
/**
     * 修改系统菜单
     * @param updMenuDto
     * @return
     * @throws ThingsboardException
     *//*

    @ApiOperation("修改系统菜单")
    @ApiImplicitParam(name = "updMenuDto",value = "入参实体",dataType = "UpdMenuDto",paramType="body")
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/updMenu", method = RequestMethod.POST)
    @ResponseBody
    public MenuVo updMenu(@RequestBody UpdMenuDto updMenuDto) throws ThingsboardException {
        try {
            checkNotNull(updMenuDto);
            checkParameter("tenantId",updMenuDto.getTenantId());
            checkParameter("id",updMenuDto.getId());
            Menu menu = checkNotNull(menuService.updateMenu(updMenuDto.toMenu()));
            if(menu != null){
                menuVo = new MenuVo(menu);
            }
            return menuVo;
        } catch (Exception e) {
            throw handleException(e);
        }
    }
*/

    /**
     * 修改系统菜单排序
     * @param id
     * @param frontId  id前面一个菜单
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("修改系统菜单排序")
    @ApiImplicitParams({@ApiImplicitParam(name = "id",value = "当前菜单",dataType = "String",paramType="query",required = true),
            @ApiImplicitParam(name = "frontId",value = "移动到指定位置后，前面一个菜单标识",dataType = "String",paramType="query")})
    @RequestMapping(value = "/updantMenuSort", method = RequestMethod.PUT)
    @ResponseBody
    public MenuVo updMenuSort(@RequestParam String id, @RequestParam String frontId) throws ThingsboardException {
        try {
            //校验参数
            checkParameter("id",id);
            checkParameter("前面一个菜单",frontId);
            Menu menu = menuService.updMenuSort(id, frontId);
            if(menu != null){
                menuVo = new MenuVo(menu);
            }
            return menuVo;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 删除菜单
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("删除菜单")
    @ApiImplicitParam(name = "id",value = "当前菜单标识",dataType = "String",paramType="path",required = true)
    @RequestMapping(value = "/delMenu/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delMenu(@PathVariable("id") String id) throws ThingsboardException {
        try {
            //校验参数
            checkParameter("id",id);
            menuService.delMenu(toUUID(id));
        } catch (Exception e) {
            throw handleException(e);
        }
    }



    /**
    *  查询系统菜单列表分页
     */
//    @ApiOperation("查询系统菜单列表分页")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "textSearch",value = "菜单类型（PC/APP）",dataType = "string",paramType = "query",required = true),
//            @ApiImplicitParam(name = "sortProperty",value = "排序字段",dataType = "string",paramType = "query",required = true),
//            @ApiImplicitParam(name = "sortOrder",value = "排序方式（DESC/ASC）",dataType = "string",paramType = "query")})
//    @PreAuthorize("hasAuthority('SYS_ADMIN')")
//    @RequestMapping(value = "/getMenus", params = {"pageSize", "page"}, method = RequestMethod.GET)
//    @ResponseBody
//    public PageData<Menu> getMenus(@RequestParam int pageSize,
//                                           @RequestParam int page,
//                                           @RequestParam(required = false) String textSearch,
//                                           @RequestParam(required = false) String sortProperty,
//                                           @RequestParam(required = false) String sortOrder) throws ThingsboardException {
//        try {
//            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
//            return checkNotNull(menuService.findMenus(pageLink));
//        } catch (Exception e) {
//            throw handleException(e);
//        }
//    }

    /**
     *  查询系统菜单列表分页
     */
    @ApiOperation("查询系统菜单列表分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuQueryCdnQry",value = "多条件入参",dataType = "MenuQueryCdnQry",paramType = "query"),
            @ApiImplicitParam(name = "sortProperty",value = "排序字段",dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "sortOrder",value = "排序方式（DESC/ASC）",dataType = "string",paramType = "query")})
    @RequestMapping(value = "/getMenuPage", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<MenuVo> getMenuPage(@RequestParam int pageSize,
                                        @RequestParam int page,
                                        @RequestParam(required = false) String sortProperty,
                                        @RequestParam(required = false) String sortOrder,
                                        MenuQueryCdnQry menuQueryCdnQry) throws ThingsboardException {
        try {
            PageData<MenuVo> resultPage = new PageData<>();
            List<MenuVo> resultMenuVos = new ArrayList<>();
            PageLink pageLink = createPageLink(pageSize, page,"", sortProperty, sortOrder);
            PageData<Menu> menuPageData = checkNotNull(menuService.getMenuPage(menuQueryCdnQry.toMenu(), pageLink));
            List<Menu> menuList = menuPageData.getData();
            if(!CollectionUtils.isEmpty(menuList)){
                for (Menu menu : menuList) {
                    resultMenuVos.add(new MenuVo(menu));
                }
            }
            resultPage = new PageData<>(resultMenuVos,menuPageData.getTotalPages(),menuPageData.getTotalElements(),menuPageData.hasNext());
            return resultPage;
        } catch (Exception e) {
            throw handleException(e);
        }
    }



    /**
    * 根据菜单标识查询菜单详情信息
     */
    @ApiOperation(value="根据菜单标识查询菜单详情信息")
    @ApiImplicitParam(name = "id",value = "当前菜单id",dataType = "String",paramType="path",required = true)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public MenuVo getTenantById(@PathVariable("id") String id) throws ThingsboardException {
        checkParameter("id", id);
        try {
            MenuId menuId = new MenuId(toUUID(id));
            checkParameter("id",id);
            Menu menu = menuService.getTenantById(UUID.fromString(id));
            return new MenuVo(menu);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询系统菜单列表（标记被当前租户绑定过的）
     * @param menuType
     * @param name
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询系统菜单列表（标记被当前租户绑定过的）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuType",value = "菜单类型（PC/APP）",dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "tenantId",value = "租户标识",dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "name",value = "菜单名称",dataType = "string",paramType = "query")})
    @RequestMapping(value = "/getTenantMenuListByTenantId", method = RequestMethod.GET)
    @ResponseBody
    public List<MenuInfo> getTenantMenuListByTenantId(@RequestParam String menuType,@RequestParam String tenantId,@RequestParam(required = false) String name)throws ThingsboardException{
        try {
            checkParameter("menuType",menuType);
            checkParameter("租户标识tenantId不能为空",tenantId);
            return checkNotNull(menuService.getTenantMenuListByTenantId(menuType,UUID.fromString(tenantId),name));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation("查询一级菜单列表")
    @ApiImplicitParam(name = "menuType",value = "菜单类型（PC/APP）",dataType = "string",paramType = "query")
    @RequestMapping(value = "/getOneLevel", method = RequestMethod.GET)
    @ResponseBody
    public List<MenuVo> getOneLevel(@RequestParam String menuType)throws ThingsboardException{
        try {
            checkParameter("menuType",menuType);
            List<MenuVo> result = new ArrayList<>();
            List<Menu> menuList = checkNotNull(menuService.getOneLevel(menuType));
            if(!CollectionUtils.isEmpty(menuList)){
                menuList.forEach(i->{
                    result.add(new MenuVo(i));
                });
            }
            return result;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 条件查询系统菜单列表
     * @param menuQueryCdnQry
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("条件查询系统菜单列表")
    @ApiImplicitParam(name = "menuQueryCdnQry",value = "条件内容",dataType = "MenuQueryCdnQry",paramType = "query")
    @RequestMapping(value = "/getMenuListByCdn", method = RequestMethod.GET)
    @ResponseBody
    public List<MenuVo> getMenuListByCdn(MenuQueryCdnQry menuQueryCdnQry)throws ThingsboardException{
        try {
            List<MenuVo> result = new ArrayList<>();
            Menu queryMenu = new Menu();
            if(menuQueryCdnQry != null){
                queryMenu = menuQueryCdnQry.toMenu();
            }
            List<Menu> menuList = menuService.getMenuListByCdn(queryMenu);
            if(!CollectionUtils.isEmpty(menuList)){
                for (Menu menu:menuList){
                    MenuVo menuVo = new MenuVo(menu);
                    Menu parentMenu = new Menu();
                    if(menu.getParentId() != null){
                        parentMenu = menuService.findMenuById(new MenuId(menu.getParentId()));
                    }
                    if(parentMenu != null){
                        menuVo.setParentName(parentMenu.getName());
                    }
                    result.add(menuVo);
                }
            }
            return result;
        } catch (Exception e) {
            throw handleException(e);
        }
    }


}
