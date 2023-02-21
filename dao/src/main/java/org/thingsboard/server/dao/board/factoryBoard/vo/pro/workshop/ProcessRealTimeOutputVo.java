package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlColumnAnnotation;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.jdbcTabel.query.SqlOnFromTableAnnotation;

/**
 * @Project Name: thingsboard
 * @File Name: ProcessRealTimeOutputVo
 * @Date: 2023/2/21 17:32
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "工序实时产量 ")
@SqlOnFromTableAnnotation(from=" dbo.ppTrackOutput A(NOLOCK)\n" +
        "JOIN dbo.ppTrackJob B(NOLOCK) ON B.uGUID = A.uppTrackJobGUID\n" +
        "JOIN dbo.pbWorkingProcedure C(NOLOCK) ON C.uGUID=B.upbWorkingProcedureGUID ",
        whereValue = " A.tTrackTime>=CONVERT(NVARCHAR(10),GETDATE(),120)",
        groupByLast = "C.sWorkingProcedureName"

)
public class ProcessRealTimeOutputVo {

    @ApiModelProperty("工序")
    @SqlColumnAnnotation(name = "C.sWorkingProcedureName")
    private String processName;

    @ApiModelProperty("产量")
    @SqlColumnAnnotation(name = "SUM(A.nTrackQty)")
    private String yieldValue;

}
