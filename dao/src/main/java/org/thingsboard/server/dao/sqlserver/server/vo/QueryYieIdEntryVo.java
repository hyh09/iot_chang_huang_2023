package org.thingsboard.server.dao.sqlserver.server.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: QueryYieIdVo
 * @Date: 2022/12/26 16:07
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@ToString
@Data
public class QueryYieIdEntryVo {


    private Long startTime;

    private Long endTime;


    /**
     * 工序名称
     */
    private String workingProcedureName;

    /**
     * 班组成员
     */
    private String workerNameList;

    /**
     * 班组名称
     */
    private String  workerGroupName;
    /**
     * 机台号
     */




}
