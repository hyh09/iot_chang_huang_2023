package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.device.AppCapacityDeviceVo;
import org.thingsboard.server.common.data.vo.device.CapacityDeviceHoursVo;
import org.thingsboard.server.common.data.vo.device.CapacityDeviceVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 产能运算配置界面接口
 * @author: HU.YUNHUI
 * @create: 2021-12-06 14:09
 **/
@Slf4j
@Api(value = "产能运算配置界面接口", tags = {"产能运算配置界面接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/capacityDevice/")
public class CapacityDeviceConfigController extends BaseController {
    private final String ONE = "1";

    @ApiOperation("查询列表接口")
    @RequestMapping(value = "/pageQuery", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂id"),
            @ApiImplicitParam(name = "workshopId", value = "车间id"),
            @ApiImplicitParam(name = "productionLineId", value = "产线id"),
            @ApiImplicitParam(name = "deviceId", value = "设备id"),
            @ApiImplicitParam(name = "deviceName", value = "设备名称"),

    })
    @ResponseBody
    public PageData<CapacityDeviceVo> pageQuery(
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
            CapacityDeviceVo vo = new CapacityDeviceVo();

            vo.setFactoryId(getUidByStr(factoryId));
            vo.setWorkshopId(getUidByStr(workshopId));
            vo.setProductionLineId(getUidByStr(productionLineId));
            vo.setDeviceId(getUidByStr(deviceId));
            vo.setDeviceName(deviceName);
            vo.setTenantId(getTenantId().getId());
            log.info("配置入参的:{}", vo);
            return deviceService.queryPage(vo, pageLink);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("===产能运算配置界面接口查询==>{}", e);
            throw handleException(e);
        }
    }


    @ApiOperation("app查询列表接口")
    @RequestMapping(value = "/app/pageQuery", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂id"),
            @ApiImplicitParam(name = "workshopId", value = "车间id"),
            @ApiImplicitParam(name = "productionLineId", value = "产线id"),
            @ApiImplicitParam(name = "deviceId", value = "设备id"),
            @ApiImplicitParam(name = "deviceName", value = "设备名称"),

    })
    @ResponseBody
    public PageData<AppCapacityDeviceVo> appPageQuery(
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
            CapacityDeviceVo vo = new CapacityDeviceVo();

            vo.setFactoryId(getUidByStr(factoryId));
            vo.setWorkshopId(getUidByStr(workshopId));
            vo.setProductionLineId(getUidByStr(productionLineId));
            vo.setDeviceId(getUidByStr(deviceId));
            vo.setDeviceName(deviceName);
            vo.setTenantId(getTenantId().getId());
            log.info("配置入参的:{}", vo);
            return deviceService.appQueryPage(vo, pageLink);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("===产能运算配置界面接口查询==>{}", e);
            throw handleException(e);
        }
    }


    @ApiOperation("更新状态")
    @RequestMapping(value = "/updateFlgById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id"),
            @ApiImplicitParam(name = "deviceFlg", value = "是否参与产能运算"),
    })
    @ResponseBody
    public String updateFlgById(
            @RequestParam UUID deviceId,
            @RequestParam Boolean deviceFlg
    ) throws ThingsboardException {
        deviceService.updateFlgById(deviceFlg, deviceId);

        return "success";
    }

    @ApiOperation("查询设备时间区间内每小时产量/能耗历史")
    @RequestMapping(value = "/getDeviceCapacity", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "0【产量】，1【能耗】", required = true, paramType = "query"),
            @ApiImplicitParam(name = "keyNum", value = "1【水】，2【电】，3【气】；", paramType = "query")
    })
    @ResponseBody
    public List<CapacityDeviceHoursVo> getDeviceCapacity(
            @RequestParam UUID deviceId, @RequestParam long startTime, @RequestParam long endTime, @RequestParam String type, @RequestParam String keyNum) throws ThingsboardException {

        try {
            checkParameterChinees("deviceId", deviceId);
            checkParameterChinees("type 0【产量】，1【能耗", type);
            if (ONE.equals(type)) {
                checkParameterChinees("keyNum 1【水】，2【电】，3【气", keyNum);
            }
            return energyChartService.getDeviceCapacity(deviceId, startTime, endTime, type, keyNum);
        } catch (Exception e) {
            log.error("查询设备时间区间内每小时产量/能耗历史异常",e);
            e.printStackTrace();
        }
        return null;
    }


    private UUID getUidByStr(String str) {
        return StringUtils.isBlank(str) ? null : UUID.fromString(str);
    }

}
