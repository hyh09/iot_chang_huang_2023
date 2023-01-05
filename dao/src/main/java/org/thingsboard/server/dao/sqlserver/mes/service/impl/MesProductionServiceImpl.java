package org.thingsboard.server.dao.sqlserver.mes.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionMonitorDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionPlanDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionWorkDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionMonitorVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionPlanVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionWorkVo;
import org.thingsboard.server.dao.sqlserver.mes.service.MesProductionService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MesProductionServiceImpl implements MesProductionService {

    @Resource(name = "sqlServerTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询生产排班
     */
    private String sqlPlanCount = "SELECT count(1) "+
            " FROM dbo.psWorkFlowCard A(NOLOCK)" +
            " JOIN dbo.ppTrackJob B ( NOLOCK ) ON B.upsWorkFlowCardGUID= A.uGUID " +
            " JOIN dbo.ppTrackOutput C ( NOLOCK ) ON C.uppTrackJobGUID= B.uGUID " +
            " JOIN dbo.pbWorkingProcedure D ( NOLOCK ) ON D.uGUID= B.upbWorkingProcedureGUID " +
            " LEFT JOIN dbo.psWPP E ( NOLOCK ) ON E.upsWorkFlowCardGUID= A.uGUID AND E.upbWorkingProcedureGUID= B.upbWorkingProcedureGUID " +
            " WHERE 1=1 ";
    private String sqlPlan = " SELECT row_number () OVER (ORDER BY C.tTrackTime DESC) AS rownumber ," +
            "CONVERT(NVARCHAR(10),C.tTrackTime,120) as tTrackTime,C.sWorkerGroupName,C.sWorkerNameList,D.sWorkingProcedureName,A.sOrderNo,A.sCardNo,CONVERT(NVARCHAR(10)," +
            "E.tPlanEndTime,120) as tPlanEndTime,A.nPlanOutputQty,C.nTrackQty,CASE WHEN E.tPlanEndTime IS NULL OR C.tTrackTime<=E.tPlanEndTime THEN NULL ELSE " +
            "dbo.fnMESGetDiffTimeStr(E.tPlanEndTime,C.tTrackTime)END as timeout" +
            " FROM dbo.psWorkFlowCard A(NOLOCK)" +
            " JOIN dbo.ppTrackJob B ( NOLOCK ) ON B.upsWorkFlowCardGUID= A.uGUID " +
            " JOIN dbo.ppTrackOutput C ( NOLOCK ) ON C.uppTrackJobGUID= B.uGUID " +
            " JOIN dbo.pbWorkingProcedure D ( NOLOCK ) ON D.uGUID= B.upbWorkingProcedureGUID " +
            " LEFT JOIN dbo.psWPP E ( NOLOCK ) ON E.upsWorkFlowCardGUID= A.uGUID AND E.upbWorkingProcedureGUID= B.upbWorkingProcedureGUID " +
            " WHERE 1=1  " ;

    /**
     * 查询生产报工
     */
    private String sqlWorkCount = "SELECT count(1) "+
            "FROM dbo.psWorkFlowCard A(NOLOCK) " +
            "JOIN dbo.ppTrackJob B(NOLOCK) ON B.upsWorkFlowCardGUID=A.uGUID " +
            "JOIN dbo.ppTrackOutput C(NOLOCK)ON C.uppTrackJobGUID=B.uGUID " +
            "JOIN dbo.tmColor D(NOLOCK)ON D.uGUID=A.utmColorGUID " +
            "JOIN dbo.emEquipment E(NOLOCK)ON E.uGUID=C.uemEquipmentGUID " +
            " WHERE 1=1 ";
    private String sqlWork = " SELECT row_number () OVER (ORDER BY B.tFactStartTime DESC) AS rownumber ," +
            "A.sOrderNo,A.sCardNo,D.sColorNo,B.tFactStartTime,B.tFactEndTime,dbo.fnMESGetDiffTimeStr(B.tFactStartTime,B.tFactEndTime) as fnMESGetDiffTimeStr, " +
            "C.nTrackQty,C.sWorkerGroupName,E.sEquipmentName " +
            "FROM dbo.psWorkFlowCard A(NOLOCK) " +
            "JOIN dbo.ppTrackJob B(NOLOCK) ON B.upsWorkFlowCardGUID=A.uGUID " +
            "JOIN dbo.ppTrackOutput C(NOLOCK)ON C.uppTrackJobGUID=B.uGUID " +
            "JOIN dbo.tmColor D(NOLOCK)ON D.uGUID=A.utmColorGUID " +
            "JOIN dbo.emEquipment E(NOLOCK)ON E.uGUID=C.uemEquipmentGUID " +
            " WHERE 1=1  " ;

    /**
     * 查询生产监控
     */
    private String sqlMonitorCount = "SELECT count(1) "+
            "FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            "JOIN dbo.psWorkFlowCard D(NOLOCK)ON D.usdOrderLotGUID=C.uGUID " +
            "JOIN dbo.pbCustomer E(NOLOCK)ON E.uGUID=A.upbCustomerGUID " +
            "JOIN dbo.mmMaterial F(NOLOCK)ON F.uGUID=D.ummMaterialGUID " +
            "JOIN dbo.tmColor G(NOLOCK)ON G.uGUID=D.utmColorGUID " +
            "JOIN( " +
            "SELECT A1.upsWorkFlowCardGUID,B1.sWorkingProcedureName,A1.tFactStartTime " +
            "FROM dbo.ppTrackJob A1(NOLOCK) " +
            "JOIN dbo.pbWorkingProcedure B1(NOLOCK)ON B1.uGUID=A1.upbWorkingProcedureGUID " +
            "WHERE A1.bIsCurrent=1 " +
            ")H ON H.upsWorkFlowCardGUID=D.uGUID " +
            "LEFT JOIN( " +
            "SELECT B2.upsWorkFlowCardGUID,A2.sWorkingProcedureName,B2.tFactEndTime " +
            "FROM dbo.pbWorkingProcedure A2(NOLOCK) " +
            "JOIN( " +
            "SELECT iIndex=ROW_NUMBER() OVER(PARTITION BY A3.upsWorkFlowCardGUID ORDER BY A3.iOrderProcedure DESC),A3.upsWorkFlowCardGUID,A3.upbWorkingProcedureGUID,A3.tFactEndTime " +
            "FROM dbo.ppTrackJob A3(NOLOCK) " +
            "WHERE A3.bIsComplete=1 " +
            ")B2 ON B2.upbWorkingProcedureGUID=A2.uGUID AND B2.iIndex=1 " +
            ")I ON I.upsWorkFlowCardGUID=D.uGUID " +
            " WHERE 1=1 ";
    private String sqlMonitor = " SELECT row_number () OVER (ORDER BY C.dDeliveryDate DESC) AS rownumber ," +
            "D.sCardNo,A.sOrderNo,E.sCustomerName,C.dDeliveryDate,F.sMaterialName,G.sColorName,D.nPlanOutputQty,I.sWorkingProcedureName as sWorkingProcedureNameFinish,H.sWorkingProcedureName, " +
            "CASE WHEN H.tFactStartTime IS NULL THEN dbo.fnMESGetDiffTimeStr(I.tFactEndTime,GETDATE())ELSE NULL END as fnMESGetDiffTimeStr " +
            "FROM dbo.sdOrderHdr A(NOLOCK) " +
            "JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID " +
            "JOIN dbo.sdOrderLot C(NOLOCK)ON C.usdOrderDtlGUID=B.uGUID " +
            "JOIN dbo.psWorkFlowCard D(NOLOCK)ON D.usdOrderLotGUID=C.uGUID " +
            "JOIN dbo.pbCustomer E(NOLOCK)ON E.uGUID=A.upbCustomerGUID " +
            "JOIN dbo.mmMaterial F(NOLOCK)ON F.uGUID=D.ummMaterialGUID " +
            "JOIN dbo.tmColor G(NOLOCK)ON G.uGUID=D.utmColorGUID " +
            "JOIN( " +
            "SELECT A1.upsWorkFlowCardGUID,B1.sWorkingProcedureName,A1.tFactStartTime " +
            "FROM dbo.ppTrackJob A1(NOLOCK) " +
            "JOIN dbo.pbWorkingProcedure B1(NOLOCK)ON B1.uGUID=A1.upbWorkingProcedureGUID " +
            "WHERE A1.bIsCurrent=1 " +
            ")H ON H.upsWorkFlowCardGUID=D.uGUID " +
            "LEFT JOIN( " +
            "SELECT B2.upsWorkFlowCardGUID,A2.sWorkingProcedureName,B2.tFactEndTime " +
            "FROM dbo.pbWorkingProcedure A2(NOLOCK) " +
            "JOIN( " +
            "SELECT iIndex=ROW_NUMBER() OVER(PARTITION BY A3.upsWorkFlowCardGUID ORDER BY A3.iOrderProcedure DESC),A3.upsWorkFlowCardGUID,A3.upbWorkingProcedureGUID,A3.tFactEndTime " +
            "FROM dbo.ppTrackJob A3(NOLOCK) " +
            "WHERE A3.bIsComplete=1 " +
            ")B2 ON B2.upbWorkingProcedureGUID=A2.uGUID AND B2.iIndex=1 " +
            ")I ON I.upsWorkFlowCardGUID=D.uGUID " +
            " WHERE 1=1  " ;

    /**
     * 查询所有工序名称
     */
    private String sqlWorkingProcedureName = "SELECT A.sWorkingProcedureName FROM pbWorkingProcedure A ";



    /**
     * 查询生产班组列表
     * @param dto
     * @param pageLink
     * @return
     */
    @Override
    public PageData<MesProductionPlanVo> findPlanList(MesProductionPlanDto dto, PageLink pageLink) {
        try {
            int rowNumber = (pageLink.getPage() - 1) * pageLink.getPageSize();
            //queryTotal方法统计总数
            int total = queryPlanTotal(dto);
            //queryRecordList方法查询并转换实体类List
            List<MesProductionPlanVo> recordList = queryPlanRecordList(dto,pageLink.getPageSize(),rowNumber);
            PageData<MesProductionPlanVo> resultPage = new PageData<>();
            resultPage = new PageData<MesProductionPlanVo>(recordList,total/pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
            return resultPage;
        } catch (Exception e) {
            log.error("异常信息{}", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * 查询生产报工列表
     * @param dto
     * @param pageLink
     * @return
     */
    @Override
    public PageData<MesProductionWorkVo> findWorkList(MesProductionWorkDto dto, PageLink pageLink) {
        try {
            int rowNumber = (pageLink.getPage() - 1) * pageLink.getPageSize();
            //queryTotal方法统计总数
            int total = this.queryWorkTotal(dto);
            //queryRecordList方法查询并转换实体类List
            List<MesProductionWorkVo> recordList = this.queryWorkRecordList(dto,pageLink.getPageSize(),rowNumber);
            PageData<MesProductionWorkVo> resultPage = new PageData<>();
            resultPage = new PageData<MesProductionWorkVo>(recordList,total/pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
            return resultPage;
        } catch (Exception e) {
            log.error("异常信息{}", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询生产监控列表
     * @param dto
     * @param pageLink
     * @return
     */
    @Override
    public PageData<MesProductionMonitorVo> findMonitorList(MesProductionMonitorDto dto, PageLink pageLink) {
        try {
            int rowNumber = (pageLink.getPage() - 1) * pageLink.getPageSize();
            //queryTotal方法统计总数
            int total = this.queryMonitorTotal(dto);
            //queryRecordList方法查询并转换实体类List
            List<MesProductionMonitorVo> recordList = this.queryMonitorRecordList(dto,pageLink.getPageSize(),rowNumber);
            PageData<MesProductionMonitorVo> resultPage = new PageData<>();
            resultPage = new PageData<MesProductionMonitorVo>(recordList,total/pageLink.getPageSize(), total, CollectionUtils.isNotEmpty(recordList));
            return resultPage;
        } catch (Exception e) {
            log.error("异常信息{}", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询工序名称下拉
     * @return
     */
    @Override
    public List<String> findWorkingProcedureNameList() {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlWorkingProcedureName);
        Object[] para = params.toArray(new Object[params.size()]);
        log.info(">>>>>>>>>sql.toString()" + sql.toString());
        return this.jdbcTemplate.queryForList(sql.toString(), String.class);
    }

    /**
     * 生产监控查询sql拼接
     * @param dto
     * @param pageSize
     * @param rowNumber
     * @return
     */
    private List<MesProductionMonitorVo> queryMonitorRecordList(MesProductionMonitorDto dto, Integer pageSize, Integer rowNumber) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();

        if(pageSize != null){
            sql.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        sql.append(sqlMonitor);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getSWorkingProcedureName())){
                sql.append("and I.sWorkingProcedureName =? ");
                params.add(dto.getSWorkingProcedureName());
            }
        }
        if(rowNumber != null){
            sql.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());

        return this.jdbcTemplate.query(sql.toString(), para, new BeanPropertyRowMapper(MesProductionMonitorVo.class));
    }

    /**
     * 生产监控查询数量sql拼接
     * @param dto
     * @return
     */
    private Integer queryMonitorTotal(MesProductionMonitorDto dto) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlMonitorCount);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getSWorkingProcedureName())){
                sql.append("and I.sWorkingProcedureName =? ");
                params.add(dto.getSWorkingProcedureName());
            }
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());
        return this.jdbcTemplate.queryForObject(sql.toString(), para, Integer.class);
    }

    /**
     * 生产报工查询sql拼接
     * @param dto
     * @param pageSize
     * @param rowNumber
     * @return
     */
    private List<MesProductionWorkVo> queryWorkRecordList(MesProductionWorkDto dto, Integer pageSize, Integer rowNumber) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();

        if(pageSize != null){
            sql.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        sql.append(sqlWork);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getTFactStartTime())){
                sql.append("and B.tFactStartTime >= ? ");
                params.add(dto.getTFactStartTime());
            }

            if(StringUtils.isNotEmpty(dto.getTFactEndTime())){
                sql.append("and B.tFactEndTime <=? ");
                params.add(dto.getTFactEndTime());
            }

            if(StringUtils.isNotEmpty(dto.getSOrderNo())){
                sql.append("and A.sOrderNo =?");
                params.add(dto.getSOrderNo());
            }
        }
        if(rowNumber != null){
            sql.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());

        return this.jdbcTemplate.query(sql.toString(), para, new BeanPropertyRowMapper(MesProductionWorkVo.class));
    }

    /**
     * 生产报工查询数量sql拼接
     * @param dto
     * @return
     */
    private Integer queryWorkTotal(MesProductionWorkDto dto) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlWorkCount);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getTFactStartTime())){
                sql.append("and B.tFactStartTime >= ? ");
                params.add(dto.getTFactStartTime());
            }

            if(StringUtils.isNotEmpty(dto.getTFactEndTime())){
                sql.append("and B.tFactEndTime <=? ");
                params.add(dto.getTFactEndTime());
            }

            if(StringUtils.isNotEmpty(dto.getSOrderNo())){
                sql.append("and A.sOrderNo =?");
                params.add(dto.getSOrderNo());
            }
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());
        return this.jdbcTemplate.queryForObject(sql.toString(), para, Integer.class);
    }


    /**
     * 生产排班查询sql拼接
     * @param dto
     * @param pageSize
     * @param rowNumber
     * @return
     */
    private List<MesProductionPlanVo> queryPlanRecordList(MesProductionPlanDto dto, Integer pageSize, Integer rowNumber) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();

        if(pageSize != null){
            sql.append("SELECT TOP(?) * FROM ( ");
            params.add(pageSize);
        }
        sql.append(sqlPlan);

        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getTTrackTimeStart())){
                sql.append("and CONVERT(NVARCHAR(10),C.tTrackTime,120) >= ? ");
                params.add(dto.getTTrackTimeStart());
            }
            if(StringUtils.isNotEmpty(dto.getTTrackTimeEnd())){
                sql.append("and CONVERT(NVARCHAR(10),C.tTrackTime,120) <= ? ");
                params.add(dto.getTTrackTimeEnd());
            }

            if(StringUtils.isNotEmpty(dto.getSWorkerGroupName())){
                sql.append("and C.sWorkerGroupName =? ");
                params.add(dto.getSWorkerGroupName());
            }

            if(StringUtils.isNotEmpty(dto.getSWorkingProcedureName())){
                sql.append("and D.sWorkingProcedureName =? ");
                params.add(dto.getSWorkingProcedureName());
            }
        }
        if(rowNumber != null){
            sql.append(" )temp_row where rownumber >? ");
            params.add(rowNumber);
        }

        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());

        return this.jdbcTemplate.query(sql.toString(), para, new BeanPropertyRowMapper(MesProductionPlanVo.class));
    }

    /**
     * 生产排班查询sql拼接
     * @param dto
     * @return
     */
    private Integer queryPlanTotal(MesProductionPlanDto dto) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(sqlPlanCount);
        if(dto != null){
            if(StringUtils.isNotEmpty(dto.getTTrackTimeStart())){
                sql.append("and CONVERT(NVARCHAR(10),C.tTrackTime,120) >= ? ");
                params.add(dto.getTTrackTimeStart());
            }
            if(StringUtils.isNotEmpty(dto.getTTrackTimeEnd())){
                sql.append("and CONVERT(NVARCHAR(10),C.tTrackTime,120) <= ? ");
                params.add(dto.getTTrackTimeEnd());
            }
            if(StringUtils.isNotEmpty(dto.getSWorkerGroupName())){
                sql.append("and C.sWorkerGroupName =? ");
                params.add(dto.getSWorkerGroupName());
            }

            if(StringUtils.isNotEmpty(dto.getSWorkingProcedureName())){
                sql.append("and D.sWorkingProcedureName =? ");
                params.add(dto.getSWorkingProcedureName());
            }
        }


        Object[] para = params.toArray(new Object[params.size()]);

        log.info(">>>>>>>>>sql.toString()" + sql.toString());
        return this.jdbcTemplate.queryForObject(sql.toString(), para, Integer.class);
    }

}
