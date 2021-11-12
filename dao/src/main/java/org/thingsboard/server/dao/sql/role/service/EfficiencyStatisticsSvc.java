package org.thingsboard.server.dao.sql.role.service;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
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



}
