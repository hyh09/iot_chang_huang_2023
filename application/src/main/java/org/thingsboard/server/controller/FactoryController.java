package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.dao.model.sql.AbstractFactoryEntity;
import org.thingsboard.server.dao.model.sql.AbstractProductionLineEntity;
import org.thingsboard.server.dao.model.sql.AbstractWorkshopEntity;
import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;
import org.thingsboard.server.entity.factory.dto.AddFactoryDto;
import org.thingsboard.server.entity.factory.dto.FactoryVersionDto;
import org.thingsboard.server.entity.factory.dto.QueryFactoryDto;
import org.thingsboard.server.entity.factory.vo.*;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
            //地址不能为空
            checkParameter("国家",addFactoryDto.getCountry());
            //checkParameter("省",addFactoryDto.getProvince());
            checkParameter("市",addFactoryDto.getCity());
            //checkParameter("区",addFactoryDto.getArea());
            checkParameter("详细地址",addFactoryDto.getAddress());
            //校验名称是否重复
            checkFactoryName(addFactoryDto.getId(),addFactoryDto.getName());
            Factory factory = addFactoryDto.toFactory();
            factory.setTenantId(getCurrentUser().getTenantId().getId());
            if(addFactoryDto.getId() == null){
                factory.setCreatedUser(getCurrentUser().getUuidId());
                factory = factoryService.saveFactory(factory);
            }else {
                factory.setUpdatedUser(getCurrentUser().getUuidId());
                factory = factoryService.updFactory(factory);
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
    @ApiImplicitParam(name = "tenantId",value = "租户标识",dataType = "string",paramType = "query")
    @RequestMapping(value = "/findFactoryList", method = RequestMethod.GET)
    @ResponseBody
    public List<FactoryVo> findFactoryList(@RequestParam(required = false) String tenantId) throws ThingsboardException {
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
    @RequestMapping(value = "/findFactoryListByCdn", method = RequestMethod.GET)
    @ResponseBody
    public FactoryLevelAllListVo findFactoryListByCdn(QueryFactoryDto queryFactoryDto) throws ThingsboardException {
        try {
            checkParameter("没有获取到租户tenantId",getCurrentUser().getTenantId().getId());
            Factory factory = queryFactoryDto.toFactory();
            factory.setTenantId(getCurrentUser().getTenantId().getId());
            factory.setLoginUserId(getCurrentUser().getId().getId());
            return new FactoryLevelAllListVo(factoryService.findFactoryListByCdn(factory));
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
     * 查询工厂所有版本列表
     * @param queryFactoryDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询工厂所有版本列表")
    @ApiImplicitParam(name = "queryFactoryDto",value = "入参对象",dataType = "FactoryVersionDto",paramType = "query")
    @RequestMapping(value = "/findFactoryVersionList", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<FactoryVersionVo> findFactoryVersionList(@RequestParam int pageSize,
                                                             @RequestParam int page,
                                                             FactoryVersionDto queryFactoryDto) throws ThingsboardException {
        try {
            PageData<FactoryVersionVo> resultPage = new PageData<>();
            List<FactoryVersionVo> resultVo = new ArrayList<>();
            checkParameter("没有获取到登录人所在租户tenantId",getCurrentUser().getTenantId().getId());
            Factory factory = queryFactoryDto.toFactory();
            factory.setTenantId(getCurrentUser().getTenantId().getId());
            factory.setLoginUserId(getCurrentUser().getId().getId());
            List<Factory> factoryList = factoryService.findFactoryVersionList(factory);
            Boolean hasNext = false;
            if(CollectionUtils.isNotEmpty(factoryList)){
                List<Factory> collect = factoryList.stream().skip((page * pageSize)).limit(pageSize).collect(Collectors.toList());
                collect.forEach(i->{
                    resultVo.add(new FactoryVersionVo(i));
                });
                hasNext = factoryList.size() > (page * pageSize)?true:false;
            }
            return new PageData<FactoryVersionVo>(resultVo,page,resultVo.size(),hasNext);
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
    public List<FactoryBoardVo> findFactoryListByLoginRole() throws ThingsboardException {
        try {
            List<FactoryBoardVo> factoryVoList = new ArrayList<>();
            List<Factory> factoryList = factoryService.findFactoryListByLoginRole(getCurrentUser().getId().getId(),getCurrentUser().getTenantId().getId());
            if(!CollectionUtils.isEmpty(factoryList)){
                factoryList.forEach(i->{
                    factoryVoList.add(new FactoryBoardVo(i));
                });
            }
            return factoryVoList;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation("获取实体属性(工厂、车间、产线)")
    @ApiImplicitParam(name = "entity",value = "入参对象（FACYORY/WORKSHOP/PRODUCTION_LINE）",dataType = "String",paramType = "query")
    @RequestMapping(value = "/getEntityAttributeList", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getEntityAttributeList(String entity) throws IllegalAccessException {
        List<String> list = new ArrayList<>();
        Class<?> clazz = null;
        Boolean flag = false;
        if(EntityType.FACTORY.name().equals(entity)){
            flag = true;
            clazz = AbstractFactoryEntity.class;
        }
        if(EntityType.WORKSHOP.name().equals(entity)){
            flag = true;
            clazz = AbstractWorkshopEntity.class;
        }
        if(EntityType.PRODUCTION_LINE.name().equals(entity)){
            flag = true;
            clazz = AbstractProductionLineEntity.class;
        }
        if(flag){
            for(; clazz != Object.class;clazz = clazz.getSuperclass()){
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    list.add(field.getName());
                    // 获取bean的属性和值
                    field.setAccessible(true);
                }

            }
        }
        return list;
    }

    @ApiOperation("校验工厂下是否有网关（true-有，false-无）")
    @ApiImplicitParam(name = "factoryId",value = "factoryId工厂标识",dataType = "string",paramType="query",required = true)
    @RequestMapping(value = "/checkFactoryHaveGateway", method = RequestMethod.GET)
    @ResponseBody
    public Boolean checkFactoryHaveGateway(@RequestParam(required = true) String factoryId) throws ThingsboardException{
        checkParameterChinees("id",factoryId);
        return factoryService.checkFactoryHaveGateway(factoryId);
    }

    @ApiOperation("根据登录人角色查询工厂状态")
    @RequestMapping(value = "/findFactoryStatusByLoginRole", method = RequestMethod.GET)
    @ResponseBody
    public List<FactoryStatusVo> findFactoryStatusByLoginRole() throws ThingsboardException {
        try {
            List<FactoryStatusVo> factoryVoList = new ArrayList<>();
            List<Factory> factoryList = factoryService.findFactoryStatusByLoginRole(getCurrentUser().getId().getId(),getCurrentUser().getTenantId().getId());
            if(!CollectionUtils.isEmpty(factoryList)){
                factoryList.forEach(i->{
                    factoryVoList.add(new FactoryStatusVo(i));
                });
            }
            return factoryVoList;
        } catch (Exception e) {
            log.info("根据登录人角色查询工厂状态异常", e);
            throw handleException(e);
        }
    }
}
