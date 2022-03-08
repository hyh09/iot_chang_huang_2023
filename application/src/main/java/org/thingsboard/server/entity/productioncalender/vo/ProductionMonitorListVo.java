package org.thingsboard.server.entity.productioncalender.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;

import java.util.UUID;

@ApiModel(value = "ProductionMonitorListVo",description = "看板生产监控展示数据")
@Data
public class ProductionMonitorListVo {

    @ApiModelProperty("设备名称")
    public String deviceName;

    @ApiModelProperty("完成量/计划量")
    private String achieveOrPlan;

    @ApiModelProperty("年产能达成率")
    private String yearAchieve;

    @ApiModelProperty("生产状态")
    private String productionState;


    public ProductionMonitorListVo(){}

    public ProductionMonitorListVo(ProductionCalender productionCalender) {
        this.deviceName = productionCalender.getDeviceName();
        this.achieveOrPlan = productionCalender.getAchieveOrPlan();
        this.yearAchieve = productionCalender.getYearAchieve();
        this.productionState = productionCalender.getProductionState();
    }


    @ApiModelProperty(value = "区间开始时间")
    private Long startTime;
    @ApiModelProperty(value = "区间结束时间")
    private Long endTime;
    @ApiModelProperty(value = "工厂Id")
    private UUID factoryId;
    @ApiModelProperty(value = "车间Id")
    private UUID workshopId;
}
