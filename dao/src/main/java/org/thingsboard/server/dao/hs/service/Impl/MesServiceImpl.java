package org.thingsboard.server.dao.hs.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.hs.service.MesService;
import org.thingsboard.server.dao.hsms.entity.vo.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * mes
 *
 * @author wwj
 * @since 2021.11.26
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class MesServiceImpl implements MesService, CommonService {

    @Autowired
    private ClientService clientService;

    @Resource(name = "sqlServerTemplate")
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * 车间下全部产线
     *
     * @param tenantId   租户Id
     * @param workshopId 车间Id
     * @return 车间下全部产线
     */
    @Override
    public List<MesBoarProductionLineVO> listProductionLinesByWorkshopId(TenantId tenantId, UUID workshopId) {
        return this.clientService.listProductionLinesByWorkshopId(tenantId, workshopId).stream().map(v -> MesBoarProductionLineVO.builder()
                .name(v.getName())
                .id(v.getId()).build()).collect(Collectors.toList());
    }

    /**
     * 产线下全部设备
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 产线下全部设备
     */
    @Override
    public List<MesBoarDeviceVO> listDevicesByProductionLineId(TenantId tenantId, UUID productionLineId) {
        return this.clientService.listDevicesByQuery(tenantId, new FactoryDeviceQuery().setProductionLineId(productionLineId.toString())).stream().map(v -> MesBoarDeviceVO.builder()
                .name(v.getRename())
                .id(v.getId().getId()).build()).collect(Collectors.toList());
    }

    /**
     * 开机率分析top
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 开机率分析top
     */
    @Override
    @SuppressWarnings("all")
    public List<MesBoarDeviceOperationRateVO> getDeviceOperationRateTop(TenantId tenantId, UUID productionLineId) {
        return null;
    }

    /**
     * 产量趋势
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 产量趋势
     */
    @Override
    public List<MesBoarCapacityTrendItemVO> getCapacityTrend(TenantId tenantId, UUID productionLineId) {
        return null;
    }

    /**
     * 生产监控
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 生产监控
     */
    @Override
    public List<MesBoarProductionMonitoringVO> getProductionMonitoring(TenantId tenantId, UUID productionLineId) {
        return null;
    }

    /**
     * 机台产量对比
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 机台产量对比
     */
    @Override
    public List<MesBoarCapacityComparisonVO> getCapacityComparison(TenantId tenantId, UUID productionLineId) {
        return null;
    }

    /**
     * 产量信息
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 产量信息
     */
    @Override
    public MesBoarCapacityInfoVO getCapacityInfo(TenantId tenantId, UUID productionLineId) {
        return null;
    }

    /**
     * 生产进度跟踪
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 生产进度跟踪
     */
    @Override
    public List<MesBoardProductionProgressTrackingItemVO> getProductionProgressTracking(TenantId tenantId, UUID productionLineId) {
        return null;
    }

    /**
     * 机台当前生产任务
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 机台当前生产任务
     */
    @Override
    public List<String> getProductionTask(TenantId tenantId, UUID productionLineId) {
        return null;
    }

    /**
     * 异常预警
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 异常预警
     */
    @Override
    public List<String> getAbnormalWarning(TenantId tenantId, UUID productionLineId) {
        return null;
    }
}
