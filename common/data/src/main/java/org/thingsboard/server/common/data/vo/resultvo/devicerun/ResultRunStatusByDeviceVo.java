package org.thingsboard.server.common.data.vo.resultvo.devicerun;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description: 运行状态出参
 * @author: HU.YUNHUI
 * @create: 2021-11-10 16:05
 **/
@Data
@ToString
@ApiModel(value = "运行状态出参")
public class ResultRunStatusByDeviceVo {

    @ApiModelProperty("属性名;")
    private String  keyName;

    @ApiModelProperty("属性名id")
    private  int  keyId;

//    @ApiModelProperty("设备id")
//    private String  deviceId;
    @ApiModelProperty("当前的值")
    private  String value;
    @ApiModelProperty("时间")
    private  Long time;


    @ApiModelProperty("标题###2021-12-03新增")
    private String title="";
    @ApiModelProperty("单位##2021-12-03新增")
    private  String unit="";

}
