package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionWorkDto",description = "生产报工")
public class MesProductionWorkDto {
    @ApiModelProperty("订单号")
    private String sOrderNo;
    @ApiModelProperty("开始时间")
    private String tFactStartTime;
    @ApiModelProperty("结束时间")
    private String tFactEndTime;
}
