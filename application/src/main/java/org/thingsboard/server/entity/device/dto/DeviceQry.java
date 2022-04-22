package org.thingsboard.server.entity.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.entity.device.AbstractDevice;

@Data
@ApiModel("DeviceQry")
public class DeviceQry extends AbstractDevice{

    //是否过滤掉网关true是，false否
    private Boolean filterGatewayFlag = false;

    @ApiModelProperty("是否过滤设备图片")
    private Boolean filterPictureFlag = false;

    @ApiModelProperty("是否过滤设备图标")
    private Boolean filterIconFlag = false;

    @Override
    public Device toDevice() {
        Device device = super.toDevice();
        device.setFilterGatewayFlag(filterGatewayFlag);
        device.setFilterPictureFlag(filterPictureFlag);
        device.setFilterIconFlag(filterIconFlag);
        return device;
    }


}
