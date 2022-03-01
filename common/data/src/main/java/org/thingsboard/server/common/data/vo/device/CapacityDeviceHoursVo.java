package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CapacityDeviceHoursVo {

    @ApiModelProperty(name="时间")
    protected String dateTime;

    @ApiModelProperty(name="产能/能耗")
    protected Double capacityOrEnergy;

    public CapacityDeviceHoursVo(){}

    public CapacityDeviceHoursVo(String dateTime,Double capacityOrEnergy){
        this.dateTime = dateTime;
        this.capacityOrEnergy = capacityOrEnergy;
    }
}
