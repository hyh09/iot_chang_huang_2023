package org.thingsboard.server.dao.hs.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.board.factoryBoard.dto.MesBoardDeviceOperationRateDto;
import org.thingsboard.server.dao.board.factoryBoard.impl.base.SqlServerBascFactoryImpl;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.vo.FactoryDeviceQuery;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.hs.service.MesService;
import org.thingsboard.server.dao.hsms.entity.dto.MesDeviceDTO;
import org.thingsboard.server.dao.hsms.entity.dto.MesProductionMonitoringDTO;
import org.thingsboard.server.dao.hsms.entity.dto.MesWorkshopDTO;
import org.thingsboard.server.dao.hsms.entity.vo.*;
import org.thingsboard.server.dao.util.decimal.BigDecimalUtil;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class MesServiceImpl extends SqlServerBascFactoryImpl implements MesService, CommonService {

    @Autowired
    private ClientService clientService;

    @Resource(name = "sqlServerTemplate")
    private JdbcTemplate jdbcTemplate;


    @PersistenceContext
    protected EntityManager entityManager;

    public MesServiceImpl(@Autowired JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }


    private static final String QUERY_ALL_MES_WORKSHOPS = "SELECT A.uGUID,??????=A.sWorkCentreName " +
            "FROM dbo.pbWorkCentre A(NOLOCK) " +
            "WHERE A.bUsable=1";

    private static final String QUERY_ALL_MES_WORKSHOP_DEVICES = "SELECT B.uGUID,?????????=B.sEquipmentName " +
            "FROM dbo.pbWorkCentre A(NOLOCK) " +
            "JOIN dbo.emEquipment B(NOLOCK) ON B.upbWorkCentreGUID = A.uGUID " +
            "WHERE A.uGUID= ?";

    /**
     * emEquipment???????????????
     * pbWorkCentre?????????????????????
     */
    private static final String QUERY_MES_WORKSHOP = "SELECT B.uGUID,??????=B.sWorkCentreName " +
            "FROM dbo.emEquipment A(NOLOCK) " +
            "JOIN dbo.pbWorkCentre B(NOLOCK) ON B.uGUID = A.upbWorkCentreGUID " +
            "WHERE A.uGUID= ?";

    /**
     * ????????????
     */
    private static final String QUERY_MES_DEVICE = "SELECT A.uGUID,?????????=A.sEquipmentName " +
            "FROM dbo.emEquipment A(NOLOCK) " +
            "WHERE A.uGUID= ?";

    /**
     * ??????7????????????
     */
    private static final String QUERY_CAPACITY_TREND = "SELECT ??????=A.sDate,??????=SUM(A.nTrackQty) " +
            "FROM ( " +
            "SELECT sDate=CONVERT(NVARCHAR(10),A1.tTrackTime,120),A1.nTrackQty " +
            "FROM dbo.ppTrackOutput A1(NOLOCK) " +
            "JOIN dbo.emEquipment B1(NOLOCK) ON B1.uGUID=A1.uemEquipmentGUID AND B1.upbWorkCentreGUID= ? " +
            "WHERE A1.tTrackTime>=DATEADD(DAY,-7,CONVERT(NVARCHAR(10),GETDATE(),120)) " +
            ") A " +
            "GROUP BY A.sDate";

    private static final String QUERY_PRODUCTION_MONITORING = "SELECT ??????=A.sDate,??????=A.sEquipmentName,??????=SUM(A.nTrackQty) " +
            "FROM ( " +
            "SELECT sDate=CONVERT(NVARCHAR(10),A1.tTrackTime,120),B1.sEquipmentName,A1.nTrackQty " +
            "FROM dbo.ppTrackOutput A1(NOLOCK) " +
            "JOIN dbo.emEquipment B1(NOLOCK) ON B1.uGUID=A1.uemEquipmentGUID " +
            "JOIN dbo.pbWorkCentre C1(NOLOCK) ON C1.uGUID = ? " +
            "WHERE A1.tTrackTime>=DATEADD(DAY,-7,CONVERT(NVARCHAR(10),GETDATE(),120)) " +
            ") A " +
            "GROUP BY A.sDate,A.sEquipmentName";

    private static final String QUERY_CAPACITY_INFO_MONTH_PLAN = "SELECT ????????????=SUM(B.nPlanOutputQty)  " +
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

    private static final String QUERY_CAPACITY_INFO_MONTH_CAPACITY = "SELECT ????????????=SUM(A.nTrackQty) " +
            "FROM dbo.ppTrackOutput A(NOLOCK)  " +
            "JOIN dbo.emEquipment B(NOLOCK) ON B.uGUID=A.uemEquipmentGUID  " +
            "JOIN dbo.pbWorkCentre C(NOLOCK) ON C.uGUID = B.upbWorkCentreGUID AND C.uGUID= ?  " +
            "WHERE A.tTrackTime>=DATEADD(MONTH,DATEDIFF(MONTH,0,GETDATE()),0)";

    private static final String QUERY_CAPACITY_INFO_RATE = ""; // ???????????? / ????????????

    private static final String QUERY_CAPACITY_INFO_TODAY_CAPACITY = "SELECT ????????????=SUM(A.nTrackQty)  " +
            "FROM dbo.ppTrackOutput A(NOLOCK)  " +
            "JOIN dbo.emEquipment B(NOLOCK) ON B.uGUID=A.uemEquipmentGUID  " +
            "JOIN dbo.pbWorkCentre C(NOLOCK) ON C.uGUID = B.upbWorkCentreGUID AND C.uGUID= ?  " +
            "WHERE A.tTrackTime>=CONVERT(NVARCHAR(10),GETDATE(),120)";

    private static final String QUERY_CAPACITY_INFO_TODAY_PRODUCTION_NUM = "SELECT ????????????=COUNT(1) " +
            "FROM ( SELECT DISTINCT B.uGUID FROM dbo.mnProducting A(NOLOCK) JOIN dbo.emEquipment B(NOLOCK) ON B.uGUID=A.uemEquipmentGUID AND B.upbWorkCentreGUID= ?) A1";

    private static final String QUERY_CAPACITY_INFO_TODAY_REPAIR_NUM = "SELECT ????????????=SUM(C.nTrackQty)  " +
            "FROM dbo.psWorkFlowCard A(NOLOCK)  " +
            "JOIN dbo.ppTrackJob B(NOLOCK) ON B.upsWorkFlowCardGUID = A.uGUID  " +
            "JOIN dbo.ppTrackOutput C(NOLOCK) ON C.uppTrackJobGUID = B.uGUID AND C.tTrackTime>=CONVERT(NVARCHAR(10),GETDATE(),120)  " +
            "JOIN dbo.emEquipment D(NOLOCK) ON D.uGUID=C.uemEquipmentGUID  " +
            "JOIN dbo.pbWorkCentre E(NOLOCK) ON E.uGUID = D.upbWorkCentreGUID AND E.uGUID= ?  " +
            "WHERE A.sType='??????'";

    /**
     * ????????????????????????
     * ??????????????????????????????????????????????????????
     */
    private static final String QUERY_PRODUCTION_PROGRESS_TRACKING = "SELECT TOP 10 ??????=A.sCardNo,??????=max(D.sCustomerName),??????=max(E.sColorNo),[??????(??????)]=CASE WHEN max(F.uGUID) IS NULL THEN NULL WHEN max(F.tPlanEndTime)>GETDATE() THEN 0 ELSE DATEDIFF(HOUR,max(F.tPlanEndTime),GETDATE()) END,????????????=max(G.sWorkingProcedureName),????????????=max(I.sWorkingProcedureName),?????????????????????=max(H.tFactEndTime)   " +
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
            "  GROUP BY A.sCardNo ORDER BY max(A.tPlanEndTime) DESC ";

    private static final String QUERY_PRODUCTION_TASK = "SELECT DISTINCT B.sEquipmentName " +
            "FROM dbo.mnProducting A(NOLOCK) " +
            "JOIN dbo.emEquipment B(NOLOCK) ON B.uGUID=A.uemEquipmentGUID AND B.upbWorkCentreGUID= ?";


//    /**
//     * ?????????????????????
//     *
//     * @param tenantId   ??????Id
//     * @param workshopId ??????Id
//     * @return ?????????????????????
//     */
//    @Override
//    public List<MesBoardProductionLineVO> listProductionLinesByWorkshopId(TenantId tenantId, UUID workshopId) {
//        return this.clientService.listProductionLinesByWorkshopId(tenantId, workshopId).stream().map(v -> MesBoardProductionLineVO.builder()
//                .name(v.getName())
//                .id(v.getId()).build()).collect(Collectors.toList());
//    }

    /**
     * ?????????????????????
     *
     * @param tenantId  ??????Id
     * @param factoryId ??????Id
     * @return ?????????????????????
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
     * ?????????????????????
     * ??????????????????????????? ???mesId
     *
     * @param tenantId         ??????Id
     * @param productionLineId ??????Id
     * @return ?????????????????????
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
     * ???????????????top
     * ###################
     * ##?????????: wb04
     * ##??????????????? 2023-03-06
     * ##???????????????sql???????????????JdbcTemplate ??????
     *
     * @param tenantId         ??????Id
     * @param productionLineId ??????Id
     * @return ???????????????top
     */
    @Override
    @SuppressWarnings("all")
    public List<MesBoardDeviceOperationRateVO> getDeviceOperationRateTop(TenantId tenantId, UUID productionLineId) {
        MesBoardDeviceOperationRateDto mesBoardDeviceOperationRateDto = new MesBoardDeviceOperationRateDto();
        mesBoardDeviceOperationRateDto.setWorkshopId(productionLineId);
        PageLink pageLink = new PageLink(10);
        PageData<MesBoardDeviceOperationRateDto> fulfillmentVoList = jdbcByAssembleSqlUtil.pageQuery(mesBoardDeviceOperationRateDto, DaoUtil.toPageable(pageLink));
        if (CollectionUtils.isEmpty(fulfillmentVoList.getData())) {
            return new ArrayList<>();
        }
        return fulfillmentVoList.getData().stream().map(dto -> {
            MesBoardDeviceOperationRateVO v1 = new MesBoardDeviceOperationRateVO();
            v1.setId(dto.getId());
            v1.setName(dto.getName());
            v1.setTime(dto.getTime());
            BigDecimalUtil bigDecimalUtil = new BigDecimalUtil(4, RoundingMode.HALF_UP);
            String value = bigDecimalUtil.divide(dto.getTime(), HSConstants.DAY_TIME.toString()).toPlainString();
            v1.setRate(BigDecimalUtil.INSTANCE.multiply(value, "100"));
            return v1;
        }).collect(Collectors.toList());


        /** ???????????? mes???????????????*/
        /**    var devices = this.listDevicesByProductionLineId(tenantId, productionLineId);
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

         Map<UUID, Long> map = resultList.stream().collect(Collectors.toMap(k -> k.getId(), v -> {
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
         */

    }

    /**
     * ????????????
     *
     * @param tenantId         ??????Id
     * @param productionLineId ??????Id
     * @return ????????????
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
                                rs.getBigDecimal("??????"),
                                rs.getString("??????")
                        )
        );
    }

    /**
     * ????????????
     *
     * @param tenantId         ??????Id
     * @param productionLineId ??????Id
     * @return ????????????
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
                                rs.getString("??????"),
                                rs.getString("??????"),
                                rs.getBigDecimal("??????")
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
     * ??????????????????
     *
     * @param tenantId         ??????Id
     * @param productionLineId ??????Id
     * @return ??????????????????
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
                                rs.getString("??????"),
                                rs.getString("??????"),
                                rs.getBigDecimal("??????")
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
     * ????????????
     *
     * @param tenantId         ??????Id
     * @param productionLineId ??????Id
     * @return ????????????
     */
    @Override
    @SuppressWarnings("all")
    public MesBoardCapacityInfoVO getCapacityInfo(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        var mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, productionLineId);
        if (mesWorkshopDTO == null) {
            log.info("mes ???????????????????????? >>> ??????????????????");
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
     * ??????????????????
     *
     * @param tenantId         ??????Id
     * @param productionLineId ??????Id
     * @return ??????????????????
     */
    @Override
    public List<MesBoardProductionProgressTrackingItemVO> getProductionProgressTracking(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        var mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, productionLineId);
        if (mesWorkshopDTO == null) {
            return Lists.newArrayList();
        }
        return this.jdbcTemplate.query(
                QUERY_PRODUCTION_PROGRESS_TRACKING,
                new Object[]{mesWorkshopDTO.getId()},
                (rs, rowNum) ->
                        new MesBoardProductionProgressTrackingItemVO(
                                rs.getString("??????"),
                                rs.getString("??????"),
                                rs.getString("??????"),
                                rs.getString("??????(??????)"),
                                rs.getString("????????????"),
                                rs.getString("????????????"),
                                rs.getString("?????????????????????")
                        )
        );
    }

    /**
     * ????????????????????????
     *
     * @param tenantId         ??????Id
     * @param productionLineId ??????Id
     * @return ????????????????????????
     */
    @Override
    public List<String> getProductionTask(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        var mesWorkshopDTO = this.getMesWorkshopByProductionLineId(tenantId, productionLineId);
        if (mesWorkshopDTO == null) {
            return Lists.newArrayList();
        }
        return this.jdbcTemplate.query(
                QUERY_PRODUCTION_TASK,
                new Object[]{mesWorkshopDTO.getId()},
                (rs, rowNum) -> rs.getString("sCardNo")
        );
    }

    /**
     * ????????????
     *
     * @param tenantId         ??????Id
     * @param productionLineId ??????Id
     * @return ????????????
     */
    @Override
    public List<String> getAbnormalWarning(TenantId tenantId, UUID productionLineId) {
        return null;
    }

    /**
     * ??????Mes??????
     *
     * @param productionLineId ??????Id
     * @param tenantId         ??????Id
     * @return Mes??????Id
     */
    public MesWorkshopDTO getMesWorkshopByProductionLineId(TenantId tenantId, UUID productionLineId) throws ThingsboardException {
        /**1 ???????????????*/
        List<Device> devices = this.clientService.listSimpleDevicesByQuery(tenantId, new FactoryDeviceQuery().setWorkshopId(productionLineId.toString()));
        if (devices.isEmpty()) {
            return null;
        }
        List<UUID> deviceIds = devices.stream().map(v -> v.getId().getId()).collect(Collectors.toList());
        /** 2.iot??????Id?????????MesId */
        List<UUID> mesDeviceIds = this.clientService.toMesDeviceIds(deviceIds);
        if (mesDeviceIds.isEmpty()) {
            throw new ThingsboardException("?????????????????????mes??????Id", ThingsboardErrorCode.GENERAL);
        }
        /** 3.??? ??????????????????id */
        var mesDeviceId = mesDeviceIds.get(0);
        /** 4.?????? ??????id????????? ???????????? */
        var mesWorkshopDTO = this.jdbcTemplate.queryForObject(QUERY_MES_WORKSHOP, new Object[]{mesDeviceId.toString()}, (rs, rowNum) ->
                new MesWorkshopDTO(
                        rs.getString("uGUID"),
                        rs.getString("??????")
                ));
        if (mesWorkshopDTO == null || StringUtils.isBlank(mesWorkshopDTO.getId())) {
            throw new ThingsboardException("????????????mes?????????????????????, mes??????Id:" + mesDeviceId.toString(), ThingsboardErrorCode.GENERAL);
        }
        return mesWorkshopDTO;
    }

    /**
     * ??????Mes ??????
     *
     * @param deviceId ??????Id
     * @return Mes ??????
     */
    public MesDeviceDTO getMesDeviceByDeviceId(UUID deviceId) throws ThingsboardException {
        var mesDeviceIds = this.clientService.toMesDeviceIds(Lists.newArrayList(deviceId));
        if (mesDeviceIds.isEmpty()) {
            throw new ThingsboardException("?????????????????????mes??????Id", ThingsboardErrorCode.GENERAL);
        }
        var mesDeviceId = mesDeviceIds.get(0);
        var MesDeviceDTO = this.jdbcTemplate.queryForObject(QUERY_MES_DEVICE, new Object[]{mesDeviceId.toString()}, (rs, rowNum) ->
                new MesDeviceDTO(
                        rs.getString("uGUID"),
                        rs.getString("?????????")
                ));
        if (MesDeviceDTO == null || StringUtils.isBlank(MesDeviceDTO.getId())) {
            throw new ThingsboardException("????????????mes????????????, mes??????Id:" + mesDeviceId.toString(), ThingsboardErrorCode.GENERAL);
        }
        return MesDeviceDTO;
    }

    /**
     * ????????????Mes ??????
     *
     * @param mesWorkshopId mes??????Id
     * @return Mes ??????
     */
    public List<MesDeviceDTO> listMesDevicesByMesWorkshopId(UUID mesWorkshopId) throws ThingsboardException {
        return this.jdbcTemplate.query(
                QUERY_ALL_MES_WORKSHOP_DEVICES,
                new Object[]{mesWorkshopId},
                (rs, rowNum) ->
                        new MesDeviceDTO(
                                rs.getString("uGUID"),
                                rs.getString("?????????")
                        )
        );
    }

    /**
     * ????????????Mes ??????
     *
     * @return Mes ??????
     */
    public List<MesWorkshopDTO> listMesWorkshops() throws ThingsboardException {
        return this.jdbcTemplate.query(
                QUERY_ALL_MES_WORKSHOPS,
                (rs, rowNum) ->
                        new MesWorkshopDTO(
                                rs.getString("uGUID"),
                                rs.getString("??????")
                        )
        );
    }
}
