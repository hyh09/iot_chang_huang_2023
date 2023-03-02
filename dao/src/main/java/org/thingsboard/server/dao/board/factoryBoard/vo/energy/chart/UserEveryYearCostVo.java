package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: UserEveryYearCostVo
 * @Date: 2023/1/30 16:57
 * @author: wb04
 * 业务中文描述: 年度使用成本
 * Copyright (c) 2023,All Rights Reserved.
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("年度使用成本")
public class UserEveryYearCostVo {
    private String time;

    private String value;
}
