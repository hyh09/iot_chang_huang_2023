package org.thingsboard.server.common.data.vo.device.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description: 具体的遥测数据
 * @author: HU.YUNHUI
 * @create: 2022-02-08 10:48
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Pc端-运行状态数据的图表数据部分")
public class OutOperationStatusChartTsKvDataVo {

    @ApiModelProperty(value = "时间")
    private Long ts;

    @ApiModelProperty(value = "值")
    private String value;
}
