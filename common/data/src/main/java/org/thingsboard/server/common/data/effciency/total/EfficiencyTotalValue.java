package org.thingsboard.server.common.data.effciency.total;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Project Name: all-in-one-multi-end-code
 * File Name: EfficiencyTotalValue
 * Package Name: org.thingsboard.server.common.data.effciency.total
 * Date: 2022/7/25 13:27
 * author: wb04
 * 业务中文描述:总能耗
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "能耗列表的结果的 总能耗")
public class EfficiencyTotalValue {


    @ApiModelProperty("总耗水量")
    private String totalWaterConsumption;


    @ApiModelProperty("总耗电量")
    private String totalElectricConsumption;


    @ApiModelProperty("总耗气量")
    private String totalGasConsumption;

}
