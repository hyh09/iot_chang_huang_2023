package org.thingsboard.server.dao.hs.service.Impl;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.dao.*;
import org.thingsboard.server.dao.hs.entity.bo.OrderCapacityBO;
import org.thingsboard.server.dao.hs.entity.bo.OrderDeviceCapacityBO;
import org.thingsboard.server.dao.hs.entity.bo.OrderExcelBO;
import org.thingsboard.server.dao.hs.entity.po.Order;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.hs.service.OrderService;
import org.thingsboard.server.dao.hs.utils.CommonUtil;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final ClientService clientService;

    /**
     * 保存订单-从文件导入
     *
     * @param tenantId          租户Id
     * @param checksum          校验和
     * @param checksumAlgorithm 校验和算法
     * @param file              文件
     */
    @Override
    @Transactional
    public void saveOrdersFromFile(TenantId tenantId, String checksum, ChecksumAlgorithm checksumAlgorithm, MultipartFile file) throws IOException, ThingsboardException {
    }

    /**
     * 订单-模板
     */
    @Override
    public XSSFWorkbook createTemplate() throws IOException {
        // todo delete
        var xssfWorkbook = new XSSFWorkbook();
        xssfWorkbook.createSheet("xxx");
        return xssfWorkbook;
    }

    /**
     * 订单列表- 分页
     *
     * @param tenantId   租户id
     * @param userId     用户Id
     * @param orderQuery 查询参数
     * @param pageLink   分页参数
     * @return 订单列表
     */
    @Override
    public PageData<OrderListResult> listPageOrdersByQuery(TenantId tenantId, UserId userId, OrderListQuery orderQuery, PageLink pageLink) {
        List<Factory> factories;
        if (orderQuery.getFactoryId() != null)
            factories = List.of(new Factory(orderQuery.getFactoryId()));
        else
            factories = Optional.ofNullable(orderQuery.getFactoryName()).filter(StringUtils::isNotBlank)
                    .map(v -> this.clientService.listFactoriesByUserIdAndFactoryName(tenantId, userId, orderQuery.getFactoryName()))
                    .orElseGet(() -> this.clientService.listFactoriesByUserId(tenantId, userId));

        if (factories.isEmpty())
            return new PageData<>(Lists.newArrayList(), 0, 0L, false);

        List<SortOrder> sortOrders = Lists.newArrayList(pageLink.getSortOrder());
        if (!pageLink.getSortOrder().getProperty().equals("orderNo"))
            sortOrders.add(new SortOrder("orderNo", SortOrder.Direction.DESC));
        var temp = DaoUtil.toPageData(this.orderRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.<UUID>get("tenantId"), tenantId.getId()));
            if (StringUtils.isNotBlank(orderQuery.getOrderNo()))
                predicates.add(cb.like(root.get("orderNo"), "%" + orderQuery.getOrderNo().trim() + "%"));
            if (StringUtils.isNotBlank(orderQuery.getType()))
                predicates.add(cb.like(root.get("type"), "%" + orderQuery.getType().trim() + "%"));

            var in = cb.in(root.<UUID>get("factoryId"));
            factories.forEach(v -> in.value(v.getId()));
            predicates.add(in);
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, DaoUtil.toPageable(pageLink, sortOrders)));

        return CompletableFuture.supplyAsync(() -> this.clientService.mapIdToFactory(temp.getData().stream().map(Order::getFactoryId)
                .filter(Objects::nonNull).map(this::toUUID).collect(Collectors.toList())))
                .thenCombine(CompletableFuture.supplyAsync(() -> this.clientService.mapIdToUser(temp.getData().stream().map(Order::getCreatedUser)
                        .filter(Objects::nonNull).map(this::toUUID).collect(Collectors.toList()))), (factoryMap, userMap) -> new PageData<>(temp.getData().stream().map(e -> OrderListResult.builder()
                        .orderNo(e.getOrderNo())
                        .id(e.getId())
                        .createdTime(e.getCreatedTime())
                        .creator(
                                Optional.ofNullable(e.getCreatedUser()).map(this::toUUID).map(userMap::get).map(User::getUserName).orElse(null))
                        .emergencyDegree(e.getEmergencyDegree())
                        .factoryName(
                                Optional.ofNullable(e.getFactoryId()).map(this::toUUID).map(factoryMap::get).map(Factory::getName).orElse(null))
                        .intendedTime(e.getIntendedTime())
                        .merchandiser(e.getMerchandiser())
                        .salesman(e.getSalesman())
                        .totalAmount(e.getTotalAmount())
                        .total(e.getTotal())
                        .build()).collect(Collectors.toList()),
                        temp.getTotalPages(), temp.getTotalElements(), temp.hasNext())).join();
    }

    /**
     * 订单-详情
     *
     * @param tenantId 租户id
     * @param orderId  订单id
     * @return 订单
     */
    @Override
    public OrderVO getOrderDetail(TenantId tenantId, UUID orderId) throws ThingsboardException {
        OrderVO orderVO = new OrderVO();
        var order = this.orderRepository.findByTenantIdAndId(tenantId.getId(), orderId).map(OrderEntity::toData).orElseThrow(() -> new ThingsboardException("订单不存在！", ThingsboardErrorCode.GENERAL));
        BeanUtils.copyProperties(order, orderVO);

        CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
                    if (StringUtils.isNotBlank(order.getFactoryId()))
                        Optional.ofNullable(this.clientService.mapIdToFactory(List.of(toUUID(order.getFactoryId()))).get(toUUID(order.getFactoryId()))).ifPresent(v -> orderVO.setFactoryName(v.getName()));
                }),
                CompletableFuture.runAsync(() -> {
                    if (StringUtils.isNotBlank(order.getProductionLineId()))
                        Optional.ofNullable(this.clientService.mapIdToProductionLine(List.of(toUUID(order.getProductionLineId()))).get(toUUID(order.getProductionLineId()))).ifPresent(v -> orderVO.setProductionLineName(v.getName()));
                }), CompletableFuture.runAsync(() -> {
                    if (StringUtils.isNotBlank(order.getWorkshopId()))
                        Optional.ofNullable(this.clientService.mapIdToWorkshop(List.of(toUUID(order.getWorkshopId()))).get(toUUID(order.getWorkshopId()))).ifPresent(v -> orderVO.setWorkshopName(v.getName()));
                }),
                this.orderPlanRepository.findAllByTenantIdAndOrderIdOrderBySortAsc(tenantId.getId(), orderId).thenAcceptAsync(v -> {
                    var map = this.clientService.mapIdToDevice(v.stream().map(OrderPlanEntity::getDeviceId).collect(Collectors.toList()));
                    orderVO.setPlanDevices(v.stream().map(OrderPlanEntity::toData).map(e -> {
                        OrderPlanDeviceVO orderPlanDeviceVO = new OrderPlanDeviceVO();
                        BeanUtils.copyProperties(e, orderPlanDeviceVO);
                        Optional.ofNullable(map.get(toUUID(e.getDeviceId()))).ifPresent(device -> orderPlanDeviceVO.setDeviceName(device.getName()));
                        return orderPlanDeviceVO;
                    }).collect(Collectors.toList()));
                })
        ).join();

        return orderVO;
    }

    /**
     * 订单-更新或创建
     *
     * @param tenantId 租户id
     * @param orderVO  订单
     */
    @Override
    @Transactional
    public OrderVO updateOrSaveOrder(TenantId tenantId, OrderVO orderVO) throws ThingsboardException {
        Order order = new Order();
        if (StringUtils.isNotBlank(orderVO.getId())) {
            order = this.orderRepository.findByTenantIdAndId(tenantId.getId(), toUUID(orderVO.getId())).map(OrderEntity::toData)
                    .orElseThrow(() -> new ThingsboardException("订单不存在", ThingsboardErrorCode.GENERAL));
            BeanUtils.copyProperties(orderVO, order, "id", "code");
            deleteOrderPlan(toUUID(orderVO.getId()));
        } else {
            BeanUtils.copyProperties(orderVO, order);
            order.setTenantId(tenantId.toString());
        }

        OrderEntity orderEntity = new OrderEntity(order);
        this.orderRepository.save(orderEntity);

        AtomicInteger sort = new AtomicInteger(1);
        this.orderPlanRepository.saveAll(orderVO.getPlanDevices().stream().map(v -> {
            OrderPlanEntity orderPlanEntity = new OrderPlanEntity();
            BeanUtils.copyProperties(v, orderPlanEntity);
            orderPlanEntity.setDeviceId(toUUID(v.getDeviceId()));
            orderPlanEntity.setTenantId(tenantId.getId());
            orderPlanEntity.setOrderId(orderEntity.getId());
            orderPlanEntity.setSort(sort.get());
            sort.addAndGet(1);
            return orderPlanEntity;
        }).collect(Collectors.toList()));

        orderVO.setId(orderEntity.getId().toString());
        return orderVO;
    }

    /**
     * 订单-删除
     *
     * @param tenantId 租户id
     * @param orderId  订单id
     */
    @Override
    @Transactional
    public void deleteOrder(TenantId tenantId, UUID orderId) throws ThingsboardException {
        this.orderRepository.findByTenantIdAndId(tenantId.getId(), orderId).orElseThrow(() -> new ThingsboardException("订单不存在", ThingsboardErrorCode.GENERAL));
        this.orderRepository.deleteById(orderId);
        this.deleteOrderPlan(orderId);
    }

    /**
     * 获得当前可用订单编码
     *
     * @param tenantId 租户id
     * @return 编码
     */
    @Override
    public String getAvailableCode(TenantId tenantId) {
        var prefix = HSConstants.CODE_PREFIX_ORDER + CommonUtil.getTodayDate();
        return this.orderRepository.findAllOrderNoByTenantIdAndOrderNoLike(tenantId.getId(), prefix)
                .thenApplyAsync(r -> r.stream().map(OrderEntity::getOrderNo)
                        .filter(v -> {
                            try {
                                var numStr = v.split(prefix)[1];
                                if (numStr.length() != 5)
                                    return false;
                                Integer.valueOf(numStr);
                                return true;
                            } catch (Exception ignore) {
                                return false;
                            }
                        }).map(v -> Integer.valueOf(v.split(prefix)[1])).collect(Collectors.toSet()))
                .thenApplyAsync(r -> IntStream.iterate(1, k -> k + 1).boxed().filter(e -> !r.contains(e)).findFirst()
                        .map(e -> prefix + String.format("%05d", e)).orElse(null))
                .join();
    }

    /**
     * 订单产能监控-列表查询
     *
     * @param tenantId 租户id
     * @param userId   用户Id
     * @param query    查询参数
     * @param pageLink 分页参数
     */
    @Override
    public PageData<OrderListResult> listPageOrderCapacityMonitorByQuery(TenantId tenantId, UserId userId, OrderListQuery query, PageLink pageLink) {
        var pageData = this.listPageOrdersByQuery(tenantId, userId, query, pageLink);
        var dataMap = this.mapOrderIdToCapacity(pageData.getData().stream().map(OrderListResult::getId).map(this::toUUID).collect(Collectors.toList()));
        pageData.getData().forEach(v -> {
            var r = Optional.ofNullable(dataMap.get(toUUID(v.getId()))).map(OrderCapacityBO::getCapacities).orElse(BigDecimal.ZERO);
            v.setCapacities(this.formatCapacity(r));
            v.setCompleteness(this.calculateCompleteness(r, v.getTotal()));
        });
        return pageData;
    }

    /**
     * 订单产能监控-详情
     *
     * @param tenantId 租户id
     * @param orderId  订单Id
     */
    @Override
    public OrderVO getOrderCapacityMonitorDetail(TenantId tenantId, UUID orderId) throws ThingsboardException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.getOrderDetail(tenantId, orderId);
            } catch (ThingsboardException e) {
                throw new CompletionException(e);
            }
        }).thenApplyAsync(orderVO -> CompletableFuture.supplyAsync(() -> this.clientService.getOrderCapacities(orderVO.getPlanDevices().stream().map(OrderPlanDeviceVO::toOrderPlan).collect(Collectors.toList()), orderId))
                .thenApplyAsync(orderCapacityBO -> {
                    orderVO.setCapacities(this.formatCapacity(orderCapacityBO.getCapacities()));
                    orderVO.setCompleteness(this.calculateCompleteness(orderCapacityBO.getCapacities(), orderVO.getTotal()));
                    var deviceCapacitiesMap = orderCapacityBO.getDeviceCapacities().stream().collect(Collectors.toMap(OrderDeviceCapacityBO::getPlanId, OrderDeviceCapacityBO::getCapacities));
                    orderVO.getPlanDevices().forEach(v -> v.setCapacities(this.formatCapacity(deviceCapacitiesMap.getOrDefault(toUUID(v.getId()), BigDecimal.ZERO))));
                    return orderVO;
                }).join()).join();
    }

    /**
     * 订单产能监控-看板
     *
     * @param tenantId   租户id
     * @param factoryIds 工厂Id
     * @param timeQuery  时间请求参数
     */
    @Override
    public List<OrderBoardCapacityResult> listBoardCapacityMonitorOrders(TenantId tenantId, List<UUID> factoryIds, TimeQuery timeQuery) {
        return CompletableFuture.supplyAsync(() -> DaoUtil.convertDataList(this.orderRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.<UUID>get("tenantId"), tenantId.getId()));
            predicates.add(cb.between(root.get("createdTime"), timeQuery.getStartTime(), timeQuery.getEndTime()));
            if (!factoryIds.isEmpty()) {
                var in = cb.in(root.<UUID>get("factoryId"));
                factoryIds.forEach(in::value);
                predicates.add(in);
            }
            query.orderBy(cb.desc(root.get("createdTime"))).orderBy(cb.desc(root.get("orderNo")));
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }))).thenApplyAsync(orders -> CompletableFuture.supplyAsync(() -> this.clientService.mapIdToFactory(orders.stream().map(Order::getFactoryId)
                .filter(Objects::nonNull).map(this::toUUID).collect(Collectors.toList())))
                .thenCombineAsync(CompletableFuture.supplyAsync(() -> this.mapOrderIdToCapacity(orders.stream().map(Order::getId).map(this::toUUID).collect(Collectors.toList()))), (factoryMap, dataMap) -> orders.stream().map(v ->
                        OrderBoardCapacityResult.builder()
                                .factoryId(v.getFactoryId())
                                .factoryName(Optional.ofNullable(v.getFactoryId()).map(this::toUUID).map(factoryMap::get).map(Factory::getName).orElse(null))
                                .orderNo(v.getOrderNo())
                                .total(v.getTotal())
                                .completeness(this.calculateCompleteness(Optional.ofNullable(dataMap.get(toUUID(v.getId()))).map(OrderCapacityBO::getCapacities).orElse(BigDecimal.ZERO), v.getTotal()))
                                .build()
                ).collect(Collectors.toList())).join()).join();
    }

    /**
     * 订单产能监控-App-首页
     *
     * @param tenantId   租户id
     * @param factoryIds 工厂Id
     * @param timeQuery  时间请求参数
     */
    @Override
    public OrderAppIndexCapacityResult getAppIndexOrderCapacityResult(TenantId tenantId, List<UUID> factoryIds, TimeQuery timeQuery) {
        var result = this.listBoardCapacityMonitorOrders(tenantId, factoryIds, timeQuery);
        return OrderAppIndexCapacityResult.builder()
                .num(result.size())
                .completeness(result.isEmpty() ? BigDecimal.ZERO : this.calculatePercentage(BigDecimal.valueOf(result.stream()
                        .filter(v -> v.getCompleteness().compareTo(BigDecimal.valueOf(100L)) >= 0)
                        .count()), BigDecimal.valueOf(result.size())))
                .build();
    }

    /**
     * App-订单-订单监控
     *
     * @param tenantId       租户id
     * @param orderListQuery 请求参数
     * @param userId         用户Id
     * @param pageLink       分页参数
     */
    @Override
    public PageData<OrderListResult> listPageAppOrderCapacityMonitorByQuery(TenantId tenantId, UserId userId, OrderListQuery orderListQuery, PageLink pageLink) {
        return this.listPageOrderCapacityMonitorByQuery(tenantId, userId, orderListQuery, pageLink);
    }

    /**
     * App-订单-订单监控-生产计划-更新实际时间
     *
     * @param tenantId 租户id
     * @param planId   设备计划Id
     * @param timeVO   时间参数
     */
    @Override
    @Transactional
    public void updateOrderPlanDeviceActualTime(TenantId tenantId, UUID planId, OrderPlanDeviceActualTimeVO timeVO) throws ThingsboardException {
        var plan = this.orderPlanRepository.findByTenantIdAndId(tenantId.getId(), planId).map(OrderPlanEntity::toData).
                orElseThrow(() -> new ThingsboardException("设备计划不存在！", ThingsboardErrorCode.GENERAL));
        plan.setActualStartTime(timeVO.getActualStartTime());
        plan.setActualEndTime(timeVO.getActualEndTime());
        this.orderPlanRepository.save(new OrderPlanEntity(plan));
    }

    /**
     * 订单-生产计划删除
     */
    @Transactional
    public void deleteOrderPlan(UUID orderId) {
        this.orderPlanRepository.deleteAllByOrderId(orderId);
    }

    /**
     * 订单-产能计算
     */
    public Map<UUID, OrderCapacityBO> mapOrderIdToCapacity(List<UUID> orderIds) {
        return orderIds.stream()
                .map(v -> this.orderPlanRepository.findAllByOrderId(v).thenApplyAsync(e -> ImmutablePair.of(v, DaoUtil.convertDataList(e))))
                .map(v -> v.thenApplyAsync(e -> this.clientService.getOrderCapacities(e.getRight(), e.getLeft())))
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(OrderCapacityBO::getOrderId, Function.identity()));
    }
}
