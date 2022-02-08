package org.thingsboard.server.common.data.vo.device.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.vo.device.RunningStateVo;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: pc的运行状态列表查询的入参
 * @author: HU.YUNHUI
 * @create: 2022-02-08 10:54
 **/
@Data
@ToString
@ApiModel(value = "pc的运行状态列表查询的入参")
public class InputRunningSateVo {

    @ApiModelProperty("起始时间 Long类型 ")
    private Long startTime;

    @ApiModelProperty("结束时间 Long类型")
    private Long endTime;

    @ApiModelProperty("设备id UUID类型")
    private UUID deviceId;

    @ApiModelProperty("属性列表对象(和下拉框返回一致)")
    List<RunningStateVo>  attributeParameterList;
}
