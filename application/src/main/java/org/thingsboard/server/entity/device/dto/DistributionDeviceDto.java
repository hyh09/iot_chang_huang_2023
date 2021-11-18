package org.thingsboard.server.entity.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.thingsboard.server.common.data.Device;

import java.util.List;
import java.util.UUID;

@ApiModel("DistributionDeviceDto")
@Data
public class DistributionDeviceDto {

    @ApiModelProperty(value = "分配入工厂标识",required = true)
    private UUID factoryId;

    @ApiModelProperty(value = "分配入车间标识",required = true)
    public UUID workshopId;

    @ApiModelProperty(value = "分配入产线标识",required = true)
    public UUID productionLineId;

    @ApiModelProperty(value = "被分配设备标识",required = true)
    public List<UUID> deviceIdList;

    public Device toDevice(){
        Device device = new Device();
        device.setFactoryId(this.factoryId);
        device.setWorkshopId(this.workshopId);
        device.setProductionLineId(this.productionLineId);
        if(CollectionUtils.isNotEmpty(this.deviceIdList)){
            device.setDeviceIdList(this.deviceIdList);
        }

        return device;
    }

}
