package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;
import org.thingsboard.server.entity.factory.dto.AddFactoryDto;
import org.thingsboard.server.entity.factory.dto.FactoryVersionDto;
import org.thingsboard.server.entity.factory.dto.QueryFactoryDto;
import org.thingsboard.server.entity.factory.vo.FactoryVersionVo;
import org.thingsboard.server.entity.factory.vo.FactoryVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;

@Api(value="工厂管理Controller",tags={"工厂管理接口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/factory")
public class FactoryController extends BaseController  {

    @Autowired
    private UserRoleMenuSvc userRoleMenuSvc;

    /**
     * 新增/更新工厂
     * @param addFactoryDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增/更新工厂")
    @ApiImplicitParam(name = "addFactoryDto",value = "入参实体",dataType = "AddFactoryDto",paramType="body")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public FactoryVo saveFactory(@RequestBody AddFactoryDto addFactoryDto) throws ThingsboardException {
        try {
            checkNotNull(addFactoryDto);
            Factory factory = addFactoryDto.toFactory();
            if(addFactoryDto.getId() == null){
                factory.setCreatedUser(getCurrentUser().getUuidId());
                factory = checkNotNull(factoryService.saveFactory(factory));
            }else {
                factory.setUpdatedUser(getCurrentUser().getUuidId());
                factory =  checkNotNull(factoryService.updFactory(factory));
            }
            return new FactoryVo(factory);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 删除工厂
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("删除工厂")
    @ApiImplicitParam(name = "id",value = "工厂标识",dataType = "string",paramType="query",required = true)
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delFactory(@PathVariable("id") String id) throws ThingsboardException {
        try {
            checkParameter("id",id);
            factoryService.delFactory(toUUID(id));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询租户下所有工厂列表
     * @param tenantId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询租户下所有工厂列表")
    @ApiImplicitParam(name = "tenantId",value = "租户标识",dataType = "string",paramType = "query",required = true)
    @RequestMapping(value = "/findFactoryList", method = RequestMethod.GET)
    @ResponseBody
    public List<FactoryVo> findFactoryList(@RequestParam String tenantId) throws ThingsboardException {
        try {
            List<FactoryVo> factoryVoList = new ArrayList<>();
            if(StringUtils.isEmpty(tenantId)){
                tenantId = getCurrentUser().getTenantId().getId().toString();
            }
            List<Factory> factoryList = factoryService.findFactoryList(toUUID(tenantId));
            if(!CollectionUtils.isEmpty(factoryList)){
                factoryList.forEach(i->{
                    factoryVoList.add(new FactoryVo(i));
                });
            }
            return factoryVoList;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 条件查询工厂列表
     * @param queryFactoryDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("条件查询工厂列表")
    @ApiImplicitParam(name = "queryFactoryDto",value = "入参对象",dataType = "QueryFactoryDto",paramType = "query")
    @RequestMapping(value = "/findFactoryListBuyCdn", method = RequestMethod.GET)
    @ResponseBody
    public FactoryListVo findFactoryListBuyCdn(QueryFactoryDto queryFactoryDto) throws ThingsboardException {
        try {
            checkParameter("没有获取到租户tenantId",getCurrentUser().getTenantId().getId());
            Factory factory = queryFactoryDto.toFactory();
            factory.setTenantId(getCurrentUser().getTenantId().getId());
            factory.setLoginUserId(getCurrentUser().getId().getId());
            return checkNotNull(factoryService.findFactoryListBuyCdn(factory));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询工厂详情
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询工厂详情")
    @ApiImplicitParam(name = "id",value = "当前id",dataType = "String",paramType="path",required = true)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public FactoryVo findById(@PathVariable("id") String id) throws ThingsboardException {
        try {
            checkParameter("id",id);
            return new FactoryVo(checkNotNull(factoryService.findById(toUUID(id))));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询工厂最新版本
     * @param queryFactoryDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询工厂最新版本")
    @ApiImplicitParam(name = "queryFactoryDto",value = "入参对象",dataType = "FactoryVersionDto",paramType = "query")
    @RequestMapping(value = "/findFactoryVersion", method = RequestMethod.GET)
    @ResponseBody
    public List<FactoryVersionVo> findFactoryVersion(FactoryVersionDto queryFactoryDto) throws ThingsboardException {
        try {
            List<FactoryVersionVo> resultVo = new ArrayList<>();
            checkParameter("没有获取到登录人所在租户tenantId",getCurrentUser().getTenantId().getId());
            Factory factory = queryFactoryDto.toFactory();
            factory.setTenantId(getCurrentUser().getTenantId().getId());
            factory.setLoginUserId(getCurrentUser().getId().getId());
            List<Factory> factoryList = factoryService.findFactoryVersion(factory);
            if(CollectionUtils.isNotEmpty(factoryList)){
                factoryList.forEach(i->{
                    resultVo.add(new FactoryVersionVo(i));
                });
            }
            return resultVo;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 根据登录人角色查询工厂列表
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("根据登录人角色查询工厂列表")
    @RequestMapping(value = "/findFactoryListByLoginRole", method = RequestMethod.GET)
    @ResponseBody
    public List<FactoryVo> findFactoryListByLoginRole() throws ThingsboardException {
        try {
            List<FactoryVo> factoryVoList = new ArrayList<>();
            List<Factory> factoryList = factoryService.findFactoryListByLoginRole(getCurrentUser().getId().getId(),getCurrentUser().getTenantId().getId());
            if(!CollectionUtils.isEmpty(factoryList)){
                factoryList.forEach(i->{
                    factoryVoList.add(new FactoryVo(i));
                });
            }
            return factoryVoList;
        } catch (Exception e) {
            throw handleException(e);
        }
    }
}
