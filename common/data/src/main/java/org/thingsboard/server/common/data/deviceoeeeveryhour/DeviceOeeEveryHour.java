package org.thingsboard.server.common.data.deviceoeeeveryhour;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.statisticoee.StatisticOee;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DeviceOeeEveryHour {

   @ApiModelProperty( "id")
    public UUID id;
   @ApiModelProperty( "设备标识")
    public UUID deviceId;
   @ApiModelProperty( "当前时间")
    public Long ts;
   @ApiModelProperty( "设备oee值")
    public BigDecimal oeeValue;
   @ApiModelProperty( "工厂标识")
    public UUID factoryId;
   @ApiModelProperty( "车间标识")
    public UUID workshopId;
   @ApiModelProperty( "产线标识")
    public UUID productionLineId;
   @ApiModelProperty( "租户表示")
    public UUID tenantId;
   @ApiModelProperty( "创建时间")
    public long createdTime;



   /******************************************/


   @ApiModelProperty(value = "区间开始时间")
   private Long startTime;
    @ApiModelProperty(value = "区间结束时间")
    private Long endTime;

   /******************************************/

    public DeviceOeeEveryHour() {

    }
    public DeviceOeeEveryHour(UUID deviceId, Long ts, BigDecimal oeeValue, UUID tenantId) {
        this.deviceId = deviceId;
        this.ts = ts;
        this.oeeValue = oeeValue;
        this.tenantId = tenantId;
    }

    public DeviceOeeEveryHour(UUID deviceId, Long ts) {
        this.deviceId = deviceId;
        this.ts = ts;
    }

    public DeviceOeeEveryHour(StatisticOee statisticOee) {
        this.deviceId = statisticOee.getDeviceId();
        this.startTime = statisticOee.getStartTime();
        this.endTime = statisticOee.getEndTime();
        this.factoryId = statisticOee.getFactoryId();
        this.workshopId = statisticOee.getWorkshopId();
        this.tenantId = statisticOee.getTenantId();
    }

}
