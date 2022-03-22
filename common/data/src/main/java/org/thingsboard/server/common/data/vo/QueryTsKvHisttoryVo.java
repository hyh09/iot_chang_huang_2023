package org.thingsboard.server.common.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 查询产能的入参实体
 * @author: HU.YUNHUI
 * @create: 2021-11-09 09:34
 **/
@Data
@ToString
@ApiModel(value = "查询产能的入参实体")
public class QueryTsKvHisttoryVo {

    @ApiModelProperty("起始时间 Long类型 ")
    private Long startTime;

    @ApiModelProperty("结束时间 Long类型")
    private Long endTime;

    @ApiModelProperty("设备id UUID类型")
    private UUID deviceId;


    @ApiModelProperty("当前要传的属性 数组类型  ###能耗的水 电气 入参; ##也不需要传了")
    private List<String> keys;


    /**
     *
     */
    private  String  sortOrder;




}
