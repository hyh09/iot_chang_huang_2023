package org.thingsboard.server.entity.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.Device;

import java.util.UUID;

@Data
@ApiModel("DeviceIssueQry")
public class DeviceIssueQry {
    @ApiModelProperty(name = "设备字典id")
    public UUID dictDeviceId;

    @ApiModelProperty(name = "工厂名称")
    public String factoryName;

    @ApiModelProperty(name = "车间名称")
    public String workshopName;

    @ApiModelProperty(name = "产线名称")
    public String productionlineName;

    @ApiModelProperty(name = "设备名称")
    public String deviceName;

    @ApiModelProperty(name = "网关名称")
    public String gatewayName;

    public Device toDevice(){
        Device device = new Device();
        device.setDictDeviceId(dictDeviceId);
        device.setFactoryName(factoryName);
        device.setWorkshopName(workshopName);
        device.setProductionLineName(productionlineName);
        device.setName(deviceName);
        device.setGatewayName(gatewayName);
        return device;
    }

}
