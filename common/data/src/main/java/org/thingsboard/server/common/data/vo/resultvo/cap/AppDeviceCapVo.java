package org.thingsboard.server.common.data.vo.resultvo.cap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: springboot-jpa-20210106
 * @description: app产能返回之设备对象
 * @author: HU.YUNHUI
 * @create: 2021-11-09 10:56
 **/
@Data
@ToString
@ApiModel(value = "查询设备产能 实体")
public class AppDeviceCapVo {


    /**
     * 设备的名称
     */
    @ApiModelProperty("设备的名称 ")
    private String deviceName;

    @ApiModelProperty("设备图片 ")
    private  String  picture;
    /**
     * 设备的id
     */
    @ApiModelProperty("设备的id ")
    private String deviceId;
    /**
     * 产能
     */
    @ApiModelProperty("产能 ")
    private String value;  //比如多个属性呢？

    /**
     * 设备所属的车间 车间名称
     */
    @ApiModelProperty("设备所属的车间 车间名称 ")
    private String workshopName;
    /**
     * 设备所属的产线 产线名称
     */
    @ApiModelProperty("设备所属的产线 产线名称 ")
    private String productionName;


    /**
     * 设备所属的产线 产线名称
     */
    @ApiModelProperty("是否加入产能运算 ")
    private Boolean flg;
}
