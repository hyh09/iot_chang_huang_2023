package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单看板产能监控
 *
 * @author wwj
 * @since 2021.10.18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "订单看板产能监控")
public class OrderBoardCapacityResult {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "工厂Id")
    private String factoryId;

    @ApiModelProperty(value = "工厂名称", notes = "仅用于显示")
    private String factoryName;

    @ApiModelProperty(value = "总数量")
    private BigDecimal total;

    @ApiModelProperty(value = "完成度")
    private BigDecimal completeness;

    @ApiModelProperty(value = "是否超时")
    private Boolean isOvertime;
}
