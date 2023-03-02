package org.thingsboard.server.entity.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceVO;
import org.thingsboard.server.entity.device.AbstractDevice;

@Data
@ApiModel("DeviceVo")
public class DeviceVo extends AbstractDevice {

    //设备字典
    @ApiModelProperty("设备字典")
    private DictDeviceVO dictDeviceVO;

    public DeviceVo(){super();}
    public DeviceVo(Device device){
        super(device);
        super.renameDevice();
    }
}
