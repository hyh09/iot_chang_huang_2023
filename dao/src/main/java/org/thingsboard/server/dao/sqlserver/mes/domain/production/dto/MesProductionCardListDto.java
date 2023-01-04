package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionCardListDto",description = "生产卡进度列表")
public class MesProductionCardListDto {

    @ApiModelProperty("交货日期")
    private String dDeliveryDateBegin;

    @ApiModelProperty("交货日期")
    private String dDeliveryDateEnd;

    @ApiModelProperty("订单号")
    private String sOrderNo;

    @ApiModelProperty("颜色")
    private String sColorName;

    @ApiModelProperty("客户")
    private String sCustomerName;

    @ApiModelProperty("卡号")
    private String sCardNo;

    @ApiModelProperty("品名")
    private String sMaterialName;




}
