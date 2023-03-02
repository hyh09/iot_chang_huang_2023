package org.thingsboard.server.entity.productionline.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.entity.productionline.AbstractProductionLine;

@Data
@ApiModel("ProductionLineVo")
public class ProductionLineVo extends AbstractProductionLine {
    @ApiModelProperty("工厂名称")
    private String factoryName;

    @ApiModelProperty("车间名称")
    private String workshopName;

    public ProductionLineVo(){super();}

    public ProductionLineVo(ProductionLine productionLine){super(productionLine);}
}
