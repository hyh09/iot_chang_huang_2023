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
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryUserVo;
import org.thingsboard.server.common.data.vo.rolevo.RoleBindUserVo;
import org.thingsboard.server.common.data.vo.user.UpdateOperationVo;
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
@Api(value = "??????????????????", tags = {"?????????????????????"})
public class UserRoleController extends BaseController{



    @Autowired
    private TenantSysRoleService tenantSysRoleService;
    @Autowired private UserRoleMemuSvc userRoleMemuSvc;

    @ApiOperation(value = "?????????????????????")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public   TenantSysRoleEntity  save(@RequestBody  TenantSysRoleEntity  entity) throws ThingsboardException {
        SecurityUser securityUser =  getCurrentUser();
        if(securityUser.getUserLevel() == 3){
            entity.setOperationType(1);
        }

        DataValidator.validateCode(entity.getRoleCode());
        entity.setUpdatedUser(securityUser.getUuidId());
        entity.setTenantId(securityUser.getTenantId().getId());
        entity.setUserLevel(securityUser.getUserLevel());
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
            throw new ThingsboardException("?????????????????????????????????["+entity.getRoleCode()+"]????????????!", ThingsboardErrorCode.FAIL_VIOLATION);
        }
        var data = tenantSysRoleService.saveEntity(entity);
        saveAuditLog(getCurrentUser(), null, EntityType.USER_ROLE, null, ActionType.ADDED, entity);
        return data;
    }

    @ApiOperation(value = "?????????????????????")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public   TenantSysRoleEntity  updateRecord(@RequestBody  TenantSysRoleEntity  entity) throws ThingsboardException {
        SecurityUser securityUser =  getCurrentUser();
        entity.setUpdatedUser(securityUser.getUuidId());
        entity.setRoleCode(null);
        var data =   tenantSysRoleService.updateRecord(entity);
        saveAuditLog(getCurrentUser(), null, EntityType.USER_ROLE, null, ActionType.UPDATED, entity);
        return data;
    }

    @ApiOperation(value = "??????id??????????????????")
    @RequestMapping(value = "/getRoleById/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getRoleById(@PathVariable("roleId") String roleId) throws ThingsboardException {
        return   tenantSysRoleService.queryById(toUUID(roleId));
    }


    @ApiOperation(value = "??????????????? ????????????????????????")
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
        tenantSysRoleEntity.setOperationType(null);
        tenantSysRoleEntity.setUserLevelList(setParametersByRoleLevel());
        List<TenantSysRoleEntity>  result01= tenantSysRoleService.findAllByTenantSysRoleEntity(tenantSysRoleEntity);
        return  result01;

    }


    @ApiOperation(value = "?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "??????id"),})
    @RequestMapping(value = "/delete/{roleId}", method = RequestMethod.DELETE)
    @ResponseBody
    public   Object delete(@PathVariable("roleId") String roleId) throws ThingsboardException {
        log.info("???????????????????????????:{}",roleId);
         long count =    userMenuRoleService.countAllByTenantSysRoleId(strUuid(roleId));
         if(count > 0)
         {
             throw new ThingsboardException("?????????????????????????????????!??????????????????!", ThingsboardErrorCode.FAIL_VIOLATION);
         }
        TenantSysRoleEntity  tenantSysRoleEntity=    tenantSysRoleService.findById(strUuid(roleId));
         if(tenantSysRoleEntity != null &&  tenantSysRoleEntity.getSystemTab().equals("1"))
         {
             throw new ThingsboardException("????????????????????????????????????????????????!", ThingsboardErrorCode.FAIL_VIOLATION);

         }
            tenantSysRoleService.deleteById(strUuid(roleId));
            userRoleMemuSvc.deleteRoleByRole(strUuid(roleId));
            tenantMenuRoleService.deleteByTenantSysRoleId(strUuid(roleId));
        saveAuditLog(getCurrentUser(), toUUID(roleId), EntityType.USER_ROLE, null, ActionType.DELETED, roleId);

        return "success";


        
    }



    @ApiOperation(value = "?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleCode", value = "??????????????????????????????"),
            @ApiImplicitParam(name = "roleName", value = "??????????????????????????????"),
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
        try {
            SecurityUser securityUser = getCurrentUser();
            Map<String, Object> queryParam = new HashMap<>();
            if (!StringUtils.isEmpty(roleCode)) {
                queryParam.put("roleCode", roleCode);
            }
            if (!StringUtils.isEmpty(roleName)) {
                queryParam.put("roleName", roleName);
            }

            queryParam.put("tenantId", securityUser.getTenantId().getId());
            if (securityUser.getType().equals(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode())) {
                log.info("????????????????????????????????????");
                queryParam.put("factoryId", securityUser.getFactoryId());
            }
            queryParam.put("systemTab", "0");
            queryParam.put("type", securityUser.getType());
            setParametersByRoleLevel(queryParam);
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            PageData<TenantSysRoleEntity> roleEntityPageData = tenantSysRoleService.pageQuery(queryParam, pageLink);
            return roleEntityPageData;
        }catch (Exception e)
        {
            log.info("===>??????????????????????????????:{}",e);
            return  new PageData<TenantSysRoleEntity>();
        }
    }




    /**
     * ???????????????????????????
     * @return
     */
    @ApiOperation(value = "??????????????????")
    @RequestMapping(value = "/relationUser", method = RequestMethod.POST)
    public Object  relationUser(@RequestBody @Valid RoleBindUserVo vo, BindingResult result) throws ThingsboardException {
        if (result.hasErrors()) {
            return ResultVo.getFail("??????????????????: " +result.getFieldError().getDefaultMessage());
        }
        log.info("[??????????????????]??????????????????:{}",vo);
    
      var data =   userRoleMemuSvc.relationUserAndRole(vo, getTenantId());
        saveAuditLog(getCurrentUser(), null, EntityType.USER_ROLE, null, ActionType.UPDATED, vo);

        return data;
    }



    @ApiOperation(value = "??????????????????????????????????????????????????????")
    @RequestMapping(value = "/unboundUser", method = RequestMethod.POST)
    public Object  unboundUser(@RequestBody @Valid RoleBindUserVo vo, BindingResult result) throws ThingsboardException {
        if (result.hasErrors()) {
            return ResultVo.getFail("??????????????????: " +result.getFieldError().getDefaultMessage());
        }

        log.info("[??????????????????]??????????????????:{}",vo);

        var data =   userRoleMemuSvc.unboundUser(vo);
        saveAuditLog(getCurrentUser(), null, EntityType.USER_ROLE, null, ActionType.UPDATED, vo);

        return data;
    }


    /**
     * ????????????????????????????????????
     * @return
     */
    @ApiOperation(value = "?????????????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "??????id"),
            @ApiImplicitParam(name = "UserCode", value = "????????????"),
            @ApiImplicitParam(name = "UserCode", value = "????????????"),
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
        log.info("?????????????????????:{}",vo);
       return userRoleMemuSvc.getUserByInRole(vo,pageLink,new SortRowName(sortProperty,sortOrder));

    }




    /**
     * ??????????????????mei???????????????
     * @return
     */
    @ApiOperation(value = "?????????????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "??????id"),
            @ApiImplicitParam(name = "UserCode", value = "????????????"),
            @ApiImplicitParam(name = "UserCode", value = "????????????"),
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
        log.info("?????????????????????:{}",vo);
        SecurityUser securityUser =  getCurrentUser();
        vo.setTenantId(securityUser.getTenantId().getId());
        if (securityUser.getType().equals(CreatorTypeEnum.FACTORY_MANAGEMENT.getCode())) {
            log.info("??????????????????????????????????????????,?????????????????????????????????:{}", securityUser.getFactoryId());
             vo.setFactoryId(securityUser.getFactoryId());
        }
        vo.setType(securityUser.getType());
        vo.setUserLevel(securityUser.getUserLevel());//??????????????????
        return userRoleMemuSvc.getUserByNotInRole(vo,pageLink,new SortRowName(sortProperty,sortOrder));

    }



    /**
     * ????????????
     */
    @ApiOperation(value = "????????????-???????????????????????????")
    @RequestMapping(value="/updateOperationType",method = RequestMethod.POST)
    @ResponseBody
    public UpdateOperationVo updateOperationType(@RequestBody @Valid UpdateOperationVo vo) throws ThingsboardException {
        return   tenantSysRoleService.updateOperationType(vo);
    }







    private UUID   strUuid(String  strId)
    {
        UUID uuid2=UUID.fromString(strId);
        return  uuid2;
    }

}
