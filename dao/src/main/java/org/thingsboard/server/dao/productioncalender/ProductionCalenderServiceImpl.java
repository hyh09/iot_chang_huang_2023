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
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.factory.FactoryDao;
import org.thingsboard.server.dao.hs.dao.OrderEntity;
import org.thingsboard.server.dao.hs.dao.OrderPlanEntity;
import org.thingsboard.server.dao.hs.dao.OrderPlanRepository;
import org.thingsboard.server.dao.hs.dao.OrderRepository;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    public ProductionCalenderServiceImpl(ProductionCalenderDao productionCalenderDao, DeviceDao deviceDao, OrderRepository orderRepository, FactoryDao factoryDao, OrderPlanRepository orderPlanRepository, BulletinBoardSvc bulletinBoardSvc) {
        this.productionCalenderDao = productionCalenderDao;
        this.deviceDao = deviceDao;
        this.orderRepository = orderRepository;
        this.factoryDao = factoryDao;
        this.orderPlanRepository = orderPlanRepository;
        this.bulletinBoardSvc = bulletinBoardSvc;
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
     * @param deviceId
     * @return
     */
    @Override
    public PageData<ProductionCalender> getHistoryPageByDeviceId(UUID deviceId, PageLink pageLink){
        return productionCalenderDao.getHistoryPageByDeviceId(deviceId,pageLink);
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

                                //查询每个工厂下所有的设备产能
                                this.statisticsDeviceoutput(factory.getId(), orderEntityList, orderPlanEntityList, productionCalender.getStartTime(), productionCalender.getEndTime());
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
    public String statisticsDeviceoutput(UUID factoryId, List<OrderEntity> orderEntityList, List<OrderPlanEntity> orderPlanEntityList, Long startTime, Long endTime) {
        List<DeviceCapacityVo> deviceCapacityVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderEntityList)) {
            for (OrderEntity orderEntity : orderEntityList) {

                //同一个工厂
                if(orderEntity.getFactoryId() != null && orderEntity.getFactoryId() == factoryId){

                    if(!CollectionUtils.isEmpty(orderPlanEntityList)){
                        for (OrderPlanEntity orderPlanEntity : orderPlanEntityList) {

                            //同一个订单
                            if(orderEntity.getId() == orderPlanEntity.getOrderId()){
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
                                    if (actualStartTime < startTime && actualEndTime < endTime) {
                                        actualStartTime = startTime;
                                        actualEndTime = actualEndTime;
                                    }
                                    if (actualStartTime < startTime && actualStartTime < endTime && endTime < actualStartTime) {
                                        actualStartTime = startTime;
                                        actualEndTime = endTime;
                                    }
                                    deviceCapacityVoList.add(new DeviceCapacityVo(orderPlanEntity.getId(), orderPlanEntity.getDeviceId(), actualStartTime, actualEndTime));
                                }

                            }
                        }
                    }
                }
            }
        }
        if(!CollectionUtils.isEmpty(deviceCapacityVoList)){
            Map<UUID, String> map = bulletinBoardSvc.queryCapacityValueByDeviceIdAndTime(deviceCapacityVoList);
            //计算总产量
            if(map != null){

                /*for (EntityKeyValueType valueType:map.entrySet()){

                }*/

            }
        }
        return null;
    }

}
