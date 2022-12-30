package org.thingsboard.server.dao.sqlserver.server.vo;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

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
public class QueryYieIdVo extends  RownumberDto{

    /**
     * 工序编号
     */
    private String workOrderNumber;

    /**
     * 工序名称
     */
    private String workingProcedureName;
    /**
     * 班组名称
     */
    private String  workerGroupName;
    /**
     * 班组成员
     */
    private String workerNameList;

    /**
     * 产出数量
     */
    private String nTrackQty;
    /**
     * 计量单位
     */
    private String unit;
    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 布种
     */
    private String materialNo;

    /**
     * 色名
     */
    private String colorName;



    /**
     * 时长
     */
    private String duration;
    /**
     * 机台
     */
    private String sEquipmentNo;



}
