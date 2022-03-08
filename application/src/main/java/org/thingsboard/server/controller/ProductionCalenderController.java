package org.thingsboard.server.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.entity.productioncalender.dto.ProductionCalenderAddDto;
import org.thingsboard.server.entity.productioncalender.dto.ProductionMonitorListQry;
import org.thingsboard.server.entity.productioncalender.vo.ProductionCalenderHisListVo;
import org.thingsboard.server.entity.productioncalender.vo.ProductionCalenderPageListVo;
import org.thingsboard.server.entity.productioncalender.vo.ProductionMonitorListVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Api(value="生产管理Controller",tags={"生产管理接口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/productionCalender")
public class ProductionCalenderController extends BaseController{

    /**
     * 新增/修改
     * @param productionCalenderAddDto
     * @throws ThingsboardException
     */
    @ApiOperation("新增/修改")
    @ApiImplicitParam(name = "productionCalenderAddDto",value = "入参实体",dataType = "ProductionCalenderAddDto",paramType="body")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public void save(@RequestBody ProductionCalenderAddDto productionCalenderAddDto) throws ThingsboardException {
        try {
            SecurityUser currentUser = getCurrentUser();
            ProductionCalender productionCalender = productionCalenderAddDto.toProductionCalender();
            UUID uuid = Uuids.timeBased();
            if(productionCalenderAddDto.getId() == null){
                productionCalender.setCreatedUser(currentUser.getUuidId());
                productionCalender.setCreatedTime(Uuids.unixTimestamp(uuid));
            }else {
                checkParameter("id", productionCalenderAddDto.getId());
                productionCalender.setUpdatedUser(currentUser.getUuidId());
                productionCalender.setUpdatedTime(Uuids.unixTimestamp(uuid));
            }
            productionCalender.setTenantId(currentUser.getTenantId().getId());
            productionCalenderService.saveProductionCalender(productionCalender);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation("生产日历分页查询")
    @RequestMapping(value = "/getPageList", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryName", value = "工厂名称",paramType = "query"),
            @ApiImplicitParam(name = "deviceName", value = "设备名称",paramType = "query")
    })
    @ResponseBody
    public PageData<ProductionCalenderPageListVo> getTenantDeviceInfoList(@RequestParam int pageSize, @RequestParam int page,
                                                                          @RequestParam String factoryName, @RequestParam String deviceName) throws ThingsboardException {
        try {
            PageData<ProductionCalenderPageListVo> voPageData = new PageData<>();
            List<ProductionCalenderPageListVo> calenderPageListVos = new ArrayList<>();
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            PageData<ProductionCalender> productionCalenderPageData = productionCalenderService.findProductionCalenderPage(new ProductionCalender(deviceName,factoryName,getCurrentUser().getTenantId().getId()),pageLink);
            List<ProductionCalender> productionCalenderList = productionCalenderPageData.getData();
            if(!CollectionUtils.isEmpty(productionCalenderList)){
                for (ProductionCalender productionCalender : productionCalenderList) {
                    calenderPageListVos.add(new ProductionCalenderPageListVo(productionCalender));
                }
            }
            voPageData = new PageData<>(calenderPageListVos,productionCalenderPageData.getTotalPages(),productionCalenderPageData.getTotalElements(),productionCalenderPageData.hasNext());
            return voPageData;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation("设备历史生产日历别表")
    @ApiImplicitParam(name = "deviceId",value = "设备标识",dataType = "String",paramType="query",required = true)
    @RequestMapping(value = "/getHistoryById", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductionCalenderHisListVo> getHistoryById(@RequestParam("deviceId") String deviceId)throws ThingsboardException{
        try {
            List<ProductionCalenderHisListVo> result = new ArrayList<>();
            checkParameterChinees("deviceId",deviceId);
            List<ProductionCalender> calenderList = productionCalenderService.getHistoryById(toUUID(deviceId));
            if(!org.springframework.util.CollectionUtils.isEmpty(calenderList)){
                long systemTime = System.currentTimeMillis();
                for (ProductionCalender productionCalender :calenderList){
                    result.add(new ProductionCalenderHisListVo(productionCalender,systemTime));
                }
            }
            return result;
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    /**
     * 查询详情
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("查询详情")
    @ApiImplicitParam(name = "id",value = "当前id",dataType = "String",paramType="path",required = true)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ProductionCalenderHisListVo findById(@PathVariable("id") String id) throws ThingsboardException {
        try {
            checkParameter("id",id);
            return new ProductionCalenderHisListVo(productionCalenderService.findById(toUUID(id)));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 删除单条
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("删除单条")
    @ApiImplicitParam(name = "id",value = "当前id",dataType = "String",paramType="path",required = true)
    @RequestMapping(value = "deleteById/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteById(@PathVariable("id") String id) throws ThingsboardException {
        try {
            checkParameter("id",id);
            productionCalenderService.delProductionCalender(toUUID(id));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation("看板大屏生产监控")
    @ApiImplicitParam(name = "productionMonitorListQry",value = "设备标识",dataType = "ProductionMonitorListQry",paramType="query")
    @RequestMapping(value = "/dp/getHistoryById", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductionMonitorListVo> getProductionMonitorList(ProductionMonitorListQry dto)throws ThingsboardException{
        try {
            List<ProductionMonitorListVo> result = new ArrayList<>();
            List<ProductionCalender> calenderList = productionCalenderService.getProductionMonitorList(dto.toProductionCalender(getCurrentUser().getTenantId().getId()));
            if(!org.springframework.util.CollectionUtils.isEmpty(calenderList)){
                for (ProductionCalender productionCalender :calenderList){
                    result.add(new ProductionMonitorListVo(productionCalender));
                }
            }
            return result;
        } catch (Exception e) {
            throw handleException(e);
        }
    }


}
