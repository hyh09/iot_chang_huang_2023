package org.thingsboard.server.dao.hs.service;

import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;
import org.thingsboard.server.dao.hs.entity.po.DictData;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;
import org.thingsboard.server.dao.hs.entity.po.DictDeviceComponent;
import org.thingsboard.server.dao.hs.entity.vo.*;
import org.thingsboard.server.dao.hsms.entity.vo.DeviceSwitchVO;
import org.thingsboard.server.dao.hsms.entity.vo.DictDevicePropertySwitchNewVO;
import org.thingsboard.server.dao.hsms.entity.vo.DictDevicePropertySwitchVO;
import org.thingsboard.server.dao.hsms.entity.vo.DictDeviceSwitchDeviceVO;

import java.io.IOException;
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
    DictDeviceVO saveOrUpdateDictDevice(DictDeviceVO dictDeviceVO, TenantId tenantId) throws ThingsboardException;

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
    PageData<DictDevice> listPageDictDevicesByQuery(DictDeviceListQuery dictDeviceListQuery, TenantId tenantId, PageLink pageLink);

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
    void deleteDictDeviceById(String id, TenantId tenantId) throws ThingsboardException;

    /**
     * 获得设备字典分组及分组属性
     *
     * @param dictDeviceId 设备字典Id
     */
    List<DictDeviceGroupVO> listDictDeviceGroups(UUID dictDeviceId);

    /**
     * 获得设备字典部件
     *
     * @param dictDeviceId 设备字典Id
     */
    List<DictDeviceComponentVO> listDictDeviceComponents(UUID dictDeviceId);

    /**
     * 获得设备字典分组属性，不包含分组
     *
     * @param dictDeviceId 设备字典Id
     */
    List<DictDeviceGroupPropertyVO> listDictDeviceGroupProperties(UUID dictDeviceId);

    /**
     * 获得设备字典部件属性
     *
     * @param dictDeviceId 设备字典Id
     */
    List<DictDeviceComponentPropertyVO> listDictDeviceComponentProperties(UUID dictDeviceId);

    /**
     * 获得当前默认初始化的分组及分组属性
     */
    List<DictDeviceGroupVO> getDictDeviceGroupInitData();

    /**
     * 【不分页】获得设备字典列表
     *
     * @param tenantId 租户Id
     * @return 设备字典列表
     */
    List<DictDevice> listDictDevices(TenantId tenantId);

    /**
     * 获得全部设备字典属性(包括部件)-描述 Map
     *
     * @param dictDeviceId 设备字典Id
     */
    Map<String, String> getDictDeviceNameToTitleMap(UUID dictDeviceId);

    /**
     * 获得全部设备字典属性(包括部件)-数据字典Id Map
     *
     * @param dictDeviceId 设备字典Id
     */
    Map<String, String> getNameToDictDataIdMap(UUID dictDeviceId);

    /**
     * 获得全部设备字典属性(包括部件)-数据字典 Map
     *
     * @param tenantId     租户Id
     * @param dictDeviceId 设备字典Id
     */
    Map<String, DictData> getNameToDictDataMap(TenantId tenantId, UUID dictDeviceId);

    /**
     * 【不分页】获得设备字典绑定的部件，平铺
     *
     * @param tenantId     租户Id
     * @param dictDeviceId 设备字典Id
     * @return 部件列表
     */
    List<DictDeviceComponent> listDictDeviceTileComponents(TenantId tenantId, UUID dictDeviceId);

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
     * @param tenantId  租户Id
     * @param startTime 设备字典Id
     */
    List<DictDevice> listDictDevicesByStartTime(TenantId tenantId, long startTime);

    /**
     * 【不分页】获得设备字典全部遥测属性
     *
     * @param tenantId     租户Id
     * @param dictDeviceId 设备字典Id
     * @return 遥测属性列表
     */
    List<DictDeviceTsPropertyResult> listDictDeviceIssueProperties(TenantId tenantId, UUID dictDeviceId);

    /**
     * 设备字典-图表-列表
     *
     * @param tenantId     租户Id
     * @param dictDeviceId 设备字典Id
     * @return 设备字典-图表-列表
     */
    List<DictDeviceGraphVO> listDictDeviceGraphs(TenantId tenantId, UUID dictDeviceId);

    /**
     * 设备字典-图表-详情
     *
     * @param tenantId 租户Id
     * @param graphId  设备字典图表Id
     * @return 设备字典-图表-详情
     */
    DictDeviceGraphVO getDictDeviceGraphDetail(TenantId tenantId, UUID graphId) throws ThingsboardException;

    /**
     * 设备字典-图表-新增或修改
     *
     * @param tenantId          租户Id
     * @param dictDeviceId      设备字典Id
     * @param dictDeviceGraphVO 图表
     * @return 图表
     */
    UUID updateOrSaveDictDeviceGraph(TenantId tenantId, UUID dictDeviceId, DictDeviceGraphVO dictDeviceGraphVO) throws ThingsboardException;

    /**
     * 设备字典-图表-删除
     *
     * @param tenantId 租户Id
     * @param graphId  设备字典图表Id
     */
    void deleteDictDeviceGraph(TenantId tenantId, UUID graphId);

    /**
     * 设备字典-遥测属性查询
     *
     * @param propertyId   属性Id
     * @param propertyType 属性类型
     * @return 遥测属性
     */
    DictDeviceTsPropertyVO getTsPropertyByIdAndType(UUID propertyId, DictDevicePropertyTypeEnum propertyType);

    /**
     * 设备字典-遥测属性查询
     *
     * @param dictDeviceId   设备字典Id
     * @param tsPropertyName 遥测属性名
     * @return 遥测属性
     */
    DictDeviceTsPropertyVO getTsPropertyByPropertyName(UUID dictDeviceId, String tsPropertyName);

    /**
     * 【不分页】获得设备字典全部遥测属性
     *
     * @param tenantId     租户Id
     * @param dictDeviceId 设备字典Id
     * @return 全部遥测属性
     */
    List<DictDeviceTsPropertyVO> listDictDeviceProperties(TenantId tenantId, UUID dictDeviceId);

    /**
     * 根据设备字典id查询设备字典信息
     *
     * @param dictDeviceId
     * @return
     */
    DictDevice findById(UUID dictDeviceId);

    /**
     * 设备字典-导入
     *
     * @param tenantId          租户Id
     * @param userId            用户Id
     * @param checksum          校验和
     * @param checksumAlgorithm 检验和算法
     * @param file              文件
     */
    void saveDictDevicesFromFile(TenantId tenantId, UserId userId, String checksum, ChecksumAlgorithm checksumAlgorithm, MultipartFile file) throws IOException, ThingsboardException;

    /**
     * 数据过滤-设备列表
     *
     * @param query    设备查询参数
     * @param tenantId 租户Id
     * @param pageLink 分页参数
     * @return 数据过滤-设备列表
     */
    PageData<DictDeviceSwitchDeviceVO> listDictDeviceSwitchDevicesByQuery(FactoryDeviceQuery query, TenantId tenantId, PageLink pageLink);

    /**
     * 数据过滤-参数管理列表
     *
     * @param tenantId 租户Id
     * @param deviceId 设备Id
     * @param q        查询参数
     * @param pageLink 分页参数
     * @return 数据过滤-参数管理列表
     */
    PageData<DictDevicePropertySwitchNewVO> listDictDeviceSwitches(TenantId tenantId, String deviceId, String q, PageLink pageLink) throws ThingsboardException;

    /**
     * 数据过滤-参数管理列表
     *
     * @param tenantId 租户Id
     * @param deviceId 设备Id
     * @return 数据过滤-参数管理列表
     */
    List<DictDevicePropertySwitchVO> listDictDeviceSwitches(TenantId tenantId, String deviceId) throws ThingsboardException;

    /**
     * 数据过滤-属性开关更新或新增
     *
     * @param tenantId       租户Id
     * @param propertySwitches 设备开关信息
     */
    void updateOrSaveDiceDeviceSwitches(TenantId tenantId, List<DictDevicePropertySwitchNewVO> propertySwitches);
}
