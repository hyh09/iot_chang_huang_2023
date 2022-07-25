package org.thingsboard.server.common.data.effciency;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * Project Name: all-in-one-multi-end-code
 * File Name: EffciencyEntityInfo
 * Package Name: org.thingsboard.server.common.data.effciency
 * Date: 2022/7/25 11:12
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
@ApiModel(value = "能耗列表的结果对象")
public class EfficiencyEntityInfo {



    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("设备id")
    private UUID deviceId;

    @ApiModelProperty("耗水量")
    private String waterConsumption;
    @ApiModelProperty("单位耗水量")
    private  String unitWaterConsumption;


    @ApiModelProperty("耗电量")
    private  String electricConsumption;
    @ApiModelProperty("单位耗电量")
    private  String unitElectricConsumption;


    @ApiModelProperty("耗气量")
    private String  gasConsumption;
    @ApiModelProperty("单位耗气量")
    private String unitGasConsumption;

    @ApiModelProperty("耗产量")
    private String capacityConsumption;

}
