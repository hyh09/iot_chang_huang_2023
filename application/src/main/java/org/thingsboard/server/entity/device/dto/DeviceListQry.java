package org.thingsboard.server.entity.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.DeviceProfileId;

import java.util.UUID;

@Data
@ApiModel("DeviceListQry")
public class DeviceListQry {

    @ApiModelProperty("true-已分配，false-未分配（默认值）,null-查所有")
    private Boolean isAllot;

    @ApiModelProperty("设备配置名称")
    private String type;

    @ApiModelProperty("设备配置标识")
    private UUID deviceProfileId;

    /****    以下内置属性     ***/
    @ApiModelProperty("设备名称")
    private String searchText;

    @ApiModelProperty("排序字段")
    private String sortProperty;

    @ApiModelProperty("排序类型（DESC-倒序，ASC-倒序）")
    private String sortOrder;
    /****    以上内置属性     ***/


    public Device toDevice(){
        Device device = new Device();
        device.setType(this.type);
        device.setDeviceProfileId(new DeviceProfileId(this.deviceProfileId));
        device.setAllot(this.isAllot);
        device.setName(this.searchText);
        return device;
    }

}
