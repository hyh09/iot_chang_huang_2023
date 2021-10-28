package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.rolemenu.InMenuByUserVo;
import org.thingsboard.server.entity.rolemenu.RoleMenuVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.userrole.RoleMenuSvc;

import javax.validation.Valid;

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


    @ApiOperation(value = "角色模块下的 【配置的权限菜单的查询】")
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST)
    @ResponseBody
    public Object queryAll(@RequestBody @Valid InMenuByUserVo vo) {
//        if (result.hasErrors()) {
//            return ResultVo.getFail("入参校验错误: " +result.getFieldError().getDefaultMessage());
//        }
        try {
            SecurityUser  securityUser=  getCurrentUser();
            vo.setUserId(securityUser.getUuidId());
        } catch (ThingsboardException e) {
            e.printStackTrace();
            return ResultVo.getFail(e.getMessage());
        }

        log.info("[角色用户绑定]打印得入参为:{}",vo);
        return   roleMenuSvc.queryAll(vo);
    }



}
