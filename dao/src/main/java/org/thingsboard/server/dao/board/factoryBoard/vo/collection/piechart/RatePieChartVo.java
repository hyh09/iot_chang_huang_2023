package org.thingsboard.server.dao.board.factoryBoard.vo.collection.piechart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: RatePieChartVo
 * @Date: 2023/2/15 11:02
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "看板的-开机率的饼状图返回实体")
public class RatePieChartVo {
    /**
     * 今日的开机率
     * 到当前系统时间的计算
     */
    @ApiModelProperty("今日的开机率%")
    private String currentValue;

    /**
     * 昨日的开机率
     */
    @ApiModelProperty("昨日的开机率%")
    private String yesterdayValue;
}
