package org.thingsboard.server.entity.productionline.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.entity.productionline.AbstractProductionLine;

@Data
@ApiModel("ProductionLineVo")
public class ProductionLineVo extends AbstractProductionLine {

    public ProductionLineVo(){super();}

    public ProductionLineVo(ProductionLine productionLine){super(productionLine);}
}
