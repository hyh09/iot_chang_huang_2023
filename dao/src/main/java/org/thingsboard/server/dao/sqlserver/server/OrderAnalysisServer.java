package org.thingsboard.server.dao.sqlserver.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.dao.sqlserver.server.vo.QueryYieIdVo;
import org.thingsboard.server.dao.sqlserver.server.vo.order.HwEnergyEnums;
import org.thingsboard.server.dao.sqlserver.server.vo.order.HwEnergyVo;
import org.thingsboard.server.dao.sqlserver.server.vo.order.OrderAnalysisVo;
import org.thingsboard.server.dao.sqlserver.server.vo.process.ProcessAnalysisVo;
import org.thingsboard.server.dao.util.CommonUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project Name: thingsboard
 * @File Name: OrderAnalysisServer
 * @Date: 2022/12/28 14:11
 * @author: wb04
 * 业务中文描述:订单分析--效能分析
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Service
public class OrderAnalysisServer extends BaseRunSqlServer {


    public PageData<OrderAnalysisVo> queryPage(OrderAnalysisVo vo, Pageable pageable) {
        StringBuffer sql = new StringBuffer();
        List list = new ArrayList();
        sql.append("select A.sOrderNo as orderNo,B.uGUID as uGuid,C.sCustomerName as customerName,D.sMaterialName as materialName,E.sColorName as colorName,B.nQty as numberOfOrder,\n" +
                "A.sCreator as creator,A.tCreateTime as factStartTime,\n" +
                "( SELECT iCount=COUNT(1)\n" +
                "FROM dbo.psWorkFlowCard A1(NOLOCK)\n" +
                "JOIN dbo.sdOrderLot B1(NOLOCK) ON B1.uGUID=A1.usdOrderLotGUID where  usdOrderDtlGUID= B.uGUID\n" +
                "GROUP BY B1.usdOrderDtlGUID) as numberOfCards " +
                " from  dbo.sdOrderHdr A(NOLOCK)\n" +
                " JOIN dbo.sdOrderDtl B(NOLOCK)ON B.usdOrderHdrGUID=A.uGUID\n" +
                " JOIN dbo.pbCustomer C(NOLOCK)ON C.uGUID=A.upbCustomerGUID\n" +
                " JOIN dbo.mmMaterial D(NOLOCK) ON D.uGUID=B.ummMaterialGUID\n" +
                " JOIN dbo.tmColor E(NOLOCK) ON E.uGUID=B.utmColorGUID where 1=1 ");
        if (vo.getCreatedTime() != null) {
            sql.append(" and A.tCreateTime >= ?");
            list.add(CommonUtils.longToDateTime(vo.getCreatedTime()));
        }
        if (vo.getUpdatedTime() != null) {
            sql.append(" and A.tCreateTime < ?");
            list.add(CommonUtils.longToDateTime(vo.getUpdatedTime()));
        }
        if (StringUtils.isNotEmpty(vo.getOrderNo())) {
            sql.append(" and A.sOrderNo like  ").append("\'%").append(vo.getOrderNo()).append("%\'");
        }
        if (StringUtils.isNotEmpty(vo.getColorName())) {
            sql.append(" and E.sColorName like  ").append("\'%").append(vo.getColorName()).append("%\'");
        }
        if (StringUtils.isNotEmpty(vo.getCustomerName())) {
            sql.append(" and C.sCustomerName like  ").append("\'%").append(vo.getCustomerName()).append("%\'");
        }
        //品名
        if (StringUtils.isNotEmpty(vo.getMaterialName())) {
            sql.append(" and D.sMaterialName like  ").append("\'%").append(vo.getMaterialName()).append("%\'");
        }
        //卡片号的查询
        PageData<OrderAnalysisVo> pageData = pageQuery02("A.tCreateTime", sql.toString(), list, pageable, OrderAnalysisVo.class);
        List<OrderAnalysisVo> processAnalysisVoList = pageData.getData();
        convert(processAnalysisVoList);
        return new PageData<OrderAnalysisVo>(processAnalysisVoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());

    }


    private void convert(List<OrderAnalysisVo> processAnalysisVoList) {
        if (CollectionUtils.isEmpty(processAnalysisVoList)) {
            return;
        }
        List<String> idList = processAnalysisVoList.stream().filter(m1 -> StringUtils.isNotEmpty(m1.getUGuid())).map(OrderAnalysisVo::getUGuid).collect(Collectors.toList());
        List<HwEnergyVo> hwEnergyVoList = queryWaterAndElectricity(idList);
        processAnalysisVoList.stream().forEach(m1 -> {
            m1.setCreatedTime(CommonUtils.getTimestampOfDateTime(m1.getFactStartTime()));
            m1.setWater(filterOutData(hwEnergyVoList, HwEnergyEnums.WATER,m1.getUGuid()));
            m1.setElectricity(filterOutData(hwEnergyVoList, HwEnergyEnums.ELECTRICITY,m1.getUGuid()));
            m1.setGas(filterOutData(hwEnergyVoList, HwEnergyEnums.GAS,m1.getUGuid()));

        });
    }


    /**
     * 查询mes 的水电气的数据
     *
     * @param uGUIDList
     * @return
     */
    public List<HwEnergyVo> queryWaterAndElectricity(List<String> uGUIDList) {
        if (CollectionUtils.isEmpty(uGUIDList)) {
            return new ArrayList<>();
        }
        String sql = "SELECT C2.usdOrderDtlGUID as usdOrderDtlGUID,F2.sName as name,SUM(ISNULL(E2.nUseValue,0)) as useValue\n" +
                "FROM dbo.mnProducted A2(NOLOCK)\n" +
                "JOIN dbo.psWorkFlowCard B2(NOLOCK) ON B2.sCardNo=A2.sCardNo\n" +
                "JOIN dbo.sdOrderLot C2(NOLOCK) ON C2.uGUID=B2.usdOrderLotGUID\n" +
                "JOIN dbo.mnProductedExpend E2(NOLOCK) ON E2.umnProductedGUID=A2.uGUID\n" +
                "JOIN dbo.hwEnergy F2(NOLOCK) ON F2.sCode=E2.sEngCode  where C2.usdOrderDtlGUID in (:uGUIDList)" +
                "GROUP BY C2.usdOrderDtlGUID,F2.sName ";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("uGUIDList", uGUIDList);
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<HwEnergyVo> data = givenParamJdbcTemp.query(sql, parameters, new BeanPropertyRowMapper<>(HwEnergyVo.class));
        return data;

    }


    private String filterOutData(List<HwEnergyVo> hwEnergyVoList,HwEnergyEnums enums,String uGuid ){
        String value= hwEnergyVoList.stream().filter(m1->m1.getName().equals(enums.getChineseField()))
                .filter(m1->m1.getUsdOrderDtlGUID().equals(uGuid))
                .findFirst()
                .map(HwEnergyVo::getUseValue)
                .orElse(null);
        return value;
    }

}
