package org.thingsboard.server.dao.sqlserver.jdbc.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: HwEnergyDto
 * @Date: 2023/1/30 10:24
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class HwEnergyDto {

    /**
     * 主键id
     */
    private String  uGUID;

    private String sCode;

    private  String sName;

    private  String sClass;

    /**
     * 单价
     */
    private String nCurrPrice;

    /**
     * 单位名称
     */
    private String sUnitName;

    private  String nCalcCoefficient;

    private String isort;


}
