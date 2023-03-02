package org.thingsboard.server.dao.board.workshopBoard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Project Name: thingsboard
 * @File Name: CapacitiesTop5Vo
 * @Date: 2023/3/2 16:09
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "今日车间的top5的产量排行 ")
@NoArgsConstructor
@AllArgsConstructor
public class CapacitiesTop5Vo {
    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("产量")
    private String output;
}
