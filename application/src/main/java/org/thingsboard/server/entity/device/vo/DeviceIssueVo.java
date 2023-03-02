package org.thingsboard.server.entity.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.entity.device.AbstractDevice;

import java.util.UUID;

@Data
@ApiModel("DeviceIssueVo")
public class DeviceIssueVo extends AbstractDevice {
    @ApiModelProperty("所属网关")
    private String gatewayName;

    @ApiModelProperty("所属网关标识")
    private UUID gatewayId;

    public DeviceIssueVo(){super();}
    public DeviceIssueVo(Device device){
        super(device);
        this.gatewayName = device.getGatewayName();
        this.gatewayId = device.getGatewayId();
    }
}
