package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: thingsboard
 * @description: PC端的运行状态下拉框参数
 * @author: HU.YUNHUI
 * @create: 2022-02-07 17:48
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PC端的运行状态下拉框参数")
public class RunningStateVo {
    @ApiModelProperty("名称")
    private  String name;

    /**
     * 如果标题为空取的是名称来的;
     */
    @ApiModelProperty("标题")
    private String title="";
    @ApiModelProperty("单位")
    private  String unit="";

    @ApiModelProperty("当前的标题下的属性值")
    private List<String> attributeNames;

    @ApiModelProperty("图表的id;先返回起")
    private String  chartId;


    public static RunningStateVo  toDataByDictDeviceDataVo(DictDeviceDataVo  vo)
    {
        RunningStateVo  runningStateVo = new RunningStateVo();
        runningStateVo.setName(vo.getName());
        runningStateVo.setTitle(vo.getTitle());
        runningStateVo.setChartId("");
        List<String> attributeNames  = new ArrayList<>();
        attributeNames.add(vo.getName());
        runningStateVo.setAttributeNames(attributeNames);
        runningStateVo.setUnit(vo.getUnit());
        return runningStateVo;
    }





}
