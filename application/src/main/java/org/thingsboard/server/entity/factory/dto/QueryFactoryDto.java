package org.thingsboard.server.entity.factory.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("QueryFactoryDto")
public class QueryFactoryDto{

    @ApiModelProperty(name = "租户",required = true)
    public String tenantId;

    @ApiModelProperty(name = "工厂名称")
    public String factoryName;

    @ApiModelProperty(name = "车间名称")
    public String workshopName;

    @ApiModelProperty(name = "产线名称")
    public String productionlineName;

    public QueryFactoryDto(){}

}
