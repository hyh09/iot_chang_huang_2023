package org.thingsboard.server.entity.productioncalender.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.entity.productioncalender.AbstractProductionCalender;

@ApiModel(value = "ProductionCalenderUpdVo",description = "生产日历编辑页数据")
@Data
public class ProductionCalenderHisListVo extends AbstractProductionCalender {

    @ApiModelProperty("服务器当前时间")
    private long systemTime;

    public ProductionCalenderHisListVo(){}
    public ProductionCalenderHisListVo(ProductionCalender productionCalender){
        super(productionCalender);
        this.systemTime = systemTime;
    }public ProductionCalenderHisListVo(ProductionCalender productionCalender,long systemTime){
        super(productionCalender);
        this.systemTime = systemTime;
    }

}
