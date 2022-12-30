package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionPlanVo",description = "生产班组")
public class MesProductionPlanVo {

    @ApiModelProperty("日期")
    private String tTrackTime;
    @ApiModelProperty("班组")
    private String sWorkerGroupName;
    @ApiModelProperty("班组人员")
    private String sWorkerNameList;
    @ApiModelProperty("工序")
    private String sWorkingProcedureName;
    @ApiModelProperty("订单号")
    private String sOrderNo;
    @ApiModelProperty("卡号")
    private String sCardNo;
    @ApiModelProperty("计划完成日期")
    private String tPlanEndTime;
    @ApiModelProperty("计划数量")
    private String nPlanOutputQty;
    @ApiModelProperty("实际数量")
    private String nTrackQty;
    @ApiModelProperty("超时（分）")
    private String timeout;
}
