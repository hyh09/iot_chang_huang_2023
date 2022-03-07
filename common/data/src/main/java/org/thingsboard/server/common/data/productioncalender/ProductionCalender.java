package org.thingsboard.server.common.data.productioncalender;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductionCalender {
    @ApiModelProperty("唯一标识")
    public UUID id;
    @ApiModelProperty("设备标识")
    public UUID deviceId;
    @ApiModelProperty("设备名称")
    public String deviceName;
    @ApiModelProperty("工厂标识")
    public UUID factoryId;
    @ApiModelProperty("工厂名称")
    public String factoryName;
    @ApiModelProperty("开始时间")
    public Long startTime;
    @ApiModelProperty("结束时间")
    public Long endTime;
    @ApiModelProperty(name = "租户")
    public UUID tenantId;
    @ApiModelProperty("创建人标识")
    public UUID createdUser;
    @ApiModelProperty("创建时间")
    public Long createdTime;
    @ApiModelProperty("修改时间")
    public Long updatedTime;
    @ApiModelProperty("修改人")
    public UUID updatedUser;

    public ProductionCalender(){}

    public ProductionCalender(String deviceName, UUID factoryId, Long startTime, Long endTime) {
        this.deviceName = deviceName;
        this.factoryId = factoryId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ProductionCalender(UUID deviceId){
        this.deviceId = deviceId;
    }


}
