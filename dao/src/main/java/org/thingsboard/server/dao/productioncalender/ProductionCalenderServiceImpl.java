package org.thingsboard.server.dao.productioncalender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.dao.OrderEntity;
import org.thingsboard.server.dao.hs.dao.OrderPlanEntity;
import org.thingsboard.server.dao.hs.dao.OrderPlanRepository;
import org.thingsboard.server.dao.hs.dao.OrderRepository;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ProductionCalenderServiceImpl implements ProductionCalenderService {

    private final ProductionCalenderDao productionCalenderDao;
    private final DeviceDao deviceDao;
    private final OrderRepository orderRepository;
    private final OrderPlanRepository orderPlanRepository;
    private final FactoryDao factoryDao;
    private final BulletinBoardSvc bulletinBoardSvc;
    private final DictDeviceService dictDeviceService;

    public ProductionCalenderServiceImpl(ProductionCalenderDao productionCalenderDao, DeviceDao deviceDao, OrderRepository orderRepository, FactoryDao factoryDao, OrderPlanRepository orderPlanRepository, BulletinBoardSvc bulletinBoardSvc,DictDeviceService dictDeviceService) {
        this.productionCalenderDao = productionCalenderDao;
        this.deviceDao = deviceDao;
        this.orderRepository = orderRepository;
        this.factoryDao = factoryDao;
        this.orderPlanRepository = orderPlanRepository;
        this.bulletinBoardSvc = bulletinBoardSvc;
        this.dictDeviceService = dictDeviceService;
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
     * 查询看板设备监控统计
     *
     * @return
     */
    @Override
    public List<ProductionCalender> getProductionMonitorList(ProductionCalender productionCalender) {
        List<ProductionCalender> resultProductionCalenders = new ArrayList<>();
        switch (productionCalender.getQryType()) {
            case 1:
                //集团看板生产监控查询
                ///查询租户下所有工厂
                List<Factory> factoryList = factoryDao.findFactoryByTenantId(productionCalender.getTenantId());
                if (!CollectionUtils.isEmpty(factoryList)) {
                    //查询工厂所有的订单
                    List<OrderEntity> orderEntityList = orderRepository.findAllByFactoryIds(factoryList.stream().map(m -> m.getId()).collect(Collectors.toList()));
                    if (!CollectionUtils.isEmpty(orderEntityList)) {
                        //查询满足时间范围内的所有订单计划
                        List<OrderPlanEntity> orderPlanEntityList = orderPlanRepository.findAllByOrderIdsAndTime(orderEntityList.stream().map(m -> m.getId()).collect(Collectors.toList()), productionCalender.getStartTime(), productionCalender.getEndTime());
                        if (!CollectionUtils.isEmpty(orderPlanEntityList)) {
                            for (Factory factory : factoryList) {
                                ProductionCalender resultProductionCalender = new ProductionCalender();
                                resultProductionCalender.setFactoryId(factory.getId());
                                resultProductionCalender.setFactoryName(factory.getName());
                                //当前租户工厂下的所有订单设备完成量/计划量
                                resultProductionCalenders.add(this.statisticsFactory(factory.getId(), orderEntityList, orderPlanEntityList));

                                //查询每个工厂下所有的设备总产能
                                Double sumOutput = this.statisticsDeviceoutput(factory.getId(), orderEntityList, orderPlanEntityList, productionCalender.getStartTime(), productionCalender.getEndTime());
                                //设备的标准产能*设备日历的总和
                                //String dividend = this.statisticsDeviceoutput(factory.getId(), orderEntityList, orderPlanEntityList, productionCalender.getStartTime(), productionCalender.getEndTime());

                            }

                        }
                    }
                }

                break;
            default:
                break;
        }

        //查询所有设备
        List<Device> deviceListByCdn = deviceDao.findDeviceListByCdn(new Device(productionCalender.getTenantId(), productionCalender.getFactoryId(), productionCalender.getWorkshopId(), true));
        if (!CollectionUtils.isEmpty(deviceListByCdn)) {
            List<UUID> deviceIds = deviceListByCdn.stream().map(m -> m.getId().getId()).collect(Collectors.toList());
            // 根据设备id以及实际时间查询订单信息
            List<OrderPlanEntity> deviceAchieveOrPlanList = null;//orderService.findDeviceAchieveOrPlanList(deviceIds, productionCalender.getStartTime(), productionCalender.getEndTime());
            if (!CollectionUtils.isEmpty(deviceAchieveOrPlanList)) {
                log.isTraceEnabled();
            }
        }

        return null;
    }

    /**
     * 计算工厂的的总实际完成量/总计划量
     *
     * @param factoryId
     * @param allByFactoryIds 工厂下订单
     * @param allByOrderIds   工厂下所有订单计划
     * @return
     */
    public ProductionCalender statisticsFactory(UUID factoryId, List<OrderEntity> allByFactoryIds, List<OrderPlanEntity> allByOrderIds) {
        ProductionCalender resultProductionCalender = new ProductionCalender();

        //实际完成量
        Double actualCapacity = 0.0;
        //计划完成量
        Double intendedCapacity = 0.0;
        for (OrderEntity orderEntity : allByFactoryIds) {
            if (orderEntity.getFactoryId() == factoryId) {
                for (OrderPlanEntity orderPlanEntity : allByOrderIds) {
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
            }
        }
        resultProductionCalender.setAchieveOrPlan(actualCapacity.toString() + "/" + intendedCapacity.toString());
        return resultProductionCalender;
    }

    /**
     * 批量查询设备在查询时间区间内的产量
     *
     * @param orderPlanEntityList
     * @param startTime
     * @param endTime
     * @return
     */
    public Double statisticsDeviceoutput(UUID factoryId, List<OrderEntity> orderEntityList, List<OrderPlanEntity> orderPlanEntityList, Long startTime, Long endTime) {
        /*List<DeviceCapacityVo> deviceCapacityVoList = new ArrayList<>();
        //产能达成率 = 选择设备实际时间范围内(默认当天)参与产能运算的设备实际计算产量总和/(订 单关联的设备的标准产能*设备日历中的时间总和)
        BigDecimal yearAchieve = new BigDecimal(0);
        //总产量
        BigDecimal sumOutput = new BigDecimal(0);



        if (!CollectionUtils.isEmpty(orderEntityList)) {
            for (OrderEntity orderEntity : orderEntityList) {

                //同一个工厂
                if (orderEntity.getFactoryId() != null && orderEntity.getFactoryId() == factoryId) {

                    if (!CollectionUtils.isEmpty(orderPlanEntityList)) {
                        for (OrderPlanEntity orderPlanEntity : orderPlanEntityList) {

                            //同一个订单
                            if (orderEntity.getId() == orderPlanEntity.getOrderId()) {
                                Long actualStartTime = orderPlanEntity.getActualStartTime();
                                Long actualEndTime = orderPlanEntity.getActualEndTime();
                                //时间要取交叉时间
                                if (actualStartTime != null && actualEndTime != null) {
                                    if (startTime < actualStartTime && actualEndTime < endTime) {
                                        actualStartTime = actualStartTime;
                                        actualEndTime = actualEndTime;
                                    }
                                    if (startTime < actualStartTime && actualStartTime < endTime && endTime < actualEndTime) {
                                        actualStartTime = actualStartTime;
                                        actualEndTime = endTime;
                                    }
                                    if (actualStartTime < startTime && startTime < actualEndTime && actualEndTime < endTime) {
                                        actualStartTime = startTime;
                                        actualEndTime = actualEndTime;
                                    }
                                    if (actualStartTime < startTime && endTime < actualStartTime) {
                                        actualStartTime = startTime;
                                        actualEndTime = endTime;
                                    }
                                    //产量
                                    deviceCapacityVoList.add(new DeviceCapacityVo(orderPlanEntity.getId(), orderPlanEntity.getDeviceId(), actualStartTime, actualEndTime));
                                }
                            }
                        }

                        //计算每个设备的生产时间
                       *//* List<UUID> deviceIdList = orderPlanEntityList.stream().map(m -> m.getDeviceId()).distinct().collect(Collectors.toList());
                        if(!CollectionUtils.isEmpty(deviceIdList)){
                            deviceIdList.forEach(i->{
                                for (OrderPlanEntity orderPlanEntity : orderPlanEntityList) {
                                    if(){

                                    }
                                }

                            });
                        }*//*



                        //计算设备的生产日历，里面计划的时间
                        //每个设备 标准产能
                        BigDecimal ratedCapacity = dictDeviceService.findById(deviceDao.getDeviceInfo(orderPlanEntity.getDeviceId()).getDictDeviceId()).getRatedCapacity();

                        //每个设备日历时间（小时）
                        BigDecimal productionTimeHours = null;

                        List<ProductionCalender> productionCalenderList = productionCalenderDao.getHistoryByDeviceId(orderPlanEntity.getDeviceId());
                        if(!CollectionUtils.isEmpty(productionCalenderList)){
                            Long productionTime = null;
                            for (ProductionCalender productionCalender : productionCalenderList){
                                Long productionStartTime = productionCalender.getStartTime();
                                Long productionEndTime = productionCalender.getEndTime();

                                //时间要取交叉时间
                                if (productionStartTime != null && productionEndTime != null) {
                                    if (startTime < productionStartTime && productionEndTime < endTime) {
                                        productionStartTime = productionStartTime;
                                        productionEndTime = productionEndTime;
                                    }
                                    if (startTime < productionStartTime && productionStartTime < endTime && endTime < productionEndTime) {
                                        productionStartTime = productionStartTime;
                                        productionEndTime = endTime;
                                    }
                                    if (productionStartTime < startTime && startTime < productionEndTime && productionEndTime < endTime) {
                                        productionStartTime = startTime;
                                        productionEndTime = productionEndTime;
                                    }
                                    if (productionStartTime < startTime && endTime < productionStartTime) {
                                        productionStartTime = startTime;
                                        productionEndTime = endTime;
                                    }
                                    productionTime += endTime-startTime;
                                }
                            }
                            long day = productionTime / (24 * 60 * 60 * 1000);
                            long hour = (productionTime / (60 * 60 * 1000) - day * 24);
                            productionTimeHours = new BigDecimal(hour);
                        }

                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(deviceCapacityVoList)) {
            Map<UUID, String> map = bulletinBoardSvc.queryCapacityValueByDeviceIdAndTime(deviceCapacityVoList);
            //计算总产量
            if (map != null) {
                for (Map.Entry<UUID, String> entry : map.entrySet()) {
                    sumOutput += StringUtils.isNotEmpty(entry.getValue()) ? Double.parseDouble(entry.getValue()) : 0.0;
                }

            }
        }*/
        return 0.0;
    }

}
