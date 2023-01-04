package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesOrderProgressListDto",description = "订单进度列表")
public class MesOrderProgressListDto {

    @ApiModelProperty("交货开始日期")
    private String dDeliveryDateBegin;

    @ApiModelProperty("交货结束日期")
    private String dDeliveryDateEnd;

    @ApiModelProperty("订单号")
    private String sOrderNo;

    @ApiModelProperty("客户")
    private String sCustomerName;

    @ApiModelProperty("品名")
    private String sMaterialName;

    @ApiModelProperty("颜色")
    private String sColorName;

   /* 缺少字段
   @ApiModelProperty("流程卡号")*/

}
