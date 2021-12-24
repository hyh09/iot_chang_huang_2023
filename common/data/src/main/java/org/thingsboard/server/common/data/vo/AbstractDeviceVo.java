package org.thingsboard.server.common.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 设备入参
 * @author: HU.YUNHUI
 * @create: 2021-12-24 10:28
 **/
@Data
@ToString
@ApiModel(value = "设备入参实体")
public  class AbstractDeviceVo {
    @ApiModelProperty("设备id UUID类型")
    protected UUID deviceId;

    @ApiModelProperty("产线id  UUID类型")
    protected UUID productionLineId;


    @ApiModelProperty("车间id UUID类型")
    protected UUID workshopId;

    @ApiModelProperty("工厂id  UUID类型")
    protected UUID factoryId;


    protected UUID tenantId;
}
