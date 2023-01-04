package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesOrderProgressListVo",description = "订单进度列表")
public class MesOrderProgressListVo {

    @ApiModelProperty("订单号")
    private String sOrderNo;

    @ApiModelProperty("客户")
    private String sCustomerName;

    @ApiModelProperty("交货日期")
    private String dDeliveryDate;
    @ApiModelProperty("品名")
    private String sMaterialName;
    @ApiModelProperty("颜色")
    private String sColorName;
    @ApiModelProperty("订单数量")
    private String nQty;
    @ApiModelProperty("整理要求")
    private String sFinishingMethod;
   /* @ApiModelProperty("翻布")
    private String ;
    @ApiModelProperty("坯定")
    private String ;
    @ApiModelProperty("染色")
    private String ;
    @ApiModelProperty("成定")
    private String ;
    @ApiModelProperty("验布")
    private String ;
    @ApiModelProperty("入库")
    private String ;
*/

}
