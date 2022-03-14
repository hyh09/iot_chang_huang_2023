package org.thingsboard.server.common.data.vo.device.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.thingsboard.server.common.data.vo.AppQueryRunningStatusVo;
import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;
import org.thingsboard.server.common.data.vo.device.RunningStateVo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public InputRunningSateVo  toInputRunningSateVoByAppQuery(AppQueryRunningStatusVo  sourceObject)
    {
        List<RunningStateVo>  attributeParameterList = new ArrayList<>();

        InputRunningSateVo  targetObject =new InputRunningSateVo();
        targetObject.setDeviceId(sourceObject.getDeviceId());
        targetObject.setEndTime(sourceObject.getEndTime());
        targetObject.setStartTime(sourceObject.getStartTime());
        List<DictDeviceDataVo>  sourceTwoDataVo =  sourceObject.getAttributes();
        if(CollectionUtils.isNotEmpty(sourceTwoDataVo))
        {
            sourceTwoDataVo=  sourceTwoDataVo.stream().skip((sourceObject.getPage())*sourceObject.getPageSize()).limit(sourceObject.getPageSize()).collect(Collectors.toList());
            attributeParameterList = sourceTwoDataVo.stream().map(s1->{
                RunningStateVo  targetVo = new RunningStateVo();
                targetVo.setChartId(s1.getChartId());
                targetVo.setAttributeNames(s1.getAttributeNames());
                targetVo.setUnit(s1.getUnit());
                targetVo.setTitle(s1.getTitle());
                targetVo.setName(s1.getName());
                return targetVo;
            }).collect(Collectors.toList());
        }
        targetObject.setAttributeParameterList(attributeParameterList);
        return  targetObject;


    }
}
