package org.thingsboard.server.common.data.vo.resultvo.energy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @program: springboot-jpa-20210106
 * @description: PC产能返回之设备对象
 * @author: HU.YUNHUI
 * @create: 2021-11-09 10:56
 **/
@Data
@ToString
@ApiModel(value = "查询设备能耗 实体")
public class PcDeviceEnergyVo {


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


    @ApiModelProperty("能耗下分组key是属性名-值{比如温度:10} ")
    private Map<String,String> mapValue;

    @ApiModelProperty("能耗下分组key的各个[单位能耗] 是属性名-值{比如温度:10} ")
    private Map<String,String> mapUnitValue;

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

}
