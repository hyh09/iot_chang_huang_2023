/**
 * Copyright © 2016-2021 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.entity.tenantmenu.dto.AddTenantMenuDto;
import org.thingsboard.server.entity.tenantmenu.dto.SaveTenantMenuDto;
import org.thingsboard.server.entity.tenantmenu.dto.TenantMenuQry;
import org.thingsboard.server.entity.tenantmenu.dto.UpdTenantMenuDto;
import org.thingsboard.server.entity.tenantmenu.vo.TenantMenuVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Api(value = "租户菜单Controller", tags = {"租户菜单列表口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/tenantMenu")
public class TenantMenuController extends BaseController {

    public static final String TENANT_MENU_ID = "tenantMenuId";
    public static final String MENU_TYPE = "menuType";


    /**
     * 新增/修改租户菜单
     *
     * @param saveTenantMenuDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增/修改租户菜单")
    @ApiImplicitParam(name = "saveTenantMenuDto", value = "入参实体", dataType = "SaveTenantMenuDto", paramType = "body")
    @RequestMapping(value = "/saveOrUpdTenantMenu", method = RequestMethod.POST)
    @ResponseBody
    public void saveOrUpdTenantMenu(@RequestBody SaveTenantMenuDto saveTenantMenuDto) throws ThingsboardException {
        checkNotNull(saveTenantMenuDto);
        if (saveTenantMenuDto.getPcList() == null && saveTenantMenuDto.getAppList() == null) {
            return;
        }
        checkNotNull(saveTenantMenuDto);
        checkNotNull(saveTenantMenuDto);
        checkParameter("租户id不能为空", saveTenantMenuDto.getTenantId());
        try {
            List<TenantMenu> tenantMenuList = saveTenantMenuDto.toTenantMenuListBySave(saveTenantMenuDto.getPcList(), saveTenantMenuDto.getAppList(), getCurrentUser().getId().getId(), null, saveTenantMenuDto.getTenantId());

            //清除解绑的租户菜单，以及清除被解绑的租户菜单与角色关联的信息
            tenantMenuList = this.cleanMenu(tenantMenuList, saveTenantMenuDto.getTenantId());

            //新增/修改
            tenantMenuService.saveOrUpdTenantMenu(tenantMenuList, saveTenantMenuDto.getTenantId());
        } catch (ThingsboardException e) {
            log.error("/api/tenantMenu/saveOrUpdTenantMenu新增/修改租户菜单报错", e);
            throw handleException(e);
        }

    }

    /**
     * 清除解绑的租户菜单，以及清除被解绑的租户菜单与角色关联的信息
     *
     * @param tenantMenuList
     * @param tenantId
     * @return
     */
    private List<TenantMenu> cleanMenu(List<TenantMenu> tenantMenuList, UUID tenantId) throws ThingsboardException{

        //查询该租户下所有菜单
        TenantMenu tenantMenu = new TenantMenu();
        tenantMenu.setTenantId(tenantId);
        tenantMenu.setIsButton(false);
        List<TenantMenu> tenantMenuListFromDb = tenantMenuService.getTenantMenuList(tenantMenu);
        //筛选出被删除的菜单
        List<UUID> tenantMenuIdFilter = new ArrayList<>();

        //拿数据库的跟提交菜单比较，不存在的说明被删除。
        if (CollectionUtils.isNotEmpty(tenantMenuListFromDb)) {
            //过滤被删除的菜单
            Iterator<TenantMenu> iteratorDb = tenantMenuListFromDb.iterator();
            while (iteratorDb.hasNext()){
                TenantMenu db = iteratorDb.next();
                //是否存在  true-存在  false-不存在
                Boolean isExist = false;

                for(TenantMenu submit:tenantMenuList){
                    if (submit.getId() != null && db.getId().toString().equals(submit.getId().toString())) {
                        //存在说明是更新或者新增
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    //不存在的说明被删除
                    iteratorDb.remove();
                    tenantMenuIdFilter.add(db.getId());
                }
            }

        }
        //拿筛选出的新增或修改的菜单，跟数据库查询出来的进一步比较，筛选出新增的菜单数据，要把ID置为空，因为提交过来的新菜单的ID是系统菜单的，保存为租户菜单时要重置
        // 拿提交菜单跟数据库比较，不存在的说明新增，新增的菜单id需要重置
        //操作人
        UUID uuidId = getCurrentUser().getUuidId();
        for (TenantMenu submit:tenantMenuList){
            //是否存在  true-存在  false-不存在
            Boolean isExist = false;

            for(TenantMenu i:tenantMenuListFromDb){
                if (i.getId().toString().equals(submit.getId().toString())) {
                    //存在说明是更新
                    UUID uuid = Uuids.timeBased();
                    isExist = true;
                    submit.setUpdatedTime(Uuids.unixTimestamp(uuid));
                    submit.setUpdatedUser(uuidId);
                    break;
                }
            }
            if (!isExist) {
                //不存在的说明是新增
                submit.setOperationType("add");
                submit.setOldId(submit.getId());
                UUID uuid = Uuids.timeBased();
                submit.setId(uuid);
                submit.setCreatedTime(Uuids.unixTimestamp(uuid));
                submit.setCreatedUser(uuidId);
            }
        }
        //对新增的数据查询其父级
        //当前菜单的父级，对应的菜单
        for (TenantMenu submitFileter:tenantMenuList){
            if("add".equals(submitFileter.getOperationType())){
                for (TenantMenu submit:tenantMenuList){
                    if(submitFileter.getParentId() != null || submit.getLevel() > 0){
                        if(submitFileter.getParentId() == submit.getOldId()){
                            submitFileter.setParentId(submit.getId());
                        }
                    }else {
                        submitFileter.setParentId(null);
                        break;
                    }
                }
            }
        }

        //删除取消关联的菜单,并且删除这些菜单的按钮
        if (CollectionUtils.isNotEmpty(tenantMenuIdFilter)) {
            tenantMenuIdFilter.forEach(i -> {
                tenantMenuService.delTenantMenu(i.toString(), tenantId.toString());
            });
            //批量删除角色菜单接口
            roleMenuSvc.deleteMenuIdByIds(tenantMenuIdFilter);
        }
        return tenantMenuList;
    }


    /**
     * 新增租户菜单
     *
     * @param addTenantMenuDtos
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增租户菜单")
    @ApiImplicitParam(name = "addTenantMenuDtos", value = "入参实体", dataType = "AddTenantMenuDto", paramType = "body", allowMultiple = true)
    @RequestMapping(value = "/saveTenantMenus", method = RequestMethod.POST)
    @ResponseBody
    public List<TenantMenuVo> saveTenantMenus(@RequestBody List<AddTenantMenuDto> addTenantMenuDtos) throws ThingsboardException {
        try {
            List<TenantMenuVo> tenantMenuVos = new ArrayList<>();
            //校验参数
            List<TenantMenu> tenantMenuList = checkAddTenantMenuList(addTenantMenuDtos);
            tenantMenuList = checkNotNull(tenantMenuService.saveTenantMenuList(tenantMenuList));
            tenantMenuList.forEach(i -> {
                tenantMenuVos.add(new TenantMenuVo(i));
            });
            return tenantMenuVos;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 修改租户菜单
     *
     * @param updTenantMenuDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("修改租户菜单")
    @ApiImplicitParam(name = "updTenantMenuDto", value = "入参实体", dataType = "UpdTenantMenuDto", paramType = "body")
    @RequestMapping(value = "/updTenantMenu", method = RequestMethod.PUT)
    @ResponseBody
    public List<TenantMenuVo> updTenantMenu(@RequestBody UpdTenantMenuDto updTenantMenuDto) throws ThingsboardException {
        try {
            List<TenantMenuVo> tenantMenuVos = new ArrayList<>();
            //校验参数
            checkNotNull(updTenantMenuDto);
            checkParameter("tenantId", updTenantMenuDto.getTenantId());
            checkParameter("id", updTenantMenuDto.getId());
            checkParameter("menuType", updTenantMenuDto.getMenuType());
            TenantMenu tenantMenu = updTenantMenuDto.toTenantMenu();
            tenantMenu.setUpdatedUser(getCurrentUser().getUuidId());
            List<TenantMenu> tenantMenuList = tenantMenuService.updTenantMenu(tenantMenu);
            tenantMenuList.forEach(i -> {
                tenantMenuVos.add(new TenantMenuVo(i));
            });
            return tenantMenuVos;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 修改租户菜单排序
     *
     * @param id
     * @param frontId id前面一个菜单
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("修改租户菜单排序")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "当前菜单", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "frontId", value = "移动到指定位置后，前面一个菜单标识", dataType = "String", paramType = "query")})
    @RequestMapping(value = "/updTenantMenuSort", method = RequestMethod.PUT)
    @ResponseBody
    public List<TenantMenuVo> updTenantMenuSort(@RequestParam String id, @RequestParam String frontId) throws ThingsboardException {
        try {
            List<TenantMenuVo> tenantMenuVos = new ArrayList<>();
            //校验参数
            checkParameter("id", id);
            checkParameter("前面一个菜单", frontId);
            List<TenantMenu> tenantMenuList = tenantMenuService.updTenantMenuSort(id, frontId);
            tenantMenuList.forEach(i -> {
                tenantMenuVos.add(new TenantMenuVo(i));
            });
            return tenantMenuVos;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 删除租户菜单
     *
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("删除租户菜单")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "当前菜单", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户标识")})
    @RequestMapping(value = "/delTenantMenu", method = RequestMethod.DELETE)
    @ResponseBody
    public List<TenantMenuVo> delTenantMenu(@RequestParam(required = true) String id, @RequestParam(required = true) String tenantId) throws ThingsboardException {
        try {
            List<TenantMenuVo> tenantMenuVos = new ArrayList<>();
            //校验参数
            checkParameter("id", id);
            checkParameter("tenantId", tenantId);
            List<TenantMenu> tenantMenuList = checkNotNull(tenantMenuService.delTenantMenu(id, tenantId));
            tenantMenuList.forEach(i -> {
                tenantMenuVos.add(new TenantMenuVo(i));
            });
            return tenantMenuVos;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询租户菜单列表
     *
     * @param tenantMenuQry
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询租户菜单列表")
    @ApiImplicitParam(name = "tenantMenuQry", value = "入参实体", dataType = "TenantMenuQry", paramType = "query")
    @RequestMapping(value = "/getTenantMenuList", method = RequestMethod.GET)
    @ResponseBody
    public List<TenantMenuVo> getTenantMenuList(TenantMenuQry tenantMenuQry) throws ThingsboardException {
        try {
            List<TenantMenuVo> tenantMenuVos = new ArrayList<>();
            TenantMenu tenantMenu = tenantMenuQry.toTenantMenu();
            if (tenantMenuQry.getTenantId() == null) {
                tenantMenu.setTenantId(getCurrentUser().getTenantId().getId());
            }
            List<TenantMenu> tenantMenuList = tenantMenuService.getTenantMenuList(tenantMenu);
            if (CollectionUtils.isNotEmpty(tenantMenuList)) {
                tenantMenuList.forEach(i -> {
                    tenantMenuVos.add(new TenantMenuVo(i));
                });
            }
            return tenantMenuVos;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 根据菜单标识查询菜单详情信息
     */
    @ApiOperation(value = "根据菜单标识查询菜单详情信息")
    @ApiImplicitParam(name = "id", value = "当前菜单id", dataType = "String", paramType = "path", required = true)
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
