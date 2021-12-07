package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.hs.entity.dto.DeviceBaseDTO;
import org.thingsboard.server.dao.hs.entity.dto.DeviceListAffiliationDTO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupVO;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 二方库接口
 *
 * @author wwj
 * @since 2021.11.1
 */
public interface ClientService {

    /**
     * 查询设备基本信息、工厂、车间、产线、设备等
     *
     * @param tenantId 租户Id
     * @param t        extends FactoryDeviceQuery
     */
    <T extends FactoryDeviceQuery> DeviceBaseDTO getDeviceBase(TenantId tenantId, T t);

    /**
     * 查询设备列表
     *
     * @param tenantId 租户Id
     * @param t        extends FactoryDeviceQuery
     */
    <T extends FactoryDeviceQuery> List<Device> listDeviceByQuery(TenantId tenantId, T t);

    /**
     * 分页查询设备列表
     *
     * @param tenantId 租户Id
     * @param t        extends FactoryDeviceQuery
     * @param pageLink 分页参数
     */
    <T extends FactoryDeviceQuery> PageData<Device> listDevicePageByQuery(TenantId tenantId, T t, PageLink pageLink);

    /**
     * 查询全部设备的在线情况
     *
     * @param allDeviceIdList 设备的UUID列表
     */
    Map<String, Boolean> listAllDeviceOnlineStatus(List<UUID> allDeviceIdList);

    /**
     * 查询全部设备的工厂、车间、产线信息
     *
     * @param deviceList 设备列表
     */
    DeviceListAffiliationDTO getDeviceListAffiliation(List<Device> deviceList);

    /**
     * 获得设备字典初始化数据
     */
    List<DictDeviceGroupVO> listDictDeviceInitData();

    /**
     * 列举全部工厂
     *
     * @param tenantId 租户Id
     */
    List<Factory> listAllFactoryByTenantId(TenantId tenantId);

    /**
     * 列举工厂下全部车间
     *
     * @param tenantId  租户Id
     * @param factoryId 工厂Id
     */
    List<Workshop> listAllWorkshopByTenantIdAndFactoryId(TenantId tenantId, UUID factoryId);

    /**
     * 列举车间下全部产线
     *
     * @param tenantId   租户Id
     * @param workshopId 车间Id
     */
    List<ProductionLine> listProductionLinesByTenantIdAndWorkshopId(TenantId tenantId, UUID workshopId);

    /**
     * 根据当前登录人查询工厂列表
     *
     * @param tenantId 租户Id
     * @param userId   用户Id
     * @return 工厂列表
     */
    List<Factory> listFactoriesByUserId(TenantId tenantId, UserId userId);
}
