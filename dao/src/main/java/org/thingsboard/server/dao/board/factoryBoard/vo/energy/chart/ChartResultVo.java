package org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.board.factoryBoard.vo.energy.chart.request.ChartDateEnums;

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
@ApiModel("趋势图对象")
public class ChartResultVo {

    @ApiModelProperty("费用占比对象")
    private CostRatioVo costRatioVo;

    @JsonIgnore
    private ChartDateEnums dateEnums;

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
