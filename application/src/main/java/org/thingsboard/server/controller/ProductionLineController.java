package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.entity.productionline.dto.AddProductionLineDto;
import org.thingsboard.server.entity.productionline.vo.ProductionLineVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;

@Api(value="生产线管理Controller",tags={"生产线管理接口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/productionLine")
public class ProductionLineController extends BaseController  {

    /**
     * 新增/更新生产线
     * @param addProductionLineDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增/更新生产线")
    @ApiImplicitParam(name = "addProductionLineDto",value = "入参实体",dataType = "AddProductionLineDto",paramType="body")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ProductionLineVo saveProductionLine(@RequestBody AddProductionLineDto addProductionLineDto) throws ThingsboardException {
        try {
            checkParameter("tenantId",addProductionLineDto.getTenantId());
            checkParameter("workshopId",addProductionLineDto.getWorkshopId());
            ProductionLine productionLine = addProductionLineDto.toProductionLine();
            if(addProductionLineDto.getId() == null){
                productionLine.setCreatedUser(getCurrentUser().getUuidId());
                productionLine = checkNotNull(productionLineService.saveProductionLine(productionLine));
            }else {
                productionLine.setUpdatedUser(getCurrentUser().getUuidId());
                productionLine = checkNotNull(productionLineService.updProductionLine(productionLine));
            }
            return new ProductionLineVo(productionLine);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 删除生产线
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("删除生产线")
    @ApiImplicitParam(name = "id",value = "工厂标识",dataType = "string",paramType="query",required = true)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public void delProductionLine(@RequestParam String id) throws ThingsboardException {
        try {
            checkParameter("id",id);
            productionLineService.delProductionLine(toUUID(id));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询租户/工厂/车间下所有生产线列表
     * @param tenantId
     * @param workshopId
     * @param factoryId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询租户/车间下所有生产线列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId",value = "租户标识",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "workshopId",value = "车间标识",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "factoryId",value = "工厂标识",dataType = "string",paramType = "query")
    })
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductionLineVo> findWorkshopList(@RequestParam String tenantId,@RequestParam String workshopId,@RequestParam String factoryId) throws ThingsboardException {
        try {
            List<ProductionLineVo> productionLineVos = new ArrayList<>();
            if(StringUtils.isEmpty(tenantId)){
                tenantId = getCurrentUser().getTenantId().getId().toString();
            }
            checkNotNull(productionLineService.findProductionLineList(
                    toUUID(tenantId),
                    StringUtils.isNotEmpty(workshopId) ?toUUID(workshopId):null,
                    StringUtils.isNotEmpty(factoryId) ?toUUID(factoryId):null)).forEach(i->{
                productionLineVos.add(new ProductionLineVo(i));
            });
            return productionLineVos;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询生产线列表
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询生产线详情")
    @ApiImplicitParam(name = "id",value = "当前id",dataType = "String",paramType="path",required = true)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ProductionLine findById(@PathVariable("id") String id) throws ThingsboardException {
        try {
            checkParameter("id",id);
            return checkNotNull(productionLineService.findById(toUUID(id)));
        } catch (Exception e) {
            throw handleException(e);
        }
    }


}
