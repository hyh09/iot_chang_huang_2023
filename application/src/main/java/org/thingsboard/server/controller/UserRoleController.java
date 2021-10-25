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
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

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
    public   void   delete(@RequestParam("roleId") String roleId) throws ThingsboardException
    {
         log.info("删除角色的接口入参:{}",roleId);
           tenantSysRoleService.deleteById(strUuid(roleId));
    }


    private UUID   strUuid(String  strId)
    {
        UUID uuid2=UUID.fromString(strId);
        return  uuid2;
    }

}
