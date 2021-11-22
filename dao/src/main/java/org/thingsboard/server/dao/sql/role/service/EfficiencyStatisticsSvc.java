package org.thingsboard.server.dao.sql.role.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.QueryTsKvHisttoryVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.devicerun.ResultRunStatusByDeviceVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.PcDeviceEnergyVo;
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

    Object  queryEnergyHistory(QueryTsKvHisttoryVo queryTsKvVo,TenantId tenantId, PageLink pageLink);


    PageDataAndTotalValue<AppDeviceCapVo> queryPCCapApp(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink);


    PageDataAndTotalValue<PcDeviceEnergyVo> queryEntityByKeys(QueryTsKvVo queryTsKvVo, TenantId tenantId, PageLink pageLink) throws JsonProcessingException;

    /**
     * 产能接口
     */
     ResultCapAppVo  queryCapApp(QueryTsKvVo queryTsKvVo, TenantId tenantId);


    /**
     * 能耗的查询
     */
    ResultEnergyAppVo queryEntityByKeys(QueryTsKvVo queryTsKvVo, TenantId tenantId);



    Map<String, List<ResultRunStatusByDeviceVo>> queryTheRunningStatusByDevice(QueryRunningStatusVo vo, TenantId  tenantId);


    //查询当前的分组-分组属性
    Object   queryGroupDict(UUID deviceId,TenantId tenantId);

    List<String>   queryDictDevice(UUID deviceId,TenantId tenantId);

}
