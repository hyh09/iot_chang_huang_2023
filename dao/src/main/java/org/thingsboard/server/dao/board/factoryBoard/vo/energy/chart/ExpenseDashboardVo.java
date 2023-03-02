package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: ExpenseDashboardVo
 * @Date: 2023/2/24 16:15
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel("能耗费用仪表盘")
public class ExpenseDashboardVo {

    @ApiModelProperty("月能耗")
    private  CostRatioVo month;

    @ApiModelProperty("年能耗")
    private  CostRatioVo year;

}
