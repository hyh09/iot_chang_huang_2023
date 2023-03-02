package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.common.data.statisticoee.StatisticOee;
import org.thingsboard.server.common.data.vo.BoardV3DeviceDictionaryVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.common.data.vo.bodrd.TodaySectionHistoryVo;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;
import org.thingsboard.server.dao.board.BulletinV3BoardVsSvc;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.entity.vo.TimeQuery;
import org.thingsboard.server.dao.hs.service.DeviceMonitorService;
import org.thingsboard.server.dao.hs.service.OrderRtService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.entity.changsheng.vo.GroupBoardJsonVo;
import org.thingsboard.server.entity.productioncalender.vo.ProductionMonitorListVo;
import org.thingsboard.server.entity.statisticoee.vo.StatisticOeeVo;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Api(value = "长胜定制Controller", tags = {"长胜定制管理口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/changsheng")
public class ChangshengBoardController extends BaseController {
/*
    @Autowired
    private BulletinBoardController bulletinBoardController;
    @Autowired
    private RTMonitorBoardController rtMonitorBoardController;
    @Autowired
    private DeviceOeeEveryHourController deviceOeeEveryHourController;
    @Autowired
    private ProductionCalenderController productionCalenderController;
    @Autowired
    private OrderController orderController;
    @Autowired
    private BulletinBoardV3Controller bulletinBoardV3Controller;*/

    @Autowired
    DeviceMonitorService deviceMonitorService;
    @Autowired
    OrderRtService orderService;
    @Autowired
    private BulletinV3BoardVsSvc bulletinV3BoardVsSvc;
    @Autowired
    private BulletinBoardSvc bulletinBoardSvc;


    @ApiOperation("集团看板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束", dataType = "long", required = true, paramType = "query")
    })
    @RequestMapping(value = "/tenantBoard", method = RequestMethod.GET)
    @ResponseBody
    public GroupBoardJsonVo getYunDeviceById(@RequestParam(required = false, value = "startTime") Long startTime,
                                             @RequestParam(required = false, value = "endTime") Long endTime) throws ThingsboardException {
        GroupBoardJsonVo vo = new GroupBoardJsonVo();
        TenantId tenantId = super.getTenantId();
        //接口耗时统计
        long currentTime = System.currentTimeMillis();
        try {
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        long currentTimeMillis = System.currentTimeMillis();
                        vo.setTotalProduction(getTotalProduction(startTime, endTime, tenantId));
                        System.out.println(Thread.currentThread().getName() + "查询总产量耗时：" + (System.currentTimeMillis() - currentTimeMillis));
                    }),
                    CompletableFuture.runAsync(() -> {
                        long currentTimeMillis = System.currentTimeMillis();
                        vo.setEquipmentOverview(getEquipmentOverview(tenantId));
                        System.out.println(Thread.currentThread().getName() + "查询设备综合耗时：" + (System.currentTimeMillis() - currentTimeMillis));
                    }),
                    CompletableFuture.runAsync(() -> {
                        long currentTimeMillis = System.currentTimeMillis();
                        vo.setEquipmentOEE(getEquipmentOEE(startTime, endTime, tenantId));
                        System.out.println(Thread.currentThread().getName() + "查询设备OEE耗时：" + (System.currentTimeMillis() - currentTimeMillis));
                    }),
                    CompletableFuture.runAsync(() -> {
                        long currentTimeMillis = System.currentTimeMillis();
                        vo.setProductionMonitor(getProductionMonitor(startTime, endTime, tenantId));
                        System.out.println(Thread.currentThread().getName() + "查询生产监控耗时：" + (System.currentTimeMillis() - currentTimeMillis));
                    }),
                    CompletableFuture.runAsync(() -> {
                        long currentTimeMillis = System.currentTimeMillis();
                        vo.setOrderMonitor(getOrderMonitor(startTime, endTime, tenantId));
                        System.out.println(Thread.currentThread().getName() + "查询订单监控耗时：" + (System.currentTimeMillis() - currentTimeMillis));
                    }),
                    CompletableFuture.runAsync(() -> {
                        long currentTimeMillis = System.currentTimeMillis();
                        vo.setEquipmentTypes(getEquipmentTypes(startTime, endTime, tenantId));
                        System.out.println(Thread.currentThread().getName() + "查询设备类型列表耗时：" + (System.currentTimeMillis() - currentTimeMillis));
                    })
            ).join();
            System.out.println("接口耗时情况：总耗时：" + (System.currentTimeMillis() - currentTime));
