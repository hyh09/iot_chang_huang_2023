package org.thingsboard.server.common.data.effciency.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.vo.device.DeviceRenameVo;

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
public class EfficiencyHistoryDataVo extends DeviceRenameVo {

    @ApiModelProperty("设备名称")
    private String deviceName;



    @ApiModelProperty("创建时间")
    private long createdTime;

    @ApiModelProperty("水")
    @JsonProperty("waterConsumption")
    private String water;


    @ApiModelProperty("电")
    @JsonProperty("electricConsumption")
    private  String electric;


    @ApiModelProperty("gas")
    @JsonProperty("gasConsumption")
    private String gas;


}
