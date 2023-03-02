package org.thingsboard.server.dao.sqlserver.mes.domain.production.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.thingsboard.server.common.data.id.TenantId;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * 参数趋势dto
 *
 * @author 范王勇
 */
@Data
@ApiModel(value = "MesChartDto", description = "参数趋势dto")
public class MesChartDto {

    @JsonProperty("tStartTime")
    @ApiModelProperty("开始时间")
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date tStartTime;

    @JsonProperty("tEndTime")
    @ApiModelProperty("结束时间")
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date tEndTime;

    @JsonProperty("uemEquipmentGUID")
    @ApiModelProperty("设备id")
    private String uemEquipmentGUID;

    @ApiModelProperty("设备id,切换参数的时候使用")
    private String key;

    @ApiModelProperty("iot设备id,切换参数的时候使用")
    private UUID deviceId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private TenantId tenantId;
}
