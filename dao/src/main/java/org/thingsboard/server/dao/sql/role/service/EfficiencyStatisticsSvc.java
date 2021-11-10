package org.thingsboard.server.dao.sql.role.service;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.resultvo.cap.ResultCapAppVo;
import org.thingsboard.server.common.data.vo.resultvo.energy.ResultEnergyAppVo;

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
    public ResultCapAppVo  queryCapApp(QueryTsKvVo queryTsKvVo, TenantId tenantId);


    /**
     * 能耗的查询
     */
    ResultEnergyAppVo queryEntityByKeys(QueryTsKvVo queryTsKvVo, TenantId tenantId);
}
