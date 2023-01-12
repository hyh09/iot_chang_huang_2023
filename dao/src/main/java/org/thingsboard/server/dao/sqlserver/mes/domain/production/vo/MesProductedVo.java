package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工序选择列表
 *
 * @author 范王勇
 */
@Data
@ApiModel(value = "MesProductedVo", description = "工序选择列表")
public class MesProductedVo {

    @JsonProperty("sWorkingProcedureNo")
    @ApiModelProperty("工序编号")
    private String sWorkingProcedureNo;

    @JsonProperty("sWorkingProcedureName")
    @ApiModelProperty("工序名称")
    private String sWorkingProcedureName;

    @JsonProperty("sWorkerGroupName")
    @ApiModelProperty("班组名称")
    private String sWorkerGroupName;

    @JsonProperty("sWorkerName")
    @ApiModelProperty("当班人员")
    private String sWorkerName;

    @JsonProperty("nTrackQty")
    @ApiModelProperty("产量")
    private BigDecimal nTrackQty;

    @JsonProperty("tStartTime")
    @ApiModelProperty("开始时间")
    private Date tStartTime;

    @JsonProperty("tEndTime")
    @ApiModelProperty("结束时间")
    private Date tEndTime;
}
