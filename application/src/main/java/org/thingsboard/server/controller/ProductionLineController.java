package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    @ApiImplicitParam(name = "ProductionLine",value = "productionLine")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ProductionLineVo saveProductionLine(@RequestBody AddProductionLineDto addProductionLineDto) throws ThingsboardException {
        try {
            checkParameter("tenantId",addProductionLineDto.getTenantId());
            ProductionLine productionLine = new ProductionLine();
            if(addProductionLineDto.getId() == null){
                productionLine = checkNotNull(productionLineService.saveProductionLine(addProductionLineDto.toProductionLine()));
            }else {
                productionLine = checkNotNull(productionLineService.updProductionLine(addProductionLineDto.toProductionLine()));
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
     * 查询租户下所有生产线列表
     * @param tenantId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询租户下所有生产线列表")
    @ApiImplicitParam(name = "tenantId",value = "租户标识",dataType = "string",paramType = "query",required = true)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductionLineVo> findWorkshopList(@RequestParam String tenantId) throws ThingsboardException {
        try {
            List<ProductionLineVo> productionLineVos = new ArrayList<>();
            checkParameter("tenantId",tenantId);
            checkNotNull(productionLineService.findProductionLineList(toUUID(tenantId))).forEach(i->{
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
