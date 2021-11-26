package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.vo.menu.TenantMenuVo;
import org.thingsboard.server.dao.sql.role.userrole.ResultVo;
import org.thingsboard.server.dao.sql.role.service.rolemenu.InMenuByUserVo;
import org.thingsboard.server.dao.sql.role.service.rolemenu.RoleMenuVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

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




    @ApiOperation(value = "角色模块下的 【角色绑定菜单接口")
    @RequestMapping(value = "/binding", method = RequestMethod.POST)
    @ResponseBody
    public String binding(@RequestBody @Valid RoleMenuVo vo, BindingResult result) throws ThingsboardException {
        if (result.hasErrors()) {
            throw new ThingsboardException("There is a problem with the request for input!", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        log.info("[角色用户绑定]打印得入参为:{}",vo);
           roleMenuSvc.binding(vo);
           return  "success";
    }


    @ApiOperation("角色模块下的 【配置的权限菜单的查询】")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuType",value = "菜单类型（PC/APP）",required = true,dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "tenantId",value = "租户标识",required = true,dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "name",value = "菜单名称",dataType = "String",paramType="query")})
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST)
    @ResponseBody
    public List<TenantMenuVo> queryAllNew(@RequestBody @Valid InMenuByUserVo vo) throws Exception {
        SecurityUser securityUser = getCurrentUser();
        vo.setTenantId(securityUser.getTenantId().getId());
        vo.setUserId(securityUser.getUuidId());
         return   roleMenuSvc.queryAllNew(vo);

    }



    @ApiOperation("当前登录用户的菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuType",value = "菜单类型（PC/APP）",required = true,dataType = "String",paramType="query"),
    })
    @RequestMapping(value = "/queryByUser", method = RequestMethod.POST)
    @ResponseBody
    public  List<TenantMenuVo> queryByUser(@RequestBody @Valid InMenuByUserVo vo) {
        try {
            SecurityUser securityUser = getCurrentUser();
            vo.setTenantId(securityUser.getTenantId().getId());
            vo.setUserId(securityUser.getUuidId());
            return roleMenuSvc.queryByUser(vo);
        }catch (Exception e)
        {
            log.info("查询当前登录人的用户:{}",e);
            e.printStackTrace();
            return  null;
        }

    }


    @ApiOperation(value = "角色模块下的 【配置的权限菜单的查询】  放弃使用")
    @RequestMapping(value = "/queryAllOld", method = RequestMethod.POST)
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
