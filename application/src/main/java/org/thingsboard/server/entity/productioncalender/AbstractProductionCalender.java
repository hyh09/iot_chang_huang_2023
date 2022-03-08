package org.thingsboard.server.entity.productioncalender;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;

import java.util.UUID;

@Data
public abstract class AbstractProductionCalender {

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
    public long createdTime;
    @ApiModelProperty("修改时间")
    public long updatedTime;
    @ApiModelProperty("修改人")
    public UUID updatedUser;

    public AbstractProductionCalender(){}
    public AbstractProductionCalender(ProductionCalender productionCalender){
        if(productionCalender != null){
            if(productionCalender.getId() != null){
                this.id = productionCalender.getId();
            }
            this.deviceId = productionCalender.getDeviceId();
            this.deviceName = productionCalender.getDeviceName();
            this.factoryId = productionCalender.getFactoryId();
            this.factoryName = productionCalender.getFactoryName();
            this.startTime = productionCalender.getStartTime();
            this.endTime = productionCalender.getEndTime();
            this.tenantId = productionCalender.getTenantId();
            this.createdUser = productionCalender.getCreatedUser();
            this.createdTime = productionCalender.getCreatedTime();
            this.updatedTime = productionCalender.getUpdatedTime();
            this.updatedUser = productionCalender.getUpdatedUser();
        }
    }

    public ProductionCalender toProductionCalender(){
        ProductionCalender productionCalender = new ProductionCalender();
        if(productionCalender != null){
            if(productionCalender.getId() != null){
                this.id = productionCalender.getId();
            }
            productionCalender.setDeviceId(this.deviceId);
            productionCalender.setDeviceName(this.deviceName);
            productionCalender.setFactoryId(this.factoryId);
            productionCalender.setFactoryName(this.factoryName);
            productionCalender.setStartTime(this.startTime);
            productionCalender.setEndTime(this.endTime);
            productionCalender.setTenantId(this.tenantId);
            productionCalender.setCreatedUser(this.createdUser);
            productionCalender.setCreatedTime(this.createdTime);
            productionCalender.setUpdatedTime(this.updatedTime);
            productionCalender.setUpdatedUser(this.updatedUser);
        }
        return productionCalender;
    }

}
