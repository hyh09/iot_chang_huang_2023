package org.thingsboard.server.entity.productioncalender.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.entity.productioncalender.AbstractProductionCalender;

@ApiModel(value = "ProductionCalenderAddDto",description = "生产日历添加数据")
@Data
public class ProductionCalenderAddDto extends AbstractProductionCalender {

    public ProductionCalenderAddDto(){}
    public ProductionCalenderAddDto(ProductionCalender productionCalender){
        super(productionCalender);
    }

}
