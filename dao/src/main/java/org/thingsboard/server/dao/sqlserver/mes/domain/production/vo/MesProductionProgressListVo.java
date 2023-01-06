package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionProgressListVo",description = "生产进度列表")
public class MesProductionProgressListVo {

    @ApiModelProperty("工序号")
    private String sWorkingProcedureNo;
    @ApiModelProperty("工序")
    private String sWorkingProcedureName;
    @ApiModelProperty("开始时间")
    private String tFactStartTime;
    @ApiModelProperty("结束时间")
    private String tFactEndTime;
    @ApiModelProperty("生产机台")
    private String sEquipmentName;
    @ApiModelProperty("生产数量")
    private String nTrackQty;
    @ApiModelProperty("完工率")
    private String nPercentValue;
    @ApiModelProperty("布车号")
    private String sLocation;
    @ApiModelProperty("生产班组")
    private String sWorkerGroupName;
    @ApiModelProperty("当班员工")
    private String sWorkerNameList;

}
