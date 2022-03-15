package org.thingsboard.server.dao.productioncalender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.dao.OrderPlanEntity;
import org.thingsboard.server.dao.hs.dao.OrderPlanRepository;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.hs.service.OrderService;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;
import org.thingsboard.server.dao.sql.attributes.AttributeKvRepository;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ProductionCalenderServiceImpl implements ProductionCalenderService {

    private final ProductionCalenderDao productionCalenderDao;
    private final DeviceDao deviceDao;
    private final OrderPlanRepository orderPlanRepository;
    private final FactoryDao factoryDao;
    private final BulletinBoardSvc bulletinBoardSvc;
    private final DictDeviceService dictDeviceService;
    private final OrderService orderService;
    // 设备Service
    @Autowired
    DeviceService deviceService;

    @Autowired
    private AttributeKvRepository attributeKvRepository;

    public ProductionCalenderServiceImpl(ProductionCalenderDao productionCalenderDao, DeviceDao deviceDao, FactoryDao factoryDao, OrderPlanRepository orderPlanRepository, BulletinBoardSvc bulletinBoardSvc, DictDeviceService dictDeviceService, OrderService orderService) {
        this.productionCalenderDao = productionCalenderDao;
        this.deviceDao = deviceDao;
        this.factoryDao = factoryDao;
        this.orderPlanRepository = orderPlanRepository;
        this.bulletinBoardSvc = bulletinBoardSvc;
        this.dictDeviceService = dictDeviceService;
        this.orderService = orderService;
    }

    @Override
    public void saveProductionCalender(ProductionCalender productionCalender) throws ThingsboardException {
        productionCalenderDao.saveProductionCalender(productionCalender);
    }

    @Override
    public void delProductionCalender(UUID id) throws ThingsboardException {
        productionCalenderDao.delProductionCalender(id);
    }

    @Override
    public ProductionCalender findById(UUID id) {
        return productionCalenderDao.findById(id);
    }

    @Override
    public PageData<ProductionCalender> findProductionCalenderPage(ProductionCalender productionCalender, PageLink pageLink) {
        return productionCalenderDao.findProductionCalenderPage(productionCalender, pageLink);
    }

    /**
     * 设备生产日历历史记录分页列表
     *
     * @param deviceId
     * @return
     */
    @Override
    public PageData<ProductionCalender> getHistoryPageByDeviceId(UUID deviceId, PageLink pageLink) {
        return productionCalenderDao.getHistoryPageByDeviceId(deviceId, pageLink);
    }

    /**
     * 设备生产日历历史记录列表
     *
     * @param deviceId
     * @return
     */
    @Override
    public List<ProductionCalender> getHistoryByDeviceId(UUID deviceId) {
        return productionCalenderDao.getHistoryByDeviceId(deviceId);
    }

    /**
     * 看板生产监控统计
     * @param productionCalender
     * @return
     * @throws ThingsboardException
     */
    @Override
    public List<ProductionCalender> getProductionMonitorList(ProductionCalender productionCalender) throws ThingsboardException{
        List<ProductionCalender> result = new ArrayList<>();
        if (productionCalender.getWorkshopId() != null) {
            log.info("车间看板生产监控统计");
            result = getProductionMonitorListResult(productionCalender);
        } else if (productionCalender.getFactoryId() != null) {
            log.info("工厂看板生产监控统计");
            result = getProductionMonitorListResult(productionCalender);
        }else {
            log.info("集团看板生产监控统计");
            result = this.getProductionMonitorTenantList(productionCalender);
        }
        return result;
    }


    /**
     * 查询车间看板设备监控统计
     *
     * @return
     */
    public List<ProductionCalender> getProductionMonitorListResult(ProductionCalender productionCalender) throws ThingsboardException{
        List<ProductionCalender> resultProductionCalenders = new ArrayList<>();
        Long startTime = productionCalender.getStartTime();
        Long endTime = productionCalender.getEndTime();
        UUID tenantId = productionCalender.getTenantId();
        UUID factoryId = productionCalender.getFactoryId();
        UUID workshopId = productionCalender.getWorkshopId();

        Device deviceParam = new Device();
        deviceParam.setTenantId(new TenantId(tenantId));
        deviceParam.setFactoryId(factoryId);
        deviceParam.setWorkshopId(workshopId);
        deviceParam.setFilterGatewayFlag(true);
        List<Device> deviceList = deviceService.findDeviceListByCdn(deviceParam);

        if (!CollectionUtils.isEmpty(deviceList)) {
            deviceList.forEach(device -> {
                UUID deviceId = device.getUuidId();
                ProductionCalender resultProductionCalender = new ProductionCalender();
                //1.计算每个设备的完成量/计划量
                String actual = orderService.findActualByDeviceId(deviceId, startTime, endTime);
                String  intended = orderService.findIntendedByDeviceId(deviceId ,startTime, endTime);

                resultProductionCalender.setDeviceId(deviceId);
                resultProductionCalender.setDeviceName(device.getName());
                resultProductionCalender.setAchieveOrPlan((actual==null?"0":actual) + "/" + (intended==null?"0":intended));
                //2.产能达成率 = 选择设备实际时间范围内（默认当天）参与产能运算的设备实际计算产量总和【调云辉提供的接口】/（订单关联的设备的标准产能【设备字典的额定产能】*设备日历中的时间【生产日历-取交叉-取小时】总和）
                //2.1 时间范围内设备实际产能总和
                BigDecimal deviceOutputReality = new BigDecimal(0);
                List<DeviceCapacityVo> deviceCapacityVoList = new ArrayList<>();
                DeviceCapacityVo dcv = new DeviceCapacityVo(deviceId,startTime, endTime);
                deviceCapacityVoList.add(dcv);
                var dataMap = this.bulletinBoardSvc.queryCapacityValueByDeviceIdAndTime(deviceCapacityVoList);
                deviceOutputReality = new BigDecimal(dataMap.get(deviceId));
                //2.2 订单关联的设备的标准产能【设备字典的额定产能】* 设备日历中的时间【生产日历-取交叉-取小时】总和
                BigDecimal deviceOutputPredict = new BigDecimal(0);
                //设备标准产能
                UUID dictDiviceId = deviceDao.getDeviceInfo(deviceId).getDictDeviceId();
                BigDecimal ratedCapacity = new BigDecimal(0);
                if(dictDiviceId !=null){
                    ratedCapacity = dictDeviceService.findById(dictDiviceId).getRatedCapacity();
                }
                //单个设备生产日历总时间
                BigDecimal time = new BigDecimal(0);
                //生产日历时间
                List<ProductionCalender> historyList = productionCalenderDao.getHistoryByDeviceId(deviceId);
                if (!CollectionUtils.isEmpty(historyList)) {
                    for (ProductionCalender pc : historyList) {
                        Map<String, Long> mapTime = this.intersectionTime(pc.getStartTime(), pc.getEndTime(), startTime, endTime);
                        time.add(this.timeDifferenceForHours(mapTime.get("startTime"), mapTime.get("endTime")));
                    }
                }
                //单个设备 预计产量
                deviceOutputPredict = ratedCapacity.multiply(time);

                //3.达成率
                if(deviceOutputPredict.compareTo(BigDecimal.ZERO) == 0){
                    resultProductionCalender.setYearAchieve("0");
                }else {
                    resultProductionCalender.setYearAchieve(deviceOutputReality.divide(deviceOutputPredict).toString());
                }

                //4.生产状态
                AttributeKvEntity attributeKvEntity =  attributeKvRepository.findOneKeyByEntityId(EntityType.DEVICE,deviceId,"active");
                resultProductionCalender.setProductionState(attributeKvEntity.getBooleanValue());

                resultProductionCalenders.add(resultProductionCalender);
            });
        }

        //resultProductionCalenders.stream().sorted(Comparator.comparing(ProductionCalender::getAchieveOrPlan).reversed()).collect(Collectors.toList());
        resultProductionCalenders.stream().sorted(Comparator.comparing(ProductionCalender::getProductionState)
                .thenComparing(ProductionCalender::getAchieveOrPlan,Comparator.reverseOrder()));
        return resultProductionCalenders;
    }

    /**
     * 查询集团看板设备监控统计
     *
     * @return
     */
    public List<ProductionCalender> getProductionMonitorTenantList(ProductionCalender productionCalender) throws ThingsboardException{
        List<ProductionCalender> resultProductionCalenders = new ArrayList<>();
        Long startTime = productionCalender.getStartTime();
        Long endTime = productionCalender.getEndTime();
        //集团看板生产监控查询
        ///查询租户下所有工厂
        List<Factory> factoryList = factoryDao.findFactoryByTenantId(productionCalender.getTenantId());
        if (!CollectionUtils.isEmpty(factoryList)) {
            factoryList.forEach(factory -> {
                ProductionCalender resultProductionCalender = new ProductionCalender();
                //1.统计每个工厂的设备完成量/计划量
                //1.1总完成量
                String actual = orderService.findActualByFactoryIds(factory.getId(), startTime, endTime);
                //1.2总计划量
                String intended = orderService.findIntendedByFactoryIds(factory.getId(), startTime, endTime);

                resultProductionCalender.setFactoryId(factory.getId());
                resultProductionCalender.setFactoryName(factory.getName());
                resultProductionCalender.setAchieveOrPlan(actual + "/" + intended);

                //2.产能达成率 = 选择设备实际时间范围内(默认当天)参与产能运算的设备实际计算产量总和/(订单关联的设备的标准产能*设备日历中的时间总和)
                //2.1 参与产能运算的设备实际计算产量总和   注意：只取交叉时间内的产量
                //筛选参与计算的设备 以及 每个设备在订单计划中，实际生产时间与查询时间条件，两者的交叉时间（包含开始与结束），再用这个时间去查产能历史记录，查到具体产能。

                //一个工厂下产量总和  (实际产量总和)
                BigDecimal deviceOutputReality = new BigDecimal(0);
                //每个（设备标准产能 * 设备日历中的时间）的总和   （预计总产量）
                BigDecimal deviceOutputPredict = new BigDecimal(0);

                List<OrderPlanEntity> orderPlanEntityList = orderPlanRepository.findActualByFactoryIds(factory.getId(), startTime, endTime);
                if (!CollectionUtils.isEmpty(orderPlanEntityList)) {
                    //每个设备
                    for (OrderPlanEntity orderPlanEntity : orderPlanEntityList) {
                        UUID deviceId = orderPlanEntity.getDeviceId();
                        List<DeviceCapacityVo> deviceCapacityVoList = new ArrayList<>();
                        Map<String, Long> mapTime = this.intersectionTime(orderPlanEntity.getActualStartTime(), orderPlanEntity.getActualEndTime(), startTime, endTime);
                        deviceCapacityVoList.add(new DeviceCapacityVo(deviceId, mapTime.get("startTime"), mapTime.get("endTime")));
                        //每个设备的产能
                        Map<UUID, String> map = bulletinBoardSvc.queryCapacityValueByDeviceIdAndTime(deviceCapacityVoList);
                        deviceOutputReality.add(new BigDecimal(map.get(deviceId)));
                    }

                    //2.2 每个"订单关联的设备的标准产能*设备日历中的时间总和"相加    注意：只取交叉时间值（单位小时）

                    orderPlanEntityList.stream().map(m -> m.getDeviceId()).collect(Collectors.toList()).forEach(deviceId -> {
                        //设备标准产能
                        BigDecimal ratedCapacity = dictDeviceService.findById(deviceDao.getDeviceInfo(deviceId).getDictDeviceId()).getRatedCapacity();
                        //单个设备生产日历总时间
                        BigDecimal time = new BigDecimal(0);

                        //生产日历时间
                        List<ProductionCalender> historyList = productionCalenderDao.getHistoryByDeviceId(deviceId);
                        if (!CollectionUtils.isEmpty(historyList)) {
                            for (ProductionCalender pc : historyList) {
                                Map<String, Long> mapTime = this.intersectionTime(pc.getStartTime(), pc.getEndTime(), startTime, endTime);
                                time.add(this.timeDifferenceForHours(mapTime.get("startTime"), mapTime.get("endTime")));
                            }
                        }
                        //单个设备 预计产量
                        deviceOutputPredict.add(ratedCapacity.multiply(time));
                    });
                }
                //年产能达成率
                if(deviceOutputPredict.compareTo(BigDecimal.ZERO) == 0){
                    resultProductionCalender.setYearAchieve("0");
                }else {
                    resultProductionCalender.setYearAchieve(deviceOutputReality.divide(deviceOutputPredict).toString());
                }

                //3.生产状态:工厂下网关的在线、离线状态。有一个在线视为正常，全部离线视为异常
                try {
                    resultProductionCalender.setProductionState(factoryDao.checkoutFactoryStatus(factory.getId()));
                } catch (ThingsboardException e) {
                    new ThingsboardException("查询工厂在线报错", ThingsboardErrorCode.FAIL_VIOLATION);
                }

                resultProductionCalenders.add(resultProductionCalender);
            });
        }
        return resultProductionCalenders;
    }


    /**
     * 计算工厂的的总实际完成量/总计划量
     *
     * @param orderPlanEntityList
     * @return
     */
    public ProductionCalender statisticsFactory(List<OrderPlanEntity> orderPlanEntityList) {
        ProductionCalender resultProductionCalender = new ProductionCalender();
        //实际完成量
        Double actualCapacity = 0.0;
        //计划完成量
        Double intendedCapacity = 0.0;
        for (OrderPlanEntity orderPlanEntity : orderPlanEntityList) {
            Double qryActualCapacity = 0.0;
            Double qryIntendedCapacity = 0.0;
            if (StringUtils.isNotEmpty(orderPlanEntity.getActualCapacity())) {
                qryActualCapacity = Double.parseDouble(orderPlanEntity.getActualCapacity());
            }
            if (StringUtils.isNotEmpty(orderPlanEntity.getIntendedCapacity())) {
                qryIntendedCapacity = Double.parseDouble(orderPlanEntity.getIntendedCapacity());
            }
            actualCapacity += qryActualCapacity;
            intendedCapacity += qryIntendedCapacity;
        }
        resultProductionCalender.setAchieveOrPlan(actualCapacity.toString() + "/" + intendedCapacity.toString());
        return resultProductionCalender;
    }

    /**
     * 计算时间交集
     * @param actualStartTime 实际数据-开始时间
     * @param actualEndTime   实际数据-结束时间
     * @param startTimeQry    查询条件开始时间
     * @param endTimeQry      查询条件结束时间
     * @return
     */
    public Map<String, Long> intersectionTime(Long actualStartTime, Long actualEndTime, Long startTimeQry, Long endTimeQry) {
        Map<String, Long> map = new HashMap<>();
        map.put("startTime", new Long(0));
        map.put("endTime", new Long(0));

        Long startTime = new Long(0);
        Long endTime = new Long(0);

        //时间要取交叉时间
        if (startTimeQry < actualStartTime && actualEndTime < endTimeQry) {
            startTime = actualStartTime;
            endTime = actualEndTime;
        }
        if (startTimeQry < actualStartTime && actualStartTime < endTimeQry && endTimeQry < actualEndTime) {
            startTime = actualStartTime;
            endTime = endTimeQry;
        }
        if (actualStartTime < startTimeQry && startTimeQry < actualEndTime && actualEndTime < endTimeQry) {
            startTime = startTimeQry;
            endTime = actualEndTime;
        }
        if (actualStartTime < startTimeQry && endTimeQry < actualStartTime) {
            startTime = startTimeQry;
            endTime = endTimeQry;
        }
        map.put("startTime", startTime);
        map.put("endTime", endTime);

        return map;
    }

    /**
     * 计算时间戳相间隔的小时数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public BigDecimal timeDifferenceForHours(Long startTime, Long endTime) {
        Long timeDifference = endTime - startTime;
        Long day = timeDifference / (24 * 60 * 60 * 1000);
        Long hour = (timeDifference / (60 * 60 * 1000) - day * 24);
        return new BigDecimal(hour);
    }


}
