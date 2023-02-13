package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hsms.entity.vo.*;

import java.util.List;
import java.util.UUID;

/**
 * mes 接口
 *
 * @author wwj
 * @since 2021.11.26
 */
public interface MesService {

//    /**
//     * 车间下全部产线
//     *
//     * @param tenantId   租户Id
//     * @param workshopId 车间Id
//     * @return 车间下全部产线
//     */
//    List<MesBoardProductionLineVO> listProductionLinesByWorkshopId(TenantId tenantId, UUID workshopId);

    /**
     * 工厂下全部车间
     *
     * @param tenantId   租户Id
     * @param factoryId 工厂Id
     * @return 工厂下全部车间
     */
    List<MesBoardWorkshopVO> listWorkshopsByFactoryId(TenantId tenantId, UUID factoryId);

    /**
     * 产线下全部设备
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 产线下全部设备
     */
    List<MesBoardDeviceVO> listDevicesByProductionLineId(TenantId tenantId, UUID productionLineId);

    /**
     * 开机率分析top
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 开机率分析top
     */
    List<MesBoardDeviceOperationRateVO> getDeviceOperationRateTop(TenantId tenantId, UUID productionLineId);

    /**
     * 产量趋势
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 产量趋势
     */
    List<MesBoardCapacityTrendItemVO> getCapacityTrend(TenantId tenantId, UUID productionLineId) throws ThingsboardException;

    /**
     * 生产监控
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 生产监控
     */
    List<MesBoardProductionMonitoringVO> getProductionMonitoring(TenantId tenantId, UUID productionLineId) throws ThingsboardException;

    /**
     * 机台产量对比
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 机台产量对比
     */
    List<MesBoardCapacityComparisonVO> getCapacityComparison(TenantId tenantId, UUID productionLineId) throws ThingsboardException;

    /**
     * 产量信息
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 产量信息
     */
    MesBoardCapacityInfoVO getCapacityInfo(TenantId tenantId, UUID productionLineId) throws ThingsboardException;

    /**
     * 生产进度跟踪
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 生产进度跟踪
     */
    List<MesBoardProductionProgressTrackingItemVO> getProductionProgressTracking(TenantId tenantId, UUID productionLineId) throws ThingsboardException;

    /**
     * 机台当前生产任务
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 机台当前生产任务
     */
    List<String> getProductionTask(TenantId tenantId, UUID productionLineId) throws ThingsboardException;

    /**
     * 异常预警
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 异常预警
     */
    List<String> getAbnormalWarning(TenantId tenantId, UUID productionLineId);
}
