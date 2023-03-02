package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionMonitorDto",description = "生产监控")
public class MesProductionMonitorDto {
    @ApiModelProperty("待生产工序")
    private String sWorkingProcedureName;
}
