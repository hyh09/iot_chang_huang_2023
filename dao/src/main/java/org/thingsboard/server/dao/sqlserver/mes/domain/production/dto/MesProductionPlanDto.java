package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("MesProductionPlanDto")
public class MesProductionPlanDto {
    @ApiModelProperty("日期")
    private String tTrackTime;
    @ApiModelProperty("班组")
    private String sWorkerGroupName;
    @ApiModelProperty("工序")
    private String sWorkingProcedureName;
}
