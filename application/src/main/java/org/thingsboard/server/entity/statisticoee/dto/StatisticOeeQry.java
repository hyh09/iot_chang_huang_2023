package org.thingsboard.server.entity.statisticoee.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.statisticoee.StatisticOee;

import java.util.UUID;

@Data
@ApiModel("StatisticOeeDto")
public class StatisticOeeQry {

    @ApiModelProperty(value = "区间开始时间")
    private Long startTime;
    @ApiModelProperty(value = "区间结束时间")
    private Long endTime;
    @ApiModelProperty(value = "工厂id")
    private UUID factoryId;
    @ApiModelProperty(value = "车间id")
    private UUID workshopId;
    @ApiModelProperty(value = "设备id")
    private UUID deviceId;

    public StatisticOeeQry(){}

    public StatisticOee toStatisticOee(UUID tenantId){
        return new StatisticOee(
                this.startTime,
                this.endTime,
                this.factoryId,
                this.workshopId,
                this.deviceId,
                tenantId);
    }
}
