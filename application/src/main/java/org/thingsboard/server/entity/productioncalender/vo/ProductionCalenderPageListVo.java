package org.thingsboard.server.entity.productioncalender.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.entity.productioncalender.AbstractProductionCalender;

@ApiModel(value = "ProductionCalenderPageListVo",description = "生产日历分页列表数据")
@Data
public class ProductionCalenderPageListVo extends AbstractProductionCalender {

    public ProductionCalenderPageListVo(){}

    public ProductionCalenderPageListVo(ProductionCalender productionCalender){
        super(productionCalender);
    }
}
