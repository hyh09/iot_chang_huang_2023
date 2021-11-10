package org.thingsboard.server.common.data.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 能耗入参对象
 * @author: HU.YUNHUI
 * @create: 2021-11-09 19:06
 **/

public class QueryEnergyVo {

    @ApiModelProperty("起始时间 Long类型 ")
    private Long startTime;

    @ApiModelProperty("结束时间 Long类型")
    private Long endTime;

    @ApiModelProperty("设备id UUID类型")
    private UUID deviceId;

    @ApiModelProperty("产线id  UUID类型")
    private UUID productionLineId;


    @ApiModelProperty("车间id UUID类型")
    private UUID workshopId;

    @ApiModelProperty("工厂id  UUID类型")
    private UUID factoryId;

    @ApiModelProperty("当前要传的属性 数组类型")
    private String key;

    @ApiModelProperty("分页参数大小 不传默认是2")
    private  int pageSize=2;
    @ApiModelProperty("起始页  不传默认是0")
    private  int page=0;
}
