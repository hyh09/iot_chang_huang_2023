package org.thingsboard.server.common.data.vo.tskv.consumption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-09 11:22
 **/
@Data
@ToString
@ApiModel(value = "具体的数据")
public class ConsumptionVo {

//    @JsonIgnore
    private  String title;

    @ApiModelProperty("值")
    private  String value="0";
    @ApiModelProperty("单位")
    private  String unit="";
}
