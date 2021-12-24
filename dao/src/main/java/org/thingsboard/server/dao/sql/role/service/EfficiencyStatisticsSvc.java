package org.thingsboard.server.dao.sql.role.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.AppQueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.QueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.QueryTsKvHisttoryVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.device.DeviceDictionaryPropertiesVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 效能统计接口
 * @author: HU.YUNHUI
 * @create: 2021-11-09 11:13
 **/
public interface EfficiencyStatisticsSvc {

    /**
     * 效能分析 pc端的表头接口  返回  title (单位)
     * @return
     */
    List<String> queryEntityByKeysHeader();

    //能耗历史的表头
    List<String> queryEnergyHistoryHeader();

    Object  queryEnergyHistory(QueryTsKvHisttoryVo queryTsKvVo,TenantId tenantId, PageLink pageLink);

    @Deprecated
    PageDataAndTotalValue<AppDeviceCapVo> queryPCCapApp(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink);

    /**
     * 产能的新方法保证出入参
     * @param queryTsKvVo
     * @param tenantId
     * @param pageLink
     * @return
     */
    PageDataAndTotalValue<AppDeviceCapVo> queryPCCapAppNewMethod(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink);


    @Deprecated
    PageDataAndTotalValue<Map> queryEntityByKeys(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink) throws JsonProcessingException;


    /**
     * 能耗的查询接口  【pc端】
     * @param queryTsKvVo
     * @param tenantId
     * @param pageLink
     * @return
     * @throws JsonProcessingException
     */
    PageDataAndTotalValue<Map> queryEntityByKeysNewMethod(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink) throws JsonProcessingException;


    /**
     * 产能接口
     */
     @Deprecated
     ResultCapAppVo  queryCapApp(QueryTsKvVo queryTsKvVo, TenantId tenantId);


    /**
     * APP产能接口
     * @param queryTsKvVo
     * @param tenantId
     * @param pageLink
     * @return
     */
     ResultCapAppVo queryCapAppNewMethod(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink);


    /**
     * app端的能耗返回  注意返回和Pc不一样的
     *  如果工厂id为空查询该租户下第一个工厂数据
     * @param queryTsKvVo
     * @param tenantId
     * @param pageLink
     * @return
     */
    ResultEnergyAppVo queryAppEntityByKeysNewMethod(QueryTsKvVo queryTsKvVo, TenantId tenantId,PageLink pageLink);


    /**
     * 能耗的查询
     */
    @Deprecated
    ResultEnergyAppVo queryEntityByKeys(QueryTsKvVo queryTsKvVo, TenantId tenantId,Boolean flg);



    /**
     * PC端的运行状态
     * @param vo
     * @param tenantId
     * @return 返回的是 key:name ,
     */
    Map<String, List<ResultRunStatusByDeviceVo>> queryPcTheRunningStatusByDevice(QueryRunningStatusVo vo, TenantId  tenantId) throws ThingsboardException;

    /**
     * app端的返回的是描述字段
     * @param vo
     * @param tenantId
     * @return
     */
    Map<String, List<ResultRunStatusByDeviceVo>> queryTheRunningStatusByDevice(AppQueryRunningStatusVo vo, TenantId  tenantId,PageLink pageLink) throws ThingsboardException;


    //查询当前的分组-分组属性
    Object   queryGroupDict(UUID deviceId,TenantId tenantId);

    /**
     * 用于pc端的下拉选的 name 和 title
     * @param deviceId  设备id
     * @param tenantId
     * @return
     */
    List<DeviceDictionaryPropertiesVo> queryDictDevice(UUID deviceId, TenantId tenantId) throws ThingsboardException;




}
