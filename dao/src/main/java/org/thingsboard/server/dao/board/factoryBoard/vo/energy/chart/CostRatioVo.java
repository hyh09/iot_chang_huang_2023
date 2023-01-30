package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart;

import lombok.Data;
import lombok.ToString;

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
public class CostRatioVo {

    /**
     * 水占比费用
     */
    private String  waterRatio;

    /**
     * 电费用占比
     */
    private String electricityRatio;
    /**
     * 气占比费用
     */
    private String gasRatio;
    /**
     * 总费用（水+电+气)
     */
    private String totalCost;


}
