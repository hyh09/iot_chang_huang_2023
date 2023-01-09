package org.thingsboard.server.dao.sqlserver.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.dao.sqlserver.server.vo.order.*;
import org.thingsboard.server.dao.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;
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
            sql.append(" and A.tCreateTime >= ? ");
            list.add(CommonUtils.longToDateTime(vo.getCreatedTime()));
        }
        if (vo.getUpdatedTime() != null) {
            sql.append(" and A.tCreateTime < ? ");
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
        if (StringUtils.isNotEmpty(vo.getSCardNo())) {
            sql.append("  and  B.uGUID in (  SELECT B1.usdOrderDtlGUID\n" +
                    "FROM dbo.psWorkFlowCard A1 (NOLOCK)\n" +
                    "JOIN dbo.sdOrderLot B1 (NOLOCK) ON B1.uGUID=A1.usdOrderLotGUID\n" +
                    "where A1.sCardNo = \'").append(vo.getSCardNo()).append("\'  GROUP BY B1.usdOrderDtlGUID\n" +
                    ")  ");
        }
        PageData<OrderAnalysisVo> pageData = pageQuery02("A.tCreateTime", sql.toString(), list, pageable, OrderAnalysisVo.class);
        List<OrderAnalysisVo> processAnalysisVoList = pageData.getData();
        convert(processAnalysisVoList);
        return new PageData<OrderAnalysisVo>(processAnalysisVoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());

    }


    /**
     * 2023-01-09 新增接口
     * 查询卡片维度的数据
     *
     * @param vo
     * @param pageable
     * @return
     */
    public PageData<OrderCarNoVo> queryCarNoPage(OrderCarNoVo vo, Pageable pageable) {
        StringBuffer sql = new StringBuffer();
        List list = new ArrayList();
        sql.append("select    C2.usdOrderDtlGUID as uGuid,A2.sEquipmentNo as deviceNo,A2.sEquipmentName as deviceName ,A2.sCardNo as sCardNo," +
                "A2.sMaterialNo as materialNo,A2.sMaterialName as materialName,\n" +
                "A2.sColorNo as colorNo,t1.sColorName ,A2.sWorkerGroupNo as workerGroupNo,A2.sWorkerGroupName as workerGroupName,A2.sWorkerNo as workerNo,\n" +
                "A2.sWorkerName AS workerName,A2.nTrackQty as nTrackQty,A2.tCreateTime  as factStartTime,B2.sRemark  as sRemark\n" +
                "FROM dbo.mnProducted A2(NOLOCK)\n" +
                "JOIN dbo.psWorkFlowCard B2(NOLOCK) ON B2.sCardNo = A2.sCardNo\n" +
                "JOIN dbo.sdOrderLot C2(NOLOCK) ON C2.uGUID=B2.usdOrderLotGUID\n" +
                " LEFT JOIN tmColor t1  (NOLOCK) ON t1.sColorNo  =A2.sColorNo  WHERE 1=1 ");
        if(StringUtils.isNotEmpty(vo.getUGuid())){
            sql.append(" and C2.usdOrderDtlGUID =  ").append("\'").append(vo.getUGuid()).append("\'");
        }
        if(StringUtils.isNotEmpty(vo.getDeviceNo())){
            sql.append(" and A2.sEquipmentNo =  ").append("\'").append(vo.getDeviceNo()).append("\'");
        }
        if(StringUtils.isNotEmpty(vo.getDeviceName())){
            sql.append(" and A2.sEquipmentName =  ").append("\'%").append(vo.getDeviceName()).append("%\'");
        }
        if(StringUtils.isNotEmpty(vo.getSCardNo())){
            sql.append(" and A2.sCardNo =  ").append("\'").append(vo.getSCardNo()).append("\'");
        }
        if(StringUtils.isNotEmpty(vo.getSCardNo())){
            sql.append(" and A2.sMaterialNo =  ").append("\'").append(vo.getMaterialNo()).append("\'");
        }
        if(StringUtils.isNotEmpty(vo.getMaterialName())){
            sql.append(" and A2.sMaterialName =  ").append("\'%").append(vo.getMaterialName()).append("%\'");
        }
        if(StringUtils.isNotEmpty(vo.getColorNo())){
            sql.append(" and A2.sColorNo =  ").append("\'").append(vo.getColorNo()).append("\'");
        }
        if(StringUtils.isNotEmpty(vo.getColorName())){
            sql.append(" and A2.sColorName =  ").append("\'%").append(vo.getColorName()).append("%\'");
        }
        if(StringUtils.isNotEmpty(vo.getWorkerGroupNo())){
            sql.append(" and A2.sWorkerGroupNo =  ").append("\'").append(vo.getWorkerGroupNo()).append("\'");
        }
        if(StringUtils.isNotEmpty(vo.getWorkerGroupName())){
            sql.append(" and A2.sWorkerGroupName =  ").append("\'%").append(vo.getWorkerGroupName()).append("%\'");
        }
        if(StringUtils.isNotEmpty(vo.getWorkerNo())){
            sql.append(" and A2.sWorkerNo =  ").append("\'%").append(vo.getWorkerNo()).append("%\'");
        }
        if(StringUtils.isNotEmpty(vo.getWorkerName())){
            sql.append(" and A2.sWorkerName =  ").append("\'%").append(vo.getWorkerName()).append("%\'");
        }
        if(StringUtils.isNotEmpty(vo.getNTrackQty())){
            sql.append(" and A2.nTrackQty =  ").append("\'").append(vo.getNTrackQty()).append("\'");
        }
        if ((vo.getCreatedTime()) != null) {
            sql.append(" and A2.tCreateTime =  ").append("\'").append(vo.getCreatedTime()).append("\'");
        }
        if (StringUtils.isNotEmpty(vo.getSRemark())) {
            sql.append(" and B2.sRemark =  ").append("\'").append(vo.getSRemark()).append("\'");
        }
        PageData<OrderCarNoVo> pageData = pageQuery("A2.tCreateTime", sql.toString(), list, pageable, OrderCarNoVo.class);
        return pageData;
    }

    private void convert(List<OrderAnalysisVo> processAnalysisVoList) {
        if (CollectionUtils.isEmpty(processAnalysisVoList)) {
            return;
        }
        List<String> idList = processAnalysisVoList.stream().filter(m1 -> StringUtils.isNotEmpty(m1.getUGuid())).map(OrderAnalysisVo::getUGuid).collect(Collectors.toList());
        List<HwEnergyVo> hwEnergyVoList = queryWaterAndElectricity(idList);
        List<HwExpandVo> expandVoList = queryExpandVo(idList);
        processAnalysisVoList.stream().forEach(m1 -> {
            m1.setCreatedTime(CommonUtils.getTimestampOfDateTime(m1.getFactStartTime()));
            m1.setWater(filterOutData(hwEnergyVoList, HwEnergyEnums.WATER, m1.getUGuid()));
            m1.setElectricity(filterOutData(hwEnergyVoList, HwEnergyEnums.ELECTRICITY, m1.getUGuid()));
            m1.setGas(filterOutData(hwEnergyVoList, HwEnergyEnums.GAS, m1.getUGuid()));
            HwExpandVo hwExpandVo = filterOutExpandData(expandVoList, m1.getUGuid());
            m1.setSremark(hwExpandVo.getSRemark());
            m1.setDuration(hwExpandVo.getDuration());
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

    public List<HwExpandVo> queryExpandVo(List<String> uGUIDList) {
        if (CollectionUtils.isEmpty(uGUIDList)) {
            return new ArrayList<>();
        }
        String sql = "select  C2.usdOrderDtlGUID as usdOrderDtlGUID,dbo.fnMESGetDiffTimeStr(B.tFactStartTime,B.tFactEndTime) as duration,\n" +
                "I.sRemark as sRemark\n" +
                "                FROM \n" +
                "                 dbo.psWorkFlowCard A(NOLOCK) \n" +
                "                 JOIN dbo.ppTrackJob B(NOLOCK) ON B.upsWorkFlowCardGUID = A.uGUID\n" +
                "                JOIN dbo.sdOrderLot C2(NOLOCK) ON C2.uGUID=A.usdOrderLotGUID\n" +
                "                LEFT JOIN dbo.psWPP I(NOLOCK) ON I.upsWorkFlowCardGUID=A.uGUID \n" +
                "                where C2.usdOrderDtlGUID in (:uGUIDList)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("uGUIDList", uGUIDList);
        NamedParameterJdbcTemplate givenParamJdbcTemp = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<HwExpandVo> data = givenParamJdbcTemp.query(sql, parameters, new BeanPropertyRowMapper<>(HwExpandVo.class));
        return data;

    }


    private String filterOutData(List<HwEnergyVo> hwEnergyVoList, HwEnergyEnums enums, String uGuid) {
        String value = hwEnergyVoList.stream().filter(m1 -> m1.getName().equals(enums.getChineseField()))
                .filter(m1 -> m1.getUsdOrderDtlGUID().equals(uGuid))
                .findFirst()
                .map(HwEnergyVo::getUseValue)
                .orElse(null);
        return value;
    }


    private HwExpandVo filterOutExpandData(List<HwExpandVo> expandVoList, String uGuid) {
        HwExpandVo value = expandVoList.stream()
                .filter(m1 -> m1.getUsdOrderDtlGUID().equals(uGuid))
                .findFirst()
                .orElse(new HwExpandVo());
        return value;
    }

}
