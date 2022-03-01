package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CapacityDeviceHoursVo {

    @ApiModelProperty(name="时间（整点）")
    protected String dateTime;

    @ApiModelProperty(name="产量")
    protected Double capacityAddedValue;

    public CapacityDeviceHoursVo(){}

    public CapacityDeviceHoursVo(String dateTime,Double capacityAddedValue){
        this.dateTime = dateTime;
        this.capacityAddedValue = capacityAddedValue;
    }
}
