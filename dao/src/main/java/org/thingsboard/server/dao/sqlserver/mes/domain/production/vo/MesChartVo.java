package org.thingsboard.server.dao.sqlserver.mes.domain.production.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.dao.hs.entity.vo.HistoryGraphPropertyTsKvVO;

import java.util.List;
import java.util.UUID;

/**
 * 参数趋势vo
 *
 * @author 范王勇
 */
@Data
@ApiModel(value = "MesChartVo", description = "参数趋势vo")
public class MesChartVo {
    @ApiModelProperty("属性名称")
    private String key;

    @ApiModelProperty("iot设备id")
    private UUID deviceId;
}
