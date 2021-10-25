package org.thingsboard.server.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/role")
@Api(value = "用户角色模块", tags = {"角色相关的接口"})
public class UserRoleController extends BaseController{



    @Autowired
    private TenantSysRoleService tenantSysRoleService;

    @ApiOperation(value = "角色的新增接口")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public   TenantSysRoleEntity  save(@RequestBody  TenantSysRoleEntity  entity) throws ThingsboardException {
        SecurityUser securityUser =  getCurrentUser();
        entity.setCreatedUser(securityUser.getUuidId());
        return   tenantSysRoleService.save(entity);
    }

    @ApiOperation(value = "角色的更新接口")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public   TenantSysRoleEntity  updateRecord(@RequestBody  TenantSysRoleEntity  entity) throws ThingsboardException {
        SecurityUser securityUser =  getCurrentUser();
        entity.setUpdatedUser(securityUser.getUuidId());
        entity.setRoleCode(null);
        return   tenantSysRoleService.updateRecord(entity);
    }

    @ApiOperation(value = "角色的删除接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "用户id"),})
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public   String   delete(@RequestParam("roleId") String roleId) throws ThingsboardException
    {
           log.info("删除角色的接口入参:{}",roleId);
           tenantSysRoleService.deleteById(strUuid(roleId));
        return "success";

    }



    @ApiOperation(value = "角色的分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页大小，默认10"),
            @ApiImplicitParam(name = "page", value = "当前页,起始页，【0开始】"),
            @ApiImplicitParam(name = "textSearch", value = ""),
            @ApiImplicitParam(name = "sortOrder", value = ""),
            @ApiImplicitParam(name = "sortProperty", value = ""),
            @ApiImplicitParam(name = "roleName", value = "角色名称"),
            @ApiImplicitParam(name = "roleCode", value = "角色编码"),
            @ApiImplicitParam(name = "roleDesc", value = "描述")

    })
    @RequestMapping(value = "/pageQuery", method = RequestMethod.POST)
    @ResponseBody
    public   Object   pageQuery(@RequestBody Map<String, Object> queryParam) throws ThingsboardException
    {
        int  pageSize =  ((queryParam.get("pageSize"))==null ?10:(int) queryParam.get("pageSize"));
        int  page =  ((queryParam.get("page"))==null ?0:(int) queryParam.get("page"));
        String  textSearch = (String) queryParam.get("textSearch");
        String  sortProperty = (String) queryParam.get("sortProperty");
        String  sortOrder = (String) queryParam.get("sortOrder");
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
       return tenantSysRoleService.pageQuery(queryParam,pageLink);

    }



    private UUID   strUuid(String  strId)
    {
        UUID uuid2=UUID.fromString(strId);
        return  uuid2;
    }

}
