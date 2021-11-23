package org.thingsboard.server.common.data.vo.resultvo.energy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @program: springboot-jpa-20210106
 * @description: app产能返回之设备对象
 * @author: HU.YUNHUI
 * @create: 2021-11-09 10:56
 **/
@Data
@ToString
@ApiModel(value = "查询设备能耗 实体")
public class AppDeviceEnergyVo {


    /**
     * 设备的名称
     */
    @ApiModelProperty("设备的名称 ")
    private String deviceName;
    /**
     * 设备的id
     */
    @ApiModelProperty("设备的id ")
    private String deviceId;
//    /**
//     * 产能  水、电、气
//     */
//    private  String  value;  //比如多个属性呢？

//    /**
//     * 水的总值
//     */
//    @ApiModelProperty("水的值 ")
//    private String waterValue;
//    /**
//     * 电的总值
//     */
//    @ApiModelProperty("电的值 ")
//    private String electricValue;
//    /**
//     * 气总值
//     */
//    @ApiModelProperty("气值 ")
//    private String airValue;

    @ApiModelProperty("key是属性名-值{比如温度:10 ")
    private Map<String,String> mapValue;


    /**
     * 设备所属的车间 车间名称
     */
    @ApiModelProperty("车间名称 ")
    private String workshopName;
    /**
     * 设备所属的产线 产线名称
     */
    @ApiModelProperty("产线名称 ")
    private String productionName;

    @ApiModelProperty("时间")
    private  Long time;


    @JsonIgnore
    private Map<String,Long> timeValueMap;

}
