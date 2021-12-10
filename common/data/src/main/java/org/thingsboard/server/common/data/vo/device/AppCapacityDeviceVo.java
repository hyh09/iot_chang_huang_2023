package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 产能运算配置返回
 * @author: HU.YUNHUI
 * @create: 2021-12-06 14:19
 **/
@Data
@ToString
@ApiModel(value = "设备的返回")
public class AppCapacityDeviceVo {

    @ApiModelProperty(value = "是否参与产能运算")
    private Boolean flg;


    private UUID tenantId;

    @ApiModelProperty(value = "工厂id")
    private UUID factoryId;
    @ApiModelProperty(value = "工厂名称")
    private String  factoryName;


    @ApiModelProperty(value = "车间id")
    private UUID workshopId;
    @ApiModelProperty(value = "车间名称")
    private String  workshopName;


    @ApiModelProperty(value = "产线id")
    private UUID productionLineId;
    @ApiModelProperty(value = "产线名称")
    private String  productionLineName;


    @ApiModelProperty(value = "设备id")
    private  UUID deviceId;

    @ApiModelProperty(value = "设备名称 用于界面显示 且支持模糊入参查询")
    private  String deviceName;

    @ApiModelProperty(value = "设备名称 用于界面显示 且支持模糊入参查询")
    private String picture;


    @ApiModelProperty(value = "设备配置名称 用于界面显示")
    private  String deviceFileName;


    @ApiModelProperty(value = "设备字典名称 用于界面显示")
    private  String dictName;

    @ApiModelProperty(value = "状态(是否匹配) 用于界面显示")
    private  String  status;
    @ApiModelProperty(value = "型号  用于界面显示")
    private  String deviceNo;
    @ApiModelProperty(value = "创建时间  用于界面显示")
    private  Long createdTime;





}
