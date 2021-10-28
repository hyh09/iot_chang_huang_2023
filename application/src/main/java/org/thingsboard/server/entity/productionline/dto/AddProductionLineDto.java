package org.thingsboard.server.entity.productionline.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.entity.productionline.AbstractProductionLine;

@Data
@ApiModel("ProductionLineVo")
public class AddProductionLineDto extends AbstractProductionLine {

    public AddProductionLineDto(){super();}

    public AddProductionLineDto(ProductionLine productionLine){super(productionLine);}
}
