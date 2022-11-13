package org.thingsboard.server.controller;

import com.datastax.oss.driver.shaded.guava.common.collect.Maps;
import com.google.api.client.util.Lists;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.entity.vo.OrderCustomCapacityResult;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.entity.productioncalender.dto.ProductionMonitorListQry;
import org.thingsboard.server.entity.productioncalender.vo.ProductionMonitorListVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * 原生实体接口
 *
 * @author wwj
 * @since 2021.10.18
 */
@Api(value = "原生实体接口", tags = {"原生实体接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class OriginEntityController extends BaseController {

    @Autowired
    ClientService clientService;

    @Autowired
    OrderController orderController;

    @Autowired
    BulletinBoardController bulletinBoardController;

    @Autowired
    RTMonitorBoardController rtMonitorBoardController;

    @Autowired
    ProductionCalenderController productionCalenderController;

    /**
     * 工厂数据接口
     */
    @ApiOperation(value = "工厂数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "factoryId", value = "工厂Id", paramType = "query", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", required = false),
    })
    @GetMapping("/factory/data")
    @SuppressWarnings("all")
    public Map<String, Object> listBoardCapacityMonitorOrders(@RequestParam(value = "startTime", required = false) Long startTime,
                                                              @RequestParam(value = "endTime", required = false) Long endTime,
                                                              @RequestParam(value = "factoryId", required = false) UUID factoryId) throws ThingsboardException {
        Map<String, Object> dataMap = Maps.newHashMap();
        var factories = this.clientService.listFactories();
        List<Map<String, Object>> dataList = Lists.newArrayList();

        var todayCurrentTime = endTime;
        var todayStartTime = startTime;

        if (startTime == null || startTime == 0L) {
            todayStartTime = CommonUtil.getTodayStartTime();
        }
        if (endTime == null || endTime == 0L) {
            todayCurrentTime = CommonUtil.getTodayCurrentTime();
        }

        for (Factory factory : factories) {
            Map<String, Object> factoryDataMap = Maps.newHashMap();
            factoryDataMap.put("Name", factory.getName());
            factoryDataMap.put("FactoryID", factory.getId());

            // 总产量
            Map<String, String> capDataMap = Maps.newHashMap();
            var cap = this.bulletinBoardController.todaySectionHistory(todayStartTime, todayCurrentTime, factory.getId().toString(), null, null, null);
            capDataMap.put("ProductionToday", cap.getTodayValue());
            capDataMap.put("ProductuinTotal", cap.getSectionValue());
            capDataMap.put("HistoryProductionTotal", cap.getTodayValue());
            factoryDataMap.put("TotalProduction", capDataMap);

            //设备综合
            Map<String, String> viewDataMap = Maps.newHashMap();
            var status = this.rtMonitorBoardController.getDeviceOnlineStatusStatistics(factory.getId().toString(), null, null, null);
            viewDataMap.put("EquipmentOnline", String.valueOf(status.getOnLineDeviceCount()));
            viewDataMap.put("EquipmentOffline", String.valueOf(status.getOffLineDeviceCount()));
            factoryDataMap.put("EquipmentOverview", viewDataMap);

            //生产监控
            ProductionMonitorListQry productionMonitorListQry = new ProductionMonitorListQry();
            productionMonitorListQry.setStartTime(todayStartTime);
            productionMonitorListQry.setEndTime(todayCurrentTime);
            productionMonitorListQry.setFactoryId(factory.getId());
            productionMonitorListQry.setWorkshopId(null);
            var productionMonitorenantList = this.productionCalenderController.getProductionMonitorenantList(productionMonitorListQry);
            List<Map<String, String>> proDataMapList = Lists.newArrayList();
            if (!Collections.isEmpty(productionMonitorenantList)) {
                for (ProductionMonitorListVo productionMonitorListVo : productionMonitorenantList) {
                    Map<String, String> proDataMap = Maps.newHashMap();
                    proDataMap.put("Name", productionMonitorListVo.getFactoryName());
                    proDataMap.put("CountCompletedAndPlanned", productionMonitorListVo.getAchieveOrPlan());
                    proDataMap.put("Percentage", productionMonitorListVo.getYearAchieve());
                    proDataMap.put("State", Boolean.TRUE.equals(productionMonitorListVo.getProductionState()) ? "0" : "1");
                    proDataMapList.add(proDataMap);
                }
            }
            Map<String, Object> productionMonitorMap = Maps.newHashMap();
            productionMonitorMap.put("ProductionMonitor", proDataMapList);
            factoryDataMap.put("ProductionMonitor", productionMonitorMap);

            //订单监控
            var orderCustomCapacityResults = this.orderController.listBoardCapacityMonitorOrders(todayStartTime, todayCurrentTime, factory.getId(), null);
            List<Map<String, String>> orderDataMapList = Lists.newArrayList();
            if (!Collections.isEmpty(orderCustomCapacityResults)) {
                for (OrderCustomCapacityResult orderCustomCapacityResult : orderCustomCapacityResults) {
                    Map<String, String> orderDataMap = Maps.newHashMap();
                    orderDataMap.put("Id", orderCustomCapacityResult.getOrderNo());
                    orderDataMap.put("CompletedDividedByTotal", orderCustomCapacityResult.getCompletedCapacities().stripTrailingZeros().toPlainString() + "/" + orderCustomCapacityResult.getTotal().stripTrailingZeros().toPlainString());
                    orderDataMap.put("FactoryName", orderCustomCapacityResult.getFactoryName());
                    orderDataMap.put("CompletedPercentage", orderCustomCapacityResult.getCompleteness().stripTrailingZeros().toPlainString());
                    orderDataMap.put("IsOvertime", Boolean.TRUE.equals(orderCustomCapacityResult.getIsOvertime()) ? "1" : "0");
                    orderDataMapList.add(orderDataMap);
                }
            }
            Map<String, Object> orderMonitorMap = Maps.newHashMap();
            orderMonitorMap.put("OrderMonitor", orderDataMapList);
            factoryDataMap.put("OrderMonitor", orderMonitorMap);


            //车间数据
            List<Map<String, Object>> workshopDataMapList = Lists.newArrayList();
            var workshops = this.clientService.listWorkshopsByFactoryId(new TenantId(factory.getTenantId()), factory.getId());
            for (Workshop workshop : workshops) {
                Map<String, Object> workshopDataMap = Maps.newHashMap();
                workshopDataMap.put("Name", workshop.getName());
                workshopDataMap.put("WorkshopID", workshop.getId());

                //设备综合
                Map<String, String> viewWorkshopDataMap = Maps.newHashMap();
                var statusWorkshop = this.rtMonitorBoardController.getDeviceOnlineStatusStatistics(null, workshop.getId().toString(), null, null);
                viewWorkshopDataMap.put("EquipmentOnline", String.valueOf(statusWorkshop.getOnLineDeviceCount()));
                viewWorkshopDataMap.put("EquipmentOffline", String.valueOf(statusWorkshop.getOffLineDeviceCount()));
                workshopDataMap.put("EquipmentOverview", viewWorkshopDataMap);

                //生产监控
                ProductionMonitorListQry workshopProductionMonitorListQry = new ProductionMonitorListQry();
                workshopProductionMonitorListQry.setStartTime(todayStartTime);
                workshopProductionMonitorListQry.setEndTime(todayCurrentTime);
                workshopProductionMonitorListQry.setFactoryId(null);
                workshopProductionMonitorListQry.setWorkshopId(workshop.getId());
                var workshopProductionMonitorenantList = this.productionCalenderController.getProductionMonitorenantList(workshopProductionMonitorListQry);
                List<Map<String, String>> workshopProDataMapList = Lists.newArrayList();
                if (!Collections.isEmpty(workshopProductionMonitorenantList)) {
                    for (ProductionMonitorListVo productionMonitorListVo : workshopProductionMonitorenantList) {
                        Map<String, String> proDataMap = Maps.newHashMap();
                        proDataMap.put("Name", productionMonitorListVo.getFactoryName());
                        proDataMap.put("CountCompletedAndPlanned", productionMonitorListVo.getAchieveOrPlan());
                        proDataMap.put("Percentage", productionMonitorListVo.getYearAchieve());
                        proDataMap.put("State", Boolean.TRUE.equals(productionMonitorListVo.getProductionState()) ? "0" : "1");
                        workshopProDataMapList.add(proDataMap);
                    }
                }
                Map<String, Object> workshopProductionMonitorMap = Maps.newHashMap();
                workshopProductionMonitorMap.put("ProductionMonitor", workshopProDataMapList);
                workshopDataMap.put("ProductionMonitor", workshopProductionMonitorMap);

                //订单监控
                var workshopOrderCustomCapacityResults = this.orderController.listBoardCapacityMonitorOrders(todayStartTime, todayCurrentTime, null, workshop.getId());
                List<Map<String, String>> workshopOrderDataMapList = Lists.newArrayList();
                if (!Collections.isEmpty(workshopOrderCustomCapacityResults)) {
                    for (OrderCustomCapacityResult orderCustomCapacityResult : workshopOrderCustomCapacityResults) {
                        Map<String, String> orderDataMap = Maps.newHashMap();
                        orderDataMap.put("Id", orderCustomCapacityResult.getOrderNo());
                        orderDataMap.put("CompletedDividedByTotal", orderCustomCapacityResult.getCompletedCapacities().stripTrailingZeros().toPlainString() + "/" + orderCustomCapacityResult.getTotal().stripTrailingZeros().toPlainString());
                        orderDataMap.put("FactoryName", orderCustomCapacityResult.getFactoryName());
                        orderDataMap.put("CompletedPercentage", orderCustomCapacityResult.getCompleteness().stripTrailingZeros().toPlainString());
                        orderDataMap.put("IsOvertime", Boolean.TRUE.equals(orderCustomCapacityResult.getIsOvertime()) ? "1" : "0");
                        workshopOrderDataMapList.add(orderDataMap);
                    }
                }
                Map<String, Object> workshopOrderMonitorMap = Maps.newHashMap();
                workshopOrderMonitorMap.put("OrderMonitor", workshopOrderDataMapList);
                workshopDataMap.put("OrderMonitor", workshopOrderMonitorMap);

                // 设备数据
                List<Map<String, Object>> deviceDataMapList = Lists.newArrayList();
                var devices = this.clientService.listSimpleDevicesByQuery(new TenantId(workshop.getTenantId()), new FactoryDeviceQuery().setWorkshopId(workshop.getId().toString()));
                for (Device device : devices) {
                    var deviceKeyParametersResult = this.rtMonitorBoardController.getDeviceKeyParameters(device.getId().getId());
                    Map<String, Object> deviceDataMap = Maps.newHashMap();
                    deviceDataMap.put("EquipmentID", deviceKeyParametersResult.getId());
                    deviceDataMap.put("EquipmentName", deviceKeyParametersResult.getName());
                    deviceDataMap.put("Data1", deviceKeyParametersResult.getOperationRate());
                    deviceDataMap.put("Data2", deviceKeyParametersResult.getCapacityEfficiency());
                    deviceDataMap.put("Data3", deviceKeyParametersResult.getStartingUpDuration());
                    deviceDataMap.put("Data4", deviceKeyParametersResult.getShutdownDuration());
                    deviceDataMap.put("Data5", deviceKeyParametersResult.getMaintenanceDuration());
                    deviceDataMap.put("Data6", deviceKeyParametersResult.getShiftDuration());
                    deviceDataMap.put("Data7", deviceKeyParametersResult.getOutput());
                    deviceDataMap.put("Data8", deviceKeyParametersResult.getOee());
                    deviceDataMap.put("Data9", deviceKeyParametersResult.getQualityRate());
                    deviceDataMap.put("Data10", deviceKeyParametersResult.getInQualityNum());
                    deviceDataMapList.add(deviceDataMap);
                }
                workshopDataMap.put("EquipmentData", deviceDataMapList);

                // add map
                workshopDataMapList.add(workshopDataMap);
            }

            factoryDataMap.put("WorkshopData", workshopDataMapList);
            dataList.add(factoryDataMap);
        }

        dataMap.put("FactoryData", dataList);
        return dataMap;
    }
}
