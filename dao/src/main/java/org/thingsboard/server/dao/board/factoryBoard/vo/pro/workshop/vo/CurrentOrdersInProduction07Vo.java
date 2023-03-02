package org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: CurrentOrdersInProduction07Vo
 * @Date: 2023/2/24 11:01
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "当前在产订单-百分比 ")
public class CurrentOrdersInProduction07Vo {

    @ApiModelProperty("工序")
    private String processName;

    @ApiModelProperty("在产数量/总数")
    private String percentage;
}
