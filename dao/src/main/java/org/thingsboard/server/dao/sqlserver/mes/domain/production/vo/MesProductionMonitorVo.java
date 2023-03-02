package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionMonitorVo",description = "生产监控")
public class MesProductionMonitorVo {

    @ApiModelProperty("卡号")
    private String sCardNo;
    @ApiModelProperty("订单号")
    private String sOrderNo;
    @ApiModelProperty("客户")
    private String sCustomerName;
    @ApiModelProperty("交期")
    private String dDeliveryDate;
    @ApiModelProperty("品名")
    private String sMaterialName;
    @ApiModelProperty("颜色")
    private String sColorName;
    @ApiModelProperty("卡数量")
    private String nPlanOutputQty;
    @ApiModelProperty("完工工序")
    private String sWorkingProcedureNameFinish;
    @ApiModelProperty("待生产工序")
    private String sWorkingProcedureName;
    @ApiModelProperty("呆滞时长(h)")
    private String fnMESGetDiffTimeStr;
}
