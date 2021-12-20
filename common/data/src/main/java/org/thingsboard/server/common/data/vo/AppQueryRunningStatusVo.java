package org.thingsboard.server.common.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 查询运行状态入参
 * @author: HU.YUNHUI
 * @create: 2021-11-10 16:01
 **/
@Data
@ToString
@ApiModel(value = "查询运行状态入参")
public class AppQueryRunningStatusVo {

    @ApiModelProperty("起始时间 Long类型 ")
    private Long startTime;

    @ApiModelProperty("结束时间 Long类型")
    private Long endTime;

    @ApiModelProperty("设备id UUID类型")
    private UUID deviceId;

    @ApiModelProperty("所属属性的name;")
    List<String> keyNames;

    @ApiModelProperty("分页的大小;")
   private   int pageSize=3;
    @ApiModelProperty("分页的大小;")
    private int page=0;
    private  String textSearch;
    private String sortProperty;
    private  String sortOrder;
}
