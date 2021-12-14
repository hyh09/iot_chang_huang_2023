package org.thingsboard.server.common.data.vo.tskv.consumption;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-14 16:05
 **/
@Data
@ToString
public class TrendLineVo {
    @ApiModelProperty("值")
    private  String  value;

    @ApiModelProperty("时间")
    private  String time;
}
