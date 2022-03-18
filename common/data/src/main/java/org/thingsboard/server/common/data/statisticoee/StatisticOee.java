package org.thingsboard.server.common.data.statisticoee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@ApiModel("StatisticOee")
public class StatisticOee {

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

    @ApiModelProperty("时间")
    private Long timeHours;
    @ApiModelProperty("OEE值")
    private BigDecimal oeeValue;
    @ApiModelProperty(value = "租户id")
    private UUID tenantId;


    public StatisticOee() {
    }

    /**
     * 技算OEE数据返参
     *
     * @param timeHours
     * @param oeeValue
     */
    public StatisticOee(Long timeHours, BigDecimal oeeValue) {
        this.timeHours = timeHours;
        this.oeeValue = oeeValue;
    }

    /**
     * 技算OEE数据入参
     *
     * @param startTime
     * @param endTime
     * @param factoryId
     * @param workshopId
     * @param deviceId
     */
    public StatisticOee(Long startTime, Long endTime, UUID factoryId, UUID workshopId, UUID deviceId, UUID tenantId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.factoryId = factoryId;
        this.workshopId = workshopId;
        this.deviceId = deviceId;
        this.tenantId = tenantId;
    }
}

