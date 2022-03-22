package org.thingsboard.server.entity.factory.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;

import java.util.UUID;

@Data
@ApiModel("QueryFactoryDto")
public class QueryFactoryDto{

    @ApiModelProperty(name = "租户")
    public UUID tenantId;

    @ApiModelProperty(name = "工厂名称")
    public String name;

    @ApiModelProperty(name = "车间名称")
    public String workshopName;

    @ApiModelProperty(name = "产线名称")
    public String productionLineName;

    @ApiModelProperty(name = "设备名称")
    public String deviceName;

    public QueryFactoryDto(){}

    public Factory toFactory(){
        Factory factory = new Factory();
        factory.setTenantId(this.tenantId);
        factory.setName(this.name);
        factory.setWorkshopName(this.workshopName);
        factory.setProductionLineName(this.productionLineName);
        factory.setDeviceName(deviceName);
        return factory;
    }

    public Workshop toWorkshop(){
        Workshop workshop = new Workshop();
        workshop.setTenantId(this.tenantId);
        workshop.setName(this.workshopName);
        return workshop;
    }

    public ProductionLine toProductionLine(){
        ProductionLine productionLine = new ProductionLine();
        productionLine.setTenantId(this.tenantId);
        productionLine.setName(this.productionLineName);
        return productionLine;
    }

}
