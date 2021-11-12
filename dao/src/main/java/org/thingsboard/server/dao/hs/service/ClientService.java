package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
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
     * @param tenantId      租户Id
     * @param t             extends FactoryDeviceQuery
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
}
