package org.thingsboard.server.dao.sql.role.service;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.tskv.ConsumptionTodayVo;
import org.thingsboard.server.common.data.vo.tskv.MaxTsVo;
import org.thingsboard.server.common.data.vo.tskv.consumption.ConsumptionVo;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 看板相关接口
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:36
 **/
public interface BulletinBoardSvc {


    List<ConsumptionVo> totalEnergyConsumption(QueryTsKvVo queryTsKvVo, TenantId tenantId);

    /**
     * 今日能耗量列表
     *
     */
    ConsumptionTodayVo energyConsumptionToday(QueryTsKvVo vo, UUID tenantId );



    /**
     * 历史产能的接口
     * @param factoryId
     * @param tenantId
     * @return
     */
   String getHistoryCapValue(String factoryId, UUID tenantId);



    /**
     * 历史产能接口
     *   就是查询当前最大
     */
   String  historySumByKey (MaxTsVo MaxTsVo);
}
