package org.thingsboard.server.dao.sqlserver.mes.service.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.Aggregation;
import org.thingsboard.server.common.data.kv.BaseReadTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.entity.vo.HistoryGraphPropertyTsKvVO;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;
import org.thingsboard.server.dao.sql.attributes.AttributeKvRepository;
import org.thingsboard.server.dao.sql.mesdevicerelation.JpaMesDeviceRelationDao;
import org.thingsboard.server.dao.sql.mesdevicerelation.MesDeviceRelationRepository;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.*;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.*;
import org.thingsboard.server.dao.sqlserver.mes.service.MesOrderService;
import org.thingsboard.server.dao.sqlserver.utils.PageJdbcUtil;
import org.thingsboard.server.dao.timeseries.TimeseriesService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MesOrderServiceImpl implements MesOrderService, CommonService {
    @Resource(name = "sqlServerTemplate")
    private JdbcTemplate jdbcTemplate;
    @Resource
    private PageJdbcUtil pageJdbcUtil;
    @Resource
    private JpaMesDeviceRelationDao jpaMesDeviceRelationDao;
    @Autowired
    private MesDeviceRelationRepository mesDeviceRelationRepository;
    @Resource
    private AttributeKvRepository attributeKvRepository;
    @Resource
    private TimeseriesService timeseriesService;

    /**
     * 查询订单列表
     */
    private String sqlOrderListCount = "SELECT count(1) " +
            " FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            " WHERE 1=1 ";
    private String sqlOrderList = " SELECT row_number () OVER (ORDER BY A.tCreateTime  DESC) AS rownumber ," +
            "A.sOrderNo,A.sCreator,A.tCreateTime FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            " WHERE 1=1  ";


    /**
     * 查询订单进度
     */
    private String sqlOrderProgressList = " SELECT " +
            " A.sOrderNo,D.sCustomerName,C.dDeliveryDate,E.sMaterialName,F.sColorName,B.nQty,B.sFinishingMethod " +
            "FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            "JOIN dbo.pbCustomer D(NOLOCK)ON D.uGUID=A.upbCustomerGUID " +
            "JOIN dbo.mmMaterial E(NOLOCK)ON E.uGUID=B.ummMaterialGUID " +
            "JOIN dbo.tmColor F(NOLOCK)ON F.uGUID=B.utmColorGUID" +
            " WHERE 1=1  ";
    private String sqlOrderProgressListCount = "SELECT count(1) " +
            "FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            "JOIN dbo.pbCustomer D(NOLOCK)ON D.uGUID=A.upbCustomerGUID " +
            "JOIN dbo.mmMaterial E(NOLOCK)ON E.uGUID=B.ummMaterialGUID " +
            "JOIN dbo.tmColor F(NOLOCK)ON F.uGUID=B.utmColorGUID" +
            " WHERE 1=1  ";
    /**
     * 生产卡查询
     */
    private String sqlProductionCard = " SELECT row_number () OVER (ORDER BY B.dDeliveryDate ASC) AS rownumber ," +
            " A.sCardNo,A.sOrderNo,B.dDeliveryDate,E.sCustomerName,F.sMaterialName,C.sColorName,C.sFinishingMethod,A.nPlanOutputQty,H.sWorkingProcedureName,J.sWorkingProcedureName as sWorkingProcedureNameNext " +
            "FROM dbo.psWorkFlowCard A(NOLOCK) " +
            "JOIN dbo.sdOrderLot B(NOLOCK)ON B.uGUID=A.usdOrderLotGUID " +
            "JOIN dbo.sdOrderDtl C(NOLOCK)ON C.uGUID=B.usdOrderDtlGUID " +
            "JOIN dbo.sdOrderHdr D(NOLOCK)ON D.uGUID=C.usdOrderHdrGUID " +
            "JOIN dbo.pbCustomer E(NOLOCK)ON E.uGUID=D.upbCustomerGUID " +
            "JOIN dbo.mmMaterial F(NOLOCK)ON F.uGUID=A.ummMaterialGUID " +
            "JOIN dbo.ppTrackJob G(NOLOCK)ON G.upsWorkFlowCardGUID=A.uGUID AND G.bIsCurrent=1 " +
            "JOIN dbo.pbWorkingProcedure H(NOLOCK)ON H.uGUID=G.upbWorkingProcedureGUID " +
            "LEFT JOIN dbo.ppTrackJob I(NOLOCK)ON I.upsWorkFlowCardGUID=A.uGUID AND I.iOrderProcedure=G.iOrderProcedure+1 " +
            "LEFT JOIN dbo.pbWorkingProcedure J(NOLOCK)ON J.uGUID=I.upbWorkingProcedureGUID " +
            " WHERE 1=1  ";
    private String sqlProductionCardCount = "SELECT count(1) " +
            "FROM dbo.psWorkFlowCard A(NOLOCK) " +
            "JOIN dbo.sdOrderLot B(NOLOCK)ON B.uGUID=A.usdOrderLotGUID " +
            "JOIN dbo.sdOrderDtl C(NOLOCK)ON C.uGUID=B.usdOrderDtlGUID " +
            "JOIN dbo.sdOrderHdr D(NOLOCK)ON D.uGUID=C.usdOrderHdrGUID " +
            "JOIN dbo.pbCustomer E(NOLOCK)ON E.uGUID=D.upbCustomerGUID " +
            "JOIN dbo.mmMaterial F(NOLOCK)ON F.uGUID=A.ummMaterialGUID " +
            "JOIN dbo.ppTrackJob G(NOLOCK)ON G.upsWorkFlowCardGUID=A.uGUID AND G.bIsCurrent=1 " +
            "JOIN dbo.pbWorkingProcedure H(NOLOCK)ON H.uGUID=G.upbWorkingProcedureGUID " +
            "LEFT JOIN dbo.ppTrackJob I(NOLOCK)ON I.upsWorkFlowCardGUID=A.uGUID AND I.iOrderProcedure=G.iOrderProcedure+1 " +
            "LEFT JOIN dbo.pbWorkingProcedure J(NOLOCK)ON J.uGUID=I.upbWorkingProcedureGUID " +
            " WHERE 1=1  ";

    /**
     * 生产进度查询
     */
    private String sqlProductionProgress = " SELECT row_number () OVER (ORDER BY B.tFactEndTime ASC) AS rownumber ," +
            " E.sWorkingProcedureNo,E.sWorkingProcedureName,B.tFactStartTime,B.tFactEndTime,D.sEquipmentName,C.nTrackQty,C.nPercentValue,B.sLocation,C.sWorkerGroupName,C.sWorkerNameList " +
            "FROM dbo.psWorkFlowCard A(NOLOCK) " +
            "JOIN dbo.ppTrackJob B(NOLOCK)ON B.upsWorkFlowCardGUID=A.uGUID " +
            "JOIN dbo.pbWorkingProcedure E(NOLOCK)ON E.uGUID=B.upbWorkingProcedureGUID " +
            "LEFT JOIN dbo.ppTrackOutput C(NOLOCK)ON C.uppTrackJobGUID=B.uGUID " +
            "LEFT JOIN dbo.emEquipment D(NOLOCK)ON D.uGUID=C.uemEquipmentGUID " +
            " WHERE 1=1  ";
    private String sqlProductionProgressCount = "SELECT count(1) " +
            "FROM dbo.psWorkFlowCard A(NOLOCK) " +
            "JOIN dbo.ppTrackJob B(NOLOCK)ON B.upsWorkFlowCardGUID=A.uGUID " +
            "JOIN dbo.pbWorkingProcedure E(NOLOCK)ON E.uGUID=B.upbWorkingProcedureGUID " +
            "LEFT JOIN dbo.ppTrackOutput C(NOLOCK)ON C.uppTrackJobGUID=B.uGUID " +
            "LEFT JOIN dbo.emEquipment D(NOLOCK)ON D.uGUID=C.uemEquipmentGUID " +
            " WHERE 1=1  ";

    private String sqlOrderCardList = "SELECT\n" +
            "  a.sCardNo,\n" +
            "  d.sOrderNo,\n" +
            "  e.sMaterialName,\n" +
            "  f.sColorName \n" +
            "FROM\n" +
            "  psWorkFlowCard a WITH (NOLOCK)\n" +
            "  JOIN sdOrderLot b WITH (NOLOCK) ON a.usdOrderLotGUID= b.uGUID\n" +
            "  JOIN sdOrderDtl c WITH (NOLOCK) ON b.usdOrderDtlGUID= c.uGUID\n" +
            "  JOIN sdOrderHdr d WITH (NOLOCK) ON c.usdOrderHdrGUID= d.uGUID\n" +
            "  JOIN mmMaterial e WITH (NOLOCK) ON a.ummMaterialGUID= e.uGUID\n" +
            "  JOIN tmColor f WITH (NOLOCK) ON a.utmColorGUID= f.uGUID" +
            " WHERE 1=1  ";

    private String sqlProducted = "SELECT\n" +
            "\ta.sWorkingProcedureNo,\n" +
            "\ta.sWorkingProcedureName,\n" +
            "\ta.sWorkerGroupName,\n" +
            "\ta.sWorkerName,\n" +
            "\ta.nTrackQty,\n" +
            "\ta.tStartTime,\n" +
            "\ta.tEndTime \n" +
            "\ta.uemEquipmentGUID \n" +
            "FROM\n" +
            "\tdbo.mnProducted a ( NOLOCK ) \n" +
            "WHERE\n" +
            "\ta.sCardNo = ?";

    @Override
    public PageData<MesOrderListVo> findOrderList(MesOrderListDto dto, PageLink pageLink) {
        try {
            int rowNumber = (pageLink.getPage() - 1) * pageLink.getPageSize();
            //queryTotal方法统计总数
            int total = this.queryOrderListTotal(dto);
            //queryRecordList方法查询并转换实体类List
            List<MesOrderListVo> recordList = this.queryOrderListRecordList(dto, pageLink.getPageSize(), rowNumber);
            //查询工厂

            PageData<MesOrderListVo> resultPage = new PageData<>();
            resultPage = new PageData<MesOrderListVo>(recordList, total / pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
            return resultPage;
        } catch (Exception e) {
            log.error("异常信息{}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageData<MesOrderProgressListVo> findOrderProgressList(MesOrderProgressListDto dto, PageLink pageLink) {
        try {
            return pageJdbcUtil.queryList((params, sql, orderFlag) -> {
                if (dto != null) {
                    if (StringUtils.isNotEmpty(dto.getDDeliveryDateBegin())) {
                        sql.append("and C.dDeliveryDate >=? ");
                        params.add(dto.getDDeliveryDateBegin());
                    }
                    if (StringUtils.isNotEmpty(dto.getDDeliveryDateEnd())) {
                        sql.append("and C.dDeliveryDate <=? ");
                        params.add(dto.getDDeliveryDateEnd());
                    }
                    if (StringUtils.isNotEmpty(dto.getSOrderNo())) {
                        sql.append("and A.sOrderNo =? ");
                        params.add(dto.getSOrderNo());
                    }
                    if (StringUtils.isNotEmpty(dto.getSCustomerName())) {
                        sql.append("and D.sCustomerName =? ");
                        params.add(dto.getSCustomerName());
                    }
                    if (StringUtils.isNotEmpty(dto.getSColorName())) {
                        sql.append("and F.sColorName =? ");
                        params.add(dto.getSColorName());
                    }
                    if (orderFlag) {
                        sql.append("order by a.tCreateTime desc ");
                    }
                }
            }, MesOrderProgressListVo.class, sqlOrderProgressList, pageLink);
        } catch (Exception e) {
            log.error("异常信息{}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageData<MesProductionCardListVo> findProductionCardList(MesProductionCardListDto dto, PageLink pageLink) {
        try {
            int rowNumber = (pageLink.getPage() - 1) * pageLink.getPageSize();
            //queryTotal方法统计总数
            int total = this.queryProductionCardListTotal(dto);
            //queryRecordList方法查询并转换实体类List
            List<MesProductionCardListVo> recordList = this.queryProductionCardListRecordList(dto, pageLink.getPageSize(), rowNumber);
            PageData<MesProductionCardListVo> resultPage = new PageData<>();
            resultPage = new PageData<MesProductionCardListVo>(recordList, total / pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
            return resultPage;
        } catch (Exception e) {
            log.error("异常信息{}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageData<MesProductionProgressListVo> findProductionProgressList(MesProductionProgressListDto dto, PageLink pageLink) {
        try {
            int rowNumber = (pageLink.getPage() - 1) * pageLink.getPageSize();
            //queryTotal方法统计总数
            int total = this.queryProductionProgressListTotal(dto);
            //queryRecordList方法查询并转换实体类List
            List<MesProductionProgressListVo> recordList = this.queryProductionProgressListRecordList(dto, pageLink.getPageSize(), rowNumber);
            PageData<MesProductionProgressListVo> resultPage = new PageData<>();
            resultPage = new PageData<MesProductionProgressListVo>(recordList, total / pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
            return resultPage;
        } catch (Exception e) {
            log.error("异常信息{}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageData<MesOrderCardListVo> findOrderCardList(MesOrderCardListDto dto, PageLink pageLink) {
        return pageJdbcUtil.queryList((params, sql, orderFlag) -> {
            if (dto != null) {
                if (StringUtils.isNotEmpty(dto.getDateBegin())) {
                    sql.append("and a.tCreateTime >=? ");
                    params.add(dto.getDateBegin());
                }
                if (StringUtils.isNotEmpty(dto.getDateEnd())) {
                    sql.append("and a.tCreateTime <=? ");
                    params.add(dto.getDateEnd());
                }
                if (StringUtils.isNotEmpty(dto.getSCardNo())) {
                    sql.append("and a.sCardNo LIKE CONCAT('%',?, '%') ");
                    params.add(dto.getSCardNo());
                }
                if (StringUtils.isNotEmpty(dto.getSOrderNo())) {
                    sql.append("and d.sOrderNo LIKE CONCAT('%',?, '%') ");
                    params.add(dto.getSOrderNo());
                }
                if (StringUtils.isNotEmpty(dto.getSMaterialName())) {
                    sql.append("and e.sMaterialName LIKE CONCAT('%',?, '%') ");
                    params.add(dto.getSMaterialName());
                }
                if (orderFlag) {
                    sql.append("order by a.tCreateTime desc ");
                }
            }
        }, MesOrderCardListVo.class, sqlOrderCardList, pageLink);
    }

    @Override
    public List<MesProductedVo> findProductedList(String cardNo) {
        if (StringUtils.isEmpty(cardNo)) {
            return new ArrayList<>();
        }
        List<Object> params = new ArrayList<>();
        params.add(cardNo);
        Object[] para = params.toArray(new Object[params.size()]);
        return this.jdbcTemplate.query(sqlProducted, para, new BeanPropertyRowMapper(MesProductedVo.class));
    }

    @Override
    public List<MesChartVo> getChart(MesChartDto dto) {
        //根据mesId获取deviceId
        UUID deviceId = jpaMesDeviceRelationDao.getDeviceIdByMesId(UUID.fromString(dto.getUemEquipmentGUID()));
        if (deviceId == null) {
            return null;
        }
        List<AttributeKvEntity> allByIdEntityId = attributeKvRepository.findAllByIdEntityId(deviceId);
        List<MesChartVo> result = allByIdEntityId.stream().map(e -> {
            MesChartVo mesChartVo = new MesChartVo();
            mesChartVo.setKey(e.getId().getAttributeKey());
            return mesChartVo;
        }).collect(Collectors.toList());
        MesChartVo mesChartVo = result.get(0);
        mesChartVo.setTsKvs(this.listTsKvs(dto.getTenantId(), new DeviceId(deviceId), mesChartVo.getKey(), dto.getTStartTime().getTime(), dto.getTEndTime().getTime()));
        return result;
    }

    @Override
    public List<HistoryGraphPropertyTsKvVO> getParamChart(MesChartDto dto) {
        UUID deviceId = dto.getDeviceId();
        return listTsKvs(dto.getTenantId(), new DeviceId(deviceId), dto.getKey(), dto.getTStartTime().getTime(), dto.getTEndTime().getTime());
    }

    /**
     * 获得遥测时序数据
     *
     * @param tenantId  租户Id
     * @param deviceId  设备Id
     * @param name      名称
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    @SuppressWarnings("all")
    public List<HistoryGraphPropertyTsKvVO> listTsKvs(TenantId tenantId, DeviceId deviceId, String name, Long startTime, Long endTime) {
        try {
            List<String> keyList = new ArrayList<>() {{
                add(name);
            }};
            List<ReadTsKvQuery> tempQueries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, startTime, endTime, endTime - startTime, 1, Aggregation.COUNT, "desc"))
                    .collect(Collectors.toList());

            var tempResult = this.timeseriesService.findAll(tenantId, deviceId, tempQueries).get()
                    .stream().collect(Collectors.toMap(TsKvEntry::getKey, Function.identity()));
            if (tempResult.isEmpty())
                return Lists.newArrayList();
            int count = Integer.parseInt(String.valueOf(tempResult.get(name).getValue()));
            List<ReadTsKvQuery> queries = keyList.stream().map(key -> new BaseReadTsKvQuery(key, startTime, endTime, endTime - startTime, count, Aggregation.NONE, "desc"))
                    .collect(Collectors.toList());

            return this.timeseriesService.findAll(tenantId, deviceId, queries).get()
                    .stream().sorted(Comparator.comparing(TsKvEntry::getTs).reversed()).map(e -> HistoryGraphPropertyTsKvVO.builder()
                            .ts(e.getTs())
                            .value(this.formatKvEntryValue(e))
                            .build()).collect(Collectors.toList());
        } catch (Exception ignore) {
            return Lists.newArrayList();
        }
    }


    /**
     * 订单进度列表查询数量sql拼接
     *
     * @param dto
     * @return
     */
    private Integer queryProductionProgressListTotal(MesProductionProgressListDto dto) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlProductionProgressCount);
        if (dto != null) {
            if (StringUtils.isNotEmpty(dto.getSOrderNo())) {
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());
        return this.jdbcTemplate.queryForObject(sql.toString(), para, Integer.class);
    }

    /**
     * 订单进度列表查询sql拼接
     *
     * @param dto
     * @param pageSize
     * @param rowNumber
     * @return
     */
    private List<MesProductionProgressListVo> queryProductionProgressListRecordList(MesProductionProgressListDto dto, Integer pageSize, Integer rowNumber) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();

        if (pageSize != null) {
            sql.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        sql.append(sqlProductionProgress);
        if (dto != null) {
            if (StringUtils.isNotEmpty(dto.getSOrderNo())) {
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
        }
        if (rowNumber != null) {
            sql.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());

        return this.jdbcTemplate.query(sql.toString(), para, new BeanPropertyRowMapper(MesProductionProgressListVo.class));
    }

    /**
     * 订单进度列表查询数量sql拼接
     *
     * @param dto
     * @return
     */
    private Integer queryProductionCardListTotal(MesProductionCardListDto dto) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlProductionCardCount);
        if (dto != null) {
            if (StringUtils.isNotEmpty(dto.getDDeliveryDateBegin())) {
                sql.append("and B.dDeliveryDate >=? ");
                params.add(dto.getDDeliveryDateBegin());
            }
            if (StringUtils.isNotEmpty(dto.getDDeliveryDateEnd())) {
                sql.append("and B.dDeliveryDate <=? ");
                params.add(dto.getDDeliveryDateEnd());
            }
            if (StringUtils.isNotEmpty(dto.getSOrderNo())) {
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
            if (StringUtils.isNotEmpty(dto.getSColorName())) {
                sql.append("and F.sColorName =? ");
                params.add(dto.getSColorName());
            }
            if (StringUtils.isNotEmpty(dto.getSCustomerName())) {
                sql.append("and D.sCustomerName =? ");
                params.add(dto.getSCustomerName());
            }
            if (StringUtils.isNotEmpty(dto.getSCardNo())) {
                sql.append("and A.sCardNo =? ");
                params.add(dto.getSCardNo());
            }
            if (StringUtils.isNotEmpty(dto.getSMaterialName())) {
                sql.append("and F.sMaterialName=? ");
                params.add(dto.getSMaterialName());
            }
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());
        return this.jdbcTemplate.queryForObject(sql.toString(), para, Integer.class);
    }

    /**
     * 订单进度列表查询sql拼接
     *
     * @param dto
     * @param pageSize
     * @param rowNumber
     * @return
     */
    private List<MesProductionCardListVo> queryProductionCardListRecordList(MesProductionCardListDto dto, Integer pageSize, Integer rowNumber) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();

        if (pageSize != null) {
            sql.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        sql.append(sqlProductionCard);
        if (dto != null) {
            if (StringUtils.isNotEmpty(dto.getDDeliveryDateBegin())) {
                sql.append("and B.dDeliveryDate >=? ");
                params.add(dto.getDDeliveryDateBegin());
            }
            if (StringUtils.isNotEmpty(dto.getDDeliveryDateEnd())) {
                sql.append("and B.dDeliveryDate <=? ");
                params.add(dto.getDDeliveryDateEnd());
            }
            if (StringUtils.isNotEmpty(dto.getSOrderNo())) {
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
            if (StringUtils.isNotEmpty(dto.getSColorName())) {
                sql.append("and F.sColorName =? ");
                params.add(dto.getSColorName());
            }
            if (StringUtils.isNotEmpty(dto.getSCustomerName())) {
                sql.append("and D.sCustomerName =? ");
                params.add(dto.getSCustomerName());
            }
            if (StringUtils.isNotEmpty(dto.getSCardNo())) {
                sql.append("and A.sCardNo =? ");
                params.add(dto.getSCardNo());
            }
            if (StringUtils.isNotEmpty(dto.getSMaterialName())) {
                sql.append("and F.sMaterialName=? ");
                params.add(dto.getSMaterialName());
            }
        }
        if (rowNumber != null) {
            sql.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());

        return this.jdbcTemplate.query(sql.toString(), para, new BeanPropertyRowMapper(MesProductionCardListVo.class));
    }

    /**
     * 订单列表查询数量sql拼接
     *
     * @param dto
     * @return
     */
    private Integer queryOrderListTotal(MesOrderListDto dto) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlOrderListCount);
        if (dto != null) {
            if (StringUtils.isNotEmpty(dto.getSOrderNo())) {
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());
        return this.jdbcTemplate.queryForObject(sql.toString(), para, Integer.class);
    }

    /**
     * 订单列表查询sql拼接
     *
     * @param dto
     * @param pageSize
     * @param rowNumber
     * @return
     */
    private List<MesOrderListVo> queryOrderListRecordList(MesOrderListDto dto, Integer pageSize, Integer rowNumber) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();

        if (pageSize != null) {
            sql.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        sql.append(sqlOrderList);
        if (dto != null) {
            if (StringUtils.isNotEmpty(dto.getSOrderNo())) {
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
        }
        if (rowNumber != null) {
            sql.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());

        return this.jdbcTemplate.query(sql.toString(), para, new BeanPropertyRowMapper(MesOrderListVo.class));
    }
}