//            log.info("接口耗时情况：总耗时{},查询总产量耗时{},查询设备综合耗时{},查询设备OEE耗时{},查询生产监控耗时{},查询订单监控耗时{},查询设备类型列表耗时{}",System.currentTimeMillis()-currentTime);
        } catch (Exception e) {
            log.error("异常", e);
        }
        return vo;
    }

    /**
     * 总产量
     *
     * @return
     */
    private GroupBoardJsonVo.TotalProduction getTotalProduction(Long startTime, Long endTime, TenantId tenantId) {
        TodaySectionHistoryVo result = new TodaySectionHistoryVo();
        try {
            TsSqlDayVo tsSqlDayVo = TsSqlDayVo.constructionTsSqlDayVo(null, null, null, null);
            tsSqlDayVo.setTenantId(tenantId.getId());
            tsSqlDayVo.setStartTime(startTime);
            tsSqlDayVo.setEndTime(endTime);
            result = super.efficiencyStatisticsSvc.todaySectionHistory(tsSqlDayVo);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return new GroupBoardJsonVo().new TotalProduction(result);
        //TodaySectionHistoryVo todaySectionHistoryVo = bulletinBoardController.todaySectionHistory(startTime, endTime, null, null, null, null);
    }

    /**
     * 设备综合
     *
     * @return
     */
    private GroupBoardJsonVo.EquipmentOverview getEquipmentOverview(TenantId tenantId) {
        try {
            return new GroupBoardJsonVo().new EquipmentOverview(this.deviceMonitorService.getDeviceOnlineStatusData(tenantId, new FactoryDeviceQuery(null, null, null, null, true)));
            //return new GroupBoardJsonVo().new EquipmentOverview(rtMonitorBoardController.getDeviceOnlineStatusStatistics(null, null, null, null));
        } catch (Exception e) {
            log.error("设备综合查询异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设备OEE
     *
     * @return
     */
    private GroupBoardJsonVo.EquipmentOEE getEquipmentOEE(Long startTime, Long endTime, TenantId tenantId) {
        try {
            List<StatisticOeeVo> result = new ArrayList<>();
            List<StatisticOee> statisticOees = deviceOeeEveryHourService.getStatisticOeeEveryHourList(new StatisticOee(startTime, endTime, null, null, null, tenantId.getId()));
            if (!org.springframework.util.CollectionUtils.isEmpty(statisticOees)) {
                for (StatisticOee oee : statisticOees) {
                    result.add(new StatisticOeeVo(oee));
                }
            }
            return new GroupBoardJsonVo().new EquipmentOEE(result);
        } catch (ThingsboardException e) {
            log.error("设备OEE查询异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 生产监控
     *
     * @return
     */
    private GroupBoardJsonVo.ProductionMonitor getProductionMonitor(Long startTime, Long endTime, TenantId tenantId) {
        try {
            List<ProductionMonitorListVo> result = new ArrayList<>();
            List<ProductionCalender> calenderList = productionCalenderService.getProductionMonitorList(new ProductionCalender(
                    startTime, endTime, null, null, tenantId.getId()));
            if (!org.springframework.util.CollectionUtils.isEmpty(calenderList)) {
                for (ProductionCalender productionCalender : calenderList) {
                    ProductionMonitorListVo productionMonitorListVo = new ProductionMonitorListVo(productionCalender);
                    productionMonitorListVo.setYearAchieve(new BigDecimal(productionMonitorListVo.getYearAchieve()).multiply(new BigDecimal(100)).toString());
                    result.add(productionMonitorListVo);
                }
            }
            return new GroupBoardJsonVo().new ProductionMonitor(result);

            /*GroupBoardJsonVo.ProductionMonitor productionMonitor = new GroupBoardJsonVo().new ProductionMonitor();
            ProductionMonitorListQry dto = new ProductionMonitorListQry();
            dto.setStartTime(startTime);
            dto.setEndTime(endTime);
            List<ProductionMonitorListVo> productionMonitorenantList = productionCalenderController.getProductionMonitorenantList(dto);
            if (CollectionUtils.isNotEmpty(productionMonitorenantList)) {
                productionMonitor = new GroupBoardJsonVo().new ProductionMonitor(productionMonitorenantList);
            }
            return productionMonitor;*/
        } catch (ThingsboardException e) {
            log.error("生产监控查询异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 订单监控
     *
     * @return
     */
    private GroupBoardJsonVo.OrderMonitor getOrderMonitor(Long startTime, Long endTime, TenantId tenantId) {
        try {
            if (startTime == null || startTime == 0)
                startTime = CommonUtil.getTodayStartTime();
            if (endTime == null || endTime == 0)
                endTime = CommonUtil.getTodayCurrentTime();
            return new GroupBoardJsonVo().new OrderMonitor(this.orderService.listBoardCapacityMonitorOrders(tenantId, null, null, TimeQuery.builder().startTime(startTime).endTime(endTime).build()));

/*
            GroupBoardJsonVo.OrderMonitor orderMonitor = new GroupBoardJsonVo().new OrderMonitor();
            List<OrderCustomCapacityResult> orders = orderController.listBoardCapacityMonitorOrders(startTime, endTime, null, null);
            if (CollectionUtils.isNotEmpty(orders)) {
                orderMonitor = new GroupBoardJsonVo().new OrderMonitor(orders);
            }
            return orderMonitor;*/
        } catch (Exception e) {
            log.error("订单监控查询异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设备类型列表
     *
     * @return
     */
    private List<GroupBoardJsonVo.EquipmentTypes> getEquipmentTypes(Long startTime, Long endTime, TenantId tenantId) {
        try {
            List<GroupBoardJsonVo.EquipmentTypes> list = new ArrayList<>();
            //1.字典
//            List<BoardV3DeviceDitEntity> boardV3DeviceDitEntityList = bulletinBoardV3Controller.queryDeviceDictionary(null, null, null, null);
            List<BoardV3DeviceDitEntity> boardV3DeviceDitEntityList = this.queryDeviceDictionary(tenantId);
            if (CollectionUtils.isNotEmpty(boardV3DeviceDitEntityList)) {
                boardV3DeviceDitEntityList.forEach(i -> {
                    GroupBoardJsonVo.EquipmentTypes equipmentTypes = new GroupBoardJsonVo().new EquipmentTypes();
                    equipmentTypes.setEquipmentTypeName(i.getName());
                    //2.仪表盘水电气
                    BoardV3DeviceDictionaryVo deviceDictionaryVo = new BoardV3DeviceDictionaryVo();
                    deviceDictionaryVo.setId(i.getId());
                    deviceDictionaryVo.setStartTime(startTime);
                    deviceDictionaryVo.setEndTime(endTime);
//                    equipmentTypes.setDashBoardData(bulletinBoardV3Controller.queryDashboardValue(deviceDictionaryVo));
                    equipmentTypes.setDashBoardData(bulletinV3BoardVsSvc.queryDashboardValue(deviceDictionaryVo, tenantId));
                    //3.能耗排行
//                    equipmentTypes.setEnergyConsumptionTrend(bulletinBoardV3Controller.energyConsumptionToday(i.getId().toString(), null, startTime, endTime));
                    QueryTsKvVo queryTsKvVo = new QueryTsKvVo();
                    queryTsKvVo.setStartTime(CommonUtils.getZero());
                    queryTsKvVo.setEndTime(CommonUtils.getNowTime());
                    queryTsKvVo.setDictDeviceId(i.getId());
                    queryTsKvVo.setTenantId(tenantId.getId());
                    equipmentTypes.setEnergyConsumptionTrend(bulletinBoardSvc.todayUnitEnergy(queryTsKvVo, tenantId));

                    //4.能耗趋势
                    TrendParameterVo vo = new TrendParameterVo();
                    vo.setDictDeviceId(i.getId());
                    vo.setStartTime(startTime);
                    vo.setEndTime(endTime);
                    try {
                        vo.setKey(KeyTitleEnums.key_water.getCode());
//                        equipmentTypes.setTrendChart02VoWater(bulletinBoardV3Controller.trendChart(vo));
                        equipmentTypes.setTrendChart02VoWater(bulletinV3BoardVsSvc.trendChart(vo, tenantId));
                        vo.setKey(KeyTitleEnums.key_cable.getCode());
//                        equipmentTypes.setTrendChart02VoElectricity(bulletinBoardV3Controller.trendChart(vo));
                        equipmentTypes.setTrendChart02VoElectricity(bulletinV3BoardVsSvc.trendChart(vo, tenantId));
                        vo.setKey(KeyTitleEnums.key_gas.getCode());
//                        equipmentTypes.setTrendChart02VoGas(bulletinBoardV3Controller.trendChart(vo));
                        equipmentTypes.setTrendChart02VoGas(bulletinV3BoardVsSvc.trendChart(vo, tenantId));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    list.add(equipmentTypes);
                });
            }
            return list;
        } catch (Exception e) {
            log.error("设备类型列表查询异常", e);
            throw new RuntimeException(e);
        }
    }

    private List<BoardV3DeviceDitEntity> queryDeviceDictionary(TenantId tenantId) {
        TsSqlDayVo tsSqlDayVo = TsSqlDayVo.constructionTsSqlDayVo(null, null, null, null);
        tsSqlDayVo.setTenantId(tenantId.getId());
        return bulletinV3BoardVsSvc.queryDeviceDictionaryByEntityVo(tsSqlDayVo);
    }

}
