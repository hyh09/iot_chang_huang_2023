package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 设备返回的属性
 * @author: HU.YUNHUI
 * @create: 2021-11-11 18:24
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "提供app端--设备返回的属性")
public class DeviceDataVo {

    @ApiModelProperty(value = "设备id")
    private  UUID  deviceId;

    @ApiModelProperty(value = "设备名称")
    private   String deviceName;

    @ApiModelProperty(value = "设备编码:目前库都是空的,不要取这个,用不到")
    private  String deviceCode;

    @ApiModelProperty(value = "工厂id")
    private  UUID factoryId;

    @ApiModelProperty(value = "工厂名称")
    private  String  factoryName;

    @ApiModelProperty(value = "车间id")
    private UUID workshopId;

    @ApiModelProperty(value = "车间名称")
    private  String workshopName;

    @ApiModelProperty(value = "生产线id")
    private  UUID productionLineId;

    @ApiModelProperty(value = "生产线名称")
    private  String  productionLineName;

}
