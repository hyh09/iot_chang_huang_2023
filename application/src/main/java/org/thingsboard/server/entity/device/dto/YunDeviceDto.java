package org.thingsboard.server.entity.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.DeviceId;

@ApiModel("YunDeviceDto")
@Data
public class YunDeviceDto {

    private String tenantId;

    private Long updatedTime;

    public Device toDevice(){
        Device device = new Device();
        if(updatedTime != null){
            device.setUpdatedTime(updatedTime);
        }
        return device;
    }

}
