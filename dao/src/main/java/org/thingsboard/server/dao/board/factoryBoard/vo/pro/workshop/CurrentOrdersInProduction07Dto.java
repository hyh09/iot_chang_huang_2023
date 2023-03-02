package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlColumnAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlOnFromTableAnnotation;

/**
 * @Project Name: thingsboard
 * @File Name: CurrentOrdersInProductionVo
 * @Date: 2023/2/21 18:00
 * @author: wb04
 * 业务中文描述: 当前在产订单
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "当前在产订单-在产数 ")
@SqlOnFromTableAnnotation(from = "dbo.mnProducting A(NOLOCK)\n" +
        "JOIN dbo.emEquipment B(NOLOCK) ON B.uGUID=A.uemEquipmentGUID \n" +
        "JOIN dbo.pbWorkingProcedure C(NOLOCK) ON C.uGUID=A.upbWorkingProcedureGUID ",
        groupByLast = "C.sWorkingProcedureName")
public class CurrentOrdersInProduction07Dto {


    @ApiModelProperty("工序")
    @SqlColumnAnnotation(name = "C.sWorkingProcedureName")
    private String processName;


    @ApiModelProperty("在产数量")
    @SqlColumnAnnotation(name = "COUNT(1)")
    private String yieldValue;


}
