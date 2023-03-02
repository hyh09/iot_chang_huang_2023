package org.thingsboard.server.dao.hs.service.Impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.hs.dao.OrderPlanEntity;
import org.thingsboard.server.dao.hs.dao.OrderPlanRepository;
import org.thingsboard.server.dao.hs.dao.OrderRepository;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.hs.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 订单接口实现类
 *
 * @author wwj
 * @since 2021.10.18
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
@AllArgsConstructor
public class OrderServiceImpl extends AbstractEntityService implements OrderService, CommonService {

    private final OrderRepository orderRepository;
    private final OrderPlanRepository orderPlanRepository;

    /**
     * 查询设备订单信息
     *
     * @param deviceIds
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<OrderPlanEntity> findDeviceAchieveOrPlanList(List<UUID> deviceIds, Long startTime, Long endTime) {
        return orderPlanRepository.findDeviceAchieveOrPlanList(deviceIds, startTime, endTime);
    }

    /**
     * 查询时间范围内的总实际产量
     *
     * @param factoryIds
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public BigDecimal findActualByFactoryIds(UUID factoryIds, Long startTime, Long endTime) {
        BigDecimal sumActual = new BigDecimal(0);
        List<OrderPlanEntity> orderPlanEntityList = orderPlanRepository.findActualByFactoryIds(factoryIds, startTime, endTime);
        String actualCapacity = null;
        if (!CollectionUtils.isEmpty(orderPlanEntityList)) {
            for (OrderPlanEntity i : orderPlanEntityList) {
                actualCapacity = i.getActualCapacity();
                if (StringUtils.isNotEmpty(actualCapacity)) {
                    sumActual = sumActual.add(new BigDecimal(actualCapacity));
                }
            }
        }
        return sumActual;
    }

    /**
     * 查询时间范围内的总计划产量
     *
     * @param factoryIds
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public BigDecimal findIntendedByFactoryIds(UUID factoryIds, Long startTime, Long endTime) {
        BigDecimal sumActual = new BigDecimal(0);
        List<OrderPlanEntity> orderPlanEntityList = orderPlanRepository.findIntendedByFactoryIds(factoryIds, startTime, endTime);
        String intendedCapacity = null;
        if (!CollectionUtils.isEmpty(orderPlanEntityList)) {
            for (OrderPlanEntity i : orderPlanEntityList) {
                intendedCapacity = i.getIntendedCapacity();
                if (StringUtils.isNotEmpty(intendedCapacity)) {
                    sumActual = sumActual.add(new BigDecimal(intendedCapacity));
                }
            }
        }
        return sumActual;
    }

    @Override
    public String findIntendedByDeviceId(UUID deviceId, Long startTime, Long endTime) {
        BigDecimal intended = new BigDecimal(0);
        List<OrderPlanEntity> orderPlanEntityList = orderPlanRepository.findIntendedByDeviceId(deviceId, startTime, endTime);
        if (!CollectionUtils.isEmpty(orderPlanEntityList)) {
            for (OrderPlanEntity o : orderPlanEntityList) {
                if (StringUtils.isNotEmpty(o.getIntendedCapacity())) {
                    intended = intended.add(new BigDecimal(o.getIntendedCapacity()));
                }
            }
            ;
        }
        return intended.toString();
    }

    @Override
    public String findActualByDeviceId(UUID deviceId, Long startTime, Long endTime) {
        BigDecimal actual = new BigDecimal(0);
        List<OrderPlanEntity> orderPlanEntityList = orderPlanRepository.findActualByDeviceId(deviceId, startTime, endTime);
        if (!CollectionUtils.isEmpty(orderPlanEntityList)) {
            for (OrderPlanEntity o : orderPlanEntityList) {
                if (StringUtils.isNotEmpty(o.getActualCapacity())) {
                    actual = actual.add(new BigDecimal(o.getActualCapacity()));
                }
            }
            ;
        }
        return actual.toString();
    }


}
