package org.thingsboard.server.common.data.vo.device.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @program: thingsboard
 * @description: 运行状态输出参数pc
 * @author: HU.YUNHUI
 * @create: 2022-02-08 10:39
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "运行状态输出参数pc")
public class OutRunningStateVo {

    @ApiModelProperty(value = "图表名称(标题name) 如果不是图表就是属性的name")
    private String tableName;

    @ApiModelProperty("图表的id;先返回起 如果是图表就返回图表id")
    private String  chartId;

    @ApiModelProperty("不是图表的，属性； 返回keyname")
    private  String  keyName;


    @ApiModelProperty(value = "图表的数据部分 【多个线条的部分】")
    List<OutOperationStatusChartDataVo> properties;



}
