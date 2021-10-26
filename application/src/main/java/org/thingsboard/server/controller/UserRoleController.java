package org.thingsboard.server.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.sql.role.entity.TenantSysRoleEntity;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.sql.role.service.UserMenuRoleService;
import org.thingsboard.server.dao.util.sql.jpa.transform.NameTransform;
import org.thingsboard.server.entity.role.PageRoleVo;
import org.thingsboard.server.entity.role.ResultVo;
import org.thingsboard.server.entity.role.UserRoleVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.userrole.UserRoleMemuSvc;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
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
        entity.setCreatedUser(securityUser.getUuidId());
        return   tenantSysRoleService.saveEntity(entity);
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
    public   Object   delete(@RequestParam("roleId") String roleId) throws ThingsboardException
    {
           log.info("删除角色的接口入参:{}",roleId);
           tenantSysRoleService.deleteById(strUuid(roleId));
            userRoleMemuSvc.deleteRoleByRole(strUuid(roleId));
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


    /**
     * 用户角色的绑定接口
     * @return
     */
    @ApiOperation(value = "角色用户绑定")
    @RequestMapping(value = "/relationUser", method = RequestMethod.POST)
    public Object  relationUser(@RequestBody @Valid UserRoleVo  vo, BindingResult result)
    {
        if (result.hasErrors()) {
            return ResultVo.getFail("入参校验错误: " +result.getFieldError().getDefaultMessage());
        }
        log.info("[角色用户绑定]打印得入参为:{}",vo);
      return   userRoleMemuSvc.relationUser(vo);
    }

    @Autowired  private UserMenuRoleService userMenuRoleService;
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public    List<UserMenuRoleEntity>  findAll() throws Exception {
        userMenuRoleService.findById(UUID.fromString("98786220-3607-11ec-946b-8fb7d82943df"));
        UserMenuRoleEntity entity = new UserMenuRoleEntity();
                                        //98786220-3607-11ec-946b-8fb7d82943df
        entity.setUserId(UUID.fromString("98786220-3607-11ec-946b-8fb7d82943df"));
        List<UserMenuRoleEntity> list = userMenuRoleService.findAllByUserMenuRoleEntity(entity);
         return  list;
    }

    @RequestMapping(value = "/test02", method = RequestMethod.POST)
    public  void   test02 ()
    {
        UserMenuRoleEntity  entity = new UserMenuRoleEntity();
        entity.setRemark("1");
        userMenuRoleService.deleteByEntity(entity);
    }





    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public   Object   find()
    {


        String sql ="select role_code as deptName111 ,role_name as roleName from tb_tenant_sys_role  where role_code=:role_code ";//select convert(varchar(255),deptName) as deptName
        Map<String, Object> param= new HashMap<>();
        param.put("role_code","系统管理员4");
        boolean isNativeSql=true;
        PageLink pageLink = new PageLink(10,0);
        Pageable pageable=   DaoUtil.toPageable(pageLink);
        Page<PageRoleVo> mapPage= tenantSysRoleService.querySql(sql,param, PageRoleVo.class,pageable, NameTransform.UN_CHANGE,isNativeSql);
        System.out.println("打印当前的数据:"+mapPage);
        System.out.println("打印当前的数据:"+mapPage.getContent());
       List<PageRoleVo> pageRoleVos =  mapPage.getContent();
        System.out.println("打印当前的数据pageRoleVos:"+pageRoleVos);

        return  pageRoleVos;
    }





    private UUID   strUuid(String  strId)
    {
        UUID uuid2=UUID.fromString(strId);
        return  uuid2;
    }

}
