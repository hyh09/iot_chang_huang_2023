package org.thingsboard.server.entity.statisticoee.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.statisticoee.StatisticOee;

import java.math.BigDecimal;

@Data
@ApiModel("StatisticOeeVo")
public class StatisticOeeVo {

    @ApiModelProperty("时间")
    private Long timeHours;

    @ApiModelProperty("OEE值")
    private BigDecimal oeeValue;

    public StatisticOeeVo(StatisticOee oee) {
        this.timeHours = oee.getTimeHours();
        this.oeeValue = oee.getOeeValue();
    }

}
