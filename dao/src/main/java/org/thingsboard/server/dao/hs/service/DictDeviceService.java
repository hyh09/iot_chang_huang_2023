package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceListQuery;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceVO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 设备字典接口
 *
 * @author wwj
 * @since 2021.10.21
 */
public interface DictDeviceService {

    /**
     * 新增或修改设备字典
     *
     * @param dictDeviceVO 设备字典入参
     * @param tenantId     租户Id
     */
    void updateOrSaveDictDevice(DictDeviceVO dictDeviceVO, TenantId tenantId) throws ThingsboardException;

    /**
     * 获得当前可用设备字典编码
     *
     * @param tenantId 租户Id
     * @return 可用设备字典编码
     */
    String getAvailableCode(TenantId tenantId);

    /**
     * 获得设备字典列表
     *
     * @param dictDeviceListQuery 设备字典列表请求参数
     * @param tenantId            租户Id
     * @param pageLink            分页参数
     * @return 设备字典列表
     */
    PageData<DictDevice> listDictDeviceByQuery(DictDeviceListQuery dictDeviceListQuery, TenantId tenantId, PageLink pageLink);

    /**
     * 获得设备字典详情
     *
     * @param id       设备字典id
     * @param tenantId 租户Id
     * @return 设备字典详情
     */
    DictDeviceVO getDictDeviceDetail(String id, TenantId tenantId) throws ThingsboardException;

    /**
     * 删除设备字典
     *
     * @param id       设备字典id
     * @param tenantId 租户Id
     */
    void deleteDictDevice(String id, TenantId tenantId) throws ThingsboardException;

    /**
     * 获得未配置设备配置的设备字典列表
     *
     * @param tenantId 租户Id
     */
    List<DictDevice> listDictDeviceUnused(TenantId tenantId);

    /**
     * 获得设备字典分组及分组属性
     *
     * @param dictDeviceId 设备字典Id
     */
    List<DictDeviceGroupVO> listDictDeviceGroup(UUID dictDeviceId);

    /**
     * 获得设备字典分组属性，不包含分组
     *
     * @param dictDeviceId 设备字典Id
     */
    List<DictDeviceGroupPropertyVO> listDictDeviceGroupProperty(UUID dictDeviceId);

    /**
     * 批量获得设备字典绑定的设备配置Id
     *
     * @param dictDeviceIdList 设备字典Id列表
     */
    Map<UUID, DeviceProfileId> listDeviceProfileIdsByDictDeviceIdList(List<UUID> dictDeviceIdList);

    /**
     * 获得设备字典绑定的设备配置Id
     *
     * @param dictDeviceId 设备字典Id
     */
    DeviceProfileId getDeviceProfileIdByDictDeviceId(UUID dictDeviceId);

    /**
     * 获得当前默认初始化的分组及分组属性
     */
    List<DictDeviceGroupVO> getGroupInitData();

    /**
     * 【不分页】获得设备字典列表
     *
     * @param tenantId 租户Id
     * @return 设备字典列表
     */
    List<DictDevice> listAllDictDevice(TenantId tenantId);
}
