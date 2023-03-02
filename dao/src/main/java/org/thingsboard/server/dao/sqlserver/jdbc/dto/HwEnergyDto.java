package org.thingsboard.server.dao.sqlserver.jdbc.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

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


    private String uGUID;

    private String sCode;

    private String sName;

    private String sClass;

    /***
     * 能耗的单价
     */
    private BigDecimal nCurrPrice;


    private String sUnitName;

    private String nCalcCoefficient;

    private String isort;


}
