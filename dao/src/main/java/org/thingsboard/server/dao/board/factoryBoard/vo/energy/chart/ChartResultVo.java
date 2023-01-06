package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Project Name: thingsboard
 * @File Name: ChartResultVo
 * @Date: 2023/1/5 11:08
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class ChartResultVo {

    /**
     * 水的趋势线
     */
    private List<ChartDataVo> water;

    /**
     *
     */
    private List<ChartDataVo> electricity;

    private List<ChartDataVo> gas;

}
