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
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.entity.workshop.dto.AddWorkshopDto;
import org.thingsboard.server.entity.workshop.vo.WorkshopVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;

@Api(value="车间管理Controller",tags={"车间管理接口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/workshop")
public class WorkshopController extends BaseController  {

    /**
     * 新增/更新车间
     * @param addWorkshopDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增/更新车间")
    @ApiImplicitParam(name = "addWorkshopDto",value = "入参实体",dataType = "AddWorkshopDto",paramType="body")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public WorkshopVo saveWorkshop(@RequestBody AddWorkshopDto addWorkshopDto) throws ThingsboardException {
        try {
            checkParameter("tenantId",addWorkshopDto.getTenantId());
            checkParameter("factoryId",addWorkshopDto.getFactoryId());
            Workshop workshop = addWorkshopDto.toWorkshop();
            if(addWorkshopDto.getId() == null){
                workshop.setCreatedUser(getCurrentUser().getUuidId());
                workshop = checkNotNull(workshopService.saveWorkshop(workshop));
            }else {
                workshop.setUpdatedUser(getCurrentUser().getUuidId());
                workshop = checkNotNull(workshopService.updWorkshop(workshop));
            }
            return new WorkshopVo(workshop);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 删除车间
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("删除车间")
    @ApiImplicitParam(name = "id",value = "工厂标识",dataType = "string",paramType="query",required = true)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public void delWorkshop(@RequestParam String id) throws ThingsboardException {
        try {
            checkParameter("id",id);
            workshopService.delWorkshop(toUUID(id));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询租户/工厂下所有车间列表
     * @param tenantId
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询租户下所有车间列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId",value = "租户标识",dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "factoryId",value = "工厂标识",dataType = "string",paramType = "query")
    })
    @RequestMapping(value = "/findWorkshopListByTenant", method = RequestMethod.GET)
    @ResponseBody
    public List<WorkshopVo> findWorkshopListByTenant(@RequestParam String tenantId,@RequestParam String factoryId) throws ThingsboardException {
        try {
            List<WorkshopVo> workshopVos = new ArrayList<>();
            checkParameter("tenantId",tenantId);
            List<Workshop> workshops = checkNotNull(workshopService.findWorkshopListByTenant(toUUID(tenantId), StringUtils.isNotEmpty(factoryId) ?toUUID(factoryId):null));
            workshops.forEach(i->{
                workshopVos.add(new WorkshopVo(i));
            });
            return workshopVos;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 查询车间详情
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询车间详情")
    @ApiImplicitParam(name = "id",value = "当前id",dataType = "String",paramType="path",required = true)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public WorkshopVo findById(@PathVariable("id") String id) throws ThingsboardException {
        try {
            checkParameter("id",id);
            return new WorkshopVo(checkNotNull(workshopService.findById(toUUID(id))));
        } catch (Exception e) {
            throw handleException(e);
        }
    }
}
