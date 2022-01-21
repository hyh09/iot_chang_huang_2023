package org.thingsboard.server.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryUserVo;
import org.thingsboard.server.common.data.vo.rolevo.RoleBindUserVo;
import org.thingsboard.server.common.data.vo.user.enums.CreatorTypeEnum;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.sql.role.userrole.ResultVo;
import org.thingsboard.server.dao.sql.role.userrole.UserRoleMemuSvc;
import org.thingsboard.server.dao.util.sql.jpa.repository.SortRowName;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import org.thingsboard.server.service.userrole.UserRoleMemuSvc;

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
        if(securityUser.getUserLevel() == 3){
            entity.setOperationType("1");
        }

        DataValidator.validateCode(entity.getRoleCode());
        entity.setUpdatedUser(securityUser.getUuidId());
        entity.setTenantId(securityUser.getTenantId().getId());
        if(entity.getId() != null)
        {
           return updateRecord(entity);
        }
        entity.setCreatedUser(securityUser.getUuidId());
        entity.setType(securityUser.getType());
        entity.setFactoryId(securityUser.getFactoryId());
        TenantSysRoleEntity roleData=  tenantSysRoleService.queryEntityBy(entity.getRoleCode(),securityUser.getTenantId().getId());
        if(roleData != null )
        {
            throw new ThingsboardException("添加角色失败：角色编码["+entity.getRoleCode()+"]已经存在!", ThingsboardErrorCode.FAIL_VIOLATION);
        }
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
    public Object findAll() throws Exception {
        SecurityUser securityUser =  getCurrentUser();
        TenantSysRoleEntity  tenantSysRoleEntity = new TenantSysRoleEntity();

        if(securityUser.getType().equals(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode()))
        {
//            tenantSysRoleEntity.setType(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode());
            tenantSysRoleEntity.setFactoryId(securityUser.getFactoryId());
        }
        tenantSysRoleEntity.setType(securityUser.getType());
        tenantSysRoleEntity.setSystemTab("0");
        tenantSysRoleEntity.setTenantId(getTenantId().getId());
        List<TenantSysRoleEntity>  result01= tenantSysRoleService.findAllByTenantSysRoleEntity(tenantSysRoleEntity);
        return  result01;
//        if(securityUser.getType().equals(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode()))
//        {
//            log.info("如果是工厂管理员直接返回,不生成系统生成的工厂管理角色:");
//            return  result01;
//        }
//       Long count1= result01.stream().filter(r->r.getRoleCode().equals(RoleEnums.FACTORY_ADMINISTRATOR.getRoleCode())).count();
//       if(count1>0)
//       {
//           return  result01;
//       }
//        TenantSysRoleEntity entity = new TenantSysRoleEntity();
//        entity.setCreatedUser(securityUser.getUuidId());
//        entity.setUpdatedUser(securityUser.getUuidId());
//        entity.setRoleCode(RoleEnums.FACTORY_ADMINISTRATOR.getRoleCode());
//        entity.setRoleName(RoleEnums.FACTORY_ADMINISTRATOR.getRoleName());
//        entity.setTenantId(getTenantId().getId());
//        entity.setType(CreatorTypeEnum.TENANT_CATEGORY.getCode());
//        entity.setSystemTab("1");
//        TenantSysRoleEntity rmEntity=  tenantSysRoleService.saveEntity(entity);
//        result01.add(rmEntity);
//        return  result01;
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
        TenantSysRoleEntity  tenantSysRoleEntity=    tenantSysRoleService.findById(strUuid(roleId));
         if(tenantSysRoleEntity != null &&  tenantSysRoleEntity.getSystemTab().equals("1"))
         {
             throw new ThingsboardException("当前角色是系统创建角色不允许删除!", ThingsboardErrorCode.FAIL_VIOLATION);

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
            @RequestParam(value = "roleCode",required = false) String roleCode,
            @RequestParam(value = "roleName",required = false) String roleName,
            @RequestParam int pageSize,
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        SecurityUser securityUser =  getCurrentUser();
        Map<String, Object> queryParam  =new HashMap<>();
//        queryParam.put("updatedUser",securityUser.getUuidId().toString());
        if(!StringUtils.isEmpty(roleCode))
        {
            queryParam.put("roleCode", roleCode);
        }
        if(!StringUtils.isEmpty(roleName))
        {
            queryParam.put("roleName", roleName);
        }

          queryParam.put("tenantId",securityUser.getTenantId().getId());
        if(securityUser.getType().equals(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode()))
        {
            log.info("当前用户是工厂类别的用户");
            queryParam.put("factoryId", securityUser.getFactoryId());
        }
        queryParam.put("systemTab","0");
        queryParam.put("type",securityUser.getType());

        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        PageData<TenantSysRoleEntity>  roleEntityPageData  = tenantSysRoleService.pageQuery(queryParam,pageLink);
        if(securityUser.getUserLevel() ==  3){
            List<TenantSysRoleEntity>  list =    roleEntityPageData.getData();
            list.stream().forEach(m1->{
                m1.setOperationType("0");
            });
        }
        return  roleEntityPageData;
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
//        vo.setCreateId(securityUser.getUuidId());

        if (securityUser.getType().equals(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode())) {
            log.info("如果当前用户如果是工厂类别的,就查询当前工厂下的数据:{}", securityUser.getFactoryId());
             vo.setFactoryId(securityUser.getFactoryId());
        }
        vo.setType(securityUser.getType());



        return userRoleMemuSvc.getUserByNotInRole(vo,pageLink,new SortRowName(sortProperty,sortOrder));

    }










    private UUID   strUuid(String  strId)
    {
        UUID uuid2=UUID.fromString(strId);
        return  uuid2;
    }

}
