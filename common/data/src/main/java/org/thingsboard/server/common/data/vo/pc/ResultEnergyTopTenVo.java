package org.thingsboard.server.common.data.vo.pc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: pc端能耗top-10
 * @author: HU.YUNHUI
 * @create: 2022-01-19 17:37
 **/
@Data
@ToString
@ApiModel(value = "pc端能耗top-10出参")
public class ResultEnergyTopTenVo {

    /**
     * 设备的名称
     */
    @ApiModelProperty("设备的名称 ")
    private String deviceName;
    /**
     * 设备的id
     */
    @ApiModelProperty("设备的id ")
    private UUID deviceId;

    @ApiModelProperty("值 ")
    private  String value;
}
