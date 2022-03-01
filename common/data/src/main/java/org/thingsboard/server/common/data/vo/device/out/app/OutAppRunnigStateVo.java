package org.thingsboard.server.common.data.vo.device.out.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @program: thingsboard
 * @description: app运行状态的返回参数
 * @author: HU.YUNHUI
 * @create: 2022-02-09 17:21
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "app运行状态的返回参数")
public class OutAppRunnigStateVo {
    @ApiModelProperty(value = "图表名称(标题name) 如果不是图表就是属性的name")
    private String tableName;

    @ApiModelProperty("图表的id;先返回起 如果是图表就返回图表id")
    private String  chartId;

    @ApiModelProperty("不是图表的，属性； 返回keyname")
    private  String  keyName;

    @ApiModelProperty("图表的单位;")
    private String chartUnit;

    @ApiModelProperty(value = "图表的数据部分 【多个线条的部分】")
    List<OutAppOperationStatusChartDataVo> properties;
}
