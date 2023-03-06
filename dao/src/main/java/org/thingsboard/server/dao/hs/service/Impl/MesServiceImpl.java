package org.thingsboard.server.dao.hs.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.hs.service.MesService;
import org.thingsboard.server.dao.hsms.entity.bo.DeviceHutBO;
import org.thingsboard.server.dao.hsms.entity.dto.MesDeviceDTO;
import org.thingsboard.server.dao.hsms.entity.dto.MesProductionMonitoringDTO;
import org.thingsboard.server.dao.hsms.entity.dto.MesWorkshopDTO;
import org.thingsboard.server.dao.hsms.entity.vo.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Autowired
//    @Qualifier("JdbcTemplate")
    private JdbcTemplate jdbcTemplate_pg;

    @PersistenceContext
    protected EntityManager entityManager;

    private static final String QUERY_ALL_MES_WORKSHOPS = "SELECT A.uGUID,车间=A.sWorkCentreName " +
            "FROM dbo.pbWorkCentre A(NOLOCK) " +
            "WHERE A.bUsable=1";

    private static final String QUERY_ALL_MES_WORKSHOP_DEVICES = "SELECT B.uGUID,设备名=B.sEquipmentName " +
            "FROM dbo.pbWorkCentre A(NOLOCK) " +
            "JOIN dbo.emEquipment B(NOLOCK) ON B.upbWorkCentreGUID = A.uGUID " +
            "WHERE A.uGUID= ?";

    /**
     * emEquipment：设备列表
     * pbWorkCentre：工作中心列表
     */
    private static final String QUERY_MES_WORKSHOP = "SELECT B.uGUID,车间=B.sWorkCentreName " +
            "FROM dbo.emEquipment A(NOLOCK) " +
            "JOIN dbo.pbWorkCentre B(NOLOCK) ON B.uGUID = A.upbWorkCentreGUID " +
            "WHERE A.uGUID= ?";

    private static final String QUERY_MES_DEVICE = "SELECT A.uGUID,设备名=A.sEquipmentName " +
            "FROM dbo.emEquipment A(NOLOCK) " +
            "WHERE A.uGUID= ?";

    /**
     * 过去7日的数据
     */
    private static final String QUERY_CAPACITY_TREND = "SELECT 日期=A.sDate,产量=SUM(A.nTrackQty) " +
            "FROM ( " +
            "SELECT sDate=CONVERT(NVARCHAR(10),A1.tTrackTime,120),A1.nTrackQty " +
            "FROM dbo.ppTrackOutput A1(NOLOCK) " +
            "JOIN dbo.emEquipment B1(NOLOCK) ON B1.uGUID=A1.uemEquipmentGUID AND B1.upbWorkCentreGUID= ? " +
            "WHERE A1.tTrackTime>=DATEADD(DAY,-7,CONVERT(NVARCHAR(10),GETDATE(),120)) " +
            ") A " +
            "GROUP BY A.sDate";

    private static final String QUERY_PRODUCTION_MONITORING = "SELECT 日期=A.sDate,设备=A.sEquipmentName,产量=SUM(A.nTrackQty) " +
            "FROM ( " +
            "SELECT sDate=CONVERT(NVARCHAR(10),A1.tTrackTime,120),B1.sEquipmentName,A1.nTrackQty " +
            "FROM dbo.ppTrackOutput A1(NOLOCK) " +
            "JOIN dbo.emEquipment B1(NOLOCK) ON B1.uGUID=A1.uemEquipmentGUID " +
            "JOIN dbo.pbWorkCentre C1(NOLOCK) ON C1.uGUID = ? " +
            "WHERE A1.tTrackTime>=DATEADD(DAY,-7,CONVERT(NVARCHAR(10),GETDATE(),120)) " +
            ") A " +
            "GROUP BY A.sDate,A.sEquipmentName";

    private static final String QUERY_CAPACITY_INFO_MONTH_PLAN = "SELECT 本月计划=SUM(B.nPlanOutputQty)  " +
            "FROM dbo.psWPP A(NOLOCK)  " +
            "JOIN dbo.psWorkFlowCard B(NOLOCK) ON B.uGUID = A.upsWorkFlowCardGUID  " +
            "WHERE A.tPlanStartTime>=DATEADD(MONTH,DATEDIFF(MONTH,0,GETDATE()),0) AND EXISTS(  " +
            "SELECT 1   " +
            "FROM dbo.ppTrackJob A1(NOLOCK)  " +
            "JOIN dbo.pbWorkingProcedure B1(NOLOCK) ON B1.uGUID=A1.upbWorkingProcedureGUID  " +
            "JOIN dbo.emEquipmentWorkingProcedure C1(NOLOCK) ON C1.upbWorkingProcedureGUID=B1.uGUID  " +
            "JOIN dbo.emEquipment D1(NOLOCK) ON D1.uGUID = C1.uemEquipmentGUID  " +
            "JOIN dbo.pbWorkCentre E1(NOLOCK) ON E1.uGUID = D1.upbWorkCentreGUID AND E1.uGUID = ?  " +
            "WHERE A1.upsWorkFlowCardGUID=B.uGUID  " +
            ")";

    private static final String QUERY_CAPACITY_INFO_MONTH_CAPACITY = "SELECT 本月产量=SUM(A.nTrackQty) " +
            "FROM dbo.ppTrackOutput A(NOLOCK)  " +
            "JOIN dbo.emEquipment B(NOLOCK) ON B.uGUID=A.uemEquipmentGUID  " +
            "JOIN dbo.pbWorkCentre C(NOLOCK) ON C.uGUID = B.upbWorkCentreGUID AND C.uGUID= ?  " +
            "WHERE A.tTrackTime>=DATEADD(MONTH,DATEDIFF(MONTH,0,GETDATE()),0)";

    private static final String QUERY_CAPACITY_INFO_RATE = ""; // 本月产量 / 本月计划

    private static final String QUERY_CAPACITY_INFO_TODAY_CAPACITY = "SELECT 今日产量=SUM(A.nTrackQty)  " +
            "FROM dbo.ppTrackOutput A(NOLOCK)  " +
            "JOIN dbo.emEquipment B(NOLOCK) ON B.uGUID=A.uemEquipmentGUID  " +
            "JOIN dbo.pbWorkCentre C(NOLOCK) ON C.uGUID = B.upbWorkCentreGUID AND C.uGUID= ?  " +
            "WHERE A.tTrackTime>=CONVERT(NVARCHAR(10),GETDATE(),120)";

    private static final String QUERY_CAPACITY_INFO_TODAY_PRODUCTION_NUM = "SELECT 在产数量=COUNT(1) " +
            "FROM ( SELECT DISTINCT B.uGUID FROM dbo.mnProducting A(NOLOCK) JOIN dbo.emEquipment B(NOLOCK) ON B.uGUID=A.uemEquipmentGUID AND B.upbWorkCentreGUID= ?) A1";

    private static final String QUERY_CAPACITY_INFO_TODAY_REPAIR_NUM = "SELECT 回修数量=SUM(C.nTrackQty)  " +
            "FROM dbo.psWorkFlowCard A(NOLOCK)  " +
            "JOIN dbo.ppTrackJob B(NOLOCK) ON B.upsWorkFlowCardGUID = A.uGUID  " +
            "JOIN dbo.ppTrackOutput C(NOLOCK) ON C.uppTrackJobGUID = B.uGUID AND C.tTrackTime>=CONVERT(NVARCHAR(10),GETDATE(),120)  " +
            "JOIN dbo.emEquipment D(NOLOCK) ON D.uGUID=C.uemEquipmentGUID  " +
            "JOIN dbo.pbWorkCentre E(NOLOCK) ON E.uGUID = D.upbWorkCentreGUID AND E.uGUID= ?  " +
            "WHERE A.sType='回修'";

    private static final String QUERY_PRODUCTION_PROGRESS_TRACKING = "SELECT TOP 10 卡号=A.sCardNo,客户=D.sCustomerName,色号=E.sColorNo,[超时(小时)]=CASE WHEN F.uGUID IS NULL THEN NULL WHEN F.tPlanEndTime>GETDATE() THEN 0 ELSE DATEDIFF(HOUR,F.tPlanEndTime,GETDATE()) END,当前工序=G.sWorkingProcedureName,上到工序=I.sWorkingProcedureName,上工序完成时间=H.tFactEndTime   " +
            "FROM dbo.psWorkFlowCard A(NOLOCK)    " +
            "JOIN dbo.ppTrackJob B(NOLOCK) ON B.upsWorkFlowCardGUID = A.uGUID AND B.bIsCurrent=1   " +
            "JOIN dbo.sdOrderHdr C(NOLOCK) ON C.sOrderNo = A.sOrderNo   " +
            "JOIN dbo.pbCustomer D(NOLOCK) ON D.uGUID = C.upbCustomerGUID   " +
            "JOIN dbo.tmColor E(NOLOCK) ON E.uGUID = A.utmColorGUID   " +
            "JOIN dbo.pbWorkingProcedure G(NOLOCK) ON G.uGUID=B.upbWorkingProcedureGUID   " +
            "JOIN dbo.emEquipmentWorkingProcedure J(NOLOCK) ON J.upbWorkingProcedureGUID=G.uGUID   " +
            "JOIN dbo.emEquipment K(NOLOCK) ON K.uGUID = J.uemEquipmentGUID   " +
            "JOIN dbo.pbWorkCentre L(NOLOCK) ON L.uGUID = K.upbWorkCentreGUID AND L.uGUID= ?   " +
            "LEFT JOIN dbo.psWPP F(NOLOCK) ON F.upsWorkFlowCardGUID = A.uGUID   " +
            "LEFT JOIN dbo.ppTrackJob H(NOLOCK) ON H.upsWorkFlowCardGUID = A.uGUID AND H.iOrderProcedure=B.iOrderProcedure-1   " +
            "LEFT JOIN dbo.pbWorkingProcedure I(NOLOCK) ON I.uGUID=H.upbWorkingProcedureGUID   " +
            "ORDER BY B.tFactStartTime desc ";

    private static final String QUERY_PRODUCTION_TASK = "SELECT DISTINCT B.sEquipmentName " +
            "FROM dbo.mnProducting A(NOLOCK) " +
            "JOIN dbo.emEquipment B(NOLOCK) ON B.uGUID=A.uemEquipmentGUID AND B.upbWorkCentreGUID= ?";

