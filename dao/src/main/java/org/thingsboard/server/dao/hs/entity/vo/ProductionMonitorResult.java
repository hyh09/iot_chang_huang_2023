package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 看板生产监控
 * @author ycy
 * @since 2022.3.10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "看板生产监控")
public class ProductionMonitorResult {

    @ApiModelProperty(value = "设备Id")
    private String deviceId;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "完成量")
    private BigDecimal completeAmount;

    @ApiModelProperty(value = "计划量")
    private BigDecimal planAmount;

    @ApiModelProperty(value = "产能达成率")
    private BigDecimal completeness;

    @ApiModelProperty(value = "设备是否正常")
    private Boolean isOk;

}
