package org.thingsboard.server.dao.sql.role.service;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;
import org.thingsboard.server.common.data.vo.tskv.ConsumptionTodayVo;
import org.thingsboard.server.common.data.vo.tskv.TrendVo;
import org.thingsboard.server.common.data.vo.tskv.consumption.ConsumptionVo;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 看板相关接口
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:36
 **/
public interface BulletinBoardSvc {
    /**
     * 看板的能耗趋势图（实线 和虚线）
     * @param vo
     * @return
     */
    TrendVo energyConsumptionTrend( TrendParameterVo vo) throws ThingsboardException;


//    @Deprecated
    List<ConsumptionVo> totalEnergyConsumption(QueryTsKvVo queryTsKvVo, TenantId tenantId);

    /**
     * 今日能耗量列表  待优化的部分
     *
     */
    ConsumptionTodayVo energyConsumptionToday(QueryTsKvVo vo, UUID tenantId );


    /**
     *
     * @param deviceCapacityVoList  设备的入参
     * @return  设备id 对应的 产能值
     */
    Map<UUID,String> queryCapacityValueByDeviceIdAndTime(List<DeviceCapacityVo> deviceCapacityVoList);






}
