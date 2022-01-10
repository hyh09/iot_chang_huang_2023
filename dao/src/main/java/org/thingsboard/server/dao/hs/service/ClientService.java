package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.hs.entity.bo.OrderCapacityBO;
import org.thingsboard.server.dao.hs.entity.dto.DeviceBaseDTO;
import org.thingsboard.server.dao.hs.entity.dto.DeviceListAffiliationDTO;
import org.thingsboard.server.dao.hs.entity.po.OrderPlan;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupVO;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.entity.vo.OrderVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * 二方库接口
 *
 * @author wwj
 * @since 2021.11.1
 */
public interface ClientService {
    /**
     * 查询用户
     *
     * @param userId 用户Id
     */
    User getUserByUserId(UserId userId);

    /**
     * 查询设备基本信息、工厂、车间、产线、设备等
     *
     * @param tenantId 租户Id
     * @param t        extends FactoryDeviceQuery
     */
    <T extends FactoryDeviceQuery> DeviceBaseDTO getFactoryBaseInfoByQuery(TenantId tenantId, T t);

    /**
     * 查询设备列表
     *
     * @param tenantId 租户Id
     * @param t        extends FactoryDeviceQuery
     */
    <T extends FactoryDeviceQuery> List<Device> listDevicesByQuery(TenantId tenantId, T t);

    /**
     * 分页查询设备列表
     *
     * @param tenantId 租户Id
     * @param t        extends FactoryDeviceQuery
     * @param pageLink 分页参数
     */
    <T extends FactoryDeviceQuery> PageData<Device> listPageDevicesPageByQuery(TenantId tenantId, T t, PageLink pageLink);

    /**
     * 查询全部设备的在线情况
     *
     * @param allDeviceIdList 设备的UUID列表
     */
    Map<String, Boolean> listDevicesOnlineStatus(List<UUID> allDeviceIdList);

    /**
     * 查询全部设备的工厂、车间、产线信息
     *
     * @param deviceList 设备列表
     */
    DeviceListAffiliationDTO getDevicesAffiliationInfo(List<Device> deviceList);

    /**
     * 获得设备字典初始化数据
     */
    List<DictDeviceGroupVO> getDictDeviceInitData();

    /**
     * 列举全部工厂
     *
     * @param tenantId 租户Id
     */
    List<Factory> listFactories(TenantId tenantId);

    /**
     * 列举工厂下全部车间
     *
     * @param tenantId  租户Id
     * @param factoryId 工厂Id
     */
    List<Workshop> listWorkshopsByFactoryId(TenantId tenantId, UUID factoryId);

    /**
     * 列举车间下全部产线
     *
     * @param tenantId   租户Id
     * @param workshopId 车间Id
     */
    List<ProductionLine> listProductionLinesByWorkshopId(TenantId tenantId, UUID workshopId);

    /**
     * 根据当前登录人查询工厂列表
     *
     * @param tenantId 租户Id
     * @param userId   用户Id
     * @return 工厂列表
     */
    List<Factory> listFactoriesByUserId(TenantId tenantId, UserId userId);

    /**
     * 根据工厂名称查询工厂列表
     *
     * @param tenantId    租户Id
     * @param factoryName 工厂名称
     * @return 工厂列表
     */
    List<Factory> listFactoriesByFactoryName(TenantId tenantId, String factoryName);

    /**
     * 根据当前登录人及工厂名称查询工厂列表
     *
     * @param tenantId    租户Id
     * @param userId      用户Id
     * @param factoryName 工厂名称
     * @return 工厂列表
     */
    List<Factory> listFactoriesByUserIdAndFactoryName(TenantId tenantId, UserId userId, String factoryName);

    /**
     * 分页查询历史遥测数据
     *
     * @param tenantId     租户Id
     * @param deviceId     设备Id
     * @param timePageLink 时间分页参数
     * @return 历史遥测数据
     */
    PageData<Map<String, Object>> listPageTsHistories(TenantId tenantId, DeviceId deviceId, TimePageLink timePageLink) throws ExecutionException, InterruptedException;

    /**
     * 分页查询单个Key历史遥测数据
     *
     * @param tenantId          租户Id
     * @param deviceId          设备Id
     * @param groupPropertyName 遥测key
     * @param timePageLink      时间分页参数
     * @return 历史遥测数据
     */
    PageData<DictDeviceGroupPropertyVO> listPageTsHistories(TenantId tenantId, DeviceId deviceId, String groupPropertyName, TimePageLink timePageLink) throws ExecutionException, InterruptedException, ThingsboardException;

    /**
     * 列举全部工厂
     *
     * @param factoryIds 工厂Id列表
     */
    Map<UUID, Factory> mapIdToFactory(List<UUID> factoryIds);

    /**
     * 列举全部车间
     *
     * @param workshopIds 车间Id列表
     */
    Map<UUID, Workshop> mapIdToWorkshop(List<UUID> workshopIds);

    /**
     * 列举全部产线
     *
     * @param productionLineIds 产线Id列表
     */
    Map<UUID, ProductionLine> mapIdToProductionLine(List<UUID> productionLineIds);

    /**
     * 列举全部设备
     *
     * @param deviceIds 设备Id列表
     */
    Map<UUID, Device> mapIdToDevice(List<UUID> deviceIds);

    /**
     * 列举全部用户
     *
     * @param userIds 用户Id列表
     */
    Map<UUID, User> mapIdToUser(List<UUID> userIds);

    /**
     * 查询订单产能
     *
     * @param plans 生产计划列表
     */
    BigDecimal getOrderCapacities(List<OrderPlan> plans);

    /**
     * 查询订单产能
     *
     * @param plans 生产计划列表
     * @param orderId 订单Id
     */
    OrderCapacityBO getOrderCapacities(List<OrderPlan> plans, UUID orderId);

    /**
     * 查询订单设备产能
     *
     * @param plans 生产计划列表
     */
    Map<UUID, BigDecimal> mapPlanIdToCapacities(List<OrderPlan> plans);
}
