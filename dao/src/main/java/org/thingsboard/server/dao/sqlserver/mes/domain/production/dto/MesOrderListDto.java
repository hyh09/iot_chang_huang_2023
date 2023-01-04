package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesOrderListVo",description = "订单列表")
public class MesOrderListDto {

    @ApiModelProperty("订单号")
    private String sOrderNo;

   /* @ApiModelProperty("订单类型")


    @ApiModelProperty("所属工厂")*/

}
