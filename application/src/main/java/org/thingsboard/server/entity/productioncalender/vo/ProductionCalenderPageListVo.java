package org.thingsboard.server.entity.productioncalender.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;

import java.util.UUID;

@ApiModel(value = "ProductionCalenderPageListVo",description = "生产日历分页列表数据")
@Data
public class ProductionCalenderPageListVo{

    @ApiModelProperty("设备标识")
    public UUID deviceId;
    @ApiModelProperty("设备名称")
    public String deviceName;
    @ApiModelProperty("工厂id")
    public UUID factoryId;
    @ApiModelProperty("工厂名称")
    public String factoryName;
    @ApiModelProperty("开始时间")
    public Long startTime;
    @ApiModelProperty("结束时间")
    public Long endTime;
    public ProductionCalenderPageListVo(){}
    public ProductionCalenderPageListVo(ProductionCalender productionCalender) {
        if (productionCalender != null) {
            this.deviceId = productionCalender.getDeviceId();
            this.deviceName = productionCalender.getDeviceName();
            this.factoryId = productionCalender.getFactoryId();
            this.factoryName = productionCalender.getFactoryName();
            this.startTime = productionCalender.getStartTime();
            this.endTime = productionCalender.getEndTime();
        }
    }
}
