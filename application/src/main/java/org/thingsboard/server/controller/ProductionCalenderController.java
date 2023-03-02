package org.thingsboard.server.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.entity.productioncalender.dto.ProductionCalenderAddDto;
import org.thingsboard.server.entity.productioncalender.dto.ProductionCalenderPageQry;
import org.thingsboard.server.entity.productioncalender.dto.ProductionMonitorListQry;
import org.thingsboard.server.entity.productioncalender.vo.ProductionCalenderHisListVo;
import org.thingsboard.server.entity.productioncalender.vo.ProductionCalenderPageListVo;
import org.thingsboard.server.entity.productioncalender.vo.ProductionMonitorListVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
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
            log.info("/api/productionCalender/save保存设备生产日历报错：",e);
            throw handleException(e);
        }
        if (productionCalenderAddDto.getId() == null) {
            saveAuditLog(getCurrentUser(), null, EntityType.PRODUCTION_CALENDAR, null, ActionType.ADDED, productionCalenderAddDto);
        }else {
            saveAuditLog(getCurrentUser(), productionCalenderAddDto.getId(), EntityType.PRODUCTION_CALENDAR, null, ActionType.UPDATED, productionCalenderAddDto);
        }
    }

    @ApiOperation("生产日历分页查询")
    @RequestMapping(value = "/getPageList", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryName", value = "工厂名称",paramType = "query"),
            @ApiImplicitParam(name = "deviceName", value = "设备名称",paramType = "query"),
            @ApiImplicitParam(name = "qry", value = "其他条件",paramType = "query")
    })
    @ResponseBody
    public PageData<ProductionCalenderPageListVo> getTenantDeviceInfoList(@RequestParam int pageSize, @RequestParam int page,
                                                                          @RequestParam(required = false) String sortProperty,
                                                                          @RequestParam(required = false)  String sortOrder,
                                                                          ProductionCalenderPageQry qry) throws ThingsboardException {
        try {
            PageData<ProductionCalenderPageListVo> voPageData = new PageData<>();
            List<ProductionCalenderPageListVo> calenderPageListVos = new ArrayList<>();
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            PageData<ProductionCalender> productionCalenderPageData = productionCalenderService.findProductionCalenderPage(qry.toProductionCalender(getCurrentUser().getTenantId().getId(),sortProperty,sortOrder),pageLink);
            List<ProductionCalender> productionCalenderList = productionCalenderPageData.getData();
            if(!CollectionUtils.isEmpty(productionCalenderList)){
                for (ProductionCalender productionCalender : productionCalenderList) {
                    calenderPageListVos.add(new ProductionCalenderPageListVo(productionCalender));
                }
            }
            voPageData = new PageData<>(calenderPageListVos,productionCalenderPageData.getTotalPages(),productionCalenderPageData.getTotalElements(),productionCalenderPageData.hasNext());
            return voPageData;
        } catch (Exception e) {
            log.info("/api/productionCalender/getPageList 生产日历分页查询报错：",e);
            throw handleException(e);
        }
    }

    @ApiOperation("设备生产日历历史记录分页列表")
    @ApiImplicitParam(name = "deviceId",value = "设备标识",dataType = "String",paramType="query",required = true)
    @RequestMapping(value = "/getHistoryPageByDeviceId", method = RequestMethod.GET)
    @ResponseBody
    public PageData<ProductionCalenderHisListVo> getHistoryPageByDeviceId(@RequestParam int pageSize, @RequestParam int page,@RequestParam(required = false) String sortProperty,@RequestParam(required = false)  String sortOrder,
            @RequestParam("deviceId") String deviceId)throws ThingsboardException{
        try {
            PageData<ProductionCalenderPageListVo> voPageData = new PageData<>();
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            List<ProductionCalenderHisListVo> result = new ArrayList<>();
            checkParameterChinees("deviceId",deviceId);
            PageData<ProductionCalender> calenderPageData = productionCalenderService.getHistoryPageByDeviceId(toUUID(deviceId),pageLink,sortProperty,sortOrder);

            List<ProductionCalender> productionCalenderList = calenderPageData.getData();
            if(!org.springframework.util.CollectionUtils.isEmpty(productionCalenderList)){
                long systemTime = System.currentTimeMillis();
                for (ProductionCalender productionCalender :productionCalenderList){
                    result.add(new ProductionCalenderHisListVo(productionCalender,systemTime));
                }
            }
            return new PageData<>(result,calenderPageData.getTotalPages(),calenderPageData.getTotalElements(),calenderPageData.hasNext());
        } catch (Exception e) {
            log.info("/api/productionCalender/getHistoryPageByDeviceId设备生产日历历史记录分页列表报错：",e);
            throw handleException(e);
        }
    }

    @ApiOperation("设备生产日历历史记录列表")
    @ApiImplicitParam(name = "deviceId",value = "设备标识",dataType = "String",paramType="query",required = true)
    @RequestMapping(value = "/getHistoryByDeviceId", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductionCalenderHisListVo> getHistoryByDeviceId(@RequestParam("deviceId") String deviceId)throws ThingsboardException{
        try {
            List<ProductionCalenderHisListVo> result = new ArrayList<>();
            checkParameterChinees("deviceId",deviceId);
            List<ProductionCalender> productionCalenderList = productionCalenderService.getHistoryByDeviceId(toUUID(deviceId));
            if(!org.springframework.util.CollectionUtils.isEmpty(productionCalenderList)){
                long systemTime = System.currentTimeMillis();
                for (ProductionCalender productionCalender :productionCalenderList){
                    result.add(new ProductionCalenderHisListVo(productionCalender,systemTime));
                }
            }
            return result;
        } catch (Exception e) {
            log.info("/api/productionCalender/getHistoryByDeviceId设备生产日历历史记录列表报错：",e);
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
            log.info("/api/productionCalenderfindById查询生产日历详情报错：",e);
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
            log.info("/api/productionCalender/deleteById删除生产日历报错：",e);
            throw handleException(e);
        }
        saveAuditLog(getCurrentUser(), toUUID(id), EntityType.PRODUCTION_CALENDAR, null, ActionType.DELETED, id);
    }

    @ApiOperation("集团看板大屏生产监控")
    @ApiImplicitParam(name = "dto",value = "入参",dataType = "ProductionMonitorListQry",paramType="query")
    @RequestMapping(value = "/dp/getProductionMonitorTenantList", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductionMonitorListVo> getProductionMonitorenantList(ProductionMonitorListQry dto)throws ThingsboardException{
        try {
            List<ProductionMonitorListVo> result = new ArrayList<>();
            List<ProductionCalender> calenderList = productionCalenderService.getProductionMonitorList(dto.toProductionCalender(getCurrentUser().getTenantId().getId()));
            if(!org.springframework.util.CollectionUtils.isEmpty(calenderList)){
                for (ProductionCalender productionCalender :calenderList){
                    ProductionMonitorListVo productionMonitorListVo = new ProductionMonitorListVo(productionCalender);
                    productionMonitorListVo.setYearAchieve(new BigDecimal(productionMonitorListVo.getYearAchieve()).multiply(new BigDecimal(100)).toString());
                    result.add(productionMonitorListVo);
                }
            }
            return result;
        } catch (Exception e) {
            log.info("/api/productionCalender/dp/getProductionMonitorTenantList看板大屏生产监控报错：",e);
            throw handleException(e);
        }
    }


}
