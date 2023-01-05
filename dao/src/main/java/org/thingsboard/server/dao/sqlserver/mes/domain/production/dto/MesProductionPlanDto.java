package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionPlanDto",description = "生产班组")
public class MesProductionPlanDto {
    @ApiModelProperty("日期搜索开始")
    private String tTrackTimeStart;
    @ApiModelProperty("日期搜索结束")
    private String tTrackTimeEnd;
    @ApiModelProperty("班组")
    private String sWorkerGroupName;
    @ApiModelProperty("工序")
    private String sWorkingProcedureName;
}
