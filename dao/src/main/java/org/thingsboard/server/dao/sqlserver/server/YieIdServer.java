package org.thingsboard.server.dao.sqlserver.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.dao.PageUtil;
import org.thingsboard.server.dao.sqlserver.server.vo.QueryYieIdEntryVo;
import org.thingsboard.server.dao.sqlserver.server.vo.QueryYieIdVo;
import org.thingsboard.server.dao.util.CommonUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class YieIdServer {
    @Autowired
    @Qualifier("sqlServerTemplate")
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private JdbcTemplate jdbcTemplate01;

    /**
     * 搜索条件
     * @param queryYieIdVo
     * @param pageable
     * @return
     */
    public  PageData<QueryYieIdVo> query(QueryYieIdVo queryYieIdVo, Pageable pageable){
        StringBuffer sql = new StringBuffer();
        sql.append("select row_number() " +
                "over(order by B.tFactStartTime asc) as rownumber ," +
                "D.sWorkingProcedureNo as workOrderNumber,D.sWorkingProcedureName as workingProcedureName," +
                "C.sWorkerGroupName as workerGroupName," +
                "C.sWorkerNameList as workerNameList,C.nTrackQty as nTrackQty,A.sUnit as unit,A.sCardNo as cardNo,E.sMaterialNo as materialNo,F.sColorName as colorName," +
                "B.tFactStartTime as factStartTime,B.tFactEndTime as factEndTime,dbo.fnMESGetDiffTimeStr(B.tFactStartTime,B.tFactEndTime) as 时长 \n" +
                "FROM dbo.psWorkFlowCard A(NOLOCK)\n" +
                "JOIN dbo.ppTrackJob B(NOLOCK) ON B.upsWorkFlowCardGUID = A.uGUID\n" +
                "JOIN dbo.ppTrackOutput C(NOLOCK) ON C.uppTrackJobGUID = B.uGUID\n" +
                "JOIN dbo.pbWorkingProcedure D(NOLOCK) ON D.uGUID=B.upbWorkingProcedureGUID\n" +
                "JOIN dbo.mmMaterial E(NOLOCK) ON E.uGUID = A.ummMaterialGUID\n" +
                "JOIN dbo.tmColor F(NOLOCK) ON F.uGUID = A.utmColorGUID where 1=1 ");
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
//            list.add("\'%"+queryYieIdVo.getWorkerGroupName()+"%\'");
        }

        return pageQuery(sql.toString(),list,pageable,QueryYieIdVo.class);
    }

    public <T> PageData<T> pageQuery( String sql,List list , Pageable pageable,Class<T> mappedClass) {
        String sqlCount = "select count(*) from (" + sql + ") t_count_0";
        Integer count = jdbcTemplate.queryForObject(sqlCount, list.toArray(), Integer.class);
        StringBuffer  sqlQuery = new StringBuffer();
        sqlQuery.append(" select ").append(" top(").append(pageable.getPageSize()).append(" ) *").append(" from  ( ")
                .append(sql).append(" ) temp where rownumber > ").append((pageable.getPageNumber())*pageable.getPageSize());
        List<T> mapList = jdbcTemplate.query(sqlQuery.toString(), list.toArray(), new BeanPropertyRowMapper<>(mappedClass));
        Page<T> page = new PageImpl<T>(mapList, pageable, count);
        return new PageData<T>(mapList, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }


}
