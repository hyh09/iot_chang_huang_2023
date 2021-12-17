package org.thingsboard.server.entity.device.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.entity.device.AbstractDevice;

@Data
@ApiModel("DeviceQry")
public class DeviceQry extends AbstractDevice{

    //是否过滤掉网关true是，false否
    private Boolean filterGatewayFlag = false;
    @Override
    public Device toDevice() {
        Device device = super.toDevice();
        device.setFilterGatewayFlag(filterGatewayFlag);
        return device;
    }


}
