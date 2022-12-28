package org.thingsboard.server.dao.sqlserver.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.dao.sqlserver.server.vo.QueryYieIdVo;
import org.thingsboard.server.dao.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project Name: thingsboard
 * @File Name: YieIdServer
 * @Date: 2022/12/26 13:36
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Service
public class YieIdServer extends  BaseRunSqlServer{





    /**
     * 搜索条件
     * @param queryYieIdVo
     * @param pageable
     * @return
     */
    public  PageData<QueryYieIdVo> query(QueryYieIdVo queryYieIdVo, Pageable pageable){
        StringBuffer sql = new StringBuffer();
        sql.append("select "+
                "D.sWorkingProcedureNo as workOrderNumber,D.sWorkingProcedureName as workingProcedureName," +
                "C.sWorkerGroupName as workerGroupName," +
                "C.sWorkerNameList as workerNameList,C.nTrackQty as nTrackQty,A.sUnit as unit,A.sCardNo as cardNo,E.sMaterialNo as materialNo,F.sColorName as colorName," +
                "B.tFactStartTime as factStartTime,B.tFactEndTime as factEndTime,dbo.fnMESGetDiffTimeStr(B.tFactStartTime,B.tFactEndTime) as duration,G.sEquipmentNo as sEquipmentNo  \n" +
                "FROM dbo.psWorkFlowCard A(NOLOCK)\n" +
                "JOIN dbo.ppTrackJob B(NOLOCK) ON B.upsWorkFlowCardGUID = A.uGUID\n" +
                "JOIN dbo.ppTrackOutput C(NOLOCK) ON C.uppTrackJobGUID = B.uGUID\n" +
                "JOIN dbo.pbWorkingProcedure D(NOLOCK) ON D.uGUID=B.upbWorkingProcedureGUID\n" +
                "JOIN dbo.mmMaterial E(NOLOCK) ON E.uGUID = A.ummMaterialGUID\n" +
                "JOIN dbo.tmColor F(NOLOCK) ON F.uGUID = A.utmColorGUID" +
                " LEFT JOIN emEquipment G(NOLOCK) ON G.uGUID =C.uemEquipmentGUID  where 1=1 ");
        List list = new ArrayList();
        if(queryYieIdVo.getCreatedTime()!=null){
            sql.append(" and B.tFactStartTime >= ?");
            list.add(CommonUtils.longToDateTime(queryYieIdVo.getCreatedTime()));
        }
        if(queryYieIdVo.getUpdatedTime()!=null){
            sql.append(" and B.tFactEndTime < ?");
            list.add(CommonUtils.longToDateTime(queryYieIdVo.getUpdatedTime()));
        }
        //工序名称 workingProcedureName
        if(StringUtils.isNotEmpty(queryYieIdVo.getWorkingProcedureName())){
            sql.append(" and D.sWorkingProcedureName like  ").append("\'%").append(queryYieIdVo.getWorkingProcedureName()).append("%\'");
        }
        //员工姓名 workerNameList
        if(StringUtils.isNotEmpty(queryYieIdVo.getWorkerNameList())){
            sql.append(" and C.sWorkerNameList like ").append("\'%").append(queryYieIdVo.getWorkerNameList()).append("%\'");;
        }
        //班组名称 workerGroupName
        if(StringUtils.isNotEmpty(queryYieIdVo.getWorkerGroupName())){
            sql.append(" and C.sWorkerGroupName like  ").append("\'%").append(queryYieIdVo.getWorkerGroupName()).append("%\'");;
        }
        //机台号
        if(StringUtils.isNotEmpty(queryYieIdVo.getSEquipmentNo())){
            sql.append(" and G.sEquipmentNo like  ").append("\'%").append(queryYieIdVo.getSEquipmentNo()).append("%\'");;
        }
        return pageQuery("B.tFactStartTime",sql.toString(),list,pageable,QueryYieIdVo.class);
    }



}
