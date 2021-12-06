package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.device.CapacityDeviceVo;
import org.thingsboard.server.entity.device.dto.DeviceListQry;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 产能运算配置界面接口
 * @author: HU.YUNHUI
 * @create: 2021-12-06 14:09
 **/
@Slf4j
@Api(value="产能运算配置界面接口",tags={"产能运算配置界面接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/capacityDevice/")
public class CapacityDeviceConfigController extends BaseController{

    @ApiOperation("查询列表接口")
    @RequestMapping(value = "/pageQuery", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParam(name = "pageQuery",value = "多条件入参",dataType = "DeviceQry",paramType = "query")
    @ResponseBody
    public PageData<CapacityDeviceVo>  pageQuery(
            @RequestParam int pageSize, @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String sortProperty,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String factoryId,
            @RequestParam(required = false) String workshopId,
            @RequestParam(required = false) String productionLineId,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String deviceName

            ) throws ThingsboardException {
        try {

            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            CapacityDeviceVo vo   = new  CapacityDeviceVo();

            vo.setFactoryId(getUidByStr(factoryId));
            vo.setWorkshopId(getUidByStr(workshopId));
            vo.setProductionLineId(getUidByStr(productionLineId));
            vo.setDeviceId(getUidByStr(deviceId));
            vo.setDeviceName(deviceName);
            vo.setTenantId(getTenantId().getId());
            log.info("配置入参的:{}",vo);
            return   deviceService.queryPage(vo,pageLink);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("===产能运算配置界面接口查询==>{}",e);
            throw handleException(e);
        }
    }


    private  UUID  getUidByStr(String str)
    {
       return StringUtils.isBlank(str)?null:UUID.fromString(str);
    }

}
