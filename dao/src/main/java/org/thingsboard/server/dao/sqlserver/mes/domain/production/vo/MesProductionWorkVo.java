package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MesProductionWorkVo",description = "生产报工")
public class MesProductionWorkVo {

    @ApiModelProperty("订单号")
    private String sOrderNo;
    @ApiModelProperty("卡号")
    private String sCardNo;
    @ApiModelProperty("色号")
    private String sColorNo;
    @ApiModelProperty("开始时间")
    private String tFactStartTime;
    @ApiModelProperty("结束时间")
    private String tFactEndTime;
    @ApiModelProperty("时长")
    private String fnMESGetDiffTimeStr;
    @ApiModelProperty("生产产量")
    private String nTrackQty;
    @ApiModelProperty("班组")
    private String sWorkerGroupName;
    @ApiModelProperty("生产机台")
    private String sEquipmentName;

}
