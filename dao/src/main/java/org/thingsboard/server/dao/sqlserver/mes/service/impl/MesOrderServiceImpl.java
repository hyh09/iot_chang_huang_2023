package org.thingsboard.server.dao.sqlserver.mes.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesOrderListDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesOrderProgressListDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionCardListDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionProgressListDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesOrderListVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesOrderProgressListVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionCardListVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionProgressListVo;
import org.thingsboard.server.dao.sqlserver.mes.service.MesOrderService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MesOrderServiceImpl implements MesOrderService {
    @Resource(name = "sqlServerTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询订单列表
     */
    private String sqlOrderListCount = "SELECT count(1) "+
            " FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            " WHERE 1=1 ";
    private String sqlOrderList = " SELECT row_number () OVER (ORDER BY A.tCreateTime  DESC) AS rownumber ," +
            "A.sOrderNo,A.sCreator,A.tCreateTime FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            " WHERE 1=1  " ;


    /**
     * 查询订单进度
     */
    private String sqlOrderProgressList = " SELECT row_number () OVER (ORDER BY A.tCreateTime  DESC) AS rownumber ," +
            " A.sOrderNo,D.sCustomerName,C.dDeliveryDate,E.sMaterialName,F.sColorName,B.nQty,B.sFinishingMethod " +
            "FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            "JOIN dbo.pbCustomer D(NOLOCK)ON D.uGUID=A.upbCustomerGUID " +
            "JOIN dbo.mmMaterial E(NOLOCK)ON E.uGUID=B.ummMaterialGUID " +
            "JOIN dbo.tmColor F(NOLOCK)ON F.uGUID=B.utmColorGUID" +
            " WHERE 1=1  " ;
    private String sqlOrderProgressListCount = "SELECT count(1) "+
            "FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            "JOIN dbo.pbCustomer D(NOLOCK)ON D.uGUID=A.upbCustomerGUID " +
            "JOIN dbo.mmMaterial E(NOLOCK)ON E.uGUID=B.ummMaterialGUID " +
            "JOIN dbo.tmColor F(NOLOCK)ON F.uGUID=B.utmColorGUID" +
            " WHERE 1=1  " ;

    private String sqlProductionCard =" SELECT row_number () OVER (ORDER BY B.dDeliveryDat ASC) AS rownumber ," +
            " A.sCardNo,A.sOrderNo,B.dDeliveryDate,E.sCustomerName,F.sMaterialName,C.sColorName,C.sFinishingMethod,A.nPlanOutputQty,H.sWorkingProcedureName,J.sWorkingProcedureName " +
            "FROM dbo.psWorkFlowCard A(NOLOCK) " +
            "JOIN dbo.sdOrderLot B(NOLOCK)ON B.uGUID=A.usdOrderLotGUID " +
            "JOIN dbo.sdOrderDtl C(NOLOCK)ON C.uGUID=B.usdOrderDtlGUID " +
            "JOIN dbo.sdOrderHdr D(NOLOCK)ON D.uGUID=C.usdOrderHdrGUID " +
            "JOIN dbo.pbCustomer E(NOLOCK)ON E.uGUID=D.upbCustomerGUID " +
            "JOIN dbo.mmMaterial F(NOLOCK)ON F.uGUID=A.ummMaterialGUID " +
            "JOIN dbo.ppTrackJob G(NOLOCK)ON G.upsWorkFlowCardGUID=A.uGUID AND G.bIsCurrent=1 " +
            "JOIN dbo.pbWorkingProcedure H(NOLOCK)ON H.uGUID=G.upbWorkingProcedureGUID " +
            "LEFT JOIN dbo.ppTrackJob I(NOLOCK)ON I.upsWorkFlowCardGUID=A.uGUID AND I.iOrderProcedure=G.iOrderProcedure+1 " +
            "LEFT JOIN dbo.pbWorkingProcedure J(NOLOCK)ON J.uGUID=I.upbWorkingProcedureGUID "+
            " WHERE 1=1  " ;
    private String sqlProductionCardCount ="SELECT count(1) "+
            "FROM dbo.psWorkFlowCard A(NOLOCK) " +
            "JOIN dbo.sdOrderLot B(NOLOCK)ON B.uGUID=A.usdOrderLotGUID " +
            "JOIN dbo.sdOrderDtl C(NOLOCK)ON C.uGUID=B.usdOrderDtlGUID " +
            "JOIN dbo.sdOrderHdr D(NOLOCK)ON D.uGUID=C.usdOrderHdrGUID " +
            "JOIN dbo.pbCustomer E(NOLOCK)ON E.uGUID=D.upbCustomerGUID " +
            "JOIN dbo.mmMaterial F(NOLOCK)ON F.uGUID=A.ummMaterialGUID " +
            "JOIN dbo.ppTrackJob G(NOLOCK)ON G.upsWorkFlowCardGUID=A.uGUID AND G.bIsCurrent=1 " +
            "JOIN dbo.pbWorkingProcedure H(NOLOCK)ON H.uGUID=G.upbWorkingProcedureGUID " +
            "LEFT JOIN dbo.ppTrackJob I(NOLOCK)ON I.upsWorkFlowCardGUID=A.uGUID AND I.iOrderProcedure=G.iOrderProcedure+1 " +
            "LEFT JOIN dbo.pbWorkingProcedure J(NOLOCK)ON J.uGUID=I.upbWorkingProcedureGUID "+
            " WHERE 1=1  " ;
    @Override
    public PageData<MesOrderListVo> findOrderList(MesOrderListDto dto, PageLink pageLink) {
        try {
            int rowNumber = (pageLink.getPage() - 1) * pageLink.getPageSize();
            //queryTotal方法统计总数
            int total = this.queryOrderListTotal(dto);
            //queryRecordList方法查询并转换实体类List
            List<MesOrderListVo> recordList = this.queryOrderListRecordList(dto,pageLink.getPageSize(),rowNumber);
            PageData<MesOrderListVo> resultPage = new PageData<>();
            resultPage = new PageData<MesOrderListVo>(recordList,total/pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
            return resultPage;
        } catch (Exception e) {
            log.error("异常信息{}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageData<MesOrderProgressListVo> findOrderProgressList(MesOrderProgressListDto dto, PageLink pageLink) {
        try {
            int rowNumber = (pageLink.getPage() - 1) * pageLink.getPageSize();
            //queryTotal方法统计总数
            int total = this.queryOrderProgressListTotal(dto);
            //queryRecordList方法查询并转换实体类List
            List<MesOrderProgressListVo> recordList = this.queryOrderProgressListRecordList(dto,pageLink.getPageSize(),rowNumber);
            PageData<MesOrderProgressListVo> resultPage = new PageData<>();
            resultPage = new PageData<MesOrderProgressListVo>(recordList,total/pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
            return resultPage;
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
            List<MesProductionCardListVo> recordList = this.queryProductionCardListRecordList(dto,pageLink.getPageSize(),rowNumber);
            PageData<MesProductionCardListVo> resultPage = new PageData<>();
            resultPage = new PageData<MesProductionCardListVo>(recordList,total/pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
            return resultPage;
        } catch (Exception e) {
            log.error("异常信息{}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageData<MesProductionProgressListVo> findProductionProgressList(MesProductionProgressListDto dto, PageLink pageLink) {
        return null;
    }

    /**
     * 订单进度列表查询数量sql拼接
     * @param dto
     * @return
     */
    private Integer queryProductionCardListTotal(MesProductionCardListDto dto) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlProductionCardCount);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getDDeliveryDateBegin())){
                sql.append("and B.dDeliveryDate >=? ");
                params.add(dto.getDDeliveryDateBegin());
            }
            if(StringUtils.isNotEmpty(dto.getDDeliveryDateEnd())){
                sql.append("and B.dDeliveryDate <=? ");
                params.add(dto.getDDeliveryDateEnd());
            }
            if(StringUtils.isNotEmpty(dto.getSOrderNo())){
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
            if(StringUtils.isNotEmpty(dto.getSColorName())){
                sql.append("and F.sColorName =? ");
                params.add(dto.getSColorName());
            }
            if(StringUtils.isNotEmpty(dto.getSCustomerName())){
                sql.append("and D.sCustomerName =? ");
                params.add(dto.getSCustomerName());
            }
            if(StringUtils.isNotEmpty(dto.getSCardNo())){
                sql.append("and A.sCardNo =? ");
                params.add(dto.getSCardNo());
            }
            if(StringUtils.isNotEmpty(dto.getSMaterialName())){
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
     * @param dto
     * @param pageSize
     * @param rowNumber
     * @return
     */
    private List<MesProductionCardListVo> queryProductionCardListRecordList(MesProductionCardListDto dto, Integer pageSize, Integer rowNumber) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();

        if(pageSize != null){
            sql.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        sql.append(sqlProductionCard);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getDDeliveryDateBegin())){
                sql.append("and B.dDeliveryDate >=? ");
                params.add(dto.getDDeliveryDateBegin());
            }
            if(StringUtils.isNotEmpty(dto.getDDeliveryDateEnd())){
                sql.append("and B.dDeliveryDate <=? ");
                params.add(dto.getDDeliveryDateEnd());
            }
            if(StringUtils.isNotEmpty(dto.getSOrderNo())){
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
            if(StringUtils.isNotEmpty(dto.getSColorName())){
                sql.append("and F.sColorName =? ");
                params.add(dto.getSColorName());
            }
            if(StringUtils.isNotEmpty(dto.getSCustomerName())){
                sql.append("and D.sCustomerName =? ");
                params.add(dto.getSCustomerName());
            }
            if(StringUtils.isNotEmpty(dto.getSCardNo())){
                sql.append("and A.sCardNo =? ");
                params.add(dto.getSCardNo());
            }
            if(StringUtils.isNotEmpty(dto.getSMaterialName())){
                sql.append("and F.sMaterialName=? ");
                params.add(dto.getSMaterialName());
            }
        }
        if(rowNumber != null){
            sql.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());

        return this.jdbcTemplate.query(sql.toString(), para, new BeanPropertyRowMapper(MesProductionCardListVo.class));
    }

    /**
     * 订单进度列表查询数量sql拼接
     * @param dto
     * @return
     */
    private Integer queryOrderProgressListTotal(MesOrderProgressListDto dto) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlOrderProgressListCount);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getDDeliveryDateBegin())){
                sql.append("and C.dDeliveryDate >=? ");
                params.add(dto.getDDeliveryDateBegin());
            }
            if(StringUtils.isNotEmpty(dto.getDDeliveryDateEnd())){
                sql.append("and C.dDeliveryDate <=? ");
                params.add(dto.getDDeliveryDateEnd());
            }
            if(StringUtils.isNotEmpty(dto.getSOrderNo())){
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
            if(StringUtils.isNotEmpty(dto.getSCustomerName())){
                sql.append("and D.sCustomerName =? ");
                params.add(dto.getSCustomerName());
            }
            if(StringUtils.isNotEmpty(dto.getSColorName())){
                sql.append("and F.sColorName =? ");
                params.add(dto.getSColorName());
            }
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());
        return this.jdbcTemplate.queryForObject(sql.toString(), para, Integer.class);
    }

    /**
     * 订单进度列表查询sql拼接
     * @param dto
     * @param pageSize
     * @param rowNumber
     * @return
     */
    private List<MesOrderProgressListVo> queryOrderProgressListRecordList(MesOrderProgressListDto dto, Integer pageSize, Integer rowNumber) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();

        if(pageSize != null){
            sql.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        sql.append(sqlOrderProgressList);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getDDeliveryDateBegin())){
                sql.append("and C.dDeliveryDate >=? ");
                params.add(dto.getDDeliveryDateBegin());
            }
            if(StringUtils.isNotEmpty(dto.getDDeliveryDateEnd())){
                sql.append("and C.dDeliveryDate <=? ");
                params.add(dto.getDDeliveryDateEnd());
            }
            if(StringUtils.isNotEmpty(dto.getSOrderNo())){
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
            if(StringUtils.isNotEmpty(dto.getSCustomerName())){
                sql.append("and D.sCustomerName =? ");
                params.add(dto.getSCustomerName());
            }
            if(StringUtils.isNotEmpty(dto.getSColorName())){
                sql.append("and F.sColorName =? ");
                params.add(dto.getSColorName());
            }
        }
        if(rowNumber != null){
            sql.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());

        return this.jdbcTemplate.query(sql.toString(), para, new BeanPropertyRowMapper(MesOrderProgressListVo.class));
    }

    /**
     * 订单列表查询数量sql拼接
     * @param dto
     * @return
     */
    private Integer queryOrderListTotal(MesOrderListDto dto) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlOrderListCount);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getSOrderNo())){
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
     * @param dto
     * @param pageSize
     * @param rowNumber
     * @return
     */
    private List<MesOrderListVo> queryOrderListRecordList(MesOrderListDto dto, Integer pageSize, Integer rowNumber) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();

        if(pageSize != null){
            sql.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        sql.append(sqlOrderList);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getSOrderNo())){
                sql.append("and A.sOrderNo =? ");
                params.add(dto.getSOrderNo());
            }
        }
        if(rowNumber != null){
            sql.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());

        return this.jdbcTemplate.query(sql.toString(), para, new BeanPropertyRowMapper(MesOrderListVo.class));
    }
}
