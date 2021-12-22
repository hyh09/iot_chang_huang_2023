package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description: 设备返回的属性
 * @author: HU.YUNHUI
 * @create: 2021-11-11 18:24
 **/
@Data
@ToString
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

    @ApiModelProperty(value = "设备的图片")
    private String picture;



    @ApiModelProperty(value = "在线状态 1是在线,0是不在线")
    private  String  onlineStatus="0";

    public DeviceDataVo() {
    }

    public DeviceDataVo(UUID deviceId, String deviceName, String deviceCode, UUID factoryId, String factoryName, UUID workshopId, String workshopName, UUID productionLineId, String productionLineName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
        this.factoryId = factoryId;
        this.factoryName = factoryName;
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.productionLineId = productionLineId;
        this.productionLineName = productionLineName;
    }


    public DeviceDataVo(UUID deviceId, String deviceName, String deviceCode, UUID factoryId, String factoryName, UUID workshopId, String workshopName, UUID productionLineId, String productionLineName, String picture) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
        this.factoryId = factoryId;
        this.factoryName = factoryName;
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.productionLineId = productionLineId;
        this.productionLineName = productionLineName;
        this.picture = picture;
    }


    public static   List<DeviceDataVo>   toData(List<DeviceDataSvc> deviceDataSvcList)
    {

        List<DeviceDataVo> stationDictVOS = deviceDataSvcList.stream().map(m1 -> {
            DeviceDataVo stationDictVO = new DeviceDataVo();
            stationDictVO.setDeviceId(strToUuid(m1.getId()));
            stationDictVO.setDeviceCode(m1.getCode());
            stationDictVO.setDeviceName(m1.getName());
            stationDictVO.setPicture(m1.getPicture());
            stationDictVO.setFactoryId(strToUuid(m1.getFactoryId()));
            stationDictVO.setFactoryName(m1.getFactoryName());
            stationDictVO.setWorkshopId(strToUuid(m1.getWorkshopId()));
            stationDictVO.setWorkshopName(m1.getWorkshopName());
            stationDictVO.setProductionLineId(strToUuid(m1.getProductionLineId()));
            stationDictVO.setProductionLineName(m1.getProductionLineName());
            return stationDictVO;
        }).collect(Collectors.toList());
        return  stationDictVOS;

    }

    private static UUID   strToUuid(String str)
    {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(str))
        {
            return  UUID.fromString(str);
        }
        return null;

    }
}
