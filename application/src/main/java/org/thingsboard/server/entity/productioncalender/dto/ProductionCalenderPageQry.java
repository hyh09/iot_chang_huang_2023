package org.thingsboard.server.entity.productioncalender.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;

import java.util.UUID;


@Data
@ApiModel(value = "ProductionCalenderPageQry", description = "生产日历分页查询")
public class ProductionCalenderPageQry {

    @ApiModelProperty("工厂id")
    private UUID factoryId;
    @ApiModelProperty("工厂名称")
    private String factoryName;
    @ApiModelProperty("设备名称")
    private String deviceName;

    public ProductionCalender toProductionCalender(UUID tenantId, String sortProperty,String sortOrder){
        return new ProductionCalender(factoryId,deviceName,factoryName,tenantId,sortProperty,sortOrder);
    }
}
