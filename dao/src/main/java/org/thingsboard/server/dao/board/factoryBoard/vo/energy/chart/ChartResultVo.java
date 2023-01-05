package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart;

import lombok.Data;

/**
 * @Project Name: thingsboard
 * @File Name: ChartResultVo
 * @Date: 2023/1/5 11:08
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
public class ChartResultVo {

    private ChartDataVo  water;

    private ChartDataVo electricity;

    private ChartDataVo gas;

}
