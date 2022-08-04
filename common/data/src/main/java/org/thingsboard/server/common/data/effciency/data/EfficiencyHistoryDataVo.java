package org.thingsboard.server.common.data.effciency.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Project Name: all-in-one-multi-end-code
 * File Name: EfficiencyHistoryDataVo
 * Package Name: org.thingsboard.server.common.data.effciency
 * Date: 2022/7/26 9:37
 * author: wb04
 * 业务中文描述: 能耗历史的返回对象
 * Copyright (c) 2022,All Rights Reserved.
 * @author wb04
 */
@Data
@ToString
public class EfficiencyHistoryDataVo {

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("设备名称")
    private String rename;

    @ApiModelProperty("创建时间")
    private long createdTime;

    @ApiModelProperty("水")
    private String water;


    @ApiModelProperty("电")
    private  String electric;


    @ApiModelProperty("gas")
    private String gas;


}
