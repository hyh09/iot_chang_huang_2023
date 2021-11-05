package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.tenantmenu.TenantMenu;
import org.thingsboard.server.common.data.vo.menu.TenantMenuVo;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.RoleMenuVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.userrole.RoleMenuSvc;

import javax.validation.Valid;
import java.util.List;

/**
 创建时间: 2021-10-27 13:23:59
 创建人: HU.YUNHUI
 描述:
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/roleMenu")
@Api(value = "角色菜单关系模块", tags = {"角色菜单关系相关的接口"})
public class RoleMenuController extends BaseController{

    @Autowired private RoleMenuSvc roleMenuSvc;


    @ApiOperation(value = "角色模块下的 【角色绑定菜单接口")
    @RequestMapping(value = "/binding", method = RequestMethod.POST)
    @ResponseBody
    public Object binding(@RequestBody @Valid RoleMenuVo vo, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.getFail("入参校验错误: " +result.getFieldError().getDefaultMessage());
        }
        log.info("[角色用户绑定]打印得入参为:{}",vo);
        return   roleMenuSvc.binding(vo);
    }


    @ApiOperation("角色模块下的 【配置的权限菜单的查询】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuType",value = "菜单类型（PC/APP）",required = true,dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "tenantId",value = "租户标识",required = true,dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "name",value = "菜单名称",dataType = "String",paramType="query")})
    @RequestMapping(value = "/queryAllNew", method = RequestMethod.POST)
    @ResponseBody
    public List<TenantMenuVo> queryAllNew(@RequestBody @Valid InMenuByUserVo vo) throws Exception {
        SecurityUser securityUser = getCurrentUser();
        vo.setTenantId(securityUser.getTenantId().getId());
         return   roleMenuSvc.queryAllNew(vo);

    }



    @ApiOperation("当前登录用户的菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuType",value = "菜单类型（PC/APP）",required = true,dataType = "String",paramType="query"),
    })
    @RequestMapping(value = "/queryByUser", method = RequestMethod.POST)
    @ResponseBody
    public List<TenantMenu> queryByUser(@RequestBody @Valid InMenuByUserVo vo) throws Exception {
        SecurityUser securityUser = getCurrentUser();
        vo.setTenantId(securityUser.getTenantId().getId());
        vo.setUserId(securityUser.getUuidId());
        return   roleMenuSvc.queryByUser(vo,securityUser.getTenantId(),securityUser.getId());

    }


    @ApiOperation(value = "角色模块下的 【配置的权限菜单的查询】  放弃使用")
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST)
    @ResponseBody
    public Object queryAll(@RequestBody @Valid InMenuByUserVo vo) {
        if(IS_TEST) {

                try {
                    SecurityUser securityUser = getCurrentUser();
                    if(StringUtils.isEmpty(securityUser.getUserCode())){
                         vo.setRoleId(null);
                    }
//                    vo.setUserId(securityUser.getUuidId());

                } catch (ThingsboardException e) {
                    e.printStackTrace();
                    return ResultVo.getFail(e.getMessage());
                }
        }
        log.info("[角色用户绑定]打印得入参为:{}",vo);
        return   roleMenuSvc.queryAll(vo);
    }



}
