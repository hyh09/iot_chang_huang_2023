package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.entity.po.DictData;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceComponent;
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
    DictDeviceVO updateOrSaveDictDevice(DictDeviceVO dictDeviceVO, TenantId tenantId) throws ThingsboardException;

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

    /**
     * 获得全部设备字典属性(包括部件)-描述 Map
     *
     * @param dictDeviceId 设备字典Id
     */
    Map<String, String> mapAllPropertyTitle(UUID dictDeviceId);

    /**
     * 获得全部设备字典属性(包括部件)-数据字典Id Map
     *
     * @param dictDeviceId 设备字典Id
     */
    Map<String, String> mapAllPropertyDictDataId(UUID dictDeviceId);

    /**
     * 获得全部设备字典属性(包括部件)-数据字典 Map
     *
     * @param tenantId     租户Id
     * @param dictDeviceId 设备字典Id
     */
    Map<String, DictData> mapAllPropertyToDictData(TenantId tenantId, UUID dictDeviceId);

    /**
     * 【不分页】获得设备字典绑定的部件
     *
     * @param tenantId     租户Id
     * @param dictDeviceId 设备字典Id
     * @return 部件列表
     */
    List<DictDeviceComponent> listDictDeviceComponents(TenantId tenantId, UUID dictDeviceId);

    /**
     * 获得默认的设备字典Id
     *
     * @param tenantId 租户Id
     */
    UUID getDefaultDictDeviceId(TenantId tenantId);

    /**
     * 设置默认设备字典
     *
     * @param tenantId     租户Id
     * @param dictDeviceId 设备字典Id
     */
    void updateDictDeviceDefault(TenantId tenantId, UUID dictDeviceId) throws ThingsboardException;

    /**
     * 获得截止时间之后新增的设备字典
     *
     * @param tenantId     租户Id
     * @param startTime 设备字典Id
     */
    List<DictDevice> listDictDevicesByStartTime(TenantId tenantId, long startTime) throws ThingsboardException;

}
