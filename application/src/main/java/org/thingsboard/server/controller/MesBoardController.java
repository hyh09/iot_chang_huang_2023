package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.MesService;
import org.thingsboard.server.dao.hsms.entity.vo.*;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.UUID;


/**
 * Mes看板
 *
 * @author wwj
 * @since 2021.10.18
 */
@Api(value = "Mes看板", tags = {"Mes看板"})
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class MesBoardController extends BaseController {

    @Autowired
    ClientService clientService;

    @Autowired
    MesService mesService;

//    /**
//     * 车间下全部产线
//     */
//    @ApiOperation(value = "车间下全部产线", notes = "车间下全部产线")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "path", required = true),
//    })
//    @GetMapping(value = "/mes/board/workshop/{workshopId}/productionLines")
//    public List<MesBoardProductionLineVO> listProductionLinesByWorkshopId(@PathVariable("workshopId") UUID workshopId) throws ThingsboardException {
//        return this.mesService.listProductionLinesByWorkshopId(getTenantId(), workshopId);
//    }

    /**
     * 工厂下全部车间
     */
    @ApiOperation(value = "工厂下全部车间", notes = "工厂下全部车间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "path", required = true),
    })
    @GetMapping(value = "/mes/board/factory/{factoryId}/workshops")
    public List<MesBoardWorkshopVO> listWorkshopsByFactoryId(@PathVariable("factoryId") UUID factoryId) throws ThingsboardException {
        // todo 临时修改
        var factory = this.factoryService.findById(factoryId);
        if (factory == null)
            throw new ThingsboardException("工厂不存在", ThingsboardErrorCode.GENERAL);
        return this.mesService.listWorkshopsByFactoryId(new TenantId(factory.getTenantId()), factoryId);
    }

    /**
     * 车间下全部设备
     */
    @ApiOperation(value = "车间下全部设备", notes = "车间下全部设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "path", required = true),
    })
    @GetMapping(value = "/mes/board/workshopId/{workshopId}/devices")
    public List<MesBoardDeviceVO> listDevicesByProductionLineId(@PathVariable("workshopId") UUID workshopId) throws ThingsboardException {
        return this.mesService.listDevicesByProductionLineId(getTenantId(), workshopId);
    }

    /**
     * 开机率分析top
     */
    @ApiOperation(value = "开机率分析top", notes = "开机率分析top")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @GetMapping(value = "/mes/board/device/operation/rate/top")
    public List<MesBoardDeviceOperationRateVO> getDeviceOperationRateTop(@RequestParam(value = "workshopId") UUID workshopId) throws ThingsboardException {
        return this.mesService.getDeviceOperationRateTop(getTenantId(), workshopId);
    }

    /**
     * 产量趋势
     */
    @ApiOperation(value = "产量趋势", notes = "产量趋势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @GetMapping(value = "/mes/board/capacity/trend")
    public List<MesBoardCapacityTrendItemVO> getCapacityTrend(@RequestParam(value = "workshopId") UUID workshopId) throws ThingsboardException {
        return this.mesService.getCapacityTrend(getTenantId(), workshopId);
    }

    /**
     * 生产监控
     */
    @ApiOperation(value = "生产监控", notes = "生产监控")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @GetMapping(value = "/mes/board/production/monitoring")
    public List<MesBoardProductionMonitoringVO> getProductionMonitoring(@RequestParam(value = "workshopId") UUID workshopId) throws ThingsboardException {
        return this.mesService.getProductionMonitoring(getTenantId(), workshopId);
    }

    /**
     * 机台产量对比
     */
    @ApiOperation(value = "机台产量对比", notes = "机台产量对比")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @GetMapping(value = "/mes/board/capacity/comparison")
    public List<MesBoardCapacityComparisonVO> getCapacityComparison(@RequestParam(value = "workshopId") UUID workshopId) throws ThingsboardException {
        return this.mesService.getCapacityComparison(getTenantId(), workshopId);
    }

    /**
     * 产量信息
     */
    @ApiOperation(value = "产量信息", notes = "产量信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @GetMapping(value = "/mes/board/capacity/info")
    public MesBoardCapacityInfoVO getCapacityInfo(@RequestParam(value = "workshopId") UUID workshopId) throws ThingsboardException {
        return this.mesService.getCapacityInfo(getTenantId(), workshopId);
    }

    /**
     * 生产进度跟踪
     */
    @ApiOperation(value = "生产进度跟踪", notes = "生产进度跟踪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @GetMapping(value = "/mes/board/production/progress/tracking")
    public List<MesBoardProductionProgressTrackingItemVO> getProductionProgressTracking(@RequestParam(value = "workshopId") UUID workshopId) throws ThingsboardException {
        return this.mesService.getProductionProgressTracking(getTenantId(), workshopId);
    }

    /**
     * 机台当前生产任务
     */
    @ApiOperation(value = "机台当前生产任务", notes = "机台当前生产任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @GetMapping(value = "/mes/board/production/task")
    public List<String> getProductionTask(@RequestParam(value = "workshopId") UUID workshopId) throws ThingsboardException {
        return this.mesService.getProductionTask(getTenantId(), workshopId);
    }

    /**
     * 异常预警
     */
    @ApiOperation(value = "异常预警", notes = "异常预警")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workshopId", value = "车间Id", paramType = "query", required = true),
    })
    @GetMapping(value = "/mes/board/abnormal/warning")
    public List<String> getAbnormalWarning(@RequestParam(value = "workshopId") UUID workshopId) throws ThingsboardException {
        return this.mesService.getAbnormalWarning(getTenantId(), workshopId);
    }

}
