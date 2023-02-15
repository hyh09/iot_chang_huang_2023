package org.thingsboard.server.dao.board.factoryBoard.vo.collection.piechart;

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
public class RatePieChartVo {

    private String currentValue;

    private String yesterdayValue;
}
