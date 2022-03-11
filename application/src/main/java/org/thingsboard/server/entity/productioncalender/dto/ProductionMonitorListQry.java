package org.thingsboard.server.entity.productioncalender.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;

import java.util.UUID;

@ApiModel(value = "ProductionCalenderAddDto",description = "看板生产监控查询")
@Data
public class ProductionMonitorListQry {

    @ApiModelProperty(value = "区间开始时间")
    private Long startTime;
    @ApiModelProperty(value = "区间结束时间")
    private Long endTime;

    public ProductionMonitorListQry(){}

    public ProductionCalender toProductionCalender(UUID tenantId){
        return new ProductionCalender(
                this.startTime,
                this.endTime,
                tenantId);
    }



}
