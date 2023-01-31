package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Project Name: thingsboard
 * @File Name: CostRatioVo
 * @Date: 2023/1/30 10:04
 * @author: wb04
 * 业务中文描述: 水 电 气  费用占比
 * Copyright (c) 2023,All Rights Reserved.
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("费用占比对象")
public class CostRatioVo {

    /**
     * 水占比费用
     */
    @ApiModelProperty("水占比费用")
    private String  waterRatio;

    /**
     * 电费用占比
     */
    @ApiModelProperty("电费用占比")
    private String electricityRatio;
    /**
     * 气占比费用
     */
    @ApiModelProperty("气占比费用")
    private String gasRatio;
    /**
     * 总费用（水+电+气)
     */
    @ApiModelProperty("总费用（水+电+气)")
    private BigDecimal totalCost;


}
