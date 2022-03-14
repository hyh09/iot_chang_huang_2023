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
 * @description: Pc端-运行状态数据的图表数据部分
 * @author: HU.YUNHUI
 * @create: 2022-02-08 10:44
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Pc端-运行状态数据的图表数据部分")
public class OutOperationStatusChartDataVo {

    @ApiModelProperty(value = "属性标题")
    private String title;

    @ApiModelProperty(value = "属性名称")
    private String name;

    @ApiModelProperty(value = "属性单位")
    private String unit;

    @ApiModelProperty(value = "时序数据列表")
    List<OutOperationStatusChartTsKvDataVo> tsKvs;
}
