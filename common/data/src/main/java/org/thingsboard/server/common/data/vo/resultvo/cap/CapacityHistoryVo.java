package org.thingsboard.server.common.data.vo.resultvo.cap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.vo.device.DeviceRenameVo;

/**
 * @program: thingsboard
 * @description: 产能历史实体出参
 * @author: HU.YUNHUI
 * @create: 2022-01-19 10:24
 **/
@Data
@ToString
@ApiModel(value = "产能历史实体出参")
public class CapacityHistoryVo extends DeviceRenameVo {

    /**
     * 设备的id
     */
    @ApiModelProperty("设备的id ")
    private String deviceId;

    /**
     * 设备的名称
     */
    @ApiModelProperty("设备的名称 ")
    private String deviceName;

    @ApiModelProperty("产能值 ")
    private  String value;

    /**
     * 上报时间
     */
    @ApiModelProperty("上报时间 支持排序 ")
    private  String  createdTime;


}
