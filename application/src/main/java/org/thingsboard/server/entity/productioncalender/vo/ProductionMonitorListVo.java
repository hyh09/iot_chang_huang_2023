package org.thingsboard.server.entity.productioncalender.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;

@ApiModel(value = "ProductionMonitorListVo",description = "看板生产监控展示数据")
@Data
public class ProductionMonitorListVo {


    @ApiModelProperty("工厂名称")
    public String factoryName;

    @ApiModelProperty("设备名称")
    public String deviceName;

    @ApiModelProperty("完成量/计划量")
    private String achieveOrPlan;

    @ApiModelProperty("年产能达成率")
    private String yearAchieve;

    @ApiModelProperty("生产状态")
    private Boolean productionState;


    public ProductionMonitorListVo(){}

    public ProductionMonitorListVo(ProductionCalender productionCalender) {
        this.factoryName = productionCalender.getFactoryName();
        this.deviceName = productionCalender.getDeviceName();
        this.achieveOrPlan = productionCalender.getAchieveOrPlan();
        this.yearAchieve = productionCalender.getYearAchieve();
        this.productionState = productionCalender.getProductionState();
    }
}
