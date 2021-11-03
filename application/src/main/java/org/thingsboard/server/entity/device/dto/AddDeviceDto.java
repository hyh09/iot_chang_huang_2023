package org.thingsboard.server.entity.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.entity.device.AbstractDevice;

import java.util.UUID;

@Data
@ApiModel("AddDeviceDto")
public class AddDeviceDto extends AbstractDevice {

    @ApiModelProperty("设备字典标识")
    private UUID dictDeviceId;

}
