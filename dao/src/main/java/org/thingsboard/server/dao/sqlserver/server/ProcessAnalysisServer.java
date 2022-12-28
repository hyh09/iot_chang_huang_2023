package org.thingsboard.server.dao.sqlserver.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.dao.sqlserver.server.vo.QueryYieIdVo;
import org.thingsboard.server.dao.sqlserver.server.vo.process.ProcessAnalysisVo;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.decimal.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project Name: thingsboard
 * @File Name: ProcessAnalysisServer
 * @Date: 2022/12/27 14:48
 * @author: wb04
 * 业务中文描述: 工序分析接口
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Service
public class ProcessAnalysisServer extends BaseRunSqlServer {


    public PageData<ProcessAnalysisVo> query(ProcessAnalysisVo vo, Pageable pageable) {
        StringBuffer sql = new StringBuffer();
        sql.append("select A.sCardNo as cardNo,A.sOrderNo as orderNo,H.sCustomerName as customerName,E.sMaterialName as materialName,F.sColorName as colorName,\n" +
                "A.nPlanOutputQty as numberOfCards,I.sRemark as sRemark,D.sWorkingProcedureName as workingProcedureName,C.nTrackQty as nTrackQty," +
                "DATEDIFF(MINUTE,I.tPlanStartTime,i.tPlanEndTime) as theoreticalTime,\n" +
                "DATEDIFF(MINUTE,B.tFactStartTime,B.tFactEndTime) as actualTime,\n" +
                "C.sWorkerGroupName as workerGroupName,B.tFactStartTime as factStartTime," +
                "B.tFactEndTime as factEndTime " +
                " FROM dbo.psWorkFlowCard A(NOLOCK)\n" +
                " JOIN dbo.ppTrackJob B(NOLOCK) ON B.upsWorkFlowCardGUID=A.uGUID\n" +
                " JOIN dbo.ppTrackOutput C(NOLOCK) ON C.uppTrackJobGUID=B.uGUID\n" +
                " JOIN dbo.pbWorkingProcedure D(NOLOCK) ON D.uGUID=B.upbWorkingProcedureGUID\n" +
                " JOIN dbo.mmMaterial E(NOLOCK) ON E.uGUID=A.ummMaterialGUID\n" +
                " JOIN dbo.tmColor F(NOLOCK)    ON F.uGUID=A.utmColorGUID\n" +
                " JOIN dbo.sdOrderHdr G(NOLOCK) ON G.sOrderNo=A.sOrderNo\n" +
                " JOIN dbo.pbCustomer H(NOLOCK) ON H.uGUID=G.upbCustomerGUID\n" +
                " LEFT JOIN dbo.psWPP I(NOLOCK) ON I.upsWorkFlowCardGUID=A.uGUID AND\n" +
                " I.upbWorkingProcedureGUID=B.upbWorkingProcedureGUID where 1=1  ");
        List list = new ArrayList();
        if (vo.getCreatedTime() != null) {
            sql.append(" and B.tFactStartTime >= ?");
            list.add(CommonUtils.longToDateTime(vo.getCreatedTime()));
        }
        if (vo.getUpdatedTime() != null) {
            sql.append(" and B.tFactEndTime < ?");
            list.add(CommonUtils.longToDateTime(vo.getUpdatedTime()));
        }
        //工序名称 workingProcedureName
        if (StringUtils.isNotEmpty(vo.getWorkingProcedureName())) {
            sql.append(" and D.sWorkingProcedureName like  ").append("\'%").append(vo.getWorkingProcedureName()).append("%\'");
        }
        //颜色
        if (StringUtils.isNotEmpty(vo.getColorName())) {
            sql.append(" and F.sColorName like ").append("\'%").append(vo.getColorName()).append("%\'");
        }
        //品名 materialName
        if (StringUtils.isNotEmpty(vo.getMaterialName())) {
            sql.append(" and E.sMaterialName  like  ").append("\'%").append(vo.getMaterialName()).append("%\'");
        }
        //客户名称
        if (StringUtils.isNotEmpty(vo.getCustomerName())) {
            sql.append(" and H.sCustomerName like  ").append("\'%").append(vo.getCustomerName()).append("%\'");
        }
        //订单编号 orderNo
        if (StringUtils.isNotEmpty(vo.getOrderNo())) {
            sql.append(" and A.sOrderNo like  ").append("\'%").append(vo.getOrderNo()).append("%\'");
        }
        //流程卡号 cardNo
        if (StringUtils.isNotEmpty(vo.getCardNo())) {
            sql.append(" and A.sCardNo like  ").append("\'%").append(vo.getCardNo()).append("%\'");
        }
        PageData<ProcessAnalysisVo> pageData = pageQuery("B.tFactStartTime", sql.toString(), list, pageable, ProcessAnalysisVo.class);
        List<ProcessAnalysisVo> processAnalysisVos = pageData.getData();
        convertData(processAnalysisVos);
       return new PageData<ProcessAnalysisVo>(processAnalysisVos, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());

    }

    private void  convertData(List<ProcessAnalysisVo> processAnalysisVos) {
        processAnalysisVos.stream().forEach(m1 -> {
            m1.setTimeoutMinutes(getTimeOut(m1));
            m1.setOverTimeRatio(getRatio(m1));
        });
    }

    private String getTimeOut(ProcessAnalysisVo m1) {
        BigDecimalUtil bigDecimalUtil = new BigDecimalUtil(1, RoundingMode.HALF_UP);
        return bigDecimalUtil.subtract(m1.getActualTime(), m1.getTheoreticalTime()).toPlainString();
    }

    private String getRatio(ProcessAnalysisVo m1) {
        BigDecimalUtil bigDecimalUtil = new BigDecimalUtil(4, RoundingMode.HALF_UP);
        BigDecimal  bigDecimal = bigDecimalUtil.subtract(m1.getActualTime(), m1.getTheoreticalTime());
        BigDecimal  multiplicationResult = bigDecimalUtil.divide(bigDecimal,m1.getActualTime());
       return BigDecimalUtil.INSTANCE.multiply(multiplicationResult,"100").toPlainString();
    }


}