//    /**
//     * 车间下全部产线
//     *
//     * @param tenantId   租户Id
//     * @param workshopId 车间Id
//     * @return 车间下全部产线
//     */
//    @Override
//    public List<MesBoardProductionLineVO> listProductionLinesByWorkshopId(TenantId tenantId, UUID workshopId) {
//        return this.clientService.listProductionLinesByWorkshopId(tenantId, workshopId).stream().map(v -> MesBoardProductionLineVO.builder()
//                .name(v.getName())
//                .id(v.getId()).build()).collect(Collectors.toList());
//    }

    /**
     * 工厂下全部车间
     *
     * @param tenantId  租户Id
     * @param factoryId 工厂Id
     * @return 工厂下全部车间
     */
    @Override
    public List<MesBoardWorkshopVO> listWorkshopsByFactoryId(TenantId tenantId, UUID factoryId) {
        return this.clientService.listWorkshopsByFactoryId(tenantId, factoryId).stream().map(v -> {
                    MesWorkshopDTO mesWorkshopDTO = null;
                    try {
                        mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, v.getId());
                    } catch (ThingsboardException ignore) {
                    }
                    return MesBoardWorkshopVO.builder()
                            .name(v.getName())
                            .id(v.getId())
                            .mesId(Optional.ofNullable(mesWorkshopDTO).map(MesWorkshopDTO::getId).map(this::toUUID).orElse(null))
                            .mesName(Optional.ofNullable(mesWorkshopDTO).map(MesWorkshopDTO::getName).orElse(null))
                            .build();

                }
        ).collect(Collectors.toList());
    }

    /**
     * 产线下全部设备
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 产线下全部设备
     */
    @Override
    public List<MesBoardDeviceVO> listDevicesByProductionLineId(TenantId tenantId, UUID productionLineId) {
        return this.clientService.listDevicesByQuery(tenantId, new FactoryDeviceQuery().setWorkshopId(productionLineId.toString())).stream().map(v -> {
                    MesDeviceDTO mesDeviceDTO = null;
                    try {
                        mesDeviceDTO = this.getMesDeviceByDeviceId(v.getId().getId());
                    } catch (ThingsboardException ignore) {
                    }
                    return MesBoardDeviceVO.builder()
                            .id(v.getId().getId())
                            .name(v.getRename())
                            .mesId(Optional.ofNullable(mesDeviceDTO).map(MesDeviceDTO::getId).map(this::toUUID).orElse(null))
                            .mesName(Optional.ofNullable(mesDeviceDTO).map(MesDeviceDTO::getName).orElse(null))
                            .build();
                }
        ).collect(Collectors.toList());
    }

    /**
     * 开机率分析top
     * ###################
     * ##修改人: wb04
     * ##修改时间： 2023-03-06
     * ##原接口中的sql异常，改为JdbcTemplate 调用
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 开机率分析top
     */
    @Override
    @SuppressWarnings("all")
    public List<MesBoardDeviceOperationRateVO> getDeviceOperationRateTop(TenantId tenantId, UUID productionLineId) {
        var devices = this.listDevicesByProductionLineId(tenantId, productionLineId);
        if (devices.isEmpty())
            return Lists.newArrayList();

        String sqlString = "SELECT t.entity_id as id, t.total_time as totalTime, t.start_time as startTime FROM trep_day_sta_detail t WHERE t.tenant_id = :tenantId AND t.entity_id in (:deviceIds) AND t.bdate = date(:startTime) ";
//
//        Query query = entityManager.createNativeQuery(sqlString)
//                .setParameter("tenantId", tenantId.getId())
//                .setParameter("deviceIds", devices.stream().map(MesBoardDeviceVO::getId).collect(Collectors.toList()))
//                .setParameter("startTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//
//     List<DeviceHutBO> resultList=   query.getResultList();

        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate_pg);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("tenantId", tenantId.getId());
        parameters.addValue("deviceIds", devices.stream().map(MesBoardDeviceVO::getId).collect(Collectors.toList()));
        parameters.addValue("startTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        List<DeviceHutBO> resultList = givenParamJdbcTemp.query(sqlString, parameters, new BeanPropertyRowMapper<>(DeviceHutBO.class));

        if (!resultList.isEmpty() && resultList.get(0) != null)
            resultList = Lists.newArrayList();

        var map = resultList.stream().collect(Collectors.toMap(k -> k.getId(), v -> {
            var t = v.getStartTime() != null ? v.getStartTime() : 0L;
            if (v.getStartTime() != null)
                t += v.getStartTime();
            return t;
        }));

        return devices.stream().map(v -> MesBoardDeviceOperationRateVO.builder()
                .id(v.getId())
                .name(v.getName())
                .time(map.getOrDefault(v.getId(), 0L))
                .rate(this.calculatePercentage(new BigDecimal(map.getOrDefault(v.getId(), 0L)), new BigDecimal(HSConstants.DAY_TIME)))
                .build()).sorted(Comparator.comparing(MesBoardDeviceOperationRateVO::getTime).reversed()).collect(Collectors.toList());

    }

    /**
     * 产量趋势
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 产量趋势
     */
    @Override
    @SuppressWarnings("all")
    public List<MesBoardCapacityTrendItemVO> getCapacityTrend(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        var mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, productionLineId);
        if (mesWorkshopDTO == null)
            return Lists.newArrayList();
        return this.jdbcTemplate.query(
                QUERY_CAPACITY_TREND,
                new Object[]{mesWorkshopDTO.getId()},
                (rs, rowNum) ->
                        new MesBoardCapacityTrendItemVO(
                                rs.getBigDecimal("产量"),
                                rs.getString("日期")
                        )
        );
    }

    /**
     * 生产监控
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 生产监控
     */
    @Override
    @SuppressWarnings("all")
    public List<MesBoardProductionMonitoringVO> getProductionMonitoring(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        var mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, productionLineId);
        if (mesWorkshopDTO == null)
            return Lists.newArrayList();

        var mesProductionMonitoringDTOList = this.jdbcTemplate.query(
                QUERY_PRODUCTION_MONITORING,
                new Object[]{mesWorkshopDTO.getId()},
                (rs, rowNum) ->
                        new MesProductionMonitoringDTO(
                                rs.getString("日期"),
                                rs.getString("设备"),
                                rs.getBigDecimal("产量")
                        )
        );
        if (mesProductionMonitoringDTOList.isEmpty())
            return Lists.newArrayList();

        LinkedHashMap<String, List<MesBoardProductionMonitoringItemVO>> map = Maps.newLinkedHashMap();
        mesProductionMonitoringDTOList.forEach(v -> {
            map.compute(v.getDeviceName(), (x, y) -> {
                if (y == null)
                    return Lists.newArrayList();
                y.add(MesBoardProductionMonitoringItemVO.builder()
                        .xValue(v.getDate())
                        .yValue(v.getCapacity())
                        .build());
                return y;
            });
        });

        return map.entrySet().stream().map(entry -> {
            return MesBoardProductionMonitoringVO.builder()
                    .name(entry.getKey())
                    .items(entry.getValue())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 机台产量对比
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 机台产量对比
     */
    @Override
    @SuppressWarnings("all")
    public List<MesBoardCapacityComparisonVO> getCapacityComparison(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        var mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, productionLineId);
        if (mesWorkshopDTO == null)
            return Lists.newArrayList();

        var mesProductionMonitoringDTOList = this.jdbcTemplate.query(
                QUERY_PRODUCTION_MONITORING,
                new Object[]{mesWorkshopDTO.getId()},
                (rs, rowNum) ->
                        new MesProductionMonitoringDTO(
                                rs.getString("日期"),
                                rs.getString("设备"),
                                rs.getBigDecimal("产量")
                        )
        );
        if (mesProductionMonitoringDTOList.isEmpty())
            return Lists.newArrayList();

        LinkedHashMap<String, List<MesBoardCapacityComparisonItemVO>> map = Maps.newLinkedHashMap();
        mesProductionMonitoringDTOList.forEach(v -> {
            map.compute(v.getDeviceName(), (x, y) -> {
                if (y == null)
                    return Lists.newArrayList();
                y.add(MesBoardCapacityComparisonItemVO.builder()
                        .xValue(v.getDate())
                        .yValue(v.getCapacity())
                        .build());
                return y;
            });
        });

        return map.entrySet().stream().map(entry -> {
            return MesBoardCapacityComparisonVO.builder()
                    .name(entry.getKey())
                    .items(entry.getValue())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 产量信息
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 产量信息
     */
    @Override
    @SuppressWarnings("all")
    public MesBoardCapacityInfoVO getCapacityInfo(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        var mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, productionLineId);
        if (mesWorkshopDTO == null) {
            log.info("mes 车间信息查询为空 >>> 产量信息接口");
            return new MesBoardCapacityInfoVO();
        }

        var monthCapacity = this.jdbcTemplate.queryForObject(
                QUERY_CAPACITY_INFO_MONTH_CAPACITY,
                new Object[]{mesWorkshopDTO.getId()},
                BigDecimal.class
        );

        var monthPlan = this.jdbcTemplate.queryForObject(
                QUERY_CAPACITY_INFO_MONTH_PLAN,
                new Object[]{mesWorkshopDTO.getId()},
                BigDecimal.class
        );

        BigDecimal rate = BigDecimal.ZERO;
        if (monthPlan != null && monthPlan.compareTo(BigDecimal.ZERO) > 0) {
            rate = this.formatCapacity(this.calculatePercentage(monthCapacity, monthPlan));
        }

        return MesBoardCapacityInfoVO.builder()
                .monthCapacity(monthCapacity)
                .monthPlan(monthPlan)
                .productionNum(this.jdbcTemplate.queryForObject(
                        QUERY_CAPACITY_INFO_TODAY_PRODUCTION_NUM,
                        new Object[]{mesWorkshopDTO.getId()},
                        BigDecimal.class
                ))
                .todayCapacity(this.jdbcTemplate.queryForObject(
                        QUERY_CAPACITY_INFO_TODAY_CAPACITY,
                        new Object[]{mesWorkshopDTO.getId()},
                        BigDecimal.class
                ))
                .rate(rate)
                .repairNum(this.jdbcTemplate.queryForObject(
                        QUERY_CAPACITY_INFO_TODAY_REPAIR_NUM,
                        new Object[]{mesWorkshopDTO.getId()},
                        BigDecimal.class
                ))
                .build();
    }

    /**
     * 生产进度跟踪
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 生产进度跟踪
     */
    @Override
    public List<MesBoardProductionProgressTrackingItemVO> getProductionProgressTracking(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        var mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, productionLineId);
        if (mesWorkshopDTO == null)
            return Lists.newArrayList();
        return this.jdbcTemplate.query(
                QUERY_PRODUCTION_PROGRESS_TRACKING,
                new Object[]{mesWorkshopDTO.getId()},
                (rs, rowNum) ->
                        new MesBoardProductionProgressTrackingItemVO(
                                rs.getString("卡号"),
                                rs.getString("客户"),
                                rs.getString("色号"),
                                rs.getString("超时(小时)"),
                                rs.getString("当前工序"),
                                rs.getString("上到工序"),
                                rs.getString("上工序完成时间")
                        )
        );
    }

    /**
     * 机台当前生产任务
     *
     * @param tenantId         租户Id
     * @param productionLineId 产线Id
     * @return 机台当前生产任务
     */
    @Override
    public List<String> getProductionTask(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        var mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, productionLineId);
        if (mesWorkshopDTO == null)
            return Lists.newArrayList();
        return this.jdbcTemplate.query(
                QUERY_PRODUCTION_TASK,
                new Object[]{mesWorkshopDTO.getId()},
                (rs, rowNum) -> rs.getString("sCardNo")
        );
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

    /**
     * 查询Mes车间
     *
     * @param productionLineId 产线Id
     * @param tenantId         租户Id
     * @return Mes车间Id
     */
    public MesWorkshopDTO getMesWorkshopByProductionLineId(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        /**1 先查询设备*/
        List<Device> devices = this.clientService.listSimpleDevicesByQuery(tenantId, new FactoryDeviceQuery().setWorkshopId(productionLineId.toString()));
        if (devices.isEmpty()) {
            return null;
        }
        List<UUID> deviceIds = devices.stream().map(v -> v.getId().getId()).collect(Collectors.toList());
        /** 2.iot设备Id转换到MesId */
        List<UUID> mesDeviceIds = this.clientService.toMesDeviceIds(deviceIds);
        if (mesDeviceIds.isEmpty()) {
            throw new ThingsboardException("未查询到对应的mes设备Id", ThingsboardErrorCode.GENERAL);
        }
        /** 3.取 其中一个设备id */
        var mesDeviceId = mesDeviceIds.get(0);
        /** 4.查询 设备id所在的 车间信息 */
        var mesWorkshopDTO = this.jdbcTemplate.queryForObject(QUERY_MES_WORKSHOP, new Object[]{mesDeviceId.toString()}, (rs, rowNum) ->
                new MesWorkshopDTO(
                        rs.getString("uGUID"),
                        rs.getString("车间")
                ));
        if (mesWorkshopDTO == null || StringUtils.isBlank(mesWorkshopDTO.getId()))
            throw new ThingsboardException("未查询到mes设备对应的车间, mes设备Id:" + mesDeviceId.toString(), ThingsboardErrorCode.GENERAL);
        return mesWorkshopDTO;
    }

    /**
     * 查询Mes 设备
     *
     * @param deviceId 设备Id
     * @return Mes 设备
     */
    public MesDeviceDTO getMesDeviceByDeviceId(UUID deviceId) throws ThingsboardException {
        var mesDeviceIds = this.clientService.toMesDeviceIds(Lists.newArrayList(deviceId));
        if (mesDeviceIds.isEmpty())
            throw new ThingsboardException("未查询到对应的mes设备Id", ThingsboardErrorCode.GENERAL);
        var mesDeviceId = mesDeviceIds.get(0);
        var MesDeviceDTO = this.jdbcTemplate.queryForObject(QUERY_MES_DEVICE, new Object[]{mesDeviceId.toString()}, (rs, rowNum) ->
                new MesDeviceDTO(
                        rs.getString("uGUID"),
                        rs.getString("设备名")
                ));
        if (MesDeviceDTO == null || StringUtils.isBlank(MesDeviceDTO.getId()))
            throw new ThingsboardException("未查询到mes设备信息, mes设备Id:" + mesDeviceId.toString(), ThingsboardErrorCode.GENERAL);
        return MesDeviceDTO;
    }

    /**
     * 查询全部Mes 设备
     *
     * @param mesWorkshopId mes车间Id
     * @return Mes 设备
     */
    public List<MesDeviceDTO> listMesDevicesByMesWorkshopId(UUID mesWorkshopId) throws ThingsboardException {
        return this.jdbcTemplate.query(
                QUERY_ALL_MES_WORKSHOP_DEVICES,
                new Object[]{mesWorkshopId},
                (rs, rowNum) ->
                        new MesDeviceDTO(
                                rs.getString("uGUID"),
                                rs.getString("设备名")
                        )
        );
    }

    /**
     * 查询全部Mes 车间
     *
     * @return Mes 车间
     */
    public List<MesWorkshopDTO> listMesWorkshops() throws ThingsboardException {
        return this.jdbcTemplate.query(
                QUERY_ALL_MES_WORKSHOPS,
                (rs, rowNum) ->
                        new MesWorkshopDTO(
                                rs.getString("uGUID"),
                                rs.getString("车间")
                        )
        );
    }
}
