package org.thingsboard.server.entity.device.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.entity.device.AbstractDevice;

@Data
@ApiModel("DeviceVo")
public class DeviceVo extends AbstractDevice {
    public DeviceVo(){super();}

    public DeviceVo(Device device){super(device);}
}
