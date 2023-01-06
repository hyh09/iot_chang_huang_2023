package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fwy
 * @date 2023/1/5 15:23
 */
@Data
@ApiModel(value = "MesEquipmentProcedureVo", description = "机台生产信息")
public class MesEquipmentProcedureVo {

    @ApiModelProperty("机台id")
    private String mesDeviceId;

    @ApiModelProperty("当前卡号")
    private String cardNo;

    @ApiModelProperty("产品名称")
    private String materialName;

    @ApiModelProperty("当前班组")
    private String workerGroupName;

}
