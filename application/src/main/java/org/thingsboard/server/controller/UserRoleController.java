package org.thingsboard.server.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryUserVo;
import org.thingsboard.server.common.data.vo.rolevo.RoleBindUserVo;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.util.BeanToMap;
import org.thingsboard.server.dao.util.sql.jpa.repository.SortRowName;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.role.PageRoleVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.userrole.UserRoleMemuSvc;

import javax.validation.Valid;
import java.util.HashMap;
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
    @Autowired private UserRoleMemuSvc userRoleMemuSvc;

    @ApiOperation(value = "角色的新增接口")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public   TenantSysRoleEntity  save(@RequestBody  TenantSysRoleEntity  entity) throws ThingsboardException {
        SecurityUser securityUser =  getCurrentUser();
        entity.setUpdatedUser(securityUser.getUuidId());
        entity.setTenantId(securityUser.getTenantId().getId());
        if(entity.getId() != null)
        {
           return updateRecord(entity);
        }
        entity.setCreatedUser(securityUser.getUuidId());

        return   tenantSysRoleService.saveEntity(entity);
    }

    @ApiOperation(value = "角色的更新接口")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public   TenantSysRoleEntity  updateRecord(@RequestBody  TenantSysRoleEntity  entity) throws ThingsboardException {
        SecurityUser securityUser =  getCurrentUser();
        entity.setUpdatedUser(securityUser.getUuidId());
        entity.setRoleCode(null);
        return   tenantSysRoleService.updateRecord(entity);
    }

    @ApiOperation(value = "角色id的详情的查询")
    @RequestMapping(value = "/getRoleById/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getRoleById(@PathVariable("roleId") String roleId) throws ThingsboardException {
        return   tenantSysRoleService.queryById(toUUID(roleId));
    }


    @ApiOperation(value = "角色模块的 无参查询全部数据")
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public Object findAll() throws ThingsboardException {
        Map<String, Object> queryParam   = new HashMap<>();
        return   tenantSysRoleService.findAll(queryParam);
    }


    @ApiOperation(value = "角色的删除接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "用户id"),})
    @RequestMapping(value = "/delete/{roleId}", method = RequestMethod.DELETE)
    @ResponseBody
    public   Object delete(@PathVariable("roleId") String roleId) throws ThingsboardException {
        log.info("删除角色的接口入参:{}",roleId);
         long count =    userMenuRoleService.countAllByTenantSysRoleId(strUuid(roleId));
         if(count > 0)
         {
             throw new ThingsboardException("当前角色存在绑定的用户!不能直接删除!", ThingsboardErrorCode.FAIL_VIOLATION);
         }
            tenantSysRoleService.deleteById(strUuid(roleId));
            userRoleMemuSvc.deleteRoleByRole(strUuid(roleId));
            tenantMenuRoleService.deleteByTenantSysRoleId(strUuid(roleId));
            return "success";



    }



    @ApiOperation(value = "角色的分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleCode", value = "角色编码支持模糊查询"),
            @ApiImplicitParam(name = "roleName", value = "角色名称支持模糊查询"),
    })
    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public Object pageQuery(
            @RequestParam("roleCode") String roleCode,
            @RequestParam("roleName") String roleName,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        SecurityUser securityUser =  getCurrentUser();
        Map<String, Object> queryParam  =new HashMap<>();
        queryParam.put("updatedUser",securityUser.getUuidId().toString());
        if(!StringUtils.isEmpty(roleCode))
        {
            queryParam.put("roleCode", roleCode);
        }
        if(!StringUtils.isEmpty(roleName))
        {
            queryParam.put("roleName", roleName);
        }
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return tenantSysRoleService.pageQuery(queryParam,pageLink);
    }




    /**
     * 用户角色的绑定接口
     * @return
     */
    @ApiOperation(value = "角色用户绑定")
    @RequestMapping(value = "/relationUser", method = RequestMethod.POST)
    public Object  relationUser(@RequestBody @Valid RoleBindUserVo vo, BindingResult result)
    {
        if (result.hasErrors()) {
            return ResultVo.getFail("入参校验错误: " +result.getFieldError().getDefaultMessage());
        }
        log.info("[角色用户绑定]打印得入参为:{}",vo);

      return   userRoleMemuSvc.relationUserAndRole(vo);
    }



    @ApiOperation(value = "角色用户解绑【一个角色解绑多个用户】")
    @RequestMapping(value = "/unboundUser", method = RequestMethod.POST)
    public Object  unboundUser(@RequestBody @Valid RoleBindUserVo vo, BindingResult result)
    {
        if (result.hasErrors()) {
            return ResultVo.getFail("入参校验错误: " +result.getFieldError().getDefaultMessage());
        }

        log.info("[角色用户解绑]打印得入参为:{}",vo);

        return   userRoleMemuSvc.unboundUser(vo);
    }


    /**
     * 角色查询用户已绑定的用户
     * @return
     */
    @ApiOperation(value = "角色查询已绑定用户【分页查询】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id"),
            @ApiImplicitParam(name = "UserCode", value = "用户编码"),
            @ApiImplicitParam(name = "UserCode", value = "用户编码"),
    })
    @RequestMapping(value = "/getUserByInRole/{roleId}/users", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public Object getUserByInRole(
            @PathVariable("roleId") UUID roleId,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String userCode,
            @RequestParam(required = false) String userName

            ) throws ThingsboardException {


        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        QueryUserVo  vo = new QueryUserVo();
        vo.setRoleId(roleId);
        vo.setUserName(userName);
        vo.setUserCode(userCode);
        log.info("打印当前的入参:{}",vo);
       return userRoleMemuSvc.getUserByInRole(vo,pageLink,new SortRowName(sortProperty,sortOrder));

    }




    /**
     * 角色查询用户mei绑定的用户
     * @return
     */
    @ApiOperation(value = "角色查询未绑定用户【分页查询】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id"),
            @ApiImplicitParam(name = "UserCode", value = "用户编码"),
            @ApiImplicitParam(name = "UserCode", value = "用户编码"),
    })
    @RequestMapping(value = "/getUserByNotInRole/{roleId}/users", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public Object getUserByNotInRole(
            @PathVariable("roleId") UUID roleId,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String userCode,
            @RequestParam(required = false) String userName

    ) throws ThingsboardException {


        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        QueryUserVo  vo = new QueryUserVo();
        vo.setRoleId(roleId);
        vo.setUserName(userName);
        vo.setUserCode(userCode);
        log.info("打印当前的入参:{}",vo);
        SecurityUser securityUser =  getCurrentUser();
        vo.setTenantId(securityUser.getTenantId().getId());
        vo.setCreateId(securityUser.getUuidId());

        return userRoleMemuSvc.getUserByNotInRole(vo,pageLink,new SortRowName(sortProperty,sortOrder));

    }










    private UUID   strUuid(String  strId)
    {
        UUID uuid2=UUID.fromString(strId);
        return  uuid2;
    }

}
