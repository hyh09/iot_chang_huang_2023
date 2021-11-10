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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.entity.tenantmenu.dto.AddTenantMenuDto;
import org.thingsboard.server.entity.tenantmenu.dto.SaveTenantMenuDto;
import org.thingsboard.server.entity.tenantmenu.dto.UpdTenantMenuDto;
import org.thingsboard.server.entity.tenantmenu.vo.TenantMenuVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(value="租户菜单Controller",tags={"租户菜单列表口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/tenantMenu")
public class TenantMenuController extends BaseController {

    public static final String TENANT_MENU_ID = "tenantMenuId";
    public static final String MENU_TYPE = "menuType";
    private List<TenantMenuVo> tenantMenuVos = new ArrayList<>();


    /**
     * 新增/修改租户菜单
     * @param saveTenantMenuDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增/修改租户菜单")
    @ApiImplicitParam(name = "saveTenantMenuDto",value = "入参实体",dataType = "SaveTenantMenuDto",paramType="body")
    @RequestMapping(value = "/saveOrUpdTenantMenu", method = RequestMethod.POST)
    @ResponseBody
    public void saveOrUpdTenantMenu(@RequestBody SaveTenantMenuDto saveTenantMenuDto) throws ThingsboardException {
        checkNotNull(saveTenantMenuDto);
        if(saveTenantMenuDto.getPcList() == null && saveTenantMenuDto.getAppList() == null){
            return;
        }
        checkNotNull(saveTenantMenuDto);
        checkNotNull(saveTenantMenuDto);
        checkParameter("租户id不能为空",saveTenantMenuDto.getTenantId());
        List<TenantMenu> tenantMenuList = saveTenantMenuDto.toTenantMenuListBySave(saveTenantMenuDto.getPcList(),saveTenantMenuDto.getAppList(),getCurrentUser().getId().getId(), null,saveTenantMenuDto.getTenantId());
        tenantMenuService.saveOrUpdTenantMenu(tenantMenuList,saveTenantMenuDto.getTenantId());
        //批量删除角色菜单接口
        List<UUID> collect = tenantMenuList.stream().filter(s -> s.getCreatedUser() != null).map(TenantMenu::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(collect)){
            roleMenuSvc.deleteMenuIdByIds(collect);
        }
    }


    /**
     * 新增租户菜单
     * @param addTenantMenuDtos
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增租户菜单")
    @ApiImplicitParam(name = "addTenantMenuDtos",value = "入参实体",dataType = "AddTenantMenuDto",paramType="body",allowMultiple = true)
    @RequestMapping(value = "/saveTenantMenus", method = RequestMethod.POST)
    @ResponseBody
    public List<TenantMenuVo> saveTenantMenus(@RequestBody List<AddTenantMenuDto> addTenantMenuDtos) throws ThingsboardException {
        try {
            tenantMenuVos = new ArrayList<>();
            //校验参数
            List<TenantMenu> tenantMenuList = checkAddTenantMenuList(addTenantMenuDtos);
            tenantMenuList = checkNotNull(tenantMenuService.saveTenantMenuList(tenantMenuList));
            tenantMenuList.forEach(i->{
                tenantMenuVos.add(new TenantMenuVo(i));
            });
            return tenantMenuVos;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 修改租户菜单
     * @param updTenantMenuDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("修改租户菜单")
    @ApiImplicitParam(name = "updTenantMenuDto",value = "入参实体",dataType = "UpdTenantMenuDto",paramType="body")
    @RequestMapping(value = "/updTenantMenu", method = RequestMethod.PUT)
    @ResponseBody
    public List<TenantMenuVo> updTenantMenu(@RequestBody UpdTenantMenuDto updTenantMenuDto) throws ThingsboardException {
        try {
            tenantMenuVos = new ArrayList<>();
            //校验参数
            checkNotNull(updTenantMenuDto);
            checkParameter("tenantId",updTenantMenuDto.getTenantId());
            checkParameter("id",updTenantMenuDto.getId());
            checkParameter("menuType",updTenantMenuDto.getMenuType());
            TenantMenu tenantMenu = updTenantMenuDto.toTenantMenu();
            tenantMenu.setUpdatedUser(getCurrentUser().getUuidId());
            List<TenantMenu> tenantMenuList = tenantMenuService.updTenantMenu(tenantMenu);
            tenantMenuList.forEach(i->{
                tenantMenuVos.add(new TenantMenuVo(i));
            });
            return tenantMenuVos;
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
    @ApiOperation("修改租户菜单排序")
    @ApiImplicitParams({@ApiImplicitParam(name = "id",value = "当前菜单",dataType = "String",paramType="query",required = true),
                    @ApiImplicitParam(name = "frontId",value = "移动到指定位置后，前面一个菜单标识",dataType = "String",paramType="query")})
    @RequestMapping(value = "/updTenantMenuSort", method = RequestMethod.PUT)
    @ResponseBody
    public List<TenantMenuVo> updTenantMenuSort(@RequestParam String id,@RequestParam String frontId) throws ThingsboardException {
        try {
            tenantMenuVos = new ArrayList<>();
            //校验参数
            checkParameter("id",id);
            checkParameter("前面一个菜单",frontId);
            List<TenantMenu> tenantMenuList = tenantMenuService.updTenantMenuSort(id, frontId);
            tenantMenuList.forEach(i->{
                tenantMenuVos.add(new TenantMenuVo(i));
            });
            return tenantMenuVos;
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
    @ApiOperation("删除租户菜单")
    @ApiImplicitParams({@ApiImplicitParam(name = "id",value = "当前菜单",dataType = "String",paramType="query",required = true),
            @ApiImplicitParam(name = "tenantId",value = "租户标识")})
    @RequestMapping(value = "/delTenantMenu", method = RequestMethod.DELETE)
    @ResponseBody
    public List<TenantMenuVo> delTenantMenu(@RequestParam(required = true) String id,@RequestParam(required = true) String tenantId) throws ThingsboardException {
        try {
            tenantMenuVos = new ArrayList<>();
            //校验参数
            checkParameter("id",id);
            checkParameter("tenantId",tenantId);
            List<TenantMenu> tenantMenuList = checkNotNull(tenantMenuService.delTenantMenu(id, tenantId));
            tenantMenuList.forEach(i->{
                tenantMenuVos.add(new TenantMenuVo(i));
            });
            return tenantMenuVos;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询租户菜单列表
     * @param tenantId
     * @return
     */
    @ApiOperation("查询租户菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuType",value = "菜单类型（PC/APP）",required = true,dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "tenantId",value = "租户标识",required = true,dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "name",value = "菜单名称",dataType = "String",paramType="query")})
    @RequestMapping(value = "/getTenantMenuList", method = RequestMethod.GET)
    @ResponseBody
    public List<TenantMenuVo> getTenantMenuList(@RequestParam String menuType, @RequestParam String tenantId, @RequestParam(required = false) String name)throws ThingsboardException{
        try {
            tenantMenuVos = new ArrayList<>();
            checkParameter(TENANT_MENU_ID,tenantId);
            checkParameter(MENU_TYPE,menuType);
            List<TenantMenu> tenantMenuList = checkNotNull(tenantMenuService.getTenantMenuList(menuType,tenantId,name));
            tenantMenuList.forEach(i->{
                tenantMenuVos.add(new TenantMenuVo(i));
            });
            return tenantMenuVos;
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
    public TenantMenuVo getTenantById(@PathVariable("id") String id) throws ThingsboardException {
        try {
            checkParameter("id", id);
            return new TenantMenuVo(checkNotNull(tenantMenuService.findById(toUUID(id))));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}
