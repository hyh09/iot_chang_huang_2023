package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionCardListVo",description = "生产卡进度列表")
public class MesProductionCardListVo {

    @ApiModelProperty("卡号")
    private String sCardNo;
    @ApiModelProperty("订单号")
    private String sOrderNo;
    @ApiModelProperty("交货日期")
    private String dDeliveryDate;
    @ApiModelProperty("客户")
    private String sCustomerName;
    @ApiModelProperty("品名")
    private String sMaterialName;
    @ApiModelProperty("颜色")
    private String sColorName;
    @ApiModelProperty("整理要求")
    private String sFinishingMethod;
    @ApiModelProperty("卡数量")
    private String nPlanOutputQty;
    @ApiModelProperty("当前工序")
    private String sWorkingProcedureName;
    /*@ApiModelProperty("工序完工数量")
    private String ;*/
    @ApiModelProperty("下道工序")
    private String sWorkingProcedureNameNext;


}
