package org.thingsboard.server.dao.hs.service;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.dao.OrderPlanEntity;
import org.thingsboard.server.dao.hs.entity.po.OrderPlan;
import org.thingsboard.server.dao.hs.entity.vo.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 订单接口
 *
 * @author wwj
 * @since 2021.10.18
 */
public interface OrderService {
    /**
     * 保存订单-从文件导入
     *
     * @param tenantId          租户Id
     * @param checksum          校验和
     * @param checksumAlgorithm 校验和算法
     * @param file              文件
     * @param userId            用户Id
     */
    void saveOrdersFromFile(TenantId tenantId, UserId userId, String checksum, ChecksumAlgorithm checksumAlgorithm, MultipartFile file) throws IOException, ThingsboardException;

    /**
     * 订单-模板
     */
    XSSFWorkbook createTemplate() throws IOException;

    /**
     * 订单列表- 分页
     *
     * @param tenantId 租户id
     * @param userId   用户Id
     * @param query    查询参数
     * @param pageLink 分页参数
     * @return 订单列表
     */
    PageData<OrderListResult> listPageOrdersByQuery(TenantId tenantId, UserId userId, OrderListQuery query, PageLink pageLink);

    /**
     * 订单-详情
     *
     * @param tenantId 租户id
     * @param orderId  订单id
     * @return 订单
     */
    OrderVO getOrderDetail(TenantId tenantId, UUID orderId) throws ThingsboardException;

    /**
     * 订单-更新或创建
     *
     * @param tenantId 租户id
     * @param orderVO  订单
     */
    OrderVO updateOrSaveOrder(TenantId tenantId, OrderVO orderVO) throws ThingsboardException;

    /**
     * 订单-删除
     *
     * @param tenantId 租户id
     * @param orderId  订单id
     */
    void deleteOrder(TenantId tenantId, UUID orderId) throws ThingsboardException;

    /**
     * 获得当前可用订单编码
     *
     * @param tenantId 租户id
     * @return 编码
     */
    String getAvailableCode(TenantId tenantId);

    /**
     * 订单产能监控-列表查询
     *
     * @param tenantId 租户id
     * @param userId   用户Id
     * @param query    查询参数
     * @param pageLink 分页参数
     */
    PageData<OrderListResult> listPageOrderCapacityMonitorByQuery(TenantId tenantId, UserId userId, OrderListQuery query, PageLink pageLink);

    /**
     * 订单产能监控-详情
     *
     * @param tenantId 租户id
     * @param orderId  订单Id
     */
    OrderVO getOrderCapacityMonitorDetail(TenantId tenantId, UUID orderId) throws ThingsboardException;

    /**
     * 订单产能监控
     *
     * @param tenantId   租户id
     * @param factoryIds 工厂Id
     * @param timeQuery  时间请求参数
     */
    List<OrderCustomCapacityResult> listCustomCapacityMonitorOrders(TenantId tenantId, List<UUID> factoryIds, TimeQuery timeQuery);

    /**
     * 订单产能监控-App-首页
     *
     * @param tenantId   租户id
     * @param factoryIds 工厂Id
     * @param timeQuery  时间请求参数
     */
    OrderAppIndexCapacityResult getAppIndexOrderCapacityResult(TenantId tenantId, List<UUID> factoryIds, TimeQuery timeQuery);

    /**
     * App-订单-订单监控
     *
     * @param tenantId       租户id
     * @param orderListQuery 请求参数
     * @param userId         用户Id
     * @param pageLink       分页参数
     */
    PageData<OrderListResult> listPageAppOrderCapacityMonitorByQuery(TenantId tenantId, UserId userId, OrderListQuery orderListQuery, PageLink pageLink);

    /**
     * App-订单-订单监控-生产计划-更新实际时间
     *
     * @param tenantId 租户id
     * @param planId   设备计划Id
     * @param timeVO   时间参数
     */
    void updateOrderPlanDeviceActualTime(TenantId tenantId, UUID planId, OrderPlanDeviceActualTimeVO timeVO) throws ThingsboardException;

    /**
     * 根据设备id以及实际时间查询订单信息
     * @param deviceIds
     * @param startTime
     * @param endTime
     * @return
     */
    List<OrderPlanEntity> findDeviceAchieveOrPlanList(List<UUID> deviceIds, Long startTime, Long endTime);


    /**
     * 看板-订单监控
     *
     * @param tenantId 租户Id
     * @param factoryId 工厂Id
     * @param workshopId 车间Id
     * @param timeQuery 时间参数
     * @return 订单
     */
    List<OrderCustomCapacityResult> listBoardCapacityMonitorOrders(TenantId tenantId, UUID factoryId, UUID workshopId, TimeQuery timeQuery);

    /**
     * 查询时间范围内的实际产量
     * @param factoryIds
     * @param startTime
     * @param endTime
     * @return
     */
    String findActualByFactoryIds(UUID factoryIds,Long startTime, Long endTime);

    /**
     * 查询时间范围内的计划产量
     * @param factoryIds
     * @param startTime
     * @param endTime
     * @return
     */
    String findIntendedByFactoryIds(UUID factoryIds,Long startTime, Long endTime);

    /**
     * 查询订单-生产计划-单个设备在一个时间段内的维护时间列表
     *
     * @param tenantId  租户Id
     * @param deviceId  设备Id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 维护时间列表
     */
    List<DeviceKeyParamMaintainResult> listDeviceMaintainTimes(TenantId tenantId, UUID deviceId, Long startTime, Long endTime);

    /**
     * 查询订单-生产计划-单个设备在一个时间段内的实际时间的生产计划列表
     *
     * @param tenantId  租户Id
     * @param deviceId  设备Id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 生产计划列表
     */
    List<OrderPlan> listDeviceOrderPlansInActualTimeField(TenantId tenantId, UUID deviceId, Long startTime, Long endTime);

    /**
     * 查询时间范围内的车间计划产量
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    String findIntendedByDeviceId(UUID deviceId, Long startTime, Long endTime);

    /**
     *查询时间范围内的车间实际产量
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    String findActualByDeviceId(UUID deviceId, Long startTime, Long endTime);
}
