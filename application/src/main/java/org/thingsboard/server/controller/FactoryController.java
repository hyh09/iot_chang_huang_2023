package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.entity.factory.dto.AddFactoryDto;
import org.thingsboard.server.entity.factory.dto.QueryFactoryDto;
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

    private FactoryVo factoryVo = new FactoryVo();

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
            checkParameter("tenantId",addFactoryDto.getTenantId());
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
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public void delFactory(@RequestParam String id) throws ThingsboardException {
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
            checkParameter("tenantId",tenantId);
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
            checkParameter("tenantId",queryFactoryDto.getTenantId());
            return checkNotNull(factoryService.findFactoryListBuyCdn(queryFactoryDto.toFactory()));
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
}
