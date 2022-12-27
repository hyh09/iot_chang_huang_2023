package org.thingsboard.server.dao.sqlserver.server.vo.process;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.sqlserver.server.vo.RownumberDto;

/**
 * @Project Name: thingsboard
 * @File Name: ProcessAnalysisVo
 * @Date: 2022/12/27 14:49
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
public class ProcessAnalysisVo extends RownumberDto {
    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 品名
     */
    private String materialName;
    /**
     * 颜色
     */
    private String colorName;
    /**
     * 卡数量
     */
    private String numberOfCards;
    /**
     * 整理要求
     */
    private String sRemark;

    /**
     * 工序 =D.sWorkingProcedureName
     */
    private String workingProcedureName;
    /**
     * ,生产米数=
     */
    private String nTrackQty;

    /**
     * 理论用时
     */
    private String theoreticalTime;

    private String actualTime;

    /**
     * ,[超时(分)]  = 实际用时 - 理论用时
     */
    private String timeoutMinutes;

    /**
     * [超时(%)]
     */
    private String overTimeRatio;

    /**
     * 生产班组
     */
    private String workerGroupName;



}
