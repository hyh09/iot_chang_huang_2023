package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionProgressListDto",description = "生产进度列表")
public class MesProductionProgressListDto {

    @ApiModelProperty("订单号")
    private String sOrderNo;

}
